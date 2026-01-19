package action;

import client.RestClient;
import io.qameta.allure.Description;
import mock.WireMockBaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static client.Generator.generateToken;

@DisplayName("Действия авторизованного пользователя")
public class ActionTest extends WireMockBaseTest {
    RestClient restClient, secondClient;

    @BeforeEach
    void setup() {
        restClient = new RestClient(generateToken());
    }

    @AfterEach
    void logout() {
        restClient.logout();
        if (secondClient != null) secondClient.logout();
    }

    @Test
    @DisplayName("Запрос на выполнение действия")
    @Description("Тест проверяет, что действия доступны после авторизации")
    void actionTest() {
        stubAuthSuccess();
        stubDoActionSuccess();

        restClient.login();
        restClient.action();

        verifyAuthRequested(restClient.getToken());
        verifyDoActionRequested(restClient.getToken());
    }

    @Test
    @DisplayName("Запрос на выполнение действия незалогиненного пользователя")
    @Description("Тест проверяет, что действия не доступны без авторизации")
    void actionForbiddenTest() {
        restClient.action().assertForbidden();
        verifyDoActionNotRequested();
    }

    @Test
    @DisplayName("Обработка ошибки внешнего сервиса при запросе действия")
    @Description("Тест проверяет, что приложение обрабатывает ошибки внешнего сервиса")
    void actionServerError() {
        stubAuthSuccess();
        stubDoActionServerError();

        restClient.login();

        restClient.action().assertServerError();
    }

    @Test
    @DisplayName("Логин при существующей сессии")
    @Description("Тест проверяет, может ли приложение работать с несколькими залогиненными клиентами одновременно")
    void actionForIndependentClientsTest() {
        stubAuthSuccess();
        stubDoActionSuccess();

        restClient.login();

        secondClient = new RestClient();
        secondClient.login();

        secondClient.action().assertSuccess();
        verifyDoActionRequested(secondClient.getToken());

        restClient.action().assertSuccess();
        verifyDoActionRequested(restClient.getToken());
    }

    @ParameterizedTest(name = "Action: {0}")
    @DisplayName("Запрос некорректного действия")
    @Description("Тест проверяет, что клиенту доступны только определённые действия")
    @ValueSource(strings = {
            "DO",
            "DOACTION",
            "DELETE",
            "ENDPOINT",
            "login",
            "action",
            "logout",
            " ",
            "",
            "LOG IN",
            "LOGIN ",
            " LOGIN",
            "LOGIN\n",
            "LOGIN\t",
            "LOGIN_",
            "LOGIN-",
            "LOGIN.ACTION",
            "LOGIN;ACTION",
            "ЛОГИН",
            "123456",
            "@#$%^&*",
            "LOGINACTION"
    })
    void incorrectActionRequest(String action) {
        stubAuthSuccess();

        restClient.login();
        restClient.baseEndpointRequest(restClient.getToken(), action);

        verifyDoActionNotRequested();
    }
}
