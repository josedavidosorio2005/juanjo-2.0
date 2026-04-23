package com.app.gui;

import com.app.dao.GoalDao;
import com.app.models.GoalRecord;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GoalPanel extends JPanel {
    private GoalDao goalDao = new GoalDao();
    private JPanel listContainer;
    private JTextField nameField, targetField;

    public GoalPanel() {
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(BorderFactory.createTitledBorder("Crear Nueva Meta de Ahorro"));
        top.add(new JLabel("Nombre de Meta:"));
        nameField = new JTextField(12);
        top.add(nameField);
        top.add(new JLabel("Monto Objetivo ($):"));
        targetField = new JTextField(10);
        top.add(targetField);
        JButton btnSave = new JButton("Crear Meta");
        btnSave.addActionListener(e -> createGoal());
        top.add(btnSave);
        add(top, BorderLayout.NORTH);

        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        add(new JScrollPane(listContainer), BorderLayout.CENTER);

        refreshList();
    }

    private void createGoal() {
        try {
            double target = Double.parseDouble(targetField.getText());
            goalDao.insert(nameField.getText(), 0.0, target);
            nameField.setText("");
            targetField.setText("");
            refreshList();
        } catch (Exception e) {}
    }

    private void refreshList() {
        listContainer.removeAll();
        List<GoalRecord> goals = goalDao.getAll();
        
        for (GoalRecord g : goals) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
            p.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            p.setMaximumSize(new Dimension(800, 60));
            
            JLabel info = new JLabel(g.getGoalName() + " | Ahorrado: $" + g.getCurrentAmount() + " / Objetivo: $" + g.getTargetAmount());
            info.setFont(new Font("Arial", Font.BOLD, 14));
            
            JTextField addField = new JTextField(6);
            JButton btnAdd = new JButton("Abonar a Meta");
            btnAdd.addActionListener(e -> {
                try {
                    goalDao.addFunds(g.getId(), Double.parseDouble(addField.getText()));
                    refreshList();
                } catch (Exception ex) {}
            });
            
            p.add(info);
            p.add(new JLabel("Añadir: $"));
            p.add(addField);
            p.add(btnAdd);
            listContainer.add(p);
            listContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        revalidate();
        repaint();
    }
}
