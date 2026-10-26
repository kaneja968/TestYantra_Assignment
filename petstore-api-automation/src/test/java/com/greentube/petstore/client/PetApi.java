package com.greentube.petstore.client;

import com.greentube.petstore.config.ApiConfig;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class PetApi {
    private static final int CONNECTION_TIMEOUT_MS = 5_000;
    private static final int SOCKET_TIMEOUT_MS = 10_000;
    private final String baseUrl;

    public PetApi() {
        this.baseUrl = ApiConfig.baseUrl();
    }

    private RequestSpecification request() {
        RestAssuredConfig config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", CONNECTION_TIMEOUT_MS)
                        .setParam("http.socket.timeout", SOCKET_TIMEOUT_MS));
        return new RequestSpecBuilder()
                .setBaseUri(baseUrl)
                .setAccept(ContentType.JSON)
                .setConfig(config)
                .build();
    }

    public Response create(Object pet) {
        return given().spec(request()).contentType(ContentType.JSON).body(pet).when().post("/pet");
    }

    public Response update(Object pet) {
        return given().spec(request()).contentType(ContentType.JSON).body(pet).when().put("/pet");
    }

    public Response patch() {
        return given().spec(request()).when().patch("/pet");
    }

    public Response get(long id) {
        return given().spec(request()).pathParam("petId", id).when().get("/pet/{petId}");
    }

    public Response delete(long id) {
        return given().spec(request()).header("api_key", "special-key")
                .pathParam("petId", id).when().delete("/pet/{petId}");
    }
}
