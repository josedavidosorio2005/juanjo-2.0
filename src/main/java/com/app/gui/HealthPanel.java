package com.app.gui;

import com.app.dao.HealthDao;
import com.app.models.HealthRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HealthPanel extends JPanel {
    private HealthDao healthDao;
    private JTable table;
    private DefaultTableModel tableModel;

    private JTextField weightField;
    private JTextField bpField;
    private JTextField notesField;

    public HealthPanel() {
        healthDao = new HealthDao();
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- Form Panel ---
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));

        formPanel.add(new JLabel("Peso (kg):"));
        weightField = new JTextField();
        formPanel.add(weightField);

        formPanel.add(new JLabel("Presión Arterial (ej. 120/80):"));
        bpField = new JTextField();
        formPanel.add(bpField);

        formPanel.add(new JLabel("Notas:"));
        notesField = new JTextField();
        formPanel.add(notesField);

        // Buttons Panel
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        JButton saveButton = new JButton("Guardar Registro");
        saveButton.setBackground(new Color(41, 128, 185));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveRecord());
        
        JButton exportButton = new JButton("Exportar CSV");
        exportButton.setBackground(new Color(39, 174, 96));
        exportButton.setForeground(Color.WHITE);
        exportButton.addActionListener(e -> exportToCSV());

        btnPanel.add(saveButton);
        btnPanel.add(exportButton);

        formPanel.add(new JLabel()); // Empty
        formPanel.add(btnPanel);

        add(formPanel, BorderLayout.NORTH);

        // --- Table Panel ---
        String[] columns = {"ID", "Fecha", "Peso (kg)", "Presión Arterial", "Notas"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        loadTableData();
    }

    private void saveRecord() {
        try {
            double weight = Double.parseDouble(weightField.getText());
            String bp = bpField.getText();
            String notes = notesField.getText();
            String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

            HealthRecord record = new HealthRecord(0, date, weight, bp, notes);
            healthDao.insertRecord(record);

            weightField.setText("");
            bpField.setText("");
            notesField.setText("");

            loadTableData();
            JOptionPane.showMessageDialog(this, "Registro guardado correctamente.");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese un peso válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadTableData() {
        tableModel.setRowCount(0);
        List<HealthRecord> records = healthDao.getAllRecords();
        for (HealthRecord record : records) {
            Object[] row = {
                    record.getId(),
                    record.getDate(),
                    record.getWeight(),
                    record.getBloodPressure(),
                    record.getNotes()
            };
            tableModel.addRow(row);
        }
    }

    private void exportToCSV() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar como CSV");
        fileChooser.setSelectedFile(new File("salud.csv"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(fileToSave, "UTF-8")) {
                pw.println("ID,Fecha,Peso (kg),Presión Arterial,Notas");
                List<HealthRecord> records = healthDao.getAllRecords();
                for (HealthRecord r : records) {
                    pw.printf("%d,%s,%.2f,%s,%s\n", 
                            r.getId(), r.getDate(), r.getWeight(), 
                            r.getBloodPressure(), r.getNotes());
                }
                JOptionPane.showMessageDialog(this, "Exportado correctamente a: " + fileToSave.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
