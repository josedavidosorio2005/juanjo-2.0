package com.app.gui;

import com.app.db.DatabaseManager;
import com.app.utils.AppColors;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.Statement;

public class SettingsPanel extends JPanel {

    public SettingsPanel() {
        setLayout(new BorderLayout(25, 25));
        setBackground(AppColors.BG_MAIN);
        setBorder(new EmptyBorder(30, 30, 30, 30));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Configuración y Seguridad");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        title.setForeground(AppColors.TEXT_PRIMARY);
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // Center Content
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setOpaque(false);

        container.add(createSettingsSection("Perfil de Usuario", "Gestiona tu cuenta y seguridad."));
        container.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JButton btnLogout = new JButton("🚪 Cerrar Sesión");
        btnLogout.addActionListener(e -> {
            Window win = SwingUtilities.getWindowAncestor(this);
            if (win != null) win.dispose();
            new LoginFrame().setVisible(true);
        });
        container.add(btnLogout);
        
        container.add(Box.createRigidArea(new Dimension(0, 40)));
        container.add(createSettingsSection("Mantenimiento", "Acciones críticas del sistema."));
        container.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton btnDrop = new JButton("⚠️ Restaurar de Fábrica (Borrar Todo)");
        btnDrop.setBackground(AppColors.DANGER);
        btnDrop.setForeground(Color.WHITE);
        btnDrop.setFont(new Font("Arial", Font.BOLD, 14));
        btnDrop.addActionListener(e -> factoryReset());
        container.add(btnDrop);

        add(new JScrollPane(container), BorderLayout.CENTER);

        // Footer Version
        JLabel version = new JLabel("Salud y Finanzas Pro v1.0.0 - Build 2024.04.26");
        version.setFont(new Font("Arial", Font.PLAIN, 12));
        version.setForeground(AppColors.TEXT_LIGHT);
        add(version, BorderLayout.SOUTH);
    }

    private JPanel createSettingsSection(String title, String desc) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(800, 60));
        
        JLabel t = new JLabel(title);
        t.setFont(new Font("Arial", Font.BOLD, 18));
        t.setForeground(AppColors.TEXT_PRIMARY);
        
        JLabel d = new JLabel(desc);
        d.setFont(new Font("Arial", Font.PLAIN, 13));
        d.setForeground(AppColors.TEXT_SECONDARY);
        
        p.add(t, BorderLayout.NORTH);
        p.add(d, BorderLayout.CENTER);
        return p;
    }

    private void factoryReset() {
        int resp = JOptionPane.showConfirmDialog(this, 
            "¿Está completamente seguro?\nEsta acción es irreversible y borrará TODOS sus datos.", 
            "Confirmación de Reseteo", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (resp == JOptionPane.YES_OPTION) {
            try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement()) {
                stmt.execute("DELETE FROM finance_records");
                stmt.execute("DELETE FROM health_records");
                stmt.execute("DELETE FROM savings_goals");
                stmt.execute("DELETE FROM budgets");
                JOptionPane.showMessageDialog(this, "El sistema ha sido restaurado a su estado original.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error durante el reseteo: " + ex.getMessage());
            }
        }
    }
}
