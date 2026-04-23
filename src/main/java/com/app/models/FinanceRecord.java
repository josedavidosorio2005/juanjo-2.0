package com.app.models;

public class FinanceRecord {
    private int id;
    private String date;
    private String type; // "Ingreso" or "Gasto"
    private double amount;
    private String category;
    private String description;
    
    // New Pro Fields
    private String account;
    private String sourceTag;
    private String paymentMethod;
    private boolean recurring;

    public FinanceRecord() {}

    public FinanceRecord(int id, String date, String type, double amount, String category, String description,
                         String account, String sourceTag, String paymentMethod, boolean recurring) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.account = account;
        this.sourceTag = sourceTag;
        this.paymentMethod = paymentMethod;
        this.recurring = recurring;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }

    public String getSourceTag() { return sourceTag; }
    public void setSourceTag(String sourceTag) { this.sourceTag = sourceTag; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public boolean isRecurring() { return recurring; }
    public void setRecurring(boolean recurring) { this.recurring = recurring; }
}
