package com.greentube.petstore.config;

public final class ApiConfig {
    private ApiConfig() {}
    public static String baseUrl() {
        String configured = System.getProperty("api.baseUrl");
        return configured == null || configured.isBlank()
                ? "https://petstore.swagger.io/v2" : configured;
    }
}
