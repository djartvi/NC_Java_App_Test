package client;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static org.hamcrest.Matchers.*;


public class Response {
    ValidatableResponse response;

    public Response(ValidatableResponse response) {
        this.response = response;
    }

    public void assertErrorBody() {
        response
                .body("result", equalTo("ERROR"))
                .body("message", instanceOf(String.class));
    }

    @Step("Проверка, что ответ успешен")
    public void assertSuccess() {
        response
                .statusCode(200)
                .body("result", equalTo("OK"));
    }

    @Step("Проверка ответа на невалидный запрос")
    public void assertBadRequest() {
        response.statusCode(400);
        assertErrorBody();
    }

    @Step("Проверка ответа на неверные учётные данные")
    public void assertUnauthorized() {
        response.statusCode(401);
        assertErrorBody();
    }

    @Step("Проверка ответа на запрос неавторизованного пользователя")
    public void assertForbidden() {
        response.statusCode(403);
        assertErrorBody();
    }

    @Step("Проверка ответа на запрос по несуществующему адресу")
    public void assertNotFound() {
        response.statusCode(404);
        assertErrorBody();
    }

    @Step("Проверка ответа при ошибке сервера")
    public void assertServerError() {
        response.statusCode(greaterThan(499));
        assertErrorBody();
    }
}
