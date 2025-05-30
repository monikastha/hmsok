package Student;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;

public class StudentBills extends JPanel {

    private String studentUsername;
    private JTable billTable;
    private DefaultTableModel billModel;

    public StudentBills(String username) {
        this.studentUsername = username;
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 250, 255)); // Light background

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("Your Hostel Bills (Receipts)", SwingConstants.CENTER);
        title.setFont(new Font("Verdana", Font.BOLD, 24));
        title.setForeground(new Color(30, 60, 90)); // Dark blue
        add(title, BorderLayout.NORTH);

        // Table panel for bills
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(240, 248, 255));
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 120, 170)),
                "Your Bill History",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Tahoma", Font.BOLD, 14),
                new Color(0, 70, 140)
        ));

        // Table model and table for bills
        billModel = new DefaultTableModel(new String[]{
                "Bill No", "Bill Date", "Hostel Fee (Rs)", "Paid Amount (Rs)", "Remaining (Rs)"
        }, 0);

        billTable = new JTable(billModel);
        billTable.setRowHeight(28);
        billTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        billTable.setForeground(Color.DARK_GRAY);
        billTable.setBackground(Color.WHITE);
        billTable.setEnabled(false); // read-only

        // Customize table header
        JTableHeader header = billTable.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 15));
        header.setBackground(new Color(0, 120, 180));
        header.setForeground(Color.WHITE);

        // Center align table cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < billTable.getColumnCount(); i++) {
            billTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(billTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);

        // Load bill data
        loadStudentBills();
    }

    private void loadStudentBills() {
        billModel.setRowCount(0); // Clear existing data
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hostelms", "root", "");
             PreparedStatement pst = con.prepareStatement(
                     "SELECT b.bill_no, b.bill_date, s.std_hostel_fee, f.f_credit, f.f_remaining " +
                             "FROM bill b " +
                             "JOIN student s ON b.std_id = s.std_id " +
                             "JOIN fee f ON b.f_id = f.f_id " +
                             "WHERE s.std_username = ? " +
                             "ORDER BY b.bill_date DESC;")) {

            pst.setString(1, studentUsername);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                billModel.addRow(new Object[]{
                        rs.getString("bill_no"),
                        rs.getDate("bill_date").toString(), // Convert SQL Date to String
                        rs.getFloat("std_hostel_fee"),
                        rs.getFloat("f_credit"),
                        rs.getFloat("f_remaining")
                });
            }
            if (billModel.getRowCount() == 0) {
                // If no bills found, display a message in the table
                billModel.addRow(new Object[]{"", "", "No bills found.", "", ""});
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading your bills: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            // Optionally, add a row indicating an error
            billModel.addRow(new Object[]{"", "", "Error loading bills.", "", ""});
        }
    }

    // Main method for testing (optional, remove if integrating into a larger app)
    public static void main(String[] args) {
        // Example usage: Replace "testStudent" with an actual username from your database
        String testUsername = "testStudent"; // Replace with a valid student username for testing

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Student Bill Viewer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
            frame.add(new StudentBills(testUsername));
            frame.setVisible(true);
        });
    }
}
