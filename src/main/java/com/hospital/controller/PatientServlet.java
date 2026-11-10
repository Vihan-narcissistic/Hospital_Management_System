
package com.hospital.controller;

import com.hospital.dao.PatientDAO;
import com.hospital.model.GoogleUser;
import com.hospital.model.Patient;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;

@WebServlet("/patients")
public class PatientServlet extends HttpServlet {

    private PatientDAO patientDAO;

    @Override
    public void init() {
        patientDAO = new PatientDAO();
    }

    

@Override
protected void doGet(
        HttpServletRequest request,
        HttpServletResponse response)
        throws ServletException, IOException {

    try {
        String search = request.getParameter("search");
        String editId = request.getParameter("editId");

        // Load patient details when Edit is clicked
        if (editId != null && !editId.isBlank()) {
            int patientId = Integer.parseInt(editId);

            Patient patient = patientDAO.getPatientById(patientId);

            if (patient == null) {
                response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "Patient not found"
                );
                return;
            }

            request.setAttribute("patientToEdit", patient);
        }

        // Transfer flash messages and Google session user
        HttpSession session = request.getSession(false);
        if (session != null) {
            String[] flashKeys = {"patientAlert", "patientSuccess", "patientWarning", "patientError"};
            for (String key : flashKeys) {
                Object val = session.getAttribute(key);
                if (val != null) {
                    request.setAttribute(key, val);
                    session.removeAttribute(key);
                }
            }
            GoogleUser googleUser = (GoogleUser) session.getAttribute("googleUser");
            if (googleUser != null) {
                request.setAttribute("googleUser", googleUser);
            }
        }

        // Display patient records
        if (search != null && !search.isBlank()) {
            request.setAttribute(
                "patients",
                patientDAO.searchPatients(search)
            );
        } else {
            request.setAttribute(
                "patients",
                patientDAO.getAllPatients()
            );
        }

        request.setAttribute("search", search);

        request.getRequestDispatcher(
            "/WEB-INF/views/patients.jsp"
        ).forward(request, response);

    } catch (NumberFormatException e) {
        response.sendError(
            HttpServletResponse.SC_BAD_REQUEST,
            "Invalid patient ID"
        );
    } catch (SQLException e) {
        throw new ServletException(
            "Unable to retrieve patients", e
        );
    }
}



    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        try {
            
String action = request.getParameter("action");

if ("delete".equals(action)) {

    try {
        int patientId = Integer.parseInt(
            request.getParameter("patientId")
        );

        boolean deleted = patientDAO.deletePatient(patientId);

        if (!deleted) {
            response.sendError(
                HttpServletResponse.SC_NOT_FOUND,
                "Patient not found"
            );
            return;
        }

        response.sendRedirect(
            request.getContextPath() + "/patients"
        );
        return;

    } catch (NumberFormatException e) {
        response.sendError(
            HttpServletResponse.SC_BAD_REQUEST,
            "Invalid patient ID"
        );
        return;
    } catch (SQLException e) {
        throw new ServletException(
            "Unable to delete patient. The patient may have related records.",
            e
        );
    }
}


if ("update".equals(action)) {

    try {
        int patientId = Integer.parseInt(
            request.getParameter("patientId")
        );

        String fullName = request.getParameter("fullName");
        String gender = request.getParameter("gender");
        String dob = request.getParameter("dateOfBirth");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");

        if (fullName == null || fullName.isBlank()
                || fullName.length() > 150
                || phone == null || phone.isBlank()
                || phone.length() > 15
                || !("Male".equals(gender)
                || "Female".equals(gender)
                || "Other".equals(gender))) {

            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid patient information"
            );
            return;
        }

        Date dateOfBirth = null;

        if (dob != null && !dob.isBlank()) {
            dateOfBirth = Date.valueOf(dob);

            if (dateOfBirth.after(
                    new Date(System.currentTimeMillis()))) {
                response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Date of birth cannot be in the future"
                );
                return;
            }
        }

        Patient patient = new Patient(
            null,
            fullName.trim(),
            gender,
            dateOfBirth,
            phone.trim(),
            address == null || address.isBlank()
                ? null : address.trim()
        );

        patient.setPatientId(patientId);

        patientDAO.updatePatient(patient);

        response.sendRedirect(
            request.getContextPath() + "/patients"
        );
        return;

    } catch (IllegalArgumentException e) {
    response.sendError(
        HttpServletResponse.SC_BAD_REQUEST,
        "Invalid patient information"
    );
    return;
}
}

            String fullName = request.getParameter("fullName");
            String gender = request.getParameter("gender");
            String dob = request.getParameter("dateOfBirth");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");

            if (fullName == null || fullName.isBlank()
                    || fullName.length() > 150
                    || phone == null || phone.isBlank()
                    || phone.length() > 15
                    || !("Male".equals(gender)
                    || "Female".equals(gender)
                    || "Other".equals(gender))) {

                response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid patient information"
                );
                return;
            }

            Date dateOfBirth = null;

            if (dob != null && !dob.isBlank()) {
                dateOfBirth = Date.valueOf(dob);
                if (dateOfBirth.after(
                        new Date(System.currentTimeMillis()))) {
                    response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Date of birth cannot be in the future"
                    );
                    return;
                }
            }

            Patient patient = new Patient(
                null,
                fullName.trim(),
                gender,
                dateOfBirth,
                phone.trim(),
                address == null || address.isBlank()
                    ? null : address.trim()
            );

            HttpSession currentSession = request.getSession(false);
            GoogleUser googleUser = currentSession != null
                    ? (GoogleUser) currentSession.getAttribute("googleUser")
                    : null;

            if (googleUser != null) {
                patientDAO.registerGooglePatient(googleUser, patient);
                currentSession.removeAttribute("googleUser");
                currentSession.setAttribute(
                    "patientSuccess",
                    "Patient " + patient.getFullName() + " registered successfully and linked to Google account (" + googleUser.getEmail() + ")!"
                );
            } else {
                patientDAO.addPatient(patient);
                if (currentSession != null) {
                    currentSession.setAttribute(
                        "patientSuccess",
                        "Patient " + patient.getFullName() + " registered successfully!"
                    );
                }
            }

            response.sendRedirect(
                request.getContextPath() + "/patients"
            );

        } catch (IllegalArgumentException e) {
            response.sendError(
                HttpServletResponse.SC_BAD_REQUEST,
                "Invalid date format"
            );
        } catch (SQLException e) {
            throw new ServletException(
                "Unable to register patient", e
            );
        }
            }
}

