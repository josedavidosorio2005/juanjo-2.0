package com.app.gui;

import com.app.dao.FinanceDao;
import com.app.models.FinanceRecord;
import com.app.utils.AppColors;
import com.app.utils.ChartUtils;
import com.app.utils.ExportEngine;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ReportsPanel extends JPanel {
    private FinanceDao financeDao = new FinanceDao();
    private JPanel chartsContainer;

    public ReportsPanel() {
        setLayout(new BorderLayout(25, 25));
        setBackground(AppColors.BG_MAIN);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Análisis Estadístico Avanzado");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(AppColors.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);

        JButton btnRefresh = new JButton("🔄 Actualizar Reportes");
        btnRefresh.addActionListener(e -> loadReportsAsync());
        header.add(btnRefresh, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Charts Container
        chartsContainer = new JPanel(new GridLayout(1, 2, 25, 25));
        chartsContainer.setOpaque(false);
        add(new JScrollPane(chartsContainer), BorderLayout.CENTER);

        // Footer
        JButton btnExport = new JButton("📥 Exportar Informe de Auditoría Completo (CSV)");
        btnExport.setBackground(AppColors.PRIMARY);
        btnExport.setForeground(Color.WHITE);
        btnExport.setFont(new Font("Arial", Font.BOLD, 14));
        btnExport.addActionListener(e -> exportFullReport());
        add(btnExport, BorderLayout.SOUTH);

        loadReportsAsync();
    }

    private void loadReportsAsync() {
        new SwingWorker<JFreeChart[], Void>() {
            @Override
            protected JFreeChart[] doInBackground() {
                List<FinanceRecord> records = financeDao.getAllRecords();
                
                // 1. Bar Chart: Income vs Expenses
                DefaultCategoryDataset barDs = new DefaultCategoryDataset();
                Map<String, Double> incomeMap = new TreeMap<>();
                Map<String, Double> expenseMap = new TreeMap<>();
                for (FinanceRecord r : records) {
                    String month = (r.getDate() != null && r.getDate().length() >= 7) ? r.getDate().substring(0, 7) : "N/A";
                    if ("Ingreso".equals(r.getType())) incomeMap.put(month, incomeMap.getOrDefault(month, 0.0) + r.getAmount());
                    else expenseMap.put(month, expenseMap.getOrDefault(month, 0.0) + r.getAmount());
                }
                incomeMap.forEach((m, v) -> barDs.addValue(v, "Ingresos", m));
                expenseMap.forEach((m, v) -> barDs.addValue(v, "Gastos", m));
                JFreeChart barChart = ChartFactory.createBarChart("Comparativa Mensual", "Mes", "Monto ($)", barDs, PlotOrientation.VERTICAL, true, true, false);
                ChartUtils.applyPremiumStyle(barChart);

                // 2. Pie Chart: Expenses by Category
                DefaultPieDataset<String> pieDs = new DefaultPieDataset<>();
                Map<String, Double> catMap = new TreeMap<>();
                records.stream().filter(r -> "Gasto".equals(r.getType()))
                        .forEach(r -> catMap.put(r.getCategory(), catMap.getOrDefault(r.getCategory(), 0.0) + r.getAmount()));
                catMap.forEach(pieDs::setValue);
                JFreeChart pieChart = ChartFactory.createPieChart("Distribución de Gastos", pieDs, true, true, false);
                ChartUtils.applyPremiumStyle(pieChart);

                return new JFreeChart[]{barChart, pieChart};
            }

            @Override
            protected void done() {
                try {
                    JFreeChart[] charts = get();
                    chartsContainer.removeAll();
                    for (JFreeChart chart : charts) {
                        ChartPanel cp = new ChartPanel(chart);
                        cp.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
                        chartsContainer.add(cp);
                    }
                    chartsContainer.revalidate();
                    chartsContainer.repaint();
                } catch (Exception e) {}
            }
        }.execute();
    }

    private void exportFullReport() {
        List<FinanceRecord> records = financeDao.getAllRecords();
        String[] headers = {"ID", "Fecha", "Tipo", "Monto", "Categoría", "Descripción", "Cuenta"};
        java.util.List<Object[]> rows = new java.util.ArrayList<>();
        for (FinanceRecord r : records) {
            rows.add(new Object[]{r.getId(), r.getDate(), r.getType(), r.getAmount(), r.getCategory(), r.getDescription(), r.getAccount()});
        }
        ExportEngine.exportToCSV(this, headers, rows, "Reporte_Global");
    }
}
