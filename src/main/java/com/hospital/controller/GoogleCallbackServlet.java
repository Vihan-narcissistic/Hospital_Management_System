package com.hospital.controller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hospital.dao.PatientDAO;
import com.hospital.dao.UserDAO;
import com.hospital.model.GoogleUser;
import com.hospital.model.Patient;
import com.hospital.model.User;
import com.hospital.util.GoogleOAuthConfig;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Duration;

@WebServlet("/oauth2callback")
public class GoogleCallbackServlet extends HttpServlet {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private PatientDAO patientDAO;
    private UserDAO userDAO;

    @Override
    public void init() {
        patientDAO = new PatientDAO();
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(true);

        // 1. Check for error returned by Google
        String error = request.getParameter("error");
        if (error != null && !error.isBlank()) {
            session.setAttribute("patientWarning", "Google sign-in was cancelled or denied: " + error);
            response.sendRedirect(request.getContextPath() + "/patients");
            return;
        }

        // 2. Validate state parameter (CSRF protection)
        String state = request.getParameter("state");
        String savedState = (String) session.getAttribute("oauth_state");
        session.removeAttribute("oauth_state");

        if (savedState == null || state == null || !savedState.equals(state)) {
            session.setAttribute("patientError", "Google authentication failed: state mismatch (CSRF protection).");
            response.sendRedirect(request.getContextPath() + "/patients");
            return;
        }

        // 3. Retrieve authorization code
        String code = request.getParameter("code");
        if (code == null || code.isBlank()) {
            session.setAttribute("patientError", "Google authentication failed: no authorization code received.");
            response.sendRedirect(request.getContextPath() + "/patients");
            return;
        }

        try {
            // 4. Exchange authorization code for tokens
            String requestBody = "code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
                    + "&client_id=" + URLEncoder.encode(GoogleOAuthConfig.getClientId(), StandardCharsets.UTF_8)
                    + "&client_secret=" + URLEncoder.encode(GoogleOAuthConfig.getClientSecret(), StandardCharsets.UTF_8)
                    + "&redirect_uri=" + URLEncoder.encode(GoogleOAuthConfig.getRedirectUri(), StandardCharsets.UTF_8)
                    + "&grant_type=authorization_code";

            HttpRequest tokenRequest = HttpRequest.newBuilder()
                    .uri(URI.create(GoogleOAuthConfig.TOKEN_ENDPOINT))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .timeout(Duration.ofSeconds(15))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> tokenResponse = httpClient.send(
                    tokenRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );

            if (tokenResponse.statusCode() != 200) {
                System.err.println("Google token error: " + tokenResponse.body());
                session.setAttribute("patientError", "Failed to retrieve access token from Google.");
                response.sendRedirect(request.getContextPath() + "/patients");
                return;
            }

            JsonObject tokenJson = JsonParser.parseString(tokenResponse.body()).getAsJsonObject();
            String accessToken = tokenJson.has("access_token") ? tokenJson.get("access_token").getAsString() : null;

            if (accessToken == null || accessToken.isBlank()) {
                session.setAttribute("patientError", "Invalid token response from Google.");
                response.sendRedirect(request.getContextPath() + "/patients");
                return;
            }

            // 5. Fetch verified user info using the access token
            HttpRequest userInfoRequest = HttpRequest.newBuilder()
                    .uri(URI.create(GoogleOAuthConfig.USERINFO_ENDPOINT))
                    .header("Authorization", "Bearer " + accessToken)
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();

            HttpResponse<String> userInfoResponse = httpClient.send(
                    userInfoRequest, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );

            if (userInfoResponse.statusCode() != 200) {
                session.setAttribute("patientError", "Failed to retrieve user profile from Google.");
                response.sendRedirect(request.getContextPath() + "/patients");
                return;
            }

            JsonObject userJson = JsonParser.parseString(userInfoResponse.body()).getAsJsonObject();
            String googleId = userJson.has("sub") ? userJson.get("sub").getAsString() : "";
            String email = userJson.has("email") ? userJson.get("email").getAsString() : "";
            String name = userJson.has("name") ? userJson.get("name").getAsString() : "";
            String picture = userJson.has("picture") ? userJson.get("picture").getAsString() : null;
            boolean emailVerified = userJson.has("email_verified") && userJson.get("email_verified").getAsBoolean();

            if (email.isBlank() || googleId.isBlank()) {
                session.setAttribute("patientError", "Google profile did not provide a verified email or account ID.");
                response.sendRedirect(request.getContextPath() + "/patients");
                return;
            }

            // 6. Check if a patient record ALREADY exists for this Google account
            Patient existingPatient = patientDAO.getPatientByGoogleId(googleId);
            if (existingPatient == null) {
                User existingUser = userDAO.getUserByEmail(email);
                if (existingUser != null) {
                    existingPatient = patientDAO.getPatientByUserId(existingUser.getUserId());
                }
            }

            if (existingPatient != null) {
                // Patient record already exists! Prevent duplicate creation
                session.setAttribute(
                        "patientWarning",
                        "This Google account (" + email + ") is already registered as Patient #"
                                + existingPatient.getPatientId() + " (" + existingPatient.getFullName() + ")."
                );
                response.sendRedirect(request.getContextPath() + "/patients?editId=" + existingPatient.getPatientId());
                return;
            }

            // 7. Store Google profile in session for the patient registration form
            GoogleUser googleUser = new GoogleUser(googleId, email, name, picture, emailVerified);
            session.setAttribute("googleUser", googleUser);
            session.setAttribute(
                    "patientSuccess",
                    "Google identity verified for " + name + " (" + email + "). Please complete the remaining fields below to register."
            );

            response.sendRedirect(request.getContextPath() + "/patients");

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            session.setAttribute("patientError", "Google authentication was interrupted.");
            response.sendRedirect(request.getContextPath() + "/patients");
        } catch (SQLException e) {
            throw new ServletException("Database error during Google authentication callback", e);
        }
    }
}
