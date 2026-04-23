package com.app.gui;

import com.app.dao.BudgetDao;
import com.app.dao.FinanceDao;
import com.app.models.BudgetRecord;
import com.app.models.FinanceRecord;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class BudgetPanel extends JPanel {
    private BudgetDao budgetDao = new BudgetDao();
    private FinanceDao financeDao = new FinanceDao();
    private JPanel progressContainer;
    private JComboBox<String> catCombo;
    private JTextField limitField;

    public BudgetPanel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form to set budgets
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(BorderFactory.createTitledBorder("Definir Presupuesto Mensual"));
        top.add(new JLabel("Categoría:"));
        catCombo = new JComboBox<>(new String[]{"Alimentos", "Transporte", "Casa", "Ocio", "Salud", "Educación"});
        top.add(catCombo);
        top.add(new JLabel("Límite ($):"));
        limitField = new JTextField(10);
        top.add(limitField);
        JButton btnSave = new JButton("Fijar Presupuesto");
        btnSave.addActionListener(e -> saveBudget());
        top.add(btnSave);
        add(top, BorderLayout.NORTH);

        // Progress bars container
        progressContainer = new JPanel();
        progressContainer.setLayout(new BoxLayout(progressContainer, BoxLayout.Y_AXIS));
        add(new JScrollPane(progressContainer), BorderLayout.CENTER);

        refreshProgress();
    }

    private void saveBudget() {
        try {
            double limit = Double.parseDouble(limitField.getText());
            budgetDao.upsertBudget((String) catCombo.getSelectedItem(), limit);
            limitField.setText("");
            refreshProgress();
            JOptionPane.showMessageDialog(this, "Presupuesto guardado.");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Revisa el formato del monto.");
        }
    }

    private void refreshProgress() {
        progressContainer.removeAll();
        List<BudgetRecord> budgets = budgetDao.getAll();
        List<FinanceRecord> records = financeDao.getAllRecords();

        for (BudgetRecord b : budgets) {
            double spent = 0;
            for (FinanceRecord r : records) {
                if ("Gasto".equals(r.getType()) && r.getCategory().equals(b.getCategory())) spent += r.getAmount();
            }

            JPanel p = new JPanel(new BorderLayout(10, 5));
            p.setMaximumSize(new Dimension(800, 60));
            p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel lbl = new JLabel(b.getCategory() + " (Gastado: $" + String.format("%.2f", spent) + " / Límite: $" + String.format("%.2f", b.getLimitAmount()) + ")");
            JProgressBar bar = new JProgressBar(0, (int) b.getLimitAmount());
            bar.setValue((int) spent);
            bar.setStringPainted(true);
            
            if (spent > b.getLimitAmount()) bar.setForeground(new Color(231, 76, 60));
            else if (spent > b.getLimitAmount() * 0.8) bar.setForeground(new Color(243, 156, 18));
            else bar.setForeground(new Color(46, 204, 113));

            p.add(lbl, BorderLayout.NORTH);
            p.add(bar, BorderLayout.CENTER);
            progressContainer.add(p);
        }
        revalidate();
        repaint();
    }
}
