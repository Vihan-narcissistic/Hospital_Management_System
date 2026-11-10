
package com.hospital.model;

import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;

public class Patient {

    private int patientId;
    private Integer userId;
    private String fullName;
    private String gender;
    private Date dateOfBirth;
    private String phone;
    private String address;

    // Additional fields linked via users table
    private String email;
    private String profilePicture;
    private String googleId;

    public Patient() {
    }

    public Patient(Integer userId, String fullName,
                   String gender, Date dateOfBirth,
                   String phone, String address) {

        this.userId = userId;
        this.fullName = fullName;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.phone = phone;
        this.address = address;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    /**
     * Calculates the age dynamically in years from dateOfBirth.
     */
    public Integer getAge() {
        if (dateOfBirth == null) {
            return null;
        }
        try {
            LocalDate birth = dateOfBirth.toLocalDate();
            LocalDate now = LocalDate.now();
            if (birth.isAfter(now)) {
                return null;
            }
            return Period.between(birth, now).getYears();
        } catch (Exception e) {
            return null;
        }
    }
}

