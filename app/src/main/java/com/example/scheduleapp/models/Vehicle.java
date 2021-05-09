package com.example.scheduleapp.models;

public class Vehicle {

    private String make;
    private int mileage;
    private String model;
    private int year;

    public Vehicle() {}

    public Vehicle(String make, int mileage, String model, int year) {
        this.make = make;
        this.mileage = mileage;
        this.model = model;
        this.year = year;
    }

    public String getMake() {
        return make;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    public String getModel() {
        return model;
    }

    public int getYear() {
        return year;
    }
}
