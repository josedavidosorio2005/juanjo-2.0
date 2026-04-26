package com.app.gui;

import com.app.dao.GoalDao;
import com.app.models.GoalRecord;
import com.app.utils.AppColors;
import com.app.utils.AnimationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.List;

public class GoalPanel extends JPanel {
    private final GoalDao goalDao = new GoalDao();
    private final JPanel listContainer;
    private JTextField nameField, targetField;

    public GoalPanel() {
        setLayout(new BorderLayout(30, 30));
        setBackground(AppColors.BG_MAIN);
        setBorder(new EmptyBorder(35, 35, 35, 35));

        // 1. Header with modern subtitle
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        
        JLabel title = new JLabel("Mis Metas de Ahorro");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(AppColors.TEXT_PRIMARY);
        
        JLabel subtitle = new JLabel("Visualiza tu progreso y alcanza tus sueños");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(AppColors.TEXT_SECONDARY);
        
        JPanel titleGroup = new JPanel(new GridLayout(2, 1, 0, 5));
        titleGroup.setOpaque(false);
        titleGroup.add(title);
        titleGroup.add(subtitle);
        
        header.add(titleGroup, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // 2. Main Layout
        JPanel mainBody = new JPanel(new BorderLayout(35, 0));
        mainBody.setOpaque(false);

        // 2.1 Modern Form (Left)
        JPanel formWrapper = createModernForm();
        mainBody.add(formWrapper, BorderLayout.WEST);

        // 2.2 Goals List (Center)
        listContainer = new JPanel();
        listContainer.setLayout(new BoxLayout(listContainer, BoxLayout.Y_AXIS));
        listContainer.setOpaque(false);
        
        JScrollPane scroll = new JScrollPane(listContainer);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        
        mainBody.add(scroll, BorderLayout.CENTER);
        add(mainBody, BorderLayout.CENTER);

        refreshList();
    }

    private JPanel createModernForm() {
        JPanel container = new JPanel(new BorderLayout(0, 20));
        container.setOpaque(false);
        container.setPreferredSize(new Dimension(320, 0));

        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(AppColors.SURFACE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new ModernBorder(AppColors.BORDER, 1, 20),
            new EmptyBorder(25, 25, 25, 25)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 15, 0);

        JLabel t = new JLabel("Nueva Meta");
        t.setFont(new Font("Segoe UI", Font.BOLD, 20));
        t.setForeground(AppColors.PRIMARY);
        gbc.gridy = 0;
        card.add(t, gbc);

        gbc.gridy = 1;
        card.add(createInputLabel("Nombre de la Meta"), gbc);
        gbc.gridy = 2;
        nameField = createStyledTextField("Ej: Viaje a Japón");
        card.add(nameField, gbc);

        gbc.gridy = 3;
        card.add(createInputLabel("Monto Objetivo ($)"), gbc);
        gbc.gridy = 4;
        targetField = createStyledTextField("5000");
        card.add(targetField, gbc);

        JButton btnCreate = new ModernButton("🚀 Crear Nueva Meta", AppColors.PRIMARY);
        btnCreate.addActionListener(e -> createGoal());
        gbc.gridy = 5;
        gbc.insets = new Insets(10, 0, 0, 0);
        card.add(btnCreate, gbc);

        container.add(card, BorderLayout.NORTH);
        return container;
    }

    private JLabel createInputLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 12));
        l.setForeground(AppColors.TEXT_SECONDARY);
        return l;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField f = new JTextField();
        f.setPreferredSize(new Dimension(0, 40));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f.setBorder(BorderFactory.createCompoundBorder(
            new ModernBorder(AppColors.BORDER, 1, 10),
            new EmptyBorder(0, 15, 0, 15)
        ));
        return f;
    }

    private void createGoal() {
        try {
            String name = nameField.getText().trim();
            if (name.isEmpty()) throw new Exception();
            double target = Double.parseDouble(targetField.getText());
            goalDao.insert(name, 0.0, target);
            nameField.setText("");
            targetField.setText("");
            refreshList();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa datos válidos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshList() {
        listContainer.removeAll();
        List<GoalRecord> goals = goalDao.getAll();
        
        for (int i = 0; i < goals.size(); i++) {
            GoalRecord g = goals.get(i);
            GoalCard card = new GoalCard(g);
            listContainer.add(card);
            listContainer.add(Box.createRigidArea(new Dimension(0, 20)));
            
            // Subtle entry animation delay
            final int index = i;
            Timer entryTimer = new Timer(index * 100, e -> {
                card.setVisible(true);
                AnimationUtils.slideIn(card, 30, 500);
            });
            entryTimer.setRepeats(false);
            // card.setVisible(false); // Initially hidden if we want true slide-in
            // entryTimer.start();
        }
        revalidate();
        repaint();
    }

    // --- Custom Inner Components ---

    class GoalCard extends JPanel {
        private final GoalRecord goal;
        private final ModernProgressBar progressBar;

        public GoalCard(GoalRecord g) {
            this.goal = g;
            setLayout(new BorderLayout(20, 15));
            setBackground(AppColors.SURFACE);
            setBorder(BorderFactory.createCompoundBorder(
                new ModernBorder(AppColors.BORDER, 1, 18),
                new EmptyBorder(20, 25, 20, 25)
            ));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

            // Header: Name and Status
            JPanel top = new JPanel(new BorderLayout());
            top.setOpaque(false);
            
            JLabel nameLbl = new JLabel(goal.getGoalName());
            nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
            nameLbl.setForeground(AppColors.TEXT_PRIMARY);
            
            double pct = (goal.getCurrentAmount() / goal.getTargetAmount()) * 100;
            JLabel pctLbl = new JLabel(String.format("%.1f%%", pct));
            pctLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
            pctLbl.setForeground(pct >= 100 ? AppColors.SUCCESS : AppColors.PRIMARY);
            
            top.add(nameLbl, BorderLayout.WEST);
            top.add(pctLbl, BorderLayout.EAST);
            add(top, BorderLayout.NORTH);

            // Center: Modern Progress Bar
            progressBar = new ModernProgressBar();
            add(progressBar, BorderLayout.CENTER);

            // Bottom: Details and Action
            JPanel bottom = new JPanel(new BorderLayout());
            bottom.setOpaque(false);
            
            String statsText = String.format("<html><font color='#7f8c8d'>Ahorrado:</font> <b>$%,.0f</b> &nbsp; <font color='#bdc3c7'>|</font> &nbsp; <font color='#7f8c8d'>Meta:</font> <b>$%,.0f</b></html>", 
                goal.getCurrentAmount(), goal.getTargetAmount());
            JLabel stats = new JLabel(statsText);
            stats.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            bottom.add(stats, BorderLayout.WEST);

            JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
            actions.setOpaque(false);
            
            JTextField addField = createStyledTextField("Monto");
            addField.setPreferredSize(new Dimension(80, 35));
            JButton btnAdd = new ModernButton("Abonar", AppColors.SUCCESS);
            btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));
            btnAdd.setPreferredSize(new Dimension(80, 35));
            
            btnAdd.addActionListener(e -> {
                try {
                    String text = addField.getText().trim();
                    if (text.isEmpty()) return;
                    double amount = Double.parseDouble(text);
                    goalDao.addFunds(goal.getId(), amount);
                    refreshList();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Monto inválido", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            actions.add(new JLabel("$"));
            actions.add(addField);
            actions.add(btnAdd);
            bottom.add(actions, BorderLayout.EAST);
            
            add(bottom, BorderLayout.SOUTH);

            // Start progress animation
            AnimationUtils.animateValue(0, pct, 1200, val -> {
                progressBar.setProgress(val);
            });
            
            AnimationUtils.addHoverEffect(this);
        }
    }

    class ModernProgressBar extends JComponent {
        private double progress = 0; // 0 to 100

        public void setProgress(double p) {
            this.progress = Math.min(100, Math.max(0, p));
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            AnimationUtils.setHighQuality(g2);

            int w = getWidth();
            int h = getHeight();

            // Background track
            g2.setColor(new Color(240, 242, 245));
            g2.fill(new RoundRectangle2D.Float(0, 0, w, h, h, h));

            // Fill
            if (progress > 0) {
                int fillW = (int) (w * (progress / 100.0));
                GradientPaint gradient = new GradientPaint(0, 0, AppColors.PRIMARY, fillW, 0, AppColors.ACCENT);
                if (progress >= 100) {
                    gradient = new GradientPaint(0, 0, AppColors.SUCCESS, fillW, 0, new Color(39, 174, 96));
                }
                g2.setPaint(gradient);
                g2.fill(new RoundRectangle2D.Float(0, 0, fillW, h, h, h));
                
                // Gloss effect
                g2.setPaint(new LinearGradientPaint(0, 0, 0, h, 
                    new float[]{0f, 0.5f, 1f}, 
                    new Color[]{new Color(255,255,255,80), new Color(255,255,255,0), new Color(0,0,0,20)}));
                g2.fill(new RoundRectangle2D.Float(0, 0, fillW, h, h, h));
            }

            g2.dispose();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(0, 12);
        }
    }

    class ModernButton extends JButton {
        private final Color baseColor;
        private boolean isHovered = false;

        public ModernButton(String text, Color bg) {
            super(text);
            this.baseColor = bg;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) { isHovered = true; repaint(); }
                public void mouseExited(java.awt.event.MouseEvent e) { isHovered = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            AnimationUtils.setHighQuality(g2);
            
            Color drawColor = isHovered ? baseColor.brighter() : baseColor;
            g2.setColor(drawColor);
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
            
            super.paintComponent(g2);
            g2.dispose();
        }
    }

    class ModernBorder extends javax.swing.border.AbstractBorder {
        private final Color color;
        private final int thickness;
        private final int radius;

        public ModernBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            AnimationUtils.setHighQuality(g2);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.draw(new RoundRectangle2D.Float(x + (thickness/2f), y + (thickness/2f), 
                width - thickness, height - thickness, radius, radius));
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }
    }
}
