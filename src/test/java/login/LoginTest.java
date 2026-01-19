package login;

import client.RestClient;
import mock.WireMockBaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


@DisplayName("Аутентификация")
public class LoginTest extends WireMockBaseTest {
    RestClient restClient, secondClient;

    @BeforeEach
    void setup() {
        restClient = new RestClient();
    }

    @AfterEach
    void logout() {
        restClient.logout();
        if (secondClient != null) secondClient.logout();
    }

    @Test
    @DisplayName("Логин с валидным токеном")
    void successLoginTest() {
        stubAuthSuccess();

        restClient.login().assertSuccess();

        verifyAuthRequested(restClient.getToken());
    }

    @ParameterizedTest(name = "Токен: {0}")
    @DisplayName("Логин с невалидным токеном")
    @ValueSource(strings = {
            "",
            " ",
            "SH",
            "ABCDEFGHIJKLMNOPQRSTUVWX12345",
            "ABCDEFGHIJKLMNOPQRSTUVWX1234567",
            "abcdefghijklmnopqrstuvwxyz123456",
            "ABCDEFGHIJKLMNOPQRSTUVWX12345!",
            "ABCDEFGHIJKLMNOPQRSTUVWX12345 ",
            " ABCDEFGHIJKLMNOPQRSTUVWX12345"
    })
    void loginWithInvalidTokenTest(String token) {
        restClient = new RestClient(token);
        restClient.login().assertBadRequest();

        verifyAuthNotRequested();
    }

    @Test
    @DisplayName("Логин при существующей сессии")
    void loginIndependentClientsTest() {
        stubAuthSuccess();

        secondClient = new RestClient();
        restClient.login();
        secondClient.login().assertSuccess();

        verifyAuthRequested(secondClient.getToken());
    }

    @Test
    @DisplayName("Обработка ошибки внешнего сервиса при логине")
    void loginServerError() {
        stubAuthServerError();

        restClient.login().assertServerError();
    }
}
