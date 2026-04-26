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
import com.app.utils.AnimationUtils;

public class DashboardPanel extends JPanel {
    private FinanceDao financeDao;
    private HealthDao healthDao;
    
    private JPanel kpiPanel, chartsPanel, activityPanel;
    private JLabel balanceVal, weightVal, bpVal, savingVal, projectionVal;

    public DashboardPanel() {
        financeDao = new FinanceDao();
        healthDao = new HealthDao();

        setLayout(new BorderLayout(20, 20));
        setBackground(AppColors.BG_MAIN);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // 1. Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Resumen Inteligente");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(AppColors.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);
        
        JLabel welcome = new JLabel("Analizando tu salud financiera en tiempo real...");
        welcome.setFont(new Font("Arial", Font.PLAIN, 14));
        welcome.setForeground(AppColors.TEXT_SECONDARY);
        header.add(welcome, BorderLayout.SOUTH);
        
        add(header, BorderLayout.NORTH);

        // 2. Main Content
        JPanel mainContent = new JPanel(new BorderLayout(25, 25));
        mainContent.setOpaque(false);

        // 2.1 Top KPI Row
        kpiPanel = new JPanel(new GridLayout(1, 5, 15, 0));
        kpiPanel.setOpaque(false);
        
        balanceVal = new JLabel("$---");
        weightVal = new JLabel("--- kg");
        bpVal = new JLabel("---");
        savingVal = new JLabel("$---");
        projectionVal = new JLabel("$---");

        kpiPanel.add(createKpiCard("Balance Neto", balanceVal, "Disponible", AppColors.SUCCESS, "💰"));
        kpiPanel.add(createKpiCard("Gasto Proyectado", projectionVal, "Estimado mes", AppColors.WARNING, "🔮"));
        kpiPanel.add(createKpiCard("Último Peso", weightVal, "Salud Física", AppColors.PRIMARY, "⚖️"));
        kpiPanel.add(createKpiCard("Presión Art.", bpVal, "Reciente", AppColors.DANGER, "❤️"));
        kpiPanel.add(createKpiCard("Ahorro Sugerido", savingVal, "20% Sugerido", AppColors.ACCENT, "📈"));
        
        mainContent.add(kpiPanel, BorderLayout.NORTH);

        // 2.2 Charts Row
        chartsPanel = new JPanel(new GridLayout(1, 2, 25, 0));
        chartsPanel.setOpaque(false);
        mainContent.add(chartsPanel, BorderLayout.CENTER);

        // 2.3 Activity
        activityPanel = createStyledPanel("Actividad Reciente");
        activityPanel.setPreferredSize(new Dimension(300, 0));
        
        add(mainContent, BorderLayout.CENTER);
        add(activityPanel, BorderLayout.EAST);

        refreshDataAsync();
    }

