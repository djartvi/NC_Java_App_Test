package endpoint;

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
import static client.RestClient.*;
import static config.TestConfig.API_KEY;

@DisplayName("Обработка некорректных запросов")
public class EndpointNegativeTest extends WireMockBaseTest {
    RestClient restClient;

    @BeforeEach
    void setup() {
        restClient = new RestClient();
    }

    @AfterEach
    void logout() {
        restClient.logout();
    }

    @ParameterizedTest(name = "Action: {0}")
    @DisplayName("Запрос без параметра 'token'")
    @Description("Тест проверяет, что доступны действия 'LOGIN','ACTION' и 'LOGOUT'")
    @ValueSource(strings = {
            ACTION_LOGIN,
            ACTION_ACTION,
            ACTION_LOGOUT
    })
    void noTokenInRequestTest(String action) {
        restClient
                .noTokenParameterRequest(action)
                .assertBadRequest();

        verifyAuthNotRequested();
        verifyDoActionNotRequested();
    }

    @Test
    @DisplayName("Запрос без параметра 'action'")
    @Description("Тест проверяет обработку запроса без параметра 'action'")
    void noActionInRequestTest() {
        restClient
                .noActionParameterRequest(generateToken())
                .assertBadRequest();

        verifyAuthNotRequested();
        verifyDoActionNotRequested();
    }

    @ParameterizedTest(name = "Endpoint: {0}")
    @DisplayName("Запрос по неверному адресу")
    @Description("Тест проверяет обработку запроса по неверному адресу")
    @ValueSource(strings = {
            "/wrong",
            "/endpoint123",
            "/endpoint/login",
            "/endpoint/action",
            "/endpoin",
            "/endpoints",
            "",
    })
    void incorrectEndpointRequestTest(String endpoint) {
        restClient
                .customEndpointRequest(API_KEY, generateToken(), ACTION_LOGIN, endpoint)
                .assertNotFound();

        verifyAuthNotRequested();
        verifyDoActionNotRequested();
    }

    @Test
    @DisplayName("Запрос без API ключа")
    @Description("Тест проверяет обработку запроса без API ключа")
    void noApiKeyRequestTest() {
        restClient
                .noApiKeyRequest()
                .assertBadRequest();

        verifyAuthNotRequested();
    }

    @Test
    @DisplayName("Запрос с неверным API ключом")
    @Description("Тест проверяет обработку запроса с неверным API ключом")
    void wrongApiKeyRequest() {
        restClient
                .customEndpointRequest("123456", generateToken(), ACTION_LOGIN, ENDPOINT)
                .assertUnauthorized();

        verifyAuthNotRequested();
    }
}
