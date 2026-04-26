package com.app.gui;

import com.app.dao.HealthDao;
import com.app.models.HealthRecord;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import com.app.utils.AppColors;

public class HealthPanel extends JPanel {
    private HealthDao healthDao;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField weightField, bpField, notesField;
    private JLabel lastWeightVal, lastBpVal, avgWeightVal, recordsCountVal;

    public HealthPanel() {
        healthDao = new HealthDao();
        setLayout(new BorderLayout(25, 25));
        setBackground(AppColors.BG_MAIN);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // 1. Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Salud Integral");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(AppColors.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);
        
        JButton btnExport = new JButton("📥 Exportar Reporte CSV");
        btnExport.addActionListener(e -> exportToCSV());
        header.add(btnExport, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // 2. KPI Row
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 20, 0));
        kpiRow.setOpaque(false);
        
        lastWeightVal = new JLabel("0.0 kg");
        lastBpVal = new JLabel("N/A");
        avgWeightVal = new JLabel("0.0 kg");
        recordsCountVal = new JLabel("0");

        kpiRow.add(createKpiCard("Último Peso", lastWeightVal, AppColors.PRIMARY, "⚖️"));
        kpiRow.add(createKpiCard("Presión Arterial", lastBpVal, AppColors.DANGER, "❤️"));
        kpiRow.add(createKpiCard("Peso Promedio", avgWeightVal, AppColors.SUCCESS, "📊"));
        kpiRow.add(createKpiCard("Total Registros", recordsCountVal, AppColors.ACCENT, "📝"));
        
        // 3. Body: Form (Left) and History (Center/Right)
        JPanel mainBody = new JPanel(new BorderLayout(25, 0));
        mainBody.setOpaque(false);

        // 3.1 Form
        JPanel formWrapper = createStyledPanel("Nuevo Registro");
        formWrapper.setPreferredSize(new Dimension(350, 0));
        JPanel form = new JPanel(new GridLayout(4, 1, 10, 10));
        form.setOpaque(false);
        
        form.add(new JLabel("Peso Actual (kg):"));
        weightField = new JTextField();
        form.add(weightField);
        
        form.add(new JLabel("Presión Arterial (ej. 120/80):"));
        bpField = new JTextField();
        form.add(bpField);
        
        form.add(new JLabel("Notas Adicionales:"));
        notesField = new JTextField();
        form.add(notesField);

        JButton btnSave = new JButton("💾 Guardar Registro");
        btnSave.setBackground(AppColors.PRIMARY);
        btnSave.setForeground(AppColors.SURFACE);
        btnSave.setFont(new Font("Arial", Font.BOLD, 14));
        btnSave.addActionListener(e -> saveRecord());
        
        formWrapper.add(form, BorderLayout.CENTER);
        formWrapper.add(btnSave, BorderLayout.SOUTH);
        
        mainBody.add(formWrapper, BorderLayout.WEST);

        // 3.2 History Table
        JPanel tableWrapper = createStyledPanel("Historial de Salud");
        String[] columns = {"Fecha", "Peso (kg)", "Presión", "Notas"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setRowHeight(35);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setBackground(AppColors.SURFACE);
        table.getTableHeader().setForeground(AppColors.TEXT_PRIMARY);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        tableWrapper.add(new JScrollPane(table), BorderLayout.CENTER);
        mainBody.add(tableWrapper, BorderLayout.CENTER);

        JPanel content = new JPanel(new BorderLayout(0, 25));
        content.setOpaque(false);
        content.add(kpiRow, BorderLayout.NORTH);
        content.add(mainBody, BorderLayout.CENTER);
        
        add(content, BorderLayout.CENTER);

        loadTableData();
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

    private void saveRecord() {
        try {
            double weight = Double.parseDouble(weightField.getText());
            String bp = bpField.getText();
            String notes = notesField.getText();
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            healthDao.insertRecord(new HealthRecord(0, date, weight, bp, notes));
            weightField.setText("");
            bpField.setText("");
            notesField.setText("");
            loadTableData();
            JOptionPane.showMessageDialog(this, "Registro guardado.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Revisa los datos ingresados.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        List<HealthRecord> records = healthDao.getAllRecords();
        double sumWeight = 0;
        for (HealthRecord r : records) {
            tableModel.addRow(new Object[]{r.getDate(), r.getWeight(), r.getBloodPressure(), r.getNotes()});
            sumWeight += r.getWeight();
        }
        
        if (!records.isEmpty()) {
            lastWeightVal.setText(records.get(0).getWeight() + " kg");
            lastBpVal.setText(records.get(0).getBloodPressure());
            avgWeightVal.setText(String.format("%.1f kg", sumWeight / records.size()));
            recordsCountVal.setText(String.valueOf(records.size()));
        }
    }

    private void exportToCSV() {
        JFileChooser fc = new JFileChooser();
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter pw = new PrintWriter(fc.getSelectedFile())) {
                pw.println("Fecha,Peso,Presion,Notas");
                healthDao.getAllRecords().forEach(r -> pw.printf("%s,%.2f,%s,%s\n", r.getDate(), r.getWeight(), r.getBloodPressure(), r.getNotes()));
                JOptionPane.showMessageDialog(this, "Exportado correctamente.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al exportar.");
            }
        }
    }
}
