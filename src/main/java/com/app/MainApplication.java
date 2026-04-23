package com.app;

import com.app.db.DatabaseManager;
import com.app.gui.*;
import com.app.services.CronJobDaemon;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

public class MainApplication {

    private static CardLayout cardLayout = new CardLayout();
    private static JPanel cardsContainer = new JPanel(cardLayout);
    private static JFrame frame;

    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } catch (Exception e) {}

            // Starts with Security First
            new LoginFrame().setVisible(true);
        });
    }

    // Called only AFTER successful login
    public static void launchDashboard() {
        // Start background intelligent automations
        CronJobDaemon.runStartupChecks();

        frame = new JFrame("Salud y Finanzas Pro - Enterprise Edition");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(34, 47, 62)); 
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(30, 15, 30, 15));

        JLabel titleLabel = new JLabel("Menú Principal");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(titleLabel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        sidebar.add(createSidebarButton(" Inicio", "dashboard"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(createSidebarButton(" Gastos", "finances"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(createSidebarButton(" Presupuesto", "budget"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(createSidebarButton(" Metas", "goals"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(createSidebarButton(" Finanzas del Hogar", "home_finances"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(createSidebarButton(" Reportes", "reports"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
        sidebar.add(createSidebarButton(" Configuración", "settings"));
        
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));
        sidebar.add(createSidebarButton(" + Salud Integral", "health"));
        sidebar.add(Box.createVerticalGlue()); 
        
        JButton themeBtn = new JButton("Modo Oscuro / Claro");
        themeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        themeBtn.setMaximumSize(new Dimension(200, 40));
        themeBtn.addActionListener(e -> {
            try {
                if (UIManager.getLookAndFeel() instanceof FlatDarkLaf) UIManager.setLookAndFeel(new FlatLightLaf());
                else UIManager.setLookAndFeel(new FlatDarkLaf());
                SwingUtilities.updateComponentTreeUI(frame);
            } catch (Exception ex) {}
        });
        sidebar.add(themeBtn);

        cardsContainer.add(new DashboardPanel(), "dashboard");
        cardsContainer.add(new FinancePanel(), "finances");
        cardsContainer.add(new BudgetPanel(), "budget");
        cardsContainer.add(new GoalPanel(), "goals");
        cardsContainer.add(new HomeFinancePanel(), "home_finances");
        cardsContainer.add(new ReportsPanel(), "reports");
        cardsContainer.add(new SettingsPanel(), "settings");
        cardsContainer.add(new HealthPanel(), "health");

        frame.add(sidebar, BorderLayout.WEST);
        frame.add(cardsContainer, BorderLayout.CENTER);

        cardLayout.show(cardsContainer, "dashboard");
        frame.setVisible(true);
    }

    private static JButton createSidebarButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        
        btn.addActionListener(e -> {
            if (cardName.equals("dashboard")) cardsContainer.add(new DashboardPanel(), "dashboard");
            else if (cardName.equals("finances")) cardsContainer.add(new FinancePanel(), "finances");
            else if (cardName.equals("budget")) cardsContainer.add(new BudgetPanel(), "budget");
            else if (cardName.equals("goals")) cardsContainer.add(new GoalPanel(), "goals");
            else if (cardName.equals("home_finances")) cardsContainer.add(new HomeFinancePanel(), "home_finances");
            else if (cardName.equals("reports")) cardsContainer.add(new ReportsPanel(), "reports");
            
            cardLayout.show(cardsContainer, cardName);
        });
        return btn;
    }
}
