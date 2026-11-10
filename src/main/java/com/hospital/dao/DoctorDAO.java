
package com.hospital.dao;

import com.hospital.model.Doctor;
import com.hospital.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DoctorDAO {

    // Add a doctor
    public boolean addDoctor(Doctor doctor) throws SQLException {

        String sql = "INSERT INTO doctors " +
                "(department_id, full_name, specialization, license_number, consultation_fee) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, doctor.getDepartmentId());
            ps.setString(2, doctor.getFullName());
            ps.setString(3, doctor.getSpecialization());
            ps.setString(4, doctor.getLicenseNumber());
            ps.setBigDecimal(5, doctor.getConsultationFee());

            return ps.executeUpdate() > 0;
        }
    }

    // Get all doctors with their department names using JOIN
    public List<Doctor> getAllDoctors() throws SQLException {

        List<Doctor> doctors = new ArrayList<>();

        String sql = "SELECT d.*, dep.department_name " +
                "FROM doctors d " +
                "JOIN departments dep ON d.department_id = dep.department_id " +
                "ORDER BY d.doctor_id DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                doctors.add(mapDoctor(rs));
            }
        }

        return doctors;
    }

    // Get one doctor by ID
    public Doctor getDoctorById(int doctorId) throws SQLException {

        String sql = "SELECT d.*, dep.department_name " +
                "FROM doctors d " +
                "JOIN departments dep ON d.department_id = dep.department_id " +
                "WHERE d.doctor_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, doctorId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapDoctor(rs);
                }
            }
        }

        return null;
    }

    // Update a doctor
    public boolean updateDoctor(Doctor doctor) throws SQLException {

        String sql = "UPDATE doctors SET department_id = ?, " +
                "full_name = ?, specialization = ?, license_number = ?, " +
                "consultation_fee = ? WHERE doctor_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, doctor.getDepartmentId());
            ps.setString(2, doctor.getFullName());
            ps.setString(3, doctor.getSpecialization());
            ps.setString(4, doctor.getLicenseNumber());
            ps.setBigDecimal(5, doctor.getConsultationFee());
            ps.setInt(6, doctor.getDoctorId());

            return ps.executeUpdate() > 0;
        }
    }

    // Delete a doctor
    public boolean deleteDoctor(int doctorId) throws SQLException {

        String sql = "DELETE FROM doctors WHERE doctor_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, doctorId);

            return ps.executeUpdate() > 0;
        }
    }

    // Search doctors by name, specialization or license number
    public List<Doctor> searchDoctors(String keyword) throws SQLException {

        List<Doctor> doctors = new ArrayList<>();

        String sql = "SELECT d.*, dep.department_name " +
                "FROM doctors d " +
                "JOIN departments dep ON d.department_id = dep.department_id " +
                "WHERE d.full_name LIKE ? " +
                "OR d.specialization LIKE ? " +
                "OR d.license_number LIKE ? " +
                "ORDER BY d.doctor_id DESC";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String searchValue = "%" + keyword + "%";

            ps.setString(1, searchValue);
            ps.setString(2, searchValue);
            ps.setString(3, searchValue);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    doctors.add(mapDoctor(rs));
                }
            }
        }

        return doctors;
    }

    // Get departments for the dropdown
    public Map<Integer, String> getAllDepartments() throws SQLException {

        Map<Integer, String> departments = new LinkedHashMap<>();

        String sql = "SELECT department_id, department_name " +
                "FROM departments ORDER BY department_name";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                departments.put(
                        rs.getInt("department_id"),
                        rs.getString("department_name")
                );
            }
        }

        return departments;
    }

    // Convert ResultSet row into Doctor object
    private Doctor mapDoctor(ResultSet rs) throws SQLException {

        Doctor doctor = new Doctor();

        doctor.setDoctorId(rs.getInt("doctor_id"));

        int userId = rs.getInt("user_id");
        doctor.setUserId(rs.wasNull() ? null : userId);

        doctor.setDepartmentId(rs.getInt("department_id"));
        doctor.setDepartmentName(rs.getString("department_name"));
        doctor.setFullName(rs.getString("full_name"));
        doctor.setSpecialization(rs.getString("specialization"));
        doctor.setLicenseNumber(rs.getString("license_number"));
        doctor.setConsultationFee(rs.getBigDecimal("consultation_fee"));

        return doctor;
    }
}

