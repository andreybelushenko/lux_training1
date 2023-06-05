package com.sqa.github;

import com.sqa.helpers.DataHelperIssueRest;
import com.sqa.helpers.IDataHelperIssue;
import com.sqa.helpers.Specification;
import com.sqa.model.Issue;
import com.sqa.utils.TestLogger;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.ExtractableResponse;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public class RestAssuredDemoTests implements TestLogger {

    private static final RestAssuredConfig config = RestAssuredConfig.config().httpClient(
            HttpClientConfig.httpClientConfig()
                    .setParam("http.socket.timeout", 5000)
                    .setParam("http.connection.timeout", 5000));

    private static final String BASE_URI = "https://api.github.com";
    private static final String ZEN_END_PINT = "/zen";
    private String issueTitle = String.format("issue %s", RandomStringUtils.randomAlphabetic(5));
    private static final String issueDescription = "Description of new issue";

    private IDataHelperIssue dataHelperIssue;

    private RequestSpecification given;

    public RestAssuredDemoTests() {
        this.given = given().spec(Specification.requestSpecificationForIssues()).baseUri(BASE_URI);
        this.dataHelperIssue = new DataHelperIssueRest();
    }

    @BeforeAll
    public static void healthCheck() {
        given().config(config)
                .baseUri(BASE_URI)
                .when()
                .get("/zen")
                .then()
                .log().ifValidationFails()
                .statusCode(200);
    }
    /*
        01. Check that 200 code comes in response to a simple GET
    */

    @Test
    public void verifyHealthcheckTest() {
        given().config(config)
                .baseUri(BASE_URI)
                .when()
                .get("/zen")
                .then()
                .log().ifValidationFails()
                .statusCode(200);
    }

    /*
        02. Check that a non-empty response body to a simple GET is coming
    */
    @Test
    public void verifyDefunktBodyTest() {
        given()
                .baseUri(BASE_URI)
                .when()
                .get(ZEN_END_PINT)
                .then()
                .log().ifValidationFails()
                .body(Matchers.matchesPattern("[A-Z a-z.]*"));
    }

    /*
        03. Check that the answer body contains a field equal to the value
    */

    @Test
    public void verifyIssuesContainTest() {
        given()
                .baseUri(BASE_URI)
                .when()
                .get("/repos/musagulov/sqa/issues")
                .then()
                .log().ifValidationFails()
                .body("message", Matchers.notNullValue())
                .body("message", Matchers.equalTo("Not Found"));
    }

    /*
        04. Check that the response body contains a field after authorization
    */
    @Test
    public void verifyIssuesAuthorized() {
        given()
                .baseUri(BASE_URI)
                .header("Authorization", "Bearer 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")
                .when()
                .get("/repos/musagulov/sqa/issues")
                .then()
                .log().ifValidationFails()
                .body("title", Matchers.hasItems("issue MfnOD"));
    }

    /*
        05. Check that the response body contains an error and a 403 code
    */
    @Test
    public void verifyIssuesNoUserAgent() {
        given()
                .spec(Specification.requestSpecificationWithIncorrectAcceptWithAuth("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"))
                .when()
                .get("/repos/musagulov/sqa/issues")
                .then()
                .log().all()
                .statusCode(415)
                .body("message", Matchers.containsString("Unsupported 'Accept' header"));
    }

    /*
        06. Checking that the issue is published
    */
    @Test
    public void verifyPostIssues() {
        given()
                .spec(Specification.requestSpecification(
                        BASE_URI,
                        "Application/json",
                        "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"))
                .body("{\"title\":\"" + issueTitle + "\",\"body\":\"Im having a problem with this.\"}")
                .when()
                .post("/repos/musagulov/sqa/issues")
                .then()
                .log().body()
                .statusCode(201)
                .body("id", Matchers.greaterThan(0))
                .body("title", Matchers.equalTo(issueTitle));
    }

    /*
        07. Checking that the issue is published (data in params)
    */
    @Test
    public void verifyPostIssuesUrlParam() {
        given()
                .baseUri("https://gorest.co.in/public/v1")
                .auth().oauth2("6a2e66915f5232398603c71eda843f6076c46a853840ec5046ae6b7190db7f36")
                .header("Accept", "Application/json")
                .param("title", "test title")
                .param("body", "test body")
                .relaxedHTTPSValidation()
                .when()
                .post("/users/1234/posts")
                .then()
                .spec(Specification.responseSpec())
                .log().all()
                .statusCode(201);
    }

    /*
        08. Checking that the issue is published (POJO)
    */
    @Test
    public void verifyPostPojo() {
        // Arrange
        Issue requestIssue = new Issue()
                .setTitle(issueTitle)
                .setBody("some Body");

        // Act
        Issue responseIssue = this.given
                .body(requestIssue)
                .when()
                .post()
                .then()
                .spec(Specification.responseSpec())
                .statusCode(201)
                .extract()
                .body().as(Issue.class);

        //Assert
        Issue createdIssue = this.dataHelperIssue.getIssue(responseIssue.getNumber());

        Assertions.assertAll(() -> {
            Assertions.assertEquals(requestIssue,createdIssue);
            Assertions.assertEquals("open", createdIssue.getState());
        });
    }

    /*
        09. Checking that the issue is published (Map)
    */
    @Test
    public void verifyPostMap() {
        //Arrange
        Map<String, Object> requestIssueMap = new HashMap<>();
        requestIssueMap.put("title", issueTitle);
        requestIssueMap.put("body", "some body");

        //Act
        HashMap responseIssueMap = given()
                .baseUri(BASE_URI)
                .header("Accept", "Application/json")
                .auth().oauth2("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")
                .body(requestIssueMap)
                .when()
                .post("/repos/musagulov/sqa/issues")
                .then()
                .statusCode(201)
                .extract().body().as(HashMap.class);

        //Assert
        HashMap createdIssueMap = given()
                .baseUri(BASE_URI)
                .header("Accept", "Application/json")
                .auth().oauth2("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")
                .when()
                .get("/repos/musagulov/sqa/issues/" + responseIssueMap.get("number"))
                .body().as(HashMap.class);

        Assertions.assertAll(() -> {
            Assertions.assertEquals(requestIssueMap.get("title"), createdIssueMap.get("title"));
            Assertions.assertEquals("open", createdIssueMap.get("state"));
        });
    }

    /*
        10. Checking that the issue is published (POJO, json path)
    */
    @Test
    public void verifyPostPojoWithJsonPath() {
        //Arrange
        Issue requestIssue = new Issue()
                .setTitle(issueTitle)
                .setBody("some Body");

        //Act
        ExtractableResponse response = given()
                .baseUri(BASE_URI)
                .header("Accept", "Application/json")
                .auth().oauth2("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")
                .body(requestIssue)
                .when()
                .post("/repos/musagulov/sqa/issues")
                .then()
                .statusCode(201)
                .extract();

        //Assert
        Assertions.assertEquals("musagulov",response.body().jsonPath().get("user.login"));
    }
}
