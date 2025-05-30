package Admin;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window on the screen
        setLayout(new BorderLayout()); // Use BorderLayout for the main frame

        // Create the content panel and CardLayout for dynamic content switching
        JPanel contentPanel = new JPanel();
        CardLayout cardLayout = new CardLayout();
        contentPanel.setLayout(cardLayout);

        // Initialize and add the Home panel
        AdminHome HomePanel = new AdminHome(); // Correct initialization
        contentPanel.add(HomePanel, "home");   // Correctly adding the panel to CardLayout

        // Initialize and add the AdminRoomManager panel
        AdminRoomManager roomManagerPanel = new AdminRoomManager();
        contentPanel.add(roomManagerPanel, "rooms"); // Add with identifier "rooms"

        // Initialize and add the AdminStudentManager panel
        AdminStudentManager studentManagerPanel = new AdminStudentManager();
        contentPanel.add(studentManagerPanel, "students"); // Add with identifier "students"

        // Initialize and add the AdminFeeManager panel
        AdminFeeManager feeManagerPanel = new AdminFeeManager();
        contentPanel.add(feeManagerPanel, "fees"); // Add with identifier "fees"

        // The AdminHostelStatus panel is commented out in your original code,
        // but you can uncomment and create it similarly if needed:
        // AdminHostelStatus hostelStatusPanel = new AdminHostelStatus();
        // contentPanel.add(hostelStatusPanel, "hostelStatus");

        // Add the sidebar to the frame, passing the CardLayout and content panel
        // The sidebar will control which panel is shown in the contentPanel
        AdminSidebar sidebar = new AdminSidebar(cardLayout, contentPanel);
        add(sidebar, BorderLayout.WEST); // Place the sidebar on the left

        // Add the content panel to the center of the frame
        add(contentPanel, BorderLayout.CENTER); // Place the content in the center

        setVisible(true); // Make the frame visible
    }

    // Main method to run the application
    public static void main(String[] args) {
        // Run the dashboard in the event dispatch thread for thread-safety
        // This is crucial for Swing applications to ensure UI updates are handled correctly
        SwingUtilities.invokeLater(() -> {
            new AdminDashboard();
        });
    }
}