    private void refreshDataAsync() {
        new SwingWorker<Map<String, Object>, Void>() {
            @Override
            protected Map<String, Object> doInBackground() {
                Map<String, Object> data = new HashMap<>();
                List<FinanceRecord> financeRecords = financeDao.getAllRecords();
                List<HealthRecord> healthRecords = healthDao.getAllRecords();

                double ing = 0, gas = 0;
                for (FinanceRecord r : financeRecords) {
                    if ("Ingreso".equals(r.getType())) ing += r.getAmount();
                    else gas += r.getAmount();
                }
                double balance = ing - gas;
                
                int dayOfMonth = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH);
                double projection = (gas / dayOfMonth) * 30;

                data.put("balance", balance);
                data.put("projection", projection);
                data.put("weight", healthRecords.isEmpty() ? 0.0 : healthRecords.get(healthRecords.size()-1).getWeight());
                data.put("bp", healthRecords.isEmpty() ? "N/A" : healthRecords.get(healthRecords.size()-1).getBloodPressure());
                data.put("recent", financeRecords.stream().limit(10).collect(java.util.stream.Collectors.toList()));
                data.put("pie", createPieChart(financeRecords));
                data.put("line", createLineChart(healthRecords));
                
                return data;
            }

            @Override
            protected void done() {
                try {
                    Map<String, Object> data = get();
                    balanceVal.setText("$" + String.format("%,.0f", (Double)data.get("balance")));
                    projectionVal.setText("$" + String.format("%,.0f", (Double)data.get("projection")));
                    weightVal.setText(data.get("weight") + " kg");
                    bpVal.setText((String)data.get("bp"));
                    savingVal.setText("$" + String.format("%,.0f", (Double)data.get("balance") * 0.20));

                    chartsPanel.removeAll();
                    chartsPanel.add(createChartWrapper("Gastos por Categoría", (JFreeChart)data.get("pie")));
                    chartsPanel.add(createChartWrapper("Evolución de Peso", (JFreeChart)data.get("line")));
                    
                    DefaultListModel<String> listModel = new DefaultListModel<>();
                    List<FinanceRecord> recent = (List<FinanceRecord>)data.get("recent");
                    for (FinanceRecord r : recent) {
                        listModel.addElement(String.format("• %s: %s $%.0f", r.getCategory(), r.getType().equals("Gasto") ? "-" : "+", r.getAmount()));
                    }
                    JList<String> list = new JList<>(listModel);
                    list.setFont(new Font("Arial", Font.PLAIN, 13));
                    activityPanel.removeAll();
                    JLabel actTitle = new JLabel("Actividad Reciente");
                    actTitle.setFont(new Font("Arial", Font.BOLD, 16));
                    activityPanel.add(actTitle, BorderLayout.NORTH);
                    activityPanel.add(new JScrollPane(list), BorderLayout.CENTER);
                    
                    revalidate();
                    repaint();
                } catch (Exception e) {}
            }
        }.execute();
    }

    private JPanel createKpiCard(String title, JLabel valueLbl, String sub, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout(15, 5));
        card.setBackground(AppColors.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER, 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Arial", Font.PLAIN, 28));
        iconLbl.setForeground(color);
        card.add(iconLbl, BorderLayout.WEST);

        JPanel text = new JPanel(new GridLayout(3, 1));
        text.setOpaque(false);
        JLabel tLbl = new JLabel(title);
        tLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        tLbl.setForeground(AppColors.TEXT_SECONDARY);
        valueLbl.setFont(new Font("Arial", Font.BOLD, 18));
        valueLbl.setForeground(AppColors.TEXT_PRIMARY);
        JLabel sLbl = new JLabel(sub);
        sLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        sLbl.setForeground(AppColors.TEXT_LIGHT);

        text.add(tLbl);
        text.add(valueLbl);
        text.add(sLbl);
        card.add(text, BorderLayout.CENTER);
        AnimationUtils.addHoverEffect(card);
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

    private JFreeChart createPieChart(List<FinanceRecord> records) {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();
        Map<String, Double> data = new HashMap<>();
        records.stream()
            .filter(r -> "Gasto".equals(r.getType()))
            .forEach(r -> data.put(r.getCategory(), data.getOrDefault(r.getCategory(), 0.0) + r.getAmount()));
        
        data.forEach(dataset::setValue);
        JFreeChart chart = ChartFactory.createRingChart("", dataset, true, true, false);
        ChartUtils.applyPremiumStyle(chart);
        return chart;
    }

    private JFreeChart createLineChart(List<HealthRecord> records) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = Math.max(0, records.size() - 10); i < records.size(); i++) {
            HealthRecord r = records.get(i);
            dataset.addValue(r.getWeight(), "Peso", r.getDate().substring(5, 10));
        }
        JFreeChart chart = ChartFactory.createLineChart("", "Fecha", "kg", dataset, PlotOrientation.VERTICAL, false, true, false);
        ChartUtils.applyPremiumStyle(chart);
        return chart;
    }
}
