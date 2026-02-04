package client;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import static config.TestConfig.API_KEY;
import static config.TestConfig.BASE_URL;
import static io.restassured.RestAssured.given;


public class RestClient {
    String token;
    public static final String ENDPOINT = "/endpoint";
    public static final String ACTION_PARAM = "action";
    public static final String TOKEN_PARAM = "token";
    public static final String ACTION_LOGIN = "LOGIN";
    public static final String ACTION_ACTION = "ACTION";
    public static final String ACTION_LOGOUT = "LOGOUT";

    public RestClient() {
        this.token = Generator.generateToken();
    }

    public RestClient(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    private RequestSpecification baseRequest() {
        return given()
                .contentType(ContentType.URLENC.withCharset("UTF-8"))
                .accept(ContentType.JSON)
                .baseUri(BASE_URL)
                .filter(new AllureRestAssured());
    }

    @Step("Запрос без API ключа")
    public Response noApiKeyRequest() {
        ValidatableResponse response = baseRequest()
                .formParam(TOKEN_PARAM, token)
                .formParam(ACTION_PARAM, ACTION_LOGIN)
                .when()
                .post(ENDPOINT)
                .then();

        return new Response(response);
    }

    public Response baseEndpointRequest(String apiKey, String token, String action, String endpoint) {
        ValidatableResponse response = baseRequest()
                .header("X-Api-Key", apiKey)
                .formParam(TOKEN_PARAM, token)
                .formParam(ACTION_PARAM, action)
                .log().all()
                .when()
                .post(endpoint)
                .then();

        return new Response(response);
    }

    @Step("Отправка запроса ключ={apiKey}, токен={token}, действие={action}, адрес={endpoint}")
    public Response customEndpointRequest(String apiKey, String token, String action, String endpoint) {
        return baseEndpointRequest(apiKey, token, action, endpoint);
    }

    @Step("Логин")
    public Response login(String token) {
        return baseEndpointRequest(API_KEY, token, ACTION_LOGIN, ENDPOINT);
    }

    public Response login() {
        return login(this.token);
    }

    @Step("Действие")
    public Response action(String token) {
        return baseEndpointRequest(API_KEY, token, ACTION_ACTION, ENDPOINT);
    }

    public Response action() {
        return action(this.token);
    }

    @Step("Завершение сессии")
    public Response logout(String token) {
        return baseEndpointRequest(API_KEY, token, ACTION_LOGOUT, ENDPOINT);
    }

    public Response logout() {
        return logout(this.token);
    }

    @Step("Запрос без токена")
    public Response noTokenParameterRequest(String action) {
        ValidatableResponse response = baseRequest()
                .formParam(ACTION_PARAM, action)
                .when()
                .post(ENDPOINT)
                .then();

        return new Response(response);
    }

    @Step("Запрос без токена")
    public Response noActionParameterRequest(String token) {
        ValidatableResponse response = baseRequest()
                .formParam(TOKEN_PARAM, token)
                .when()
                .post(ENDPOINT)
                .then();

        return new Response(response);
    }
}

