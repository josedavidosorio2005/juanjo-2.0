package com.app.gui;

import com.app.MainApplication;
import com.app.dao.UserDao;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField userField;
    private JPasswordField passField;

    public LoginFrame() {
        setTitle("Acceso Seguro - Salud y Finanzas Pro");
        setSize(450, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel bgPanel = new JPanel(new BorderLayout());
        bgPanel.setBackground(new Color(34, 47, 62));
        
        JPanel loginCard = new JPanel();
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBackground(Color.WHITE);
        loginCard.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel iconLbl = new JLabel("🛡️");
        iconLbl.setFont(new Font("Arial", Font.PLAIN, 60));
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginCard.add(iconLbl);
        loginCard.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel title = new JLabel("Bienvenido");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setForeground(new Color(44, 62, 80));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginCard.add(title);
        loginCard.add(Box.createRigidArea(new Dimension(0, 30)));

        loginCard.add(createInputLabel("USUARIO"));
        userField = new JTextField("admin");
        styleTextField(userField);
        loginCard.add(userField);
        loginCard.add(Box.createRigidArea(new Dimension(0, 20)));

        loginCard.add(createInputLabel("CONTRASEÑA"));
        passField = new JPasswordField("admin");
        styleTextField(passField);
        loginCard.add(passField);
        loginCard.add(Box.createRigidArea(new Dimension(0, 40)));

        JButton btnLogin = new JButton("ACCEDER AHORA");
        btnLogin.setBackground(new Color(52, 152, 219));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLogin.addActionListener(e -> attemptLogin());
        loginCard.add(btnLogin);

        loginCard.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JButton btnRegister = new JButton("CREAR NUEVA CUENTA");
        btnRegister.setFont(new Font("Arial", Font.PLAIN, 12));
        btnRegister.setForeground(new Color(52, 152, 219));
        btnRegister.setContentAreaFilled(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegister.addActionListener(e -> attemptRegister());
        loginCard.add(btnRegister);

        bgPanel.add(loginCard, BorderLayout.CENTER);
        add(bgPanel);
    }

    private JLabel createInputLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 10));
        l.setForeground(new Color(127, 140, 141));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private void styleTextField(JTextField field) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setBackground(new Color(245, 246, 250));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 221, 225), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void attemptLogin() {
        UserDao dao = new UserDao();
        String u = userField.getText();
        String p = new String(passField.getPassword());
        
        if (dao.validateLogin(u, p)) {
            this.dispose();
            MainApplication.launchDashboard(); 
        } else {
            JOptionPane.showMessageDialog(this, "Credenciales incorrectas.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void attemptRegister() {
        UserDao dao = new UserDao();
        String u = userField.getText().trim();
        String p = new String(passField.getPassword()).trim();
        
        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Usuario y contraseña requeridos.");
            return;
        }
        
        if (dao.registerUser(u, p)) {
            JOptionPane.showMessageDialog(this, "Usuario registrado con éxito. Ya puedes iniciar sesión.");
        } else {
            JOptionPane.showMessageDialog(this, "Error al registrar: El usuario podría ya existir.");
        }
    }
}
