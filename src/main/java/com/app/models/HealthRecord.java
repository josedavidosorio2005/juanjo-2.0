package com.app.models;

public class HealthRecord {
    private int id;
    private String date;
    private double weight;
    private String bloodPressure;
    private String notes;

    public HealthRecord() {}

    public HealthRecord(int id, String date, double weight, String bloodPressure, String notes) {
        this.id = id;
        this.date = date;
        this.weight = weight;
        this.bloodPressure = bloodPressure;
        this.notes = notes;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public String getBloodPressure() { return bloodPressure; }
    public void setBloodPressure(String bloodPressure) { this.bloodPressure = bloodPressure; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
