package com.sqa.helpers;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.oauth2;

public class Specification {
    private static final String BASE_URL  =  "https://api.github.com";
    private static final String TOKEN  =  "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";
    public static RequestSpecification requestSpecification(String baseUrl, String accept, String token)
    {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .log(LogDetail.BODY)
                .setBaseUri(baseUrl)
                .setAuth(oauth2(token))
                .addHeader("Accept",accept);
        return builder.build();
    }

    public static RequestSpecification requestSpecificationWithIncorrectAcceptWithAuth(String token)
    {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .log(LogDetail.BODY)
                .setBaseUri(BASE_URL)
                .setAuth(oauth2(token))
                .setAccept(ContentType.XML);
        return builder.build();
    }

    public static RequestSpecification requestSpecificationForIssues()
    {
        RequestSpecBuilder builder = new RequestSpecBuilder()
                .log(LogDetail.BODY)
                .setBasePath("/repos/musagulov/sqa/issues")
                .setAuth(oauth2(TOKEN))
                .setAccept(ContentType.JSON);
        return builder.build();
    }


    public static ResponseSpecification responseSpec(){
        ResponseSpecBuilder builder = new ResponseSpecBuilder()
                .log(LogDetail.BODY);
        return builder.build();
    }


}
