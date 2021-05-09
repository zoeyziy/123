package com.example.scheduleapp.models;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String name;
    private List<String> vehicles;

    public User(String name, List<String> vehicles) {
        this.name = name;
        this.vehicles = vehicles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getVehicles() {
        return vehicles;
    }

    public void setVehicles(List<String> vehicles) {
        this.vehicles = vehicles;
    }

    public void addVehicle(String vin) {
        this.vehicles.add(vin);
    }

    public void removeVehicle(String vin) {
        this.vehicles.remove(vin);
    }
}
