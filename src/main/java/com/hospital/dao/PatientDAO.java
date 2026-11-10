
package com.hospital.dao;

import com.hospital.model.Patient;
import com.hospital.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO {

    // CREATE: Register a new patient
    public boolean addPatient(Patient patient) throws SQLException {

        String sql = "INSERT INTO patients "
                + "(user_id, full_name, gender, date_of_birth, phone, address) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            if (patient.getUserId() == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, patient.getUserId());
            }

            ps.setString(2, patient.getFullName());
            ps.setString(3, patient.getGender());

            if (patient.getDateOfBirth() == null) {
                ps.setNull(4, Types.DATE);
            } else {
                ps.setDate(4, patient.getDateOfBirth());
            }

            ps.setString(5, patient.getPhone());
            ps.setString(6, patient.getAddress());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        patient.setPatientId(keys.getInt(1));
                    }
                }
                return true;
            }
        }

        return false;
    }

    // READ: Get all patients (with linked user details if available)
    public List<Patient> getAllPatients() throws SQLException {

        List<Patient> patients = new ArrayList<>();

        String sql = "SELECT p.*, u.email, u.profile_picture, u.google_id "
                   + "FROM patients p "
                   + "LEFT JOIN users u ON p.user_id = u.user_id "
                   + "ORDER BY p.patient_id DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                patients.add(mapPatient(rs));
            }
        }

        return patients;
    }

    // READ: Get one patient by ID
    public Patient getPatientById(int patientId) throws SQLException {

        String sql = "SELECT p.*, u.email, u.profile_picture, u.google_id "
                   + "FROM patients p "
                   + "LEFT JOIN users u ON p.user_id = u.user_id "
                   + "WHERE p.patient_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPatient(rs);
                }
            }
        }

        return null;
    }

    // READ: Get patient by linked user ID
    public Patient getPatientByUserId(int userId) throws SQLException {

        String sql = "SELECT p.*, u.email, u.profile_picture, u.google_id "
                   + "FROM patients p "
                   + "LEFT JOIN users u ON p.user_id = u.user_id "
                   + "WHERE p.user_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPatient(rs);
                }
            }
        }

        return null;
    }

    // READ: Get patient by Google Subject ID
    public Patient getPatientByGoogleId(String googleId) throws SQLException {
        if (googleId == null || googleId.isBlank()) {
            return null;
        }

        String sql = "SELECT p.*, u.email, u.profile_picture, u.google_id "
                   + "FROM patients p "
                   + "INNER JOIN users u ON p.user_id = u.user_id "
                   + "WHERE u.google_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, googleId.trim());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPatient(rs);
                }
            }
        }

        return null;
    }

    // UPDATE: Modify patient details
    public boolean updatePatient(Patient patient) throws SQLException {

        String sql = "UPDATE patients SET "
                + "user_id = ?, full_name = ?, gender = ?, "
                + "date_of_birth = ?, phone = ?, address = ? "
                + "WHERE patient_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (patient.getUserId() == null) {
                ps.setNull(1, Types.INTEGER);
            } else {
                ps.setInt(1, patient.getUserId());
            }

            ps.setString(2, patient.getFullName());
            ps.setString(3, patient.getGender());

            if (patient.getDateOfBirth() == null) {
                ps.setNull(4, Types.DATE);
            } else {
                ps.setDate(4, patient.getDateOfBirth());
            }

            ps.setString(5, patient.getPhone());
            ps.setString(6, patient.getAddress());
            ps.setInt(7, patient.getPatientId());

            return ps.executeUpdate() > 0;
        }
    }

    // DELETE: Remove patient by ID
    public boolean deletePatient(int patientId) throws SQLException {

        String sql = "DELETE FROM patients WHERE patient_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, patientId);

            return ps.executeUpdate() > 0;
        }
    }

    // Convert database row into Patient object
    private Patient mapPatient(ResultSet rs) throws SQLException {

        Patient patient = new Patient();

        patient.setPatientId(rs.getInt("patient_id"));

        int userId = rs.getInt("user_id");
        patient.setUserId(rs.wasNull() ? null : userId);

        patient.setFullName(rs.getString("full_name"));
        patient.setGender(rs.getString("gender"));
        patient.setDateOfBirth(rs.getDate("date_of_birth"));
        patient.setPhone(rs.getString("phone"));
        patient.setAddress(rs.getString("address"));

        try {
            patient.setEmail(rs.getString("email"));
        } catch (SQLException ignored) {
        }

        try {
            patient.setProfilePicture(rs.getString("profile_picture"));
        } catch (SQLException ignored) {
        }

        try {
            patient.setGoogleId(rs.getString("google_id"));
        } catch (SQLException ignored) {
        }

        return patient;
    }

    // SEARCH: Find patients by name, phone or email
    public List<Patient> searchPatients(String keyword)
            throws SQLException {

        List<Patient> patients = new ArrayList<>();

        String sql = "SELECT p.*, u.email, u.profile_picture, u.google_id "
                   + "FROM patients p "
                   + "LEFT JOIN users u ON p.user_id = u.user_id "
                   + "WHERE p.full_name LIKE ? "
                   + "OR p.phone LIKE ? "
                   + "OR u.email LIKE ? "
                   + "ORDER BY p.patient_id DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String searchValue = "%" + keyword.trim() + "%";

            ps.setString(1, searchValue);
            ps.setString(2, searchValue);
            ps.setString(3, searchValue);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    patients.add(mapPatient(rs));
                }
            }
        }

        return patients;
    }

    // REGISTER GOOGLE PATIENT: Transactional insert of User and Patient
    public boolean registerGooglePatient(com.hospital.model.GoogleUser googleUser, Patient patient) throws SQLException {
        String username = googleUser.getEmail();
        if (googleUser.getName() != null && !googleUser.getName().isBlank()) {
            username = googleUser.getName().trim();
        }

        try (Connection con = DatabaseConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                UserDAO userDAO = new UserDAO();
                int userId = userDAO.createOrUpdateGoogleUser(
                        con, username, googleUser.getEmail(), googleUser.getGoogleId(), googleUser.getPicture()
                );

                // Prevent duplicate patient record for this user
                String checkSql = "SELECT patient_id FROM patients WHERE user_id = ?";
                try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {
                    checkPs.setInt(1, userId);
                    try (ResultSet rs = checkPs.executeQuery()) {
                        if (rs.next()) {
                            con.rollback();
                            throw new SQLException("A patient record is already registered for this Google account.");
                        }
                    }
                }

                patient.setUserId(userId);

                String sql = "INSERT INTO patients "
                        + "(user_id, full_name, gender, date_of_birth, phone, address) "
                        + "VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, userId);
                    ps.setString(2, patient.getFullName());
                    ps.setString(3, patient.getGender());

                    if (patient.getDateOfBirth() == null) {
                        ps.setNull(4, Types.DATE);
                    } else {
                        ps.setDate(4, patient.getDateOfBirth());
                    }

                    ps.setString(5, patient.getPhone());
                    ps.setString(6, patient.getAddress());

                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        try (ResultSet keys = ps.getGeneratedKeys()) {
                            if (keys.next()) {
                                patient.setPatientId(keys.getInt(1));
                            }
                        }
                        con.commit();
                        return true;
                    }
                }

                con.rollback();
                return false;

            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

}

