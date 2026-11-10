package com.hospital.dao;

import com.hospital.model.User;
import com.hospital.util.DatabaseConnection;

import java.sql.*;

public class UserDAO {

    public User getUserByGoogleId(String googleId) throws SQLException {
        if (googleId == null || googleId.isBlank()) {
            return null;
        }

        String sql = "SELECT * FROM users WHERE google_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, googleId.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    public User getUserByEmail(String email) throws SQLException {
        if (email == null || email.isBlank()) {
            return null;
        }

        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * Creates or updates a Google user inside an existing transaction connection.
     */
    public int createOrUpdateGoogleUser(Connection con, String username, String email, String googleId, String profilePicture) throws SQLException {
        // 1. Check if user already exists by googleId
        String selectByGoogle = "SELECT user_id FROM users WHERE google_id = ?";
        try (PreparedStatement ps = con.prepareStatement(selectByGoogle)) {
            ps.setString(1, googleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    // Update profile picture if needed
                    if (profilePicture != null) {
                        try (PreparedStatement updatePs = con.prepareStatement("UPDATE users SET profile_picture = ? WHERE user_id = ?")) {
                            updatePs.setString(1, profilePicture);
                            updatePs.setInt(2, id);
                            updatePs.executeUpdate();
                        }
                    }
                    return id;
                }
            }
        }

        // 2. Check if user already exists by email (link Google ID)
        String selectByEmail = "SELECT user_id, google_id FROM users WHERE email = ?";
        try (PreparedStatement ps = con.prepareStatement(selectByEmail)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    String existingGoogleId = rs.getString(2);
                    if (existingGoogleId == null || existingGoogleId.isBlank()) {
                        try (PreparedStatement updatePs = con.prepareStatement("UPDATE users SET google_id = ?, profile_picture = ? WHERE user_id = ?")) {
                            updatePs.setString(1, googleId);
                            updatePs.setString(2, profilePicture);
                            updatePs.setInt(3, id);
                            updatePs.executeUpdate();
                        }
                    }
                    return id;
                }
            }
        }

        // 3. Insert new user
        String insertSql = "INSERT INTO users (username, email, google_id, password_hash, role, profile_picture) "
                + "VALUES (?, ?, ?, NULL, 'patient', ?)";

        try (PreparedStatement ps = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, googleId);
            ps.setString(4, profilePicture);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        return keys.getInt(1);
                    }
                }
            }
        }

        throw new SQLException("Failed to create Google user");
    }

    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setEmail(rs.getString("email"));
        user.setGoogleId(rs.getString("google_id"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setProfilePicture(rs.getString("profile_picture"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        return user;
    }
}
