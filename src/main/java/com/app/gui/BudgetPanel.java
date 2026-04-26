package com.app.gui;

import com.app.dao.BudgetDao;
import com.app.dao.FinanceDao;
import com.app.models.BudgetRecord;
import com.app.models.FinanceRecord;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;
import com.app.utils.ChartUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import com.app.utils.AppColors;
import com.app.utils.ChartUtils;
import com.app.utils.AnimationUtils;

public class BudgetPanel extends JPanel {
    private BudgetDao budgetDao = new BudgetDao();
    private FinanceDao financeDao = new FinanceDao();
    
    private JLabel totalBudgetVal, spentVal, availableVal, alertsVal;
    private JLabel totalBudgetSub, spentSub, availableSub, alertsSub;
    private JTable categoryTable;
    private DefaultTableModel tableModel;
    private JPanel chartContainer;
    private JPanel alertsSidebar;

    public BudgetPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(AppColors.BG_MAIN);
        setBorder(new EmptyBorder(25, 25, 25, 25));

        // 1. Header Row
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel titleLabel = new JLabel("Presupuesto");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JPanel datePickerMock = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        datePickerMock.setOpaque(false);
        JComboBox<String> monthCombo = new JComboBox<>(new String[]{"Abril 2024", "Mayo 2024", "Junio 2024"});
        datePickerMock.add(new JLabel("📅"));
        datePickerMock.add(monthCombo);
        headerPanel.add(datePickerMock, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // 2. Main Content Wrapper (Center)
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 0, 10, 0);

