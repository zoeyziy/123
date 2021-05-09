package com.example.scheduleapp;

import com.example.scheduleapp.models.Appointment;

public class AppointmentModel {

    private String company, description;
    private AppointmentModel(){}
    private AppointmentModel(String company, String description){
        this.company = company;
        this.description = description;
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




}
