package mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import config.TestConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import static client.RestClient.TOKEN_PARAM;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static config.TestConfig.MOCK_ADDRESS;
import static config.TestConfig.MOCK_PORT;

/**
 * Перед запуском тестов необходимо запустить приложение:
 * java -jar -Dsecret={API_KEY} -Dmock=http://localhost:8888/ internal-0.0.1-SNAPSHOT.jar
 * WireMock будет запущен автоматически в тестах.
 */
public abstract class WireMockBaseTest {
    protected static WireMockServer externalMock;
    private static final String AUTH_ENDPOINT = "/auth";
    private static final String ACTION_ENDPOINT = "/doAction";

    @BeforeAll
    static void setup() {
        if (TestConfig.EXTERNAL_MOCK) {
            externalMock = new WireMockServer(MOCK_PORT);
            externalMock.start();
            configureFor(MOCK_ADDRESS, MOCK_PORT);
        }
    }

    @BeforeEach
    void resetWireMock() {
        if (externalMock != null && TestConfig.EXTERNAL_MOCK) {
            externalMock.resetAll();
            configureFor(MOCK_ADDRESS, MOCK_PORT);
        }
    }

    @AfterAll
    static void teardown() {
        if (externalMock != null) externalMock.stop();
    }

    private void baseStub(String url, int statusCode) {
        if (!TestConfig.EXTERNAL_MOCK) return;

        stubFor(post(urlEqualTo(url))
                .willReturn(aResponse().withStatus(statusCode)));
    }

    private void baseVerify(String url, String token) {
        if (!TestConfig.EXTERNAL_MOCK) return;

        verify(postRequestedFor(urlEqualTo(url))
            .withHeader("Content-Type", containing("application/x-www-form-urlencoded"))
            .withRequestBody(equalTo(TOKEN_PARAM + "=" + token)));
    }

    protected void verifyAuthRequested(String token) {
        baseVerify(AUTH_ENDPOINT, token);
    }

    protected void verifyDoActionRequested(String token) {
        baseVerify(ACTION_ENDPOINT, token);
    }

    protected void verifyAuthNotRequested() {
        if (!TestConfig.EXTERNAL_MOCK) return;

        verify(0, postRequestedFor(urlEqualTo(AUTH_ENDPOINT)));
    }

    protected void verifyDoActionNotRequested() {
        if (!TestConfig.EXTERNAL_MOCK) return;

        verify(0, postRequestedFor(urlEqualTo(ACTION_ENDPOINT)));
    }

    protected void stubAuthSuccess() {
        baseStub(AUTH_ENDPOINT, 200);
    }

    protected void stubAuthServerError() {
        baseStub(AUTH_ENDPOINT, 500);
    }

    protected void stubDoActionSuccess() {
        baseStub(ACTION_ENDPOINT, 200);
    }

    protected void stubDoActionServerError() {
        baseStub(ACTION_ENDPOINT, 500);
    }
}
