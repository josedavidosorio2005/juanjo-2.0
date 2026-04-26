package com.app.gui;

import com.app.dao.FinanceDao;
import com.app.dao.HealthDao;
import com.app.models.FinanceRecord;
import com.app.models.HealthRecord;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import com.app.utils.ChartUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import com.app.utils.AppColors;
import com.app.utils.ChartUtils;
import com.app.utils.AnimationUtils;

public class DashboardPanel extends JPanel {
    private FinanceDao financeDao;
    private HealthDao healthDao;

    public DashboardPanel() {
        financeDao = new FinanceDao();
        healthDao = new HealthDao();

        setLayout(new BorderLayout(20, 20));
        setBackground(AppColors.BG_MAIN);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // 1. Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Resumen General");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(AppColors.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);
        
        JLabel welcome = new JLabel("Bienvenido de nuevo, Samuel");
        welcome.setFont(new Font("Arial", Font.PLAIN, 14));
        welcome.setForeground(AppColors.TEXT_SECONDARY);
        header.add(welcome, BorderLayout.SOUTH);
        
        add(header, BorderLayout.NORTH);

        // 2. Main Content
        JPanel mainContent = new JPanel(new BorderLayout(25, 25));
        mainContent.setOpaque(false);

        // 2.1 Top KPI Row
        JPanel kpiPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        kpiPanel.setOpaque(false);
        
        double balance = calculateBalance();
        JPanel c1 = createKpiCard("Balance Neto", "$" + String.format("%,.0f", balance), "Total acumulado", AppColors.SUCCESS, "💰");
        JPanel c2 = createKpiCard("Último Peso", getLastWeight() + " kg", "Registro reciente", AppColors.PRIMARY, "⚖️");
        JPanel c3 = createKpiCard("Presión Arterial", getLastBP(), "Salud cardiovascular", AppColors.DANGER, "❤️");
        JPanel c4 = createKpiCard("Ahorro Estimado", "$" + String.format("%,.0f", balance * 0.15), "15% del balance", AppColors.ACCENT, "📈");
        
        AnimationUtils.addHoverEffect(c1);
        AnimationUtils.addHoverEffect(c2);
        AnimationUtils.addHoverEffect(c3);
        AnimationUtils.addHoverEffect(c4);

        kpiPanel.add(c1);
        kpiPanel.add(c2);
        kpiPanel.add(c3);
        kpiPanel.add(c4);
        
        mainContent.add(kpiPanel, BorderLayout.NORTH);

        // 2.2 Charts Row
        JPanel chartsPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        chartsPanel.setOpaque(false);
        
        chartsPanel.add(createChartWrapper("Gastos por Categoría", createPieChart()));
        chartsPanel.add(createChartWrapper("Evolución de Peso", createLineChart()));
        
        mainContent.add(chartsPanel, BorderLayout.CENTER);

        // 2.3 Recent Activity Sidebar (Optional but looks good)
        JPanel activityPanel = createStyledPanel("Actividad Reciente");
        activityPanel.setPreferredSize(new Dimension(300, 0));
        DefaultListModel<String> listModel = new DefaultListModel<>();
        List<FinanceRecord> recent = financeDao.getAllRecords().stream().limit(8).collect(java.util.stream.Collectors.toList());
        for (FinanceRecord r : recent) {
            listModel.addElement(String.format("• %s: %s $%.0f", r.getCategory(), r.getType().equals("Gasto") ? "-" : "+", r.getAmount()));
        }
        JList<String> list = new JList<>(listModel);
        list.setFont(new Font("Arial", Font.PLAIN, 13));
        list.setFixedCellHeight(35);
        activityPanel.add(new JScrollPane(list), BorderLayout.CENTER);
        
        add(mainContent, BorderLayout.CENTER);
        add(activityPanel, BorderLayout.EAST);
    }

    private JPanel createKpiCard(String title, String value, String sub, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout(15, 5));
        card.setBackground(AppColors.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER, 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Arial", Font.PLAIN, 28));
        iconLbl.setForeground(color);
        card.add(iconLbl, BorderLayout.WEST);

        JPanel text = new JPanel(new GridLayout(3, 1));
        text.setOpaque(false);
        JLabel tLbl = new JLabel(title);
        tLbl.setFont(new Font("Arial", Font.PLAIN, 13));
        tLbl.setForeground(AppColors.TEXT_SECONDARY);
        JLabel vLbl = new JLabel(value);
        vLbl.setFont(new Font("Arial", Font.BOLD, 22));
        vLbl.setForeground(AppColors.TEXT_PRIMARY);
        JLabel sLbl = new JLabel(sub);
        sLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        sLbl.setForeground(AppColors.TEXT_LIGHT);

        text.add(tLbl);
        text.add(vLbl);
        text.add(sLbl);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private JPanel createChartWrapper(String title, JFreeChart chart) {
        JPanel p = createStyledPanel(title);
        ChartPanel cp = new ChartPanel(chart);
        cp.setBackground(AppColors.SURFACE);
        p.add(cp, BorderLayout.CENTER);
        return p;
    }

    private JPanel createStyledPanel(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 10));
        p.setBackground(AppColors.SURFACE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER, 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Arial", Font.BOLD, 16));
        t.setForeground(AppColors.TEXT_PRIMARY);
        p.add(t, BorderLayout.NORTH);
        return p;
    }

    private JFreeChart createPieChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();
        Map<String, Double> data = new HashMap<>();
        financeDao.getAllRecords().stream()
            .filter(r -> "Gasto".equals(r.getType()))
            .forEach(r -> data.put(r.getCategory(), data.getOrDefault(r.getCategory(), 0.0) + r.getAmount()));
        
        data.forEach(dataset::setValue);
        JFreeChart chart = ChartFactory.createRingChart("", dataset, true, true, false);
        ChartUtils.applyPremiumStyle(chart);
        return chart;
    }

    private JFreeChart createLineChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        List<HealthRecord> records = healthDao.getAllRecords();
        // Show last 10 records for better flow
        for (int i = Math.max(0, records.size() - 10); i < records.size(); i++) {
            HealthRecord r = records.get(i);
            dataset.addValue(r.getWeight(), "Peso", r.getDate().substring(5, 10));
        }
        JFreeChart chart = ChartFactory.createLineChart("", "Fecha", "kg", dataset, PlotOrientation.VERTICAL, false, true, false);
        ChartUtils.applyPremiumStyle(chart);
        return chart;
    }

    private double calculateBalance() {
        return financeDao.getAllRecords().stream()
            .mapToDouble(r -> "Ingreso".equals(r.getType()) ? r.getAmount() : -r.getAmount())
            .sum();
    }

    private String getLastWeight() {
        List<HealthRecord> records = healthDao.getAllRecords();
        return records.isEmpty() ? "0" : String.valueOf(records.get(0).getWeight());
    }

    private String getLastBP() {
        List<HealthRecord> records = healthDao.getAllRecords();
        return records.isEmpty() ? "N/A" : records.get(0).getBloodPressure();
    }
}
