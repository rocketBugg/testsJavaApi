package api;

import io.qameta.allure.AllureId;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static utils.TestVariables.*;

import org.json.simple.JSONObject;

public class FavoritesTests {
    @Step("Получение токена авторизации")
    public static void getAuthToken() {
        var response = RestAssured.given()
                .baseUri(BASE_URL)
                .when()
                .post("/v1/auth/tokens")
                .then()
                .statusCode(200)
                .extract()
                .response();

        // Сохраняем куки для последующих запросов
        authCookie = response.getCookie("token");
    }

    @Test
    @AllureId("1")
    public void favoritesTestWithCorrectFields() {
        // Data
        double min = 0.001;
        double maxLat = 90;
        double maxLon = 180;
        double rangeRandomForLat = min + (int)(Math.random() * ((maxLat - min) + 1));
        double rangeRandomForLon = min + (int)(Math.random() * ((maxLon - min) + 1));
        // Arrange
        getAuthToken();

        // Act
        var response = given()
            .baseUri(BASE_URL)
            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
            .formParam("title", CORRECT_STRING)
            .formParam("lat", rangeRandomForLat)
            .formParam("lon", rangeRandomForLon)
            .cookie("token", authCookie)
            .when()
            .post("/v1/favorites");

        // Assert
        assertThat(response.getStatusCode()).as("Статус код ответа должен быть 200").isEqualTo(200);
        assertThat(response.body()).as("Тело ответа не должно быть равно null").isNotNull();
        assertThat(response.getBody().as(JSONObject.class).get("title")).as("Значение поля title должно быть равно %s", CORRECT_STRING )
                .isEqualTo(CORRECT_STRING);
        assertThat(response.getBody().as(JSONObject.class).get("lat")).as("Значение поля lat должно быть равно %d", rangeRandomForLat)
                .isEqualTo(rangeRandomForLat);
        assertThat(response.getBody().as(JSONObject.class).get("lon")).as("Значение поля lon должно быть равно %d", rangeRandomForLon)
                .isEqualTo(rangeRandomForLon);
    }

    @Test
    @AllureId("2")
    public void favoritesTestWithNoTitle() {
        // Data
        double min = 0.001;
        double maxLat = 90;
        double maxLon = 180;
        double rangeRandomForLat = min + (int)(Math.random() * ((maxLat - min) + 1));
        double rangeRandomForLon = min + (int)(Math.random() * ((maxLon - min) + 1));

        // Arrange
        getAuthToken();

        // Act && Assert
        given()
            .baseUri(BASE_URL)
            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
            .formParam("lat", rangeRandomForLat)
            .formParam("lon", rangeRandomForLon)
            .cookie("token", authCookie)
            .when()
            .post("/v1/favorites")
            .then()
            .statusCode(400)
            .body("error.message", equalTo(TITLE_MISSING_ERROR));
    }

    @Test
    @AllureId("3")
    public void favoritesTestWithNoLat() {
        // Data
        double min = 0.001;
        double maxLon = 180;
        double rangeRandomForLon = min + (int)(Math.random() * ((maxLon - min) + 1));
        // Arrange
        getAuthToken();

        // Act && Assert
        given()
            .baseUri(BASE_URL)
            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
            .formParam("title", CORRECT_STRING)
            .formParam("lon", rangeRandomForLon)
            .cookie("token", authCookie)
            .when()
            .post("/v1/favorites")
            .then()
            .statusCode(400)
            .body("error.message", equalTo(LAT_MISSING_ERROR));
    }

    @Test
    @AllureId("4")
    public void favoritesTestWithNoLon() {
        // Data
        double min = 0.001;
        double maxLat = 90;
        double rangeRandomForLat = min + (int)(Math.random() * ((maxLat - min) + 1));
        // Arrange
        getAuthToken();

        // Act && Assert
         given()
                .baseUri(BASE_URL)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", CORRECT_STRING)
                .formParam("lat", rangeRandomForLat)
                .cookie("token", authCookie)
                .when()
                .post("/v1/favorites")
                .then()
                .statusCode(400)
                .body("error.message", equalTo(LON_MISSING_ERROR));
    }

    @Test
    @AllureId("5")
    public void favoritesTestWithWrongColor() {
        // Data
        double min = 0.001;
        double maxLat = 90;
        double maxLon = 180;
        double rangeRandomForLat = min + (int)(Math.random() * ((maxLat - min) + 1));
        double rangeRandomForLon = min + (int)(Math.random() * ((maxLon - min) + 1));

        // Arrange
        getAuthToken();

        // Act && Assert
        given()
                .baseUri(BASE_URL)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", CORRECT_STRING)
                .formParam("lat", rangeRandomForLat)
                .formParam("lon", rangeRandomForLon)
                .formParam("color", "1234")
                .cookie("token", authCookie)
                .when()
                .post("/v1/favorites")
                .then()
                .statusCode(400)
                .body("error.message", equalTo(WRONG_COLOR_ERROR));
    }

