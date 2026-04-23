package com.app.models;

public class BudgetRecord {
    private int id;
    private String category;
    private double limitAmount;

    public BudgetRecord(int id, String category, double limitAmount) {
        this.id = id;
        this.category = category;
        this.limitAmount = limitAmount;
    }

    public int getId() { return id; }
    public String getCategory() { return category; }
    public double getLimitAmount() { return limitAmount; }
}
