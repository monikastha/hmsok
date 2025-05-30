package Auth;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterPage extends JFrame {
    private JTextField nameField, emailField, usernameField, addressField, guardianNameField, guardianPhoneField;
    private JPasswordField passwordField;
    private JButton registerButton, backButton;

    public RegisterPage() {
        setTitle("Register - Hostel Management");
        setSize(450, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());

        getContentPane().setBackground(Color.lightGray);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Name
        addComponent(new JLabel("Full Name:"), gbc, 0, 0);
        nameField = new JTextField(15);
        addComponent(nameField, gbc, 1, 0);

        // Email
        addComponent(new JLabel("Email:"), gbc, 0, 1);
        emailField = new JTextField(15);
        addComponent(emailField, gbc, 1, 1);

        // Username

        // Address
        addComponent(new JLabel("Address:"), gbc, 0, 2 );
        addressField = new JTextField(15);
        addComponent(addressField, gbc, 1, 2);

        // Guardian Name
        addComponent(new JLabel("Guardian Name:"), gbc, 0, 3);
        guardianNameField = new JTextField(15);
        addComponent(guardianNameField, gbc, 1, 3);

        // Guardian Phone
        addComponent(new JLabel("Guardian Phone:"), gbc, 0, 4);
        guardianPhoneField = new JTextField(15);
        addComponent(guardianPhoneField, gbc, 1, 4);
        // Username
        addComponent(new JLabel("Username:"), gbc, 0, 5);
        usernameField = new JTextField(15);
        addComponent(usernameField, gbc, 1, 5);
        // Password
        addComponent(new JLabel("Password:"), gbc, 0, 6);
        passwordField = new JPasswordField(15);
        addComponent(passwordField, gbc, 1, 6);
        // Register Button
        registerButton = new JButton("Register");
        styleButton(registerButton);
        registerButton.addActionListener(e -> registerUser());
        addComponent(registerButton, gbc, 0, 7, 2);

        // Back Button
        backButton = new JButton("Back to Login");
        styleButton(backButton);
        backButton.addActionListener(e -> {
            new LoginForm();
            dispose();
        });
        addComponent(backButton, gbc, 0, 8, 2);

        setVisible(true);
    }

    private void registerUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String address = addressField.getText().trim();
        String guardianName = guardianNameField.getText().trim();
        String guardianPhone = guardianPhoneField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()
                || address.isEmpty() || guardianName.isEmpty() || guardianPhone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Invalid email format!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hostelms", "root", "")) {
            // Check if username already exists
            try (PreparedStatement checkStmt = conn.prepareStatement("SELECT * FROM login WHERE log_username = ?")) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Username already taken!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Insert into login table
            try (PreparedStatement insertLogin = conn.prepareStatement(
                    "INSERT INTO login (log_username, log_password, log_role) VALUES (?, ?, 'Student')",
                    Statement.RETURN_GENERATED_KEYS)) {
                insertLogin.setString(1, username);
                insertLogin.setString(2, password);
                int affectedRows = insertLogin.executeUpdate();

                if (affectedRows > 0) {
                    // Get generated login ID
                    ResultSet generatedKeys = insertLogin.getGeneratedKeys();
                    int loginId = 0;
                    if (generatedKeys.next()) {
                        loginId = generatedKeys.getInt(1);
                    }

                    // Insert into student table

                    JOptionPane.showMessageDialog(this, "Registration Successful! You can now log in.");
                    new LoginForm();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Registration Failed!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addComponent(Component comp, GridBagConstraints gbc, int x, int y) {
        gbc.gridx = x;
        gbc.gridy = y;
        add(comp, gbc);
    }

    private void addComponent(Component comp, GridBagConstraints gbc, int x, int y, int width) {
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        add(comp, gbc);
        gbc.gridwidth = 1;
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(50, 205, 50));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
    }

    public static void main(String[] args) {
        new RegisterPage();
    }
}
