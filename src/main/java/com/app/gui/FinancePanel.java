package com.app.gui;

import com.app.controllers.FinanceController;
import com.app.dao.FinanceDao;
import com.app.models.FinanceRecord;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FinancePanel extends JPanel {
    private FinanceDao financeDao;
    private FinanceController controller; // MVC Approach
    
    // UI Elements
    private JTextField montoField, fechaField;
    private JComboBox<String> categoriaCombo, cuentaCombo, metodoPagoCombo, recurrenteCombo, tipoCombo;
    private JTextArea descArea, fuenteArea;
    
    private JLabel saldoLabel, ingresosTopLabel, gastosTopLabel, ahorroLabel;
    private JLabel resIngresosLabel, resGastosLabel, resBalanceLabel;

    public FinancePanel() {
        financeDao = new FinanceDao();
        controller = new FinanceController();
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // 1. TOP KPI CARDS
        JPanel topCards = new JPanel(new GridLayout(1, 4, 15, 0));
        saldoLabel = new JLabel("...", SwingConstants.CENTER);
        ingresosTopLabel = new JLabel("...", SwingConstants.CENTER);
        gastosTopLabel = new JLabel("...", SwingConstants.CENTER);
        ahorroLabel = new JLabel("...", SwingConstants.CENTER);
        
        topCards.add(createModernCard("Saldo", saldoLabel, new Color(46, 204, 113)));
        topCards.add(createModernCard("Total Ingreso", ingresosTopLabel, new Color(52, 152, 219)));
        topCards.add(createModernCard("Total de Gastos", gastosTopLabel, new Color(231, 76, 60)));
        topCards.add(createModernCard("Ahorro", ahorroLabel, new Color(0, 206, 209)));
        
        add(topCards, BorderLayout.NORTH);

        // 2. CENTER CONTENT (Form + Right Resumen)
        JPanel centerPanel = new JPanel(new BorderLayout(20, 0));
        
        // --- 2.1 Formularies (Left Side) ---
        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));

        // TIPO SECTION
        JPanel tipoSection = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tipoSection.add(new JLabel("Tipo de Transacción:"));
        tipoCombo = new JComboBox<>(new String[]{"Gasto", "Ingreso"});
        tipoSection.add(tipoCombo);
        
        // BOTON IA OCR
        JButton btnOcr = new JButton("📷 Auto-Escanear Recibo (IA)");
        btnOcr.setBackground(new Color(241, 196, 15));
        btnOcr.setForeground(Color.BLACK);
        btnOcr.addActionListener(e -> runOcrScan());
        tipoSection.add(Box.createRigidArea(new Dimension(30, 0)));
        tipoSection.add(btnOcr);
        
        formContainer.add(tipoSection);

        // INFO BASICA
        JPanel basicInfo = new JPanel(new GridLayout(4, 2, 15, 15));
        basicInfo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Información Básica", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)));
        
        basicInfo.add(new JLabel("Monto:"));
        basicInfo.add(new JLabel("Fecha (ej: yyyy-mm-dd):"));
        montoField = new JTextField();
        fechaField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        basicInfo.add(montoField);
        basicInfo.add(fechaField);

        basicInfo.add(new JLabel("Categoría:"));
        basicInfo.add(new JLabel("Cuenta (opcional):"));
        categoriaCombo = new JComboBox<>(new String[]{"Alimentos", "Transporte", "Casa", "Ocio", "Salud", "Educación", "Salario", "Inversión"});
        cuentaCombo = new JComboBox<>(new String[]{"Efectivo", "Cuenta Bancaria", "Tarjeta de Crédito", "Ahorros"});
        basicInfo.add(categoriaCombo);
        basicInfo.add(cuentaCombo);

        basicInfo.add(new JLabel("Descripción:"));
        basicInfo.add(new JLabel("Fuente / Etiqueta (opcional):"));
        descArea = new JTextArea(2, 20);
        fuenteArea = new JTextArea(2, 20);
        basicInfo.add(new JScrollPane(descArea));
        basicInfo.add(new JScrollPane(fuenteArea));
        
        formContainer.add(basicInfo);
        formContainer.add(Box.createRigidArea(new Dimension(0, 15)));

        // INFO ADICIONAL
        JPanel addInfo = new JPanel(new GridLayout(2, 2, 15, 15));
        addInfo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Información Adicional"));
        addInfo.add(new JLabel("Método de pago:"));
        addInfo.add(new JLabel("¿Es recurrente?"));
        metodoPagoCombo = new JComboBox<>(new String[]{"Débito", "Crédito", "Transferencia", "Efectivo", "Cripto"});
        recurrenteCombo = new JComboBox<>(new String[]{"No es recurrente", "Diario", "Semanal", "Mensual", "Anual"});
        addInfo.add(metodoPagoCombo);
        addInfo.add(recurrenteCombo);

        formContainer.add(addInfo);
        centerPanel.add(formContainer, BorderLayout.CENTER);

        // --- 2.2 Resumen del mes (Right Side) ---
        JPanel rightSummary = new JPanel(new GridLayout(4, 1, 10, 10));
        rightSummary.setPreferredSize(new Dimension(250, 0));
        rightSummary.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(52, 152, 219)), "Resumen del mes"));
        rightSummary.setBackground(new Color(41, 128, 185, 30)); 
        
        resIngresosLabel = new JLabel("Ingresos: $0.00");
        resGastosLabel = new JLabel("Gastos: $0.00");
        resBalanceLabel = new JLabel("Balance: $0.00");
        
        resIngresosLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resGastosLabel.setFont(new Font("Arial", Font.BOLD, 16));
        resBalanceLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        rightSummary.add(resIngresosLabel);
        rightSummary.add(resGastosLabel);
        rightSummary.add(new JLabel("--------------------"));
        rightSummary.add(resBalanceLabel);
        
        centerPanel.add(rightSummary, BorderLayout.EAST);
        
        add(centerPanel, BorderLayout.CENTER);

        // 3. BOTTOM BUTTONS
        JPanel bottomContainer = new JPanel();
        bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.Y_AXIS));
        
        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton btnCancel = styleButton("Cancelar", new Color(231, 76, 60));
        JButton btnSaveNew = styleButton("Guardar y Nuevo", new Color(52, 152, 219));
        JButton btnSave = styleButton("Guardar Transacción", new Color(155, 89, 182)); 
        
        btnSave.addActionListener(e -> saveRecord(false));
        btnSaveNew.addActionListener(e -> saveRecord(true));
        btnCancel.addActionListener(e -> clearForm());
        
        actionBtns.add(btnCancel);
        actionBtns.add(btnSaveNew);
        actionBtns.add(btnSave);
        bottomContainer.add(actionBtns);

        // Accesos Rápidos
        JPanel quickAccess = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        quickAccess.setBorder(BorderFactory.createTitledBorder("Accesos Rápidos"));
        JButton btnHistorial = styleButton("Ver Historial Base de Datos", new Color(46, 204, 113));
        btnHistorial.addActionListener(e -> showHistoryDialog());
        quickAccess.add(btnHistorial);
        bottomContainer.add(quickAccess);
        
        add(bottomContainer, BorderLayout.SOUTH);

        refreshMetrics();
    }

    private void runOcrScan() {
        // Dispara la simulación de lectura de ticket vía MVC
        String[] extracted = controller.simulateOcrScan();
        JOptionPane.showMessageDialog(this, "Escaneando imagen...", "Procesando IA", JOptionPane.INFORMATION_MESSAGE);
        montoField.setText(extracted[0]);
        categoriaCombo.setSelectedItem(extracted[1]);
        descArea.setText(extracted[2]);
    }

    private JPanel createModernCard(String title, JLabel valueLabel, Color borderColor) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        t.setFont(new Font("Arial", Font.PLAIN, 14));
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        card.add(t, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JButton styleButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(new Font("Arial", Font.BOLD, 13));
        return b;
    }

    private void clearForm() {
        montoField.setText("");
        fechaField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        descArea.setText("");
        fuenteArea.setText("");
        categoriaCombo.setSelectedIndex(0);
        cuentaCombo.setSelectedIndex(0);
        metodoPagoCombo.setSelectedIndex(0);
        recurrenteCombo.setSelectedIndex(0);
        tipoCombo.setSelectedIndex(0);
    }

    private void saveRecord(boolean keepOpen) {
        String tipo = (String) tipoCombo.getSelectedItem();
        String am = montoField.getText();
        String date = fechaField.getText();
        String cat = (String) categoriaCombo.getSelectedItem();
        String acc = (String) cuentaCombo.getSelectedItem();
        String desc = descArea.getText();
        String tag = fuenteArea.getText();
        String method = (String) metodoPagoCombo.getSelectedItem();
        boolean isRec = !((String)recurrenteCombo.getSelectedItem()).equals("No es recurrente");

        // MVC Approach: The controller decides if the transaction raises alarms and actually persists it.
        boolean success = controller.processAndSaveTransaction(this, am, date, cat, acc, desc, tag, method, isRec, tipo);
        
        if (success) {
            refreshMetrics();
            if (keepOpen) {
                clearForm();
                JOptionPane.showMessageDialog(this, "Guardado exitosamente. Puede registrar otra.");
            } else {
                JOptionPane.showMessageDialog(this, "Transacción guardada correctamente.");
            }
        }
    }

    private void refreshMetrics() {
        List<FinanceRecord> records = financeDao.getAllRecords();
        double ing = 0, gas = 0;
        
        for (FinanceRecord r : records) {
            if ("Ingreso".equals(r.getType())) ing += r.getAmount();
            else gas += r.getAmount();
        }
        
        double bal = ing - gas;
        
        saldoLabel.setText("$" + String.format("%.2f", bal));
        ingresosTopLabel.setText("$" + String.format("%.2f", ing));
        gastosTopLabel.setText("$" + String.format("%.2f", gas));
        ahorroLabel.setText("$" + String.format("%.2f", (ing * 0.20)));
        
        resIngresosLabel.setText(String.format("Ingresos: $%.2f", ing));
        resGastosLabel.setText(String.format("Gastos: $%.2f", gas));
        resBalanceLabel.setText(String.format("Balance: $%.2f", bal));
    }
    
    private void showHistoryDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Historial de Transacciones", true);
        dialog.setSize(900, 500);
        dialog.setLocationRelativeTo(this);
        
        String[] columns = {"ID", "Fecha", "Tipo", "Monto", "Categoría", "Cuenta", "Descripción", "Recurrente"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        List<FinanceRecord> records = financeDao.getAllRecords();
        for (FinanceRecord r : records) {
            model.addRow(new Object[]{r.getId(), r.getDate(), r.getType(), r.getAmount(), r.getCategory(), r.getAccount(), r.getDescription(), r.isRecurring() ? "Sí" : "No"});
        }
        
        dialog.add(new JScrollPane(new JTable(model)));
        dialog.setVisible(true);
    }
}