        // 2.1 KPI Cards Row
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 20, 0));
        kpiRow.setOpaque(false);
        
        // Initialize dynamic labels
        totalBudgetVal = new JLabel("$0");
        totalBudgetSub = new JLabel("Cargando...");
        spentVal = new JLabel("$0");
        spentSub = new JLabel("0% del presupuesto");
        availableVal = new JLabel("$0");
        availableSub = new JLabel("100% disponible");
        alertsVal = new JLabel("0 categorías");
        alertsSub = new JLabel("En buen estado");

        JPanel c1 = createKpiCard("Presupuesto Total", totalBudgetVal, totalBudgetSub, AppColors.SUCCESS, "💰");
        JPanel c2 = createKpiCard("Gastado del Mes", spentVal, spentSub, AppColors.PRIMARY, "💳");
        JPanel c3 = createKpiCard("Disponible", availableVal, availableSub, AppColors.ACCENT, "📊");
        JPanel c4 = createKpiCard("Alertas Activas", alertsVal, alertsSub, AppColors.WARNING, "⚠️");
        
        AnimationUtils.addHoverEffect(c1);
        AnimationUtils.addHoverEffect(c2);
        AnimationUtils.addHoverEffect(c3);
        AnimationUtils.addHoverEffect(c4);

        kpiRow.add(c1);
        kpiRow.add(c2);
        kpiRow.add(c3);
        kpiRow.add(c4);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3; gbc.weightx = 1.0; gbc.weighty = 0.15;
        mainContent.add(kpiRow, gbc);

        // 2.2 Controls Row
        JPanel controlsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        controlsRow.setOpaque(false);
        controlsRow.add(createLabeledCombo("Mes", new String[]{"Abril 2024"}));
        controlsRow.add(createLabeledCombo("Vista", new String[]{"Por Categoría"}));
        controlsRow.add(new JLabel("Buscar categoría:"));
        JTextField searchField = new JTextField(15);
        controlsRow.add(searchField);
        JButton updateBtn = new JButton("🔄 Actualizar Presupuesto");
        controlsRow.add(updateBtn);

        gbc.gridy = 1; gbc.weighty = 0.05;
        mainContent.add(controlsRow, gbc);

        // 2.3 Body: Split Chart | Table | Sidebar
        JPanel bodyPanel = new JPanel(new BorderLayout(20, 0));
        bodyPanel.setOpaque(false);

        // Left: Distribution Chart
        JPanel chartWrapper = createStyledPanel("Distribución del Presupuesto");
        chartWrapper.setPreferredSize(new Dimension(280, 400));
        chartContainer = new JPanel(new BorderLayout());
        chartContainer.setOpaque(false);
        chartWrapper.add(chartContainer, BorderLayout.CENTER);
        bodyPanel.add(chartWrapper, BorderLayout.WEST);

        // Center: Category Table
        JPanel tableWrapper = createStyledPanel("Presupuesto por Categoría");
        String[] cols = {"Categoría", "Presupuesto", "Gastado", "Disponible", "Avance", "Estado", "Acciones"};
        tableModel = new DefaultTableModel(cols, 0);
        categoryTable = new JTable(tableModel);
        categoryTable.setRowHeight(45);
        categoryTable.setShowGrid(false);
        categoryTable.setIntercellSpacing(new Dimension(0, 0));
        categoryTable.getTableHeader().setBackground(AppColors.SURFACE);
        categoryTable.getTableHeader().setForeground(AppColors.TEXT_PRIMARY);
        categoryTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        tableWrapper.add(new JScrollPane(categoryTable), BorderLayout.CENTER);
        
        JPanel tableFooter = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tableFooter.setOpaque(false);
        tableFooter.add(new JButton("+ Nueva Categoría"));
        tableFooter.add(new JButton("📜 Historial de Cambios"));
        tableWrapper.add(tableFooter, BorderLayout.SOUTH);
        
        bodyPanel.add(tableWrapper, BorderLayout.CENTER);

        // Right: Alerts Sidebar
        alertsSidebar = new JPanel();
        alertsSidebar.setLayout(new BoxLayout(alertsSidebar, BoxLayout.Y_AXIS));
        alertsSidebar.setPreferredSize(new Dimension(280, 0));
        alertsSidebar.setBackground(AppColors.SURFACE);
        alertsSidebar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel alertsTitle = new JLabel("🔔 Alertas de Presupuesto");
        alertsTitle.setFont(new Font("Arial", Font.BOLD, 16));
        alertsSidebar.add(alertsTitle);
        alertsSidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Mock Alerts
        alertsSidebar.add(createAlertItem("Transporte", 90, "Gastado $270k de $300k", Color.ORANGE));
        alertsSidebar.add(createAlertItem("Entretenimiento", 105, "Excedido por $10k", Color.RED));
        
        alertsSidebar.add(Box.createVerticalGlue());
        
        JLabel summaryTitle = new JLabel("Resumen del Mes");
        summaryTitle.setFont(new Font("Arial", Font.BOLD, 16));
        alertsSidebar.add(summaryTitle);
        alertsSidebar.add(new JLabel("Presupuesto Total: $2,000,000"));
        alertsSidebar.add(new JLabel("Total Gastado: $1,600,000"));
        alertsSidebar.add(new JLabel("Disponible: $400,000"));

        bodyPanel.add(alertsSidebar, BorderLayout.EAST);

        gbc.gridy = 2; gbc.weighty = 0.8;
        mainContent.add(bodyPanel, gbc);

        // 2.4 Footer Info
        JPanel footerInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerInfo.setBackground(AppColors.withAlpha(AppColors.INFO, 30));
        footerInfo.setBorder(new EmptyBorder(10, 15, 10, 15));
        JLabel infoLabel = new JLabel("ℹ️ Define y controla tu presupuesto mensual por categoría. El sistema te ayudará a mantener tus gastos bajo control.");
        infoLabel.setForeground(AppColors.TEXT_PRIMARY);
        footerInfo.add(infoLabel);
        
        gbc.gridy = 3; gbc.weighty = 0.05;
        mainContent.add(footerInfo, gbc);

        add(mainContent, BorderLayout.CENTER);

        refreshData();
    }

    private JPanel createKpiCard(String title, JLabel valueLbl, JLabel subLbl, Color color, String icon) {
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

        JPanel textPanel = new JPanel(new GridLayout(3, 1));
        textPanel.setOpaque(false);
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLbl.setForeground(Color.GRAY);
        
        valueLbl.setFont(new Font("Arial", Font.BOLD, 18));
        valueLbl.setForeground(AppColors.TEXT_PRIMARY);
        subLbl.setFont(new Font("Arial", Font.PLAIN, 11));
        subLbl.setForeground(AppColors.TEXT_SECONDARY);

        textPanel.add(titleLbl);
        textPanel.add(valueLbl);
        textPanel.add(subLbl);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createStyledPanel(String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(AppColors.SURFACE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER),
            new EmptyBorder(15, 15, 15, 15)
        ));
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Arial", Font.BOLD, 16));
        titleLbl.setForeground(AppColors.TEXT_PRIMARY);
        p.add(titleLbl, BorderLayout.NORTH);
        return p;
    }

    private JPanel createLabeledCombo(String label, String[] items) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setOpaque(false);
        p.add(new JLabel(label));
        p.add(new JComboBox<>(items));
        return p;
    }

    private JPanel createAlertItem(String cat, int progress, String text, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(250, 60));
        JLabel lbl = new JLabel("⚠️ " + cat + "  " + progress + "%");
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        p.add(lbl, BorderLayout.NORTH);
        
        JProgressBar bar = new JProgressBar(0, 100);
        bar.setValue(Math.min(progress, 100));
        bar.setForeground(color);
        bar.setPreferredSize(new Dimension(200, 8));
        p.add(bar, BorderLayout.CENTER);
        
        JLabel desc = new JLabel(text);
        desc.setFont(new Font("Arial", Font.PLAIN, 10));
        p.add(desc, BorderLayout.SOUTH);
        
        p.add(Box.createRigidArea(new Dimension(0, 10)), BorderLayout.SOUTH);
        return p;
    }

    private void refreshData() {
        List<BudgetRecord> budgets = budgetDao.getAll();
        List<FinanceRecord> records = financeDao.getAllRecords();

        // Update Table
        tableModel.setRowCount(0);
        double totalBudget = 0;
        double totalSpent = 0;
        int activeAlerts = 0;

        for (BudgetRecord b : budgets) {
            double spent = records.stream()
                .filter(r -> "Gasto".equals(r.getType()) && b.getCategory().equals(r.getCategory()))
                .mapToDouble(FinanceRecord::getAmount)
                .sum();
            
            double available = b.getLimitAmount() - spent;
            int progress = (int) ((spent / b.getLimitAmount()) * 100);
            String status = spent > b.getLimitAmount() ? "Excedido" : (progress > 80 ? "Alerta" : "Normal");
            
            if (spent > b.getLimitAmount()) activeAlerts++;

            tableModel.addRow(new Object[]{
                b.getCategory(),
                "$" + String.format("%.0f", b.getLimitAmount()),
                "$" + String.format("%.0f", spent),
                "$" + String.format("%.0f", available),
                progress + "%",
                status,
                "✏️ 🗑️"
            });

            totalBudget += b.getLimitAmount();
            totalSpent += spent;
        }

        // Update KPI Labels
        totalBudgetVal.setText("$" + String.format("%,.0f", totalBudget));
        totalBudgetSub.setText("Abril 2024");
        
        spentVal.setText("$" + String.format("%,.0f", totalSpent));
        double spentPct = totalBudget > 0 ? (totalSpent / totalBudget) * 100 : 0;
        spentSub.setText(String.format("%.0f%% del presupuesto", spentPct));
        
        availableVal.setText("$" + String.format("%,.0f", totalBudget - totalSpent));
        availableSub.setText(String.format("%.0f%% del presupuesto", 100 - spentPct));
        
        alertsVal.setText(activeAlerts + " categorías");
        alertsSub.setText(activeAlerts > 0 ? "Superando el límite" : "Todo bajo control");

        // Update Chart
        updateChart(budgets);
        
        revalidate();
        repaint();
    }

    private void updateChart(List<BudgetRecord> budgets) {
        chartContainer.removeAll();
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (BudgetRecord b : budgets) {
            dataset.setValue(b.getCategory(), b.getLimitAmount());
        }

        JFreeChart chart = ChartFactory.createRingChart("", dataset, false, true, false);
        ChartUtils.applyPremiumStyle(chart);
        
        ChartPanel cp = new ChartPanel(chart);
        cp.setPreferredSize(new Dimension(250, 250));
        cp.setBackground(Color.WHITE);
        chartContainer.add(cp);
    }
}
