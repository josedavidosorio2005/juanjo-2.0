package com.app.models;

public class GoalRecord {
    private int id;
    private String goalName;
    private double currentAmount;
    private double targetAmount;

    public GoalRecord(int id, String goalName, double currentAmount, double targetAmount) {
        this.id = id;
        this.goalName = goalName;
        this.currentAmount = currentAmount;
        this.targetAmount = targetAmount;
    }

    public int getId() { return id; }
    public String getGoalName() { return goalName; }
    public double getCurrentAmount() { return currentAmount; }
    public double getTargetAmount() { return targetAmount; }
}
