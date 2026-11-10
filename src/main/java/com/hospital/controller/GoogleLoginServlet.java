package com.hospital.controller;

import com.hospital.util.GoogleOAuthConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/oauth2/google/login")
public class GoogleLoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(true);

        if (!GoogleOAuthConfig.isConfigured()) {
            session.setAttribute(
                    "patientWarning",
                    "Google OAuth is not configured yet. Please set google.client_id and google.client_secret in db.properties."
            );
            response.sendRedirect(request.getContextPath() + "/patients");
            return;
        }

        // Generate cryptographically secure state token to protect against CSRF
        String state = UUID.randomUUID().toString();
        session.setAttribute("oauth_state", state);

        // Redirect user to Google Authorization Consent Screen
        String authUrl = GoogleOAuthConfig.buildAuthorizationUrl(state);
        response.sendRedirect(authUrl);
    }
}
