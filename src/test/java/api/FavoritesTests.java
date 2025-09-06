package api;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static utils.TestVariables.*;

import org.json.simple.JSONObject;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FavoritesTests {
    @Step("Получение токена авторизации")
    public static void getAuthToken() {
        Response response = RestAssured.given()
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
    // TODO: покрыть тестами поля "lon", "lat" со значениями меньше минимальных и больше максимальных
    // Если же есть возможность уточнить по поводу значений в поле "color"/посмотреть в сваггере или таске или апи схеме
    // на предмет того, должен ли метод быть чувствителен к регистру. Если да - нужно написать тесты и на это
}
