package com.greentube.petstore.client;

import com.greentube.petstore.config.ApiConfig;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;

public class PetApi {
    private final String baseUrl = ApiConfig.baseUrl();

    public Response create(Object pet) {
        return given().baseUri(baseUrl).contentType("application/json").body(pet).when().post("/pet");
    }
    public Response update(Object pet) {
        return given().baseUri(baseUrl).contentType("application/json").body(pet).when().put("/pet");
    }
     public Response patch() {
        return given().baseUri(baseUrl).when().patch("/pet");
    }
    public Response get(long id) {
        return given().baseUri(baseUrl).pathParam("petId", id).when().get("/pet/{petId}");
    }
    public Response delete(long id) {
        return given().baseUri(baseUrl).header("api_key", "special-key")
                .pathParam("petId", id).when().delete("/pet/{petId}");
    }
}
