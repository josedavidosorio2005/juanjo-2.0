package com.app.gui;

import com.app.dao.FinanceDao;
import com.app.dao.HealthDao;
import com.app.models.FinanceRecord;
import com.app.models.HealthRecord;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class DashboardPanel extends JPanel {
    private FinanceDao financeDao;
    private HealthDao healthDao;

    public DashboardPanel() {
        financeDao = new FinanceDao();
        healthDao = new HealthDao();

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top Panel: KPI Cards
        JPanel kpiPanel = new JPanel(new GridLayout(1, 3, 15, 15));
        kpiPanel.add(createKpiCard("Balance Total", "$" + String.format("%.2f", calculateBalance()), new Color(46, 204, 113)));
        kpiPanel.add(createKpiCard("Último Peso", getLastWeight() + " kg", new Color(52, 152, 219)));
        kpiPanel.add(createKpiCard("Presión Arterial", getLastBP(), new Color(231, 76, 60)));
        add(kpiPanel, BorderLayout.NORTH);

        // Center Panel: Charts
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 15, 15));
        
        // 1. Finance Pie Chart
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Gastos por Categoría",
                createExpenseDataset(),
                true, true, false);
        pieChart.setBackgroundPaint(new Color(0,0,0,0)); 
        ChartPanel piePanel = new ChartPanel(pieChart);
        piePanel.setPreferredSize(new Dimension(350, 300));
        chartsPanel.add(piePanel);

        // 2. Health Line Chart
        JFreeChart lineChart = ChartFactory.createLineChart(
                "Progreso de Peso",
                "Fecha",
                "Peso (kg)",
                createWeightDataset());
        lineChart.setBackgroundPaint(new Color(0,0,0,0));
        ChartPanel linePanel = new ChartPanel(lineChart);
        linePanel.setPreferredSize(new Dimension(350, 300));
        chartsPanel.add(linePanel);

        add(chartsPanel, BorderLayout.CENTER);
    }

    private JPanel createKpiCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }

    private double calculateBalance() {
        List<FinanceRecord> records = financeDao.getAllRecords();
        double balance = 0;
        for (FinanceRecord r : records) {
            if ("Ingreso".equals(r.getType())) {
                balance += r.getAmount();
            } else {
                balance -= r.getAmount();
            }
        }
        return balance;
    }

    private String getLastWeight() {
        List<HealthRecord> records = healthDao.getAllRecords();
        if (records.isEmpty()) return "0.0";
        return String.valueOf(records.get(0).getWeight()); // Order by ID DESC ensures this is the latest
    }

    private String getLastBP() {
        List<HealthRecord> records = healthDao.getAllRecords();
        if (records.isEmpty()) return "N/A";
        return records.get(0).getBloodPressure();
    }

    private DefaultPieDataset createExpenseDataset() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        List<FinanceRecord> records = financeDao.getAllRecords();
        Map<String, Double> expensesByCategory = new HashMap<>();
        
        for (FinanceRecord r : records) {
            if ("Gasto".equals(r.getType())) {
                String cat = r.getCategory() == null || r.getCategory().isEmpty() ? "Otros" : r.getCategory();
                expensesByCategory.put(cat, expensesByCategory.getOrDefault(cat, 0.0) + r.getAmount());
            }
        }
        
        for (Map.Entry<String, Double> entry : expensesByCategory.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue());
        }
        
        return dataset;
    }

    private DefaultCategoryDataset createWeightDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<HealthRecord> records = healthDao.getAllRecords();
        
        // Reverse iterate to show oldest to newest left to right
        for (int i = records.size() - 1; i >= 0; i--) {
            HealthRecord r = records.get(i);
            String displayDate = r.getDate().substring(0, 10);
            dataset.addValue(r.getWeight(), "Peso", displayDate);
        }
        
        return dataset;
    }
}
