package com.app.gui;

import com.app.MainApplication;
import com.app.dao.UserDao;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private JTextField userField;
    private JPasswordField passField;

    public LoginFrame() {
        setTitle("Acceso de Seguridad");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JLabel title = new JLabel("Ingreso al Sistema", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        mainPanel.add(title);

        JPanel userPanel = new JPanel(new GridLayout(2, 1));
        userPanel.add(new JLabel("Usuario:"));
        userField = new JTextField("admin");
        userPanel.add(userField);
        mainPanel.add(userPanel);

        JPanel passPanel = new JPanel(new GridLayout(2, 1));
        passPanel.add(new JLabel("Contraseña:"));
        passField = new JPasswordField();
        passPanel.add(passField);
        mainPanel.add(passPanel);

        JButton btnLogin = new JButton("Ingresar Seguro");
        btnLogin.setBackground(new Color(41, 128, 185));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 14));
        btnLogin.addActionListener(e -> attemptLogin());
        mainPanel.add(btnLogin);

        add(mainPanel);
    }

    private void attemptLogin() {
        UserDao dao = new UserDao();
        String u = userField.getText();
        String p = new String(passField.getPassword());
        
        if (dao.validateLogin(u, p)) {
            // Start Main Application App
            this.dispose();
            MainApplication.launchDashboard(); 
        } else {
            JOptionPane.showMessageDialog(this, "Credenciales Inválidas.", "Error de Seguridad", JOptionPane.ERROR_MESSAGE);
        }
    }
}
