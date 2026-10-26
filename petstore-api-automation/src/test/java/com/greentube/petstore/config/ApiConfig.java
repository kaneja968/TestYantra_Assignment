package com.greentube.petstore.config;

import java.net.URI;

public final class ApiConfig {
    private static final String DEFAULT_BASE_URL = "https://petstore.swagger.io/v2";

    private ApiConfig() {}

    public static String baseUrl() {
        String configured = System.getProperty("api.baseUrl");
        if (configured == null || configured.isBlank()) {
            configured = System.getenv("API_BASE_URL");
        }
        if (configured == null || configured.isBlank()) {
            configured = DEFAULT_BASE_URL;
        }

        URI uri;
        try {
            uri = URI.create(configured.trim());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("API base URL must be a valid absolute HTTP(S) URL", exception);
        }
        if (uri.getHost() == null || !("http".equalsIgnoreCase(uri.getScheme())
                || "https".equalsIgnoreCase(uri.getScheme()))) {
            throw new IllegalArgumentException("API base URL must be a valid absolute HTTP(S) URL");
        }
        return configured.trim().replaceAll("/+$", "");
    }
}
