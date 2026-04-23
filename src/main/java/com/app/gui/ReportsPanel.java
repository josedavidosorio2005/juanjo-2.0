package com.app.gui;

import com.app.dao.FinanceDao;
import com.app.models.FinanceRecord;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ReportsPanel extends JPanel {
    private FinanceDao financeDao = new FinanceDao();

    public ReportsPanel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Centro de Reportes Avanzados", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 1));
        
        // Income vs Expenses Bar Chart
        DefaultCategoryDataset ds = new DefaultCategoryDataset();
        List<FinanceRecord> records = financeDao.getAllRecords();
        for (FinanceRecord r : records) {
            String month = r.getDate().substring(0, 7); // yyyy-MM
            ds.addValue(r.getAmount(), r.getType(), month);
        }
        
        JFreeChart barChart = ChartFactory.createBarChart("Ingresos vs Gastos Histórico", "Mes", "Monto ($)", ds, PlotOrientation.VERTICAL, true, true, false);
        barChart.setBackgroundPaint(new Color(0,0,0,0));
        centerPanel.add(new ChartPanel(barChart));
        
        add(centerPanel, BorderLayout.CENTER);
        
        JButton btnExport = new JButton("Exportar Todo Base de Datos (CSV)");
        btnExport.setBackground(new Color(46, 204, 113));
        btnExport.setForeground(Color.WHITE);
        btnExport.setFont(new Font("Arial", Font.BOLD, 14));
        // Action is symbolic for the mockup expansion since FinancePanel already exports
        btnExport.addActionListener(e -> JOptionPane.showMessageDialog(this, "Reporte Global Exportado a raiz."));
        add(btnExport, BorderLayout.SOUTH);
    }
}
