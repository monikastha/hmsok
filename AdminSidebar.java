package Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AdminSidebar extends JPanel {

    private CardLayout cardLayout;
    private JPanel contentPanel;

    public AdminSidebar(CardLayout cardLayout, JPanel contentPanel) {
        // Set layout and size
        setLayout(new BorderLayout());
        setBackground(new Color(41, 50, 65));
        setPreferredSize(new Dimension(190, 700));

        this.cardLayout = cardLayout;
        this.contentPanel = contentPanel;

        // Sidebar Panel
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(41, 50, 65));
        sidebar.setPreferredSize(new Dimension(190, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Sidebar Title
        JLabel sidebarTitle = new JLabel("Admin Dashboard");
        sidebarTitle.setForeground(Color.WHITE);
        sidebarTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sidebarTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(sidebarTitle);
        sidebar.add(Box.createVerticalStrut(30));
        JButton btnHome = new JButton("Home");
        styleButton(btnHome); // Apply consistent styling
        btnHome.addActionListener(e -> cardLayout.show(contentPanel, "home"));
        // ==== Manage Rooms Section ====
        JButton btnManageRooms = new JButton("Manage Rooms");
        styleButton(btnManageRooms);
        btnManageRooms.addActionListener(e -> cardLayout.show(contentPanel, "rooms"));

        // ==== Manage Students Section ====
        JButton btnManageStudents = new JButton("Manage Students");
        styleButton(btnManageStudents);
        btnManageStudents.addActionListener(e -> cardLayout.show(contentPanel, "students"));

        // ==== Manage Fees Section ====
        JButton btnManageFees = new JButton("Manage Fees");
        styleButton(btnManageFees);
        btnManageFees.addActionListener(e -> cardLayout.show(contentPanel, "fees"));

        // ==== Logout Button ====
        JButton btnLogout = new JButton("Logout");
        styleButton(btnLogout); // Apply styling
        addHoverEffectSimple(btnLogout); // Apply hover effect

        btnLogout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Show a confirmation dialog when the logout button is clicked
                int option = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to log out?",
                        "Confirm Logout",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                );

                if (option == JOptionPane.YES_OPTION) {
                    // If user clicked "Yes", log out and navigate to LoginPage
                    JFrame currentFrame = (JFrame) SwingUtilities.getWindowAncestor(btnLogout);
                    currentFrame.dispose(); // Close the current window

                    // Open the LoginPage
                    Auth.LoginForm loginForm = new Auth.LoginForm();
                    loginForm.setVisible(true); // Make the login page visible
                }
                // If user clicked "No", nothing happens (they stay logged in)
            }
        });

        // Add buttons to sidebar
        sidebar.add(btnHome);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnManageRooms);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnManageStudents);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnManageFees);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(btnLogout);

        // Add the sidebar to the main panel (AdminSidebar)
        add(sidebar, BorderLayout.WEST);
    }

    // Style main sidebar button
    private void styleButton(JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(170, 35));
        button.setFocusPainted(false);
        button.setBackground(new Color(32, 74, 110));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    }

    // Simple hover color effect for Logout button
    private void addHoverEffectSimple(JButton button) {
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(41, 128, 185));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(52, 152, 219));
            }
        });
    }
}
