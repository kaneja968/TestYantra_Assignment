package com.greentube.petstore.tests;

import com.greentube.petstore.client.PetApi;
import com.greentube.petstore.model.Pet;
import com.greentube.petstore.reporting.SparkReportExtension;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.CONCURRENT)
@ExtendWith(SparkReportExtension.class)
class PetCrudTest {
    private final PetApi api = new PetApi();

    private Pet pet(long id, String name, String status) {
        return new Pet(id, new Pet.Category(1, "Dogs"), name,
                List.of("https://example.com/pet-photo.jpg"),
                List.of(new Pet.Tag(1, "automation")), status);
    }

    @Test
    @Tag("smoke")
    @DisplayName("Create, read, update, and delete a pet")
    @Timeout(60)
    void createReadUpdateDeletePet() {
        long petId = ThreadLocalRandom.current().nextLong(1_000_000_000L, Integer.MAX_VALUE);
        Pet created = pet(petId, "automation-dog-" + petId, "available");
        boolean createdSuccessfully = false;
        boolean deletedSuccessfully = false;

        try {
            Response post = api.create(created);
            assertEquals(200, post.statusCode(), "POST failed: " + post.asString());
            createdSuccessfully = true;

            Response get = api.get(petId);
            assertEquals(200, get.statusCode(), "GET failed: " + get.asString());
            Pet actual = get.as(Pet.class);
            assertEquals(created.id(), actual.id());
            assertEquals(created.name(), actual.name());
            assertEquals(created.category(), actual.category());
            assertEquals(created.photoUrls(), actual.photoUrls());
            assertEquals(created.tags(), actual.tags());
            assertEquals(created.status(), actual.status());

            Pet updated = pet(petId, "updated-dog-" + petId, "sold");
            Response put = api.update(updated);
            assertEquals(200, put.statusCode(), "PUT failed: " + put.asString());

            Response updatedGet = api.get(petId);
            assertEquals(200, updatedGet.statusCode(), "GET after PUT failed: " + updatedGet.asString());
            Pet updatedActual = updatedGet.as(Pet.class);
            assertEquals(updated.name(), updatedActual.name());
            assertEquals(updated.status(), updatedActual.status());

            Response delete = api.delete(petId);
            assertEquals(200, delete.statusCode(), "DELETE failed: " + delete.asString());
            deletedSuccessfully = true;
            assertEquals(404, api.get(petId).statusCode(), "Deleted pet should no longer be found");
        } finally {
            if (createdSuccessfully && !deletedSuccessfully) {
                api.delete(petId);
            }
        }
    }

    @Test
    @DisplayName("Unknown pet returns not found")
    @Timeout(15)
    void getUnknownPetReturnsNotFound() {
        long unknownId = 9_000_000_000L;
        Response response = api.get(unknownId);
        assertEquals(404, response.statusCode(),
                "Unknown pet should return 404 on the demo API. If the shared service behaves differently, inspect response: "
                        + response.asString());
    }

    @Test
    @DisplayName("Unsupported PATCH method returns method not allowed")
    @Timeout(15)
    void unsupportedPatchMethodReturnsMethodNotAllowed() {
        Response response = api.patch();
        assertEquals(405, response.statusCode(),
                "PATCH /pet should return 405 Method Not Allowed; body: " + response.asString());
    }
}
