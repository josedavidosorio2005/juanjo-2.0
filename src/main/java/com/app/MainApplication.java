package com.app;

import com.app.db.DatabaseManager;
import com.app.gui.BudgetPanel;
import com.app.gui.DashboardPanel;
import com.app.gui.FinancePanel;
import com.app.gui.GoalPanel;
import com.app.gui.HealthPanel;
import com.app.gui.HomeFinancePanel;
import com.app.gui.LoginFrame;
import com.app.gui.ReportsPanel;
import com.app.gui.SettingsPanel;
import com.app.services.CronJobDaemon;
import com.app.utils.AnimationUtils;
import com.app.utils.AppColors;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MainApplication {

    private static CardLayout cardLayout = new CardLayout();
    private static JPanel cardsContainer = new JPanel(cardLayout);
    private static JFrame frame;

    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
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
        sidebar.setBackground(AppColors.BG_SIDEBAR); 
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(30, 15, 30, 15));

        JLabel titleLabel = new JLabel("FINANZAS PRO");
        titleLabel.setForeground(AppColors.SURFACE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(titleLabel);
        
        JLabel subtitle = new JLabel("Enterprise Edition");
        subtitle.setForeground(AppColors.TEXT_LIGHT);
        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(subtitle);
        
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        sidebar.add(createSidebarButton("🏠  Inicio", "dashboard"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(createSidebarButton("💸  Gastos", "finances"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(createSidebarButton("📊  Presupuesto", "budget"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(createSidebarButton("🚀  Metas", "goals"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(createSidebarButton("🏠  Hogar", "home_finances"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(createSidebarButton("📈  Reportes", "reports"));
        sidebar.add(Box.createRigidArea(new Dimension(0, 8)));
        sidebar.add(createSidebarButton("⚙️  Configuración", "settings"));
        
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));
        sidebar.add(createSidebarButton("❤️  Salud Integral", "health"));
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
        btn.setMaximumSize(new Dimension(210, 45));
        btn.setPreferredSize(new Dimension(210, 45));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(AppColors.TEXT_LIGHT);
        btn.setBackground(AppColors.BG_SIDEBAR);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addActionListener(e -> {
            JPanel panel = null;
            if (cardName.equals("dashboard")) panel = new DashboardPanel();
            else if (cardName.equals("finances")) panel = new FinancePanel();
            else if (cardName.equals("budget")) panel = new BudgetPanel();
            else if (cardName.equals("goals")) panel = new GoalPanel();
            else if (cardName.equals("home_finances")) panel = new HomeFinancePanel();
            else if (cardName.equals("reports")) panel = new ReportsPanel();
            else if (cardName.equals("health")) panel = new HealthPanel();
            
            if (panel != null) {
                cardsContainer.add(panel, cardName);
                cardLayout.show(cardsContainer, cardName);
                // Innovative Slide-Up Transition
                AnimationUtils.slideIn(panel, 30, 300);
            } else {
                cardLayout.show(cardsContainer, cardName);
            }
        });
        return btn;
    }
}
