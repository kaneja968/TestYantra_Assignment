package com.greentube.petstore.tests;

import com.greentube.petstore.client.PetApi;
import com.greentube.petstore.model.Pet;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PetCrudTest {
    private static final PetApi api = new PetApi();
    // Use a unique positive ID to reduce collisions on the shared public demo service.
    private static final long petId = Math.abs(System.currentTimeMillis() % 2_000_000_000L) + 1;
    private static Pet createdPet;

    private Pet pet(String name, String status) {
        return new Pet(petId, new Pet.Category(1, "Dogs"), name,
                List.of("https://example.com/pet-photo.jpg"),
                List.of(new Pet.Tag(1, "automation")), status);
    }

    @Test @Order(1)
    void postCreatesPetAndGetReturnsMatchingRecord() {
        createdPet = pet("automation-dog-" + petId, "available");
        Response post = api.create(createdPet);
        assertTrue(post.statusCode() >= 200 && post.statusCode() < 300,
                "POST should return a 2xx response; body: " + post.asString());

        Response get = api.get(petId);
        assertEquals(200, get.statusCode(), "GET should return 200; body: " + get.asString());
        Pet actual = get.as(Pet.class);
        assertEquals(createdPet.id(), actual.id());
        assertEquals(createdPet.name(), actual.name());
        assertEquals(createdPet.category(), actual.category());
        assertEquals(createdPet.photoUrls(), actual.photoUrls());
        assertEquals(createdPet.tags(), actual.tags());
        assertEquals(createdPet.status(), actual.status());
    }

    @Test @Order(2)
    void putUpdatesExistingPet() {
        Pet updated = pet("updated-dog-" + petId, "sold");
        Response response = api.update(updated);
        assertTrue(response.statusCode() >= 200 && response.statusCode() < 300,
                "PUT should return a 2xx response; body: " + response.asString());

        Response get = api.get(petId);
        assertEquals(200, get.statusCode());
        Pet actual = get.as(Pet.class);
        assertEquals(updated.name(), actual.name());
        assertEquals(updated.status(), actual.status());
    }

    @Test @Order(3)
    void deleteRemovesPet() {
        Response response = api.delete(petId);
        assertTrue(response.statusCode() >= 200 && response.statusCode() < 300,
                "DELETE should return a 2xx response; body: " + response.asString());
    }

    @Test @Order(4)
    void getUnknownPetReturnsNotFound() {
        long unknownId = 9_000_000_000L;
        Response response = api.get(unknownId);
        assertEquals(404, response.statusCode(),
                "Unknown pet should return 404 on the demo API. If the shared service behaves differently, inspect response: "
                        + response.asString());
    }
}
