package com.app.utils;

import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.Component;

public class ExportEngine {

    /**
     * Exports any list of data to a CSV file selected by the user.
     * @param parent The parent component for dialogs
     * @param headers Column headers
     * @param rows List of object arrays representing rows
     * @param fileNamePrefix Default file name prefix
     */
    public static void exportToCSV(Component parent, String[] headers, List<Object[]> rows, String fileNamePrefix) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte CSV");
        fileChooser.setSelectedFile(new File(fileNamePrefix + "_" + System.currentTimeMillis() + ".csv"));

        if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(file)) {
                // Write Headers
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < headers.length; i++) {
                    sb.append(headers[i]);
                    if (i < headers.length - 1) sb.append(",");
                }
                writer.println(sb.toString());

                // Write Rows
                for (Object[] row : rows) {
                    sb = new StringBuilder();
                    for (int i = 0; i < row.length; i++) {
                        String val = row[i] == null ? "" : row[i].toString().replace(",", ";");
                        sb.append(val);
                        if (i < row.length - 1) sb.append(",");
                    }
                    writer.println(sb.toString());
                }

                JOptionPane.showMessageDialog(parent, "Reporte exportado con éxito a:\n" + file.getAbsolutePath(), 
                    "Exportación Exitosa", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(parent, "Error al guardar el archivo: " + e.getMessage(), 
                    "Error de Exportación", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
