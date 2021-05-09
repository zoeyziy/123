package com.example.scheduleapp.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class Appointment {

    private String company;
    private String description;
    private GeoPoint location;
    private String notes;
    private Timestamp time;
    private String user;
    private String vehicle;

    public Appointment(String company, String description, GeoPoint location, String notes, Timestamp time, String user, String vehicle) {
        this.company = company;
        this.description = description;
        this.location = location;
        this.notes = notes;
        this.time = time;
        this.user = user;
        this.vehicle = vehicle;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getVehicle() {
        return vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }
}
