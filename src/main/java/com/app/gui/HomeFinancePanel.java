package com.app.gui;

import com.app.dao.FinanceDao;
import com.app.models.FinanceRecord;
import com.app.utils.AppColors;
import com.app.utils.AnimationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class HomeFinancePanel extends JPanel {
    private FinanceDao financeDao = new FinanceDao();
    private DefaultTableModel tableModel;
    private JLabel totalHomeVal;

    public HomeFinancePanel() {
        setLayout(new BorderLayout(25, 25));
        setBackground(AppColors.BG_MAIN);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Finanzas del Hogar");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(AppColors.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);
        
        totalHomeVal = new JLabel("$0");
        totalHomeVal.setFont(new Font("Arial", Font.BOLD, 24));
        totalHomeVal.setForeground(AppColors.DANGER);
        header.add(totalHomeVal, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Content
        JPanel mainContent = new JPanel(new BorderLayout(25, 0));
        mainContent.setOpaque(false);

        // History Table
        String[] columns = {"Fecha", "Monto", "Categoría", "Cuenta", "Descripción"};
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        table.setRowHeight(40);
        table.setShowGrid(false);
        
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setBackground(AppColors.SURFACE);
        tableWrapper.setBorder(BorderFactory.createLineBorder(AppColors.BORDER));
        tableWrapper.add(new JScrollPane(table), BorderLayout.CENTER);
        
        mainContent.add(tableWrapper, BorderLayout.CENTER);

        // Quick Actions Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(300, 0));
        sidebar.setBackground(AppColors.SURFACE);
        sidebar.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        sidebar.add(new JLabel("🏠 Gastos Rápidos del Hogar"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));
        
        sidebar.add(createQuickBtn("🍎 Mercado / Alimentos", "Alimentos"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createQuickBtn("💡 Servicios (Luz, Agua, Gas)", "Casa"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createQuickBtn("🏢 Alquiler / Hipoteca", "Casa"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebar.add(createQuickBtn("🛠️ Mantenimiento", "Casa"));
        
        mainContent.add(sidebar, BorderLayout.EAST);
        add(mainContent, BorderLayout.CENTER);

        refreshData();
    }

    private JButton createQuickBtn(String text, String category) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(260, 45));
        btn.addActionListener(e -> {
            String amountStr = JOptionPane.showInputDialog(this, "Monto para " + text + ":");
            try {
                double amount = Double.parseDouble(amountStr);
                financeDao.insertRecord(new FinanceRecord(0, 
                    new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()), 
                    "Gasto", amount, category, "Gasto rápido de hogar: " + text, "Banco Principal", "Hogar", "Efectivo", false));
                refreshData();
            } catch (Exception ignore) {}
        });
        return btn;
    }

    private void refreshData() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                List<FinanceRecord> records = financeDao.getAllRecords();
                SwingUtilities.invokeLater(() -> {
                    tableModel.setRowCount(0);
                    double total = 0;
                    for (FinanceRecord r : records) {
                        if ("Casa".equalsIgnoreCase(r.getCategory()) || "Alimentos".equalsIgnoreCase(r.getCategory())) {
                            tableModel.addRow(new Object[]{r.getDate(), "$" + String.format("%,.0f", r.getAmount()), r.getCategory(), r.getAccount(), r.getDescription()});
                            if ("Gasto".equals(r.getType())) total += r.getAmount();
                        }
                    }
                    totalHomeVal.setText("$" + String.format("%,.0f", total));
                });
                return null;
            }
        }.execute();
    }
}
