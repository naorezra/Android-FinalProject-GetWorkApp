package com.shiranaor.GetWork.data.model;

import java.util.ArrayList;

public class Inquirie {
    private String id;
    private String userId;
    private String location;
    private String subject;
    private String date;
    private String description;
    private ArrayList<String> candidates;
    private String selectedEmployee;
    private String uploadsId;

    public Inquirie(String userId, String location, String subject, String date, String description, ArrayList<String> candidates, String selectedEmployee, String uploadsId) {
        this.userId = userId;
        this.location = location;
        this.subject = subject;
        this.date = date;
        this.description = description;
        this.candidates = candidates;
        this.selectedEmployee = selectedEmployee;
        this.uploadsId = uploadsId;
    }
    public Inquirie() {
    }

    public ArrayList<String> getCandidates() {
        return candidates;
    }

    public String getUploadsId() {
        return uploadsId;
    }

    public void setUploadsId(String uploadsId) {
        this.uploadsId = uploadsId;
    }

    public void setCandidates(ArrayList<String> candidates) {
        this.candidates = candidates;
    }

    public String getSelectedEmployee() {
        return selectedEmployee;
    }

    public void setSelectedEmployee(String selectedEmployee) {
        this.selectedEmployee = selectedEmployee;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
