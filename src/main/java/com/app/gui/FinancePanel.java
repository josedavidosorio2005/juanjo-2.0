package com.app.gui;

import com.app.controllers.FinanceController;
import com.app.dao.FinanceDao;
import com.app.models.FinanceRecord;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.app.utils.AppColors;
import com.app.utils.ExportEngine;

public class FinancePanel extends JPanel {
    private FinanceDao financeDao;
    private FinanceController controller;
    
    private JTextField montoField, fechaField;
    private JComboBox<String> categoriaCombo, cuentaCombo, metodoPagoCombo, recurrenteCombo, tipoCombo;
    private JTextArea descArea;
    
    private JLabel saldoVal, ingresosVal, gastosVal, ahorroVal;

    public FinancePanel() {
        financeDao = new FinanceDao();
        controller = new FinanceController();
        setLayout(new BorderLayout(25, 25));
        setBackground(AppColors.BG_MAIN);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // 1. Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Gestión de Finanzas");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(AppColors.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);
        
        JPanel headerActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerActions.setOpaque(false);
        
        JButton btnExport = new JButton("📥 Exportar CSV");
        btnExport.setBackground(new Color(46, 204, 113));
        btnExport.setForeground(Color.WHITE);
        btnExport.addActionListener(e -> exportData());
        headerActions.add(btnExport);

        JButton btnHistory = new JButton("📅 Ver Historial");
        btnHistory.addActionListener(e -> showHistoryDialog());
        headerActions.add(btnHistory);
        
        header.add(headerActions, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // 2. KPI Row
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 20, 0));
        kpiRow.setOpaque(false);
        
        saldoVal = new JLabel("$0");
        ingresosVal = new JLabel("$0");
        gastosVal = new JLabel("$0");
        ahorroVal = new JLabel("$0");

        kpiRow.add(createKpiCard("Saldo Disponible", saldoVal, AppColors.SUCCESS, "💰"));
        kpiRow.add(createKpiCard("Ingresos Totales", ingresosVal, AppColors.PRIMARY, "📥"));
        kpiRow.add(createKpiCard("Gastos Totales", gastosVal, AppColors.DANGER, "📤"));
        kpiRow.add(createKpiCard("Ahorro (20%)", ahorroVal, AppColors.ACCENT, "🏦"));
        
        // 3. Central Body (Form + Summary)
        JPanel mainBody = new JPanel(new BorderLayout(25, 0));
        mainBody.setOpaque(false);

        // 3.1 Form Side
        JPanel formWrapper = createStyledPanel("Registrar Nueva Transacción");
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.weightx = 1.0;

        // Row 1: Tipo & Monto
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1;
        tipoCombo = new JComboBox<>(new String[]{"Gasto", "Ingreso"});
        form.add(tipoCombo, gbc);
        
        gbc.gridx = 2;
        form.add(new JLabel("Monto ($):"), gbc);
        gbc.gridx = 3;
        montoField = new JTextField();
        form.add(montoField, gbc);

        // Row 2: Fecha & Categoría
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Fecha:"), gbc);
        gbc.gridx = 1;
        fechaField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        form.add(fechaField, gbc);
        
        gbc.gridx = 2;
        form.add(new JLabel("Categoría:"), gbc);
        gbc.gridx = 3;
        categoriaCombo = new JComboBox<>(new String[]{"Alimentos", "Transporte", "Casa", "Ocio", "Salud", "Educación", "Salario", "Freelance"});
        form.add(categoriaCombo, gbc);

        // Row 3: Cuenta & Método
        gbc.gridx = 0; gbc.gridy = 2;
        form.add(new JLabel("Cuenta:"), gbc);
        gbc.gridx = 1;
        cuentaCombo = new JComboBox<>(new String[]{"Banco Principal", "Efectivo", "Ahorros", "Tarjeta Crédito"});
        form.add(cuentaCombo, gbc);
        
        gbc.gridx = 2;
        form.add(new JLabel("Método:"), gbc);
        gbc.gridx = 3;
        metodoPagoCombo = new JComboBox<>(new String[]{"Transferencia", "Efectivo", "Tarjeta", "Débito"});
        form.add(metodoPagoCombo, gbc);

        // Row 4: Descripción (Full Width)
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        form.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        descArea = new JTextArea(2, 20);
        form.add(new JScrollPane(descArea), gbc);

        // Row 5: Recurrente & OCR
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1;
        form.add(new JLabel("Recurrente:"), gbc);
        gbc.gridx = 1;
        recurrenteCombo = new JComboBox<>(new String[]{"No", "Mensual", "Semanal"});
        form.add(recurrenteCombo, gbc);
        
        gbc.gridx = 2; gbc.gridwidth = 2;
        JButton btnOcr = new JButton("📸 Escanear Ticket con IA");
        btnOcr.setBackground(new Color(241, 196, 15));
        btnOcr.addActionListener(e -> runOcrScan());
        form.add(btnOcr, gbc);

        formWrapper.add(form, BorderLayout.CENTER);
        
        // Buttons Footer
        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionBtns.setOpaque(false);
        JButton btnClear = new JButton("Limpiar");
        btnClear.addActionListener(e -> clearForm());
        JButton btnSave = new JButton("💾 Guardar Transacción");
        btnSave.setBackground(AppColors.PRIMARY);
        btnSave.setForeground(AppColors.SURFACE);
        btnSave.setFont(new Font("Arial", Font.BOLD, 14));
        btnSave.addActionListener(e -> saveRecord(false));
        
        actionBtns.add(btnClear);
        actionBtns.add(btnSave);
        formWrapper.add(actionBtns, BorderLayout.SOUTH);

        mainBody.add(formWrapper, BorderLayout.CENTER);