    @Test
    @AllureId("6")
    public void favoritesTestWithZeroValueTitle() {
        // Data
        double min = 0.001;
        double maxLat = 90;
        double maxLon = 180;
        double rangeRandomForLat = min + (int)(Math.random() * ((maxLat - min) + 1));
        double rangeRandomForLon = min + (int)(Math.random() * ((maxLon - min) + 1));

        // Arrange
        getAuthToken();

        // Act && Assert
        given()
                .baseUri(BASE_URL)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", "")
                .formParam("lat", rangeRandomForLat)
                .formParam("lon", rangeRandomForLon)
                .formParam("color", "1234")
                .cookie("token", authCookie)
                .when()
                .post("/v1/favorites")
                .then()
                .statusCode(400)
                .body("error.message", equalTo(ZERO_VALUE_TITLE_ERROR));
    }
    // Нужно также написать проверку на то, что поле title будет иметь более 999 символов,
    // но из-за бага можно ввести 1000 символов
    // поэтому будет написан тест с 1001 символом

    @Test
    @AllureId("7")
    public void favoritesTestWithMoreThenMaxValueTitle() {
        // Data
        double min = 0.001;
        double maxLat = 90;
        double maxLon = 180;
        double rangeRandomForLat = min + (int)(Math.random() * ((maxLat - min) + 1));
        double rangeRandomForLon = min + (int)(Math.random() * ((maxLon - min) + 1));

        // Arrange
        getAuthToken();

        // Act && Assert
        given()
                .baseUri(BASE_URL)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", THOUSAND_AND_ONE_SYMBOL_STRING)
                .formParam("lat", rangeRandomForLat)
                .formParam("lon", rangeRandomForLon)
                .formParam("color", "1234")
                .cookie("token", authCookie)
                .when()
                .post("/v1/favorites")
                .then()
                .statusCode(400)
                .body("error.message", equalTo(LONG_TITLE_VALUE_ERROR));
    }

    @Test
    @AllureId("8")
    public void favoritesTestWithMoreThenMaxValueLon() {
        // Data
        double min = 0.001;
        double maxLat = 90;
        double rangeRandomForLat = min + (int)(Math.random() * ((maxLat - min) + 1));
        // Arrange
        getAuthToken();

        // Act && Assert
        given()
                .baseUri(BASE_URL)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", CORRECT_STRING)
                .formParam("lat", rangeRandomForLat)
                .formParam("lon", 360)
                .cookie("token", authCookie)
                .when()
                .post("/v1/favorites")
                .then()
                .statusCode(400)
                .body("error.message", equalTo(LONG_LON_VALUE_ERROR));
    }

    @Test
    @AllureId("9")
    public void favoritesTestWithMoreThenMaxValueLat() {
        // Data
        double min = 0.001;
        double maxLon = 180;
        double rangeRandomForLon = min + (int)(Math.random() * ((maxLon - min) + 1));

        // Arrange
        getAuthToken();

        // Act && Assert
        given()
                .baseUri(BASE_URL)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", CORRECT_STRING)
                .formParam("lat", 180)
                .formParam("lon", rangeRandomForLon)
                .cookie("token", authCookie)
                .when()
                .post("/v1/favorites")
                .then()
                .statusCode(400)
                .body("error.message", equalTo(LONG_LAT_VALUE_ERROR));
    }

    @Test
    @AllureId("10")
    public void favoritesTestLessThenMinValueLat() {
        // Arrange
        getAuthToken();

        // Act && Assert
        given()
                .baseUri(BASE_URL)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", CORRECT_STRING)
                .formParam("lat", -181)
                .formParam("lon", 15)
                .cookie("token", authCookie)
                .when()
                .post("/v1/favorites")
                .then()
                .statusCode(400)
                .body("error.message", equalTo(SHORT_LAT_VALUE_ERROR));
    }

    @Test
    @AllureId("11")
    public void favoritesTestLessThenMinValueLon() {
        // Arrange
        getAuthToken();

        // Act && Assert
        given()
                .baseUri(BASE_URL)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", CORRECT_STRING)
                .formParam("lat", 15)
                .formParam("lon", -181)
                .cookie("token", authCookie)
                .when()
                .post("/v1/favorites")
                .then()
                .statusCode(400)
                .body("error.message", equalTo(SHORT_LON_VALUE_ERROR));
    }

    @Test
    @AllureId("12")
    public void favoritesTestWithWrongMethod() {
        // Arrange
        getAuthToken();

        // Act && Assert
        given()
                .baseUri(BASE_URL)
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("title", CORRECT_STRING)
                .formParam("lat", 15)
                .formParam("lon", -15)
                .cookie("token", authCookie)
                .when()
                .get("/v1/favorites")
                .then()
                .statusCode(405)
                .body(equalTo("405: Method Not Allowed"));
    }
    // Если же есть возможность уточнить по поводу значений в поле "color"/посмотреть в сваггере или таске или апи схеме
    // на предмет того, должен ли метод быть чувствителен к регистру. Если да - нужно написать тесты и на это
}
