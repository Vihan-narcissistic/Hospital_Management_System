package com.hospital.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class GoogleOAuthConfig {

    public static final String AUTH_ENDPOINT = "https://accounts.google.com/o/oauth2/v2/auth";
    public static final String TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token";
    public static final String USERINFO_ENDPOINT = "https://openidconnect.googleapis.com/v1/userinfo";
    public static final String SCOPE = "openid email profile";

    private static final Properties properties = new Properties();

    static {
        try (InputStream input = GoogleOAuthConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("GoogleOAuthConfig: Unable to load db.properties: " + e.getMessage());
        }
    }

    public static String getClientId() {
        String env = System.getenv("GOOGLE_CLIENT_ID");
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        return properties.getProperty("google.client_id", "").trim();
    }

    public static String getClientSecret() {
        String env = System.getenv("GOOGLE_CLIENT_SECRET");
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        return properties.getProperty("google.client_secret", "").trim();
    }

    public static String getRedirectUri() {
        String env = System.getenv("GOOGLE_REDIRECT_URI");
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        return properties.getProperty("google.redirect_uri", "http://localhost:8080/HospitalManagement/oauth2callback").trim();
    }

    public static boolean isConfigured() {
        String clientId = getClientId();
        String clientSecret = getClientSecret();
        return !clientId.isEmpty()
                && !clientSecret.isEmpty()
                && !clientId.startsWith("YOUR_")
                && !clientSecret.startsWith("YOUR_");
    }

    public static String buildAuthorizationUrl(String state) {
        return AUTH_ENDPOINT + "?"
                + "client_id=" + URLEncoder.encode(getClientId(), StandardCharsets.UTF_8)
                + "&response_type=code"
                + "&scope=" + URLEncoder.encode(SCOPE, StandardCharsets.UTF_8)
                + "&redirect_uri=" + URLEncoder.encode(getRedirectUri(), StandardCharsets.UTF_8)
                + "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8)
                + "&prompt=select_account";
    }
}
