package Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*; // For database interaction

public class StudentHome extends JPanel {

    private JLabel lblWelcomeMessage;
    private JLabel lblStudentName; // To explicitly show the student's name separately// Added for clarity, as per previous request

    private String studentIdentifier; // Renamed from studentId to be more generic

    public StudentHome(String studentIdentifier) {
        this.studentIdentifier = studentIdentifier; // Now holds either ID or Username

        setLayout(new BorderLayout()); // Use BorderLayout for overall structure
        setBackground(new Color(178, 181, 184)); // Light background color
        setBorder(new EmptyBorder(50, 50, 50, 50)); // More padding to center content if it's minimal

        // --- Main Content Panel ---
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS)); // Stack elements vertically
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the panel horizontally

        // Welcome Message Label
        lblWelcomeMessage = new JLabel("Welcome!");
        lblWelcomeMessage.setFont(new Font("Segoe UI", Font.BOLD, 48)); // Large font for welcome
        lblWelcomeMessage.setForeground(new Color(70, 130, 180)); // Steel blue
        lblWelcomeMessage.setAlignmentX(Component.CENTER_ALIGNMENT); // Center text in its own component

        // Student Name Label
        lblStudentName = new JLabel("Loading Name..."); // Placeholder
        lblStudentName.setFont(new Font("Segoe UI", Font.PLAIN, 36)); // Slightly smaller than welcome
        lblStudentName.setForeground(new Color(50, 60, 70)); // Darker grey
        lblStudentName.setAlignmentX(Component.CENTER_ALIGNMENT); // Center text in its own component

        contentPanel.add(lblWelcomeMessage);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Add some vertical space
        contentPanel.add(lblStudentName);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Space between name and username// Add the new label


        // Add the content panel to the center of the main panel
        add(contentPanel, BorderLayout.CENTER);

        // Load student data
        loadStudentData();
    }

    private void loadStudentData() {
        if (studentIdentifier == null || studentIdentifier.isEmpty()) {
            lblWelcomeMessage.setText("Error!");
            lblStudentName.setText("Student Identifier Missing.");
            JOptionPane.showMessageDialog(this, "Student identifier is missing. Cannot load data.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // MODIFIED SQL QUERY: Now searches by std_username instead of std_id
        String sql = "SELECT std_name, std_username FROM student WHERE std_username = ?";

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hostelms", "root", "");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, studentIdentifier); // Use the identifier (which is now username)
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String studentName = rs.getString("std_name");
                String studentUsername = rs.getString("std_username"); // Get the username

                lblWelcomeMessage.setText("Welcome!");
                lblStudentName.setText(studentName + "!");
              // Display the username
            } else {
                lblWelcomeMessage.setText("Welcome!");
                lblStudentName.setText("User Not Found.");
               // Clear username label
                // The error message now reflects that the username wasn't found
                JOptionPane.showMessageDialog(this, "Student data not found for username: " + studentIdentifier, "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            lblWelcomeMessage.setText("Error!");
            lblStudentName.setText("Database Issue.");
       // Clear username label
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace(); // Crucial for seeing the exact SQL error
        }
    }

    // Main method for testing
    public static void main(String[] args) {
        // Use a valid student USERNAME from your database for testing with Option 2
        String testStudentUsername = "angila123"; // Change this to an existing username in your 'student' table

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame frame = new JFrame("Student Home Panel Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 450); // Adjusted size to accommodate new label
            frame.setLocationRelativeTo(null); // Center the frame

            StudentHome studentHomePanel = new StudentHome(testStudentUsername); // Pass the username here
            frame.add(studentHomePanel);
            frame.setVisible(true);
        });
    }
}
