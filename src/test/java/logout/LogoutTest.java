package logout;

import client.RestClient;
import io.qameta.allure.Description;
import mock.WireMockBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Выход, завершение сессии")
public class LogoutTest extends WireMockBaseTest {
    RestClient restClient;

    @BeforeEach
    void setup() {
        restClient = new RestClient();
    }

    @Test
    @DisplayName("Разлогин с валидным токеном")
    @Description("Тест проверяет возможность разлогиниться")
    void logoutTest() {
        stubAuthSuccess();

        restClient.login();

        restClient.logout().assertSuccess();
    }

    @Test
    @DisplayName("Действия недоступны после разлогина")
    @Description("Тест проверяет, что действия недоступны после разлогина")
    void noAccessAfterLogoutTest() {
        stubAuthSuccess();

        restClient.login();
        restClient.logout();

        restClient.action().assertForbidden();
        verifyDoActionNotRequested();
    }

    @Test
    @DisplayName("Логин тем же токеном")
    @Description("Тест проверяет возможность разлогиниться и повторно авторизоваться с тем же токеном")
    void loginWithSameTokenAfterLogout() {
        stubAuthSuccess();

        restClient.login();
        restClient.logout();

        restClient.login().assertSuccess();
        verifyAuthRequested(restClient.getToken());
    }
}
