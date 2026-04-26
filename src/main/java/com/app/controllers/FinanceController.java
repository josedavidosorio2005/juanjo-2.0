package com.app.controllers;

import com.app.dao.BudgetDao;
import com.app.dao.FinanceDao;
import com.app.models.BudgetRecord;
import com.app.models.FinanceRecord;

import javax.swing.*;
import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FinanceController {
    
    private FinanceDao financeDao;
    private BudgetDao budgetDao;

    public FinanceController() {
        financeDao = new FinanceDao();
        budgetDao = new BudgetDao();
    }

    /**
     * Executes the predictive alerting logic and saves the record cleanly.
     */
    public boolean processAndSaveTransaction(Component parentView, String amountTxt, String date, String category, 
                                          String account, String desc, String tag, String method, boolean isRecurring, String type) {
        try {
            double amount = Double.parseDouble(amountTxt);
            
            // 1. Predictive Alert Engine
            if ("Gasto".equals(type)) {
                checkPredictiveBudgetAlert(parentView, category, amount);
            }

            // 2. Data persistence
            FinanceRecord r = new FinanceRecord(0, date, type, amount, category, desc, account, tag, method, isRecurring);
            financeDao.insertRecord(r);
            return true;
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parentView, "Por favor ingrese un monto válido (número).", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Predictive AI: Foresees if the transaction approaches the budgeted limit.
     */
    private void checkPredictiveBudgetAlert(Component parentView, String category, double newExpense) {
        List<BudgetRecord> budgets = budgetDao.getAll();
        for (BudgetRecord b : budgets) {
            if (b.getCategory().equals(category)) {
                // Calculate current expenditure in this category
                double currentSpent = 0;
                List<FinanceRecord> records = financeDao.getAllRecords();
                for (FinanceRecord r : records) {
                    if ("Gasto".equals(r.getType()) && category.equals(r.getCategory())) {
                        currentSpent += r.getAmount();
                    }
                }
                
                double totalWillBe = currentSpent + newExpense;
                double limit = b.getLimitAmount();
                double percent = (totalWillBe / limit) * 100;
                
                if (percent >= 100) {
                    JOptionPane.showMessageDialog(parentView, 
                        "¡ALERTA CRÍTICA!\nEste gasto rompe tu límite del presupuesto definido para " + category + ".\nPresupuesto: $" + limit + " | Desbordado a: $" + String.format("%.2f", totalWillBe), 
                        "Alerta Predictiva de Quiebra", JOptionPane.ERROR_MESSAGE);
                } else if (percent >= 80) {
                    JOptionPane.showMessageDialog(parentView, 
                        "Precaución: Con este gasto llegarás al " + String.format("%.1f", percent) + "% de tu límite para " + category + ".", 
                        "Alerta Temprana", JOptionPane.WARNING_MESSAGE);
                }
                break;
            }
        }
    }

    /**
     * OCR Scanner Mock: Simulates reading a picture ticket and extracting fields.
     */
    public String[] simulateOcrScan() {
        // [Amount, Category, Description]
        return new String[]{ "85.50", "Alimentos", "Ticket Walmart - Compra de Despensa" };
    }
}
