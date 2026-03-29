package com.bengous.e2e.common.client;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RestAssuredClient {

    public static Response get(String path) {
        return RestAssured.given().log().all(true).when().get(path).then().extract().response();
    }

    public static Response post(String path, RequestSpecification requestSpec) {
        return RestAssured.given(requestSpec).log().all(true).post(path).then().extract().response();
    }

    public static Response post(RequestSpecification requestSpec) {
        return RestAssured.given(requestSpec).log().all(true).post().then().extract().response();
    }

    public static Response put(String path, RequestSpecification requestSpec) {
        return RestAssured.given(requestSpec).log().all(true).put(path).then().extract().response();
    }

    public static Response delete(String path) {
        return RestAssured.given().log().all(true).when().delete(path).then().extract().response();
    }
}
