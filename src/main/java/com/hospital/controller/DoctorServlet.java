
package com.hospital.controller;

import com.hospital.dao.DoctorDAO;
import com.hospital.model.Doctor;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@WebServlet("/doctors")
public class DoctorServlet extends HttpServlet {

    private DoctorDAO doctorDAO;

    @Override
    public void init() {
        doctorDAO = new DoctorDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String search = request.getParameter("search");
            String editId = request.getParameter("editId");

            List<Doctor> doctors;

            if (search != null && !search.trim().isEmpty()) {
                doctors = doctorDAO.searchDoctors(search.trim());
            } else {
                doctors = doctorDAO.getAllDoctors();
            }

            Map<Integer, String> departments =
                    doctorDAO.getAllDepartments();

            if (editId != null && !editId.trim().isEmpty()) {
                try {
                    int id = Integer.parseInt(editId);
                    Doctor doctor = doctorDAO.getDoctorById(id);

                    if (doctor == null) {
                        response.sendError(404, "Doctor not found");
                        return;
                    }

                    request.setAttribute("doctorToEdit", doctor);

                } catch (NumberFormatException e) {
                    response.sendError(400, "Invalid doctor ID");
                    return;
                }
            }

            request.setAttribute("doctors", doctors);
            request.setAttribute("departments", departments);
            request.setAttribute("search", search);

            request.getRequestDispatcher(
                    "/WEB-INF/views/doctors.jsp"
            ).forward(request, response);

        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to load doctor records", e
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        try {

            // Delete doctor
            if ("delete".equals(action)) {

                int doctorId = Integer.parseInt(
                        request.getParameter("doctorId")
                );

                boolean deleted = doctorDAO.deleteDoctor(doctorId);

                if (!deleted) {
                    response.sendError(404, "Doctor not found");
                    return;
                }

                response.sendRedirect(
                        request.getContextPath() + "/doctors"
                );
                return;
            }

            // Read and validate form values
            String fullName = request.getParameter("fullName");
            String specialization = request.getParameter("specialization");
            String licenseNumber = request.getParameter("licenseNumber");
            String departmentValue = request.getParameter("departmentId");
            String feeValue = request.getParameter("consultationFee");

            if (fullName == null || fullName.trim().isEmpty()
                    || fullName.trim().length() > 150) {
                throw new IllegalArgumentException("Enter a valid doctor name (maximum 150 characters).");
            }

            if (specialization == null || specialization.trim().isEmpty()
                    || specialization.trim().length() > 100) {
                throw new IllegalArgumentException("Enter a valid specialization (maximum 100 characters).");
            }

            if (licenseNumber == null || licenseNumber.trim().isEmpty()
                    || licenseNumber.trim().length() > 50) {
                throw new IllegalArgumentException("Enter a valid license number (maximum 50 characters).");
            }

            int departmentId = Integer.parseInt(departmentValue);

            Map<Integer, String> departments =
                    doctorDAO.getAllDepartments();

            if (!departments.containsKey(departmentId)) {
                throw new IllegalArgumentException("Select a valid department.");
            }

            BigDecimal fee = new BigDecimal(feeValue);

            if (fee.compareTo(BigDecimal.ZERO) < 0
                    || fee.scale() > 2
                    || fee.precision() > 10) {
                throw new IllegalArgumentException("Enter a valid consultation fee (maximum 10 digits and 2 decimal places).");
            }

            Doctor doctor = new Doctor();
            doctor.setFullName(fullName.trim());
            doctor.setSpecialization(specialization.trim());
            doctor.setLicenseNumber(licenseNumber.trim());
            doctor.setDepartmentId(departmentId);
            doctor.setConsultationFee(fee);

            if ("update".equals(action)) {

                int doctorId = Integer.parseInt(
                        request.getParameter("doctorId")
                );

                doctor.setDoctorId(doctorId);

                boolean updated = doctorDAO.updateDoctor(doctor);

                if (!updated) {
                    response.sendError(404, "Doctor not found");
                    return;
                }

            } else {

                doctorDAO.addDoctor(doctor);
            }

            response.sendRedirect(
                    request.getContextPath() + "/doctors"
            );

        } catch (NumberFormatException | NullPointerException e) {
            response.sendError(400, "Please enter valid form values.");
        } catch (IllegalArgumentException e) {
            response.sendError(400, e.getMessage());
        } catch (SQLException e) {
            throw new ServletException(
                    "Unable to save doctor. Check for duplicate license numbers or related records.",
                    e
            );
        }
    }
}

