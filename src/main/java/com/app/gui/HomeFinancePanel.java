package com.app.gui;

import com.app.dao.FinanceDao;
import com.app.models.FinanceRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HomeFinancePanel extends JPanel {
    private FinanceDao financeDao = new FinanceDao();

    public HomeFinancePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Finanzas del Hogar (Vista Filtrada)");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        add(title, BorderLayout.NORTH);

        String[] columns = {"Fecha", "Monto", "Categoría", "Cuenta", "Descripción"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        List<FinanceRecord> records = financeDao.getAllRecords();
        double totalCasa = 0;
        
        for (FinanceRecord r : records) {
            // Filtrar solo categoría Casa o Servicios o Alquiler (si existiera)
            if ("Casa".equalsIgnoreCase(r.getCategory()) || "Servicios".equalsIgnoreCase(r.getCategory())) {
                model.addRow(new Object[]{r.getDate(), "$" + r.getAmount(), r.getCategory(), r.getAccount(), r.getDescription()});
                if ("Gasto".equals(r.getType())) totalCasa += r.getAmount();
            }
        }

        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JLabel sumLabel = new JLabel("Gasto Total del Hogar Registrado: $" + String.format("%.2f", totalCasa));
        sumLabel.setFont(new Font("Arial", Font.BOLD, 16));
        sumLabel.setForeground(new Color(231, 76, 60));
        add(sumLabel, BorderLayout.SOUTH);
    }
}
