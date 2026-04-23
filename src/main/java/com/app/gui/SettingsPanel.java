package com.app.gui;

import com.app.db.DatabaseManager;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.Statement;

public class SettingsPanel extends JPanel {

    public SettingsPanel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Configuración del Sistema");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnDrop = new JButton("Borrar TODOS los Datos (Factory Reset)");
        btnDrop.setBackground(new Color(231, 76, 60)); // Red
        btnDrop.setForeground(Color.WHITE);
        btnDrop.addActionListener(e -> factoryReset());

        centerPanel.add(btnDrop);
        
        add(centerPanel, BorderLayout.CENTER);
    }

    private void factoryReset() {
        int resp = JOptionPane.showConfirmDialog(this, "¿Esta seguro? Perdera toda la informaion.", "Advertencia", JOptionPane.YES_NO_OPTION);
        if (resp == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement()) {
                stmt.execute("DELETE FROM finance_records");
                stmt.execute("DELETE FROM health_records");
                stmt.execute("DELETE FROM savings_goals");
                stmt.execute("DELETE FROM budgets");
                JOptionPane.showMessageDialog(this, "Base de datos restaurada.");
            } catch (Exception ex) {}
        }
    }
}