        // 3.2 Right Summary Side
        JPanel summarySide = createStyledPanel("Consejos Inteligentes");
        summarySide.setPreferredSize(new Dimension(300, 0));
        JTextPane tips = new JTextPane();
        tips.setEditable(false);
        tips.setText("\n💡 Tip del día:\nHas gastado un 15% más en 'Comida' que el mes pasado.\n\n🎯 Meta:\nSi ahorras $200k más este mes, alcanzarás tu meta 'Viaje a Japón' en 3 meses.");
        tips.setFont(new Font("Arial", Font.PLAIN, 14));
        summarySide.add(tips, BorderLayout.CENTER);
        
        mainBody.add(summarySide, BorderLayout.EAST);

        // Assemble Top Content
        JPanel topContent = new JPanel(new BorderLayout(0, 25));
        topContent.setOpaque(false);
        topContent.add(kpiRow, BorderLayout.NORTH);
        topContent.add(mainBody, BorderLayout.CENTER);
        
        add(topContent, BorderLayout.CENTER);

        refreshMetrics();
    }

    private JPanel createKpiCard(String title, JLabel valueLbl, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(AppColors.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER, 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel iconLbl = new JLabel(icon);
        iconLbl.setFont(new Font("Arial", Font.PLAIN, 24));
        iconLbl.setOpaque(true);
        iconLbl.setBackground(AppColors.withAlpha(color, 30));
        iconLbl.setForeground(color);
        iconLbl.setPreferredSize(new Dimension(50, 50));
        iconLbl.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(iconLbl, BorderLayout.WEST);

        JPanel text = new JPanel(new GridLayout(2, 1));
        text.setOpaque(false);
        JLabel tLbl = new JLabel(title);
        tLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        tLbl.setForeground(AppColors.TEXT_SECONDARY);
        valueLbl.setFont(new Font("Arial", Font.BOLD, 18));
        valueLbl.setForeground(AppColors.TEXT_PRIMARY);
        text.add(tLbl);
        text.add(valueLbl);
        card.add(text, BorderLayout.CENTER);
        return card;
    }

    private JPanel createStyledPanel(String title) {
        JPanel p = new JPanel(new BorderLayout(0, 15));
        p.setBackground(AppColors.SURFACE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER, 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
        JLabel t = new JLabel(title);
        t.setFont(new Font("Arial", Font.BOLD, 18));
        t.setForeground(AppColors.TEXT_PRIMARY);
        p.add(t, BorderLayout.NORTH);
        return p;
    }

    private void runOcrScan() {
        String[] extracted = controller.simulateOcrScan();
        montoField.setText(extracted[0]);
        categoriaCombo.setSelectedItem(extracted[1]);
        descArea.setText(extracted[2]);
    }

    private void clearForm() {
        montoField.setText("");
        fechaField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        descArea.setText("");
        categoriaCombo.setSelectedIndex(0);
    }

    private void saveRecord(boolean keepOpen) {
        String am = montoField.getText();
        String date = fechaField.getText();
        String cat = (String) categoriaCombo.getSelectedItem();
        String acc = (String) cuentaCombo.getSelectedItem();
        String desc = descArea.getText();
        String method = (String) metodoPagoCombo.getSelectedItem();
        boolean isRec = !((String)recurrenteCombo.getSelectedItem()).equals("No");
        String tipo = (String) tipoCombo.getSelectedItem();

        if (controller.processAndSaveTransaction(this, am, date, cat, acc, desc, "", method, isRec, tipo)) {
            refreshMetrics();
            clearForm();
        }
    }

    private void exportData() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                List<FinanceRecord> records = financeDao.getAllRecords();
                String[] headers = {"Fecha", "Tipo", "Monto", "Categoria", "Descripcion", "Cuenta", "Metodo Pago"};
                java.util.List<Object[]> rows = new java.util.ArrayList<>();
                for (FinanceRecord r : records) {
                    rows.add(new Object[]{r.getDate(), r.getType(), r.getAmount(), r.getCategory(), r.getDescription(), r.getAccount(), r.getPaymentMethod()});
                }
                ExportEngine.exportToCSV(FinancePanel.this, headers, rows, "Finanzas");
                return null;
            }
        }.execute();
    }

    private void refreshMetrics() {
        new SwingWorker<Double[], Void>() {
            @Override
            protected Double[] doInBackground() {
                List<FinanceRecord> records = financeDao.getAllRecords();
                double ing = 0, gas = 0;
                for (FinanceRecord r : records) {
                    if ("Ingreso".equals(r.getType())) ing += r.getAmount();
                    else gas += r.getAmount();
                }
                return new Double[]{ing, gas};
            }

            @Override
            protected void done() {
                try {
                    Double[] res = get();
                    double ing = res[0];
                    double gas = res[1];
                    double bal = ing - gas;
                    saldoVal.setText("$" + String.format("%,.0f", bal));
                    ingresosVal.setText("$" + String.format("%,.0f", ing));
                    gastosVal.setText("$" + String.format("%,.0f", gas));
                    ahorroVal.setText("$" + String.format("%,.0f", ing * 0.20));
                } catch (Exception e) {}
            }
        }.execute();
    }
    
    private void showHistoryDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Historial", true);
        dialog.setSize(800, 500);
        dialog.setLocationRelativeTo(this);
        String[] cols = {"Fecha", "Tipo", "Monto", "Categoría", "Descripción"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        financeDao.getAllRecords().forEach(r -> model.addRow(new Object[]{r.getDate(), r.getType(), r.getAmount(), r.getCategory(), r.getDescription()}));
        dialog.add(new JScrollPane(new JTable(model)));
        dialog.setVisible(true);
    }
}
