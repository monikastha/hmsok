package Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class AdminStudentManager extends JPanel {

    JTextField txtName, txtUsername, txtAddress, txtGuardianName, txtGuardianNo, txtHostelFee, txtBalance;
    JTable table;
    DefaultTableModel model;
    JButton btnCheckIn, btnCheckOut;
    private Map<String, Integer> roomNameToIdMap;
    private JComboBox<String> comboRoomNo;
    private Vector<String> roomNumbers;

    public AdminStudentManager() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(225, 227, 228));

        // --- Top Panel: Add Student Form ---
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder("Add New Student"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel lblName = new JLabel("Name:");
        lblName.setFont(new Font("Tahoma", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(lblName, gbc);

        txtName = new JTextField();
        txtName.setFont(new Font("Tahoma", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(txtName, gbc);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(lblUsername, gbc);

        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Tahoma", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(txtUsername, gbc);

        JLabel lblAddress = new JLabel("Address:");
        lblAddress.setFont(new Font("Tahoma", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(lblAddress, gbc);

        txtAddress = new JTextField();
        txtAddress.setFont(new Font("Tahoma", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 2;
        formPanel.add(txtAddress, gbc);

        JLabel lblGuardianName = new JLabel("Guardian Name:");
        lblGuardianName.setFont(new Font("Tahoma", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(lblGuardianName, gbc);

        txtGuardianName = new JTextField();
        txtGuardianName.setFont(new Font("Tahoma", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 3;
        formPanel.add(txtGuardianName, gbc);

        JLabel lblGuardianNo = new JLabel("Guardian No:");
        lblGuardianNo.setFont(new Font("Tahoma", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(lblGuardianNo, gbc);

        txtGuardianNo = new JTextField();
        txtGuardianNo.setFont(new Font("Tahoma", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 4;
        formPanel.add(txtGuardianNo, gbc);

        JLabel lblHostelFee = new JLabel("Hostel Fee:");
        lblHostelFee.setFont(new Font("Tahoma", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(lblHostelFee, gbc);

        txtHostelFee = new JTextField();
        txtHostelFee.setFont(new Font("Tahoma", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 5;
        formPanel.add(txtHostelFee, gbc);

        JLabel lblBalance = new JLabel("Remaining Fee:");
        lblBalance.setFont(new Font("Tahoma", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(lblBalance, gbc);

        txtBalance = new JTextField();
        txtBalance.setFont(new Font("Tahoma", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 6;
        formPanel.add(txtBalance, gbc);

        JLabel lblRoomNo = new JLabel("Room No:");
        lblRoomNo.setFont(new Font("Tahoma", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(lblRoomNo, gbc);


        roomNumbers = loadRoomNumbers();
        comboRoomNo = new JComboBox<>(roomNumbers);
        comboRoomNo.setFont(new Font("Tahoma", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 7;
        formPanel.add(comboRoomNo, gbc);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnAdd = new JButton("Add Student");
        btnAdd.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnAdd.setBackground(new Color(30, 144, 255));
        btnAdd.setForeground(Color.WHITE);

        JButton btnClear = new JButton("Clear");
        btnClear.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnClear.setBackground(new Color(220, 20, 60));
        btnClear.setForeground(Color.WHITE);
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnRefresh.setBackground(new Color(220, 20, 60));
        btnRefresh.setForeground(Color.WHITE);

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnClear);
        buttonPanel.add(btnRefresh);

        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        formPanel.add(buttonPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.NORTH);

        // --- Center Panel: Table ---

        model = new DefaultTableModel(new String[]{"ID", "Name", "Username", "Address", "Guardian Name", "Guardian No", "Hostel Fee", "Remaining Fee", "Hostel Status", "Room No"}, 0);
        table = new JTable(model);
        table.setFont(new Font("Tahoma", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 11));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder("View Students"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel: Edit/Delete and Check-in/Check-out Buttons ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        bottomPanel.setBackground(Color.WHITE);

        JButton btnEdit = new JButton("Edit Student");
        btnEdit.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnEdit.setBackground(new Color(255, 140, 0));
        btnEdit.setForeground(Color.WHITE);

        JButton btnDelete = new JButton("Delete Student");
        btnDelete.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnDelete.setBackground(new Color(220, 20, 60));
        btnDelete.setForeground(Color.WHITE);

        btnCheckIn = new JButton("Check-in");
        btnCheckIn.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnCheckIn.setBackground(new Color(0, 128, 0));
        btnCheckIn.setForeground(Color.WHITE);

        btnCheckOut = new JButton("Check-out");
        btnCheckOut.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnCheckOut.setBackground(new Color(178, 34, 34));
        btnCheckOut.setForeground(Color.WHITE);

        bottomPanel.add(btnEdit);
        bottomPanel.add(btnDelete);
        bottomPanel.add(btnCheckIn);
        bottomPanel.add(btnCheckOut);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // Initialize the roomNameToIdMap
        roomNameToIdMap = new HashMap<>();
        loadRoomData(); // Load room data from the database

        // Button Actions
        btnAdd.addActionListener(e -> addStudent());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> refreshAllData());
        btnEdit.addActionListener(e -> editStudent());
        btnDelete.addActionListener(e -> deleteStudent());
        btnCheckIn.addActionListener(e -> checkInStudent());
        btnCheckOut.addActionListener(e -> checkOutStudent());

        viewStudent();
        setVisible(true);
    }

    private void loadRoomData() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hostelms", "root", "");
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT room_id, room_no FROM room")) {

            while (rs.next()) {
                int roomId = rs.getInt("room_id");
                String roomNo = rs.getString("room_no");
                roomNameToIdMap.put(roomNo, roomId);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading room data: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Vector<String> loadRoomNumbers() {
        Vector<String> numbers = new Vector<>();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hostelms", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT room_no FROM room ORDER BY room_no ASC")) { //added order by
            while (rs.next()) {
                String roomNo = rs.getString("room_no");
                numbers.add(roomNo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading room numbers: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        return numbers;
    }

    // Helper for numeric validation (for decimal numbers like fees)
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            new java.math.BigDecimal(str); // Use BigDecimal for general numeric checks
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Helper for long integer validation (for Guardian No)
    private boolean isLong(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        // Check for digits only, no decimals or other characters
        if (!str.matches("\\d+")) { // Regex to check if string contains only digits
            return false;
        }
        try {
            // Try to parse as long to check if it fits within long range
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            // Number is too large for long, or other parsing error
            return false;
        }
    }


    void addStudent() {
        String name = txtName.getText().trim();
        String username = txtUsername.getText().trim();
        String address = txtAddress.getText().trim();
        String guardianName = txtGuardianName.getText().trim();
        String guardianNoStr = txtGuardianNo.getText().trim(); // Get as string
        String hostelFeeStr = txtHostelFee.getText().trim();
        String balanceStr = txtBalance.getText().trim();
        String roomNo = (String) comboRoomNo.getSelectedItem();

        // Input Validation
        if (name.isEmpty() || username.isEmpty() || hostelFeeStr.isEmpty() || balanceStr.isEmpty() || roomNo == null || roomNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all required fields (Name, Username, Hostel Fee, Remaining Fee, Room No).", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!isNumeric(hostelFeeStr)) {
            JOptionPane.showMessageDialog(this, "Hostel Fee must be a valid number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!isNumeric(balanceStr)) {
            JOptionPane.showMessageDialog(this, "Remaining Fee must be a valid number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // *** Specific validation for Guardian No as LONG ***
        Long guardianNo = null; // Use Long wrapper to allow null
        if (!guardianNoStr.isEmpty()) {
            if (!isLong(guardianNoStr)) { // Use new isLong method
                JOptionPane.showMessageDialog(this, "Guardian No must be a whole number (digits only).", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                guardianNo = Long.parseLong(guardianNoStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Guardian No is too large or invalid.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        // End Guardian No validation

        // Check if username already exists
        if (isUsernameTaken(username)) {
            JOptionPane.showMessageDialog(this, "Username already exists. Please choose a different username.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }


        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hostelms", "root", "");
             PreparedStatement pstInsert = con.prepareStatement("INSERT INTO student ( std_name, std_username, std_address, std_guardian_name, std_guardian_no, std_hostel_fee, std_balance, std_hostel_status, room_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            Integer roomId = roomNameToIdMap.get(roomNo);
            if (roomId != null) {
                pstInsert.setString(1, name);
                pstInsert.setString(2, username);
                pstInsert.setString(3, address);
                pstInsert.setString(4, guardianName);
                // *** Use setLong for Guardian No, and handle null ***
                if (guardianNo == null) {
                    pstInsert.setNull(5, java.sql.Types.BIGINT); // Set as NULL if empty and database allows
                } else {
                    pstInsert.setLong(5, guardianNo);
                }
                // ***
                pstInsert.setBigDecimal(6, new java.math.BigDecimal(hostelFeeStr));
                pstInsert.setBigDecimal(7, new java.math.BigDecimal(balanceStr));
                pstInsert.setString(8, "Residing");
                pstInsert.setInt(9, roomId);
                pstInsert.executeUpdate();
                JOptionPane.showMessageDialog(this, "Student Added Successfully!");
                clearForm();
                viewStudent();
            } else {
                JOptionPane.showMessageDialog(this, "Room No '" + roomNo + "' does not exist!", "Database Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Add Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isUsernameTaken(String username) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hostelms", "root", "");
             PreparedStatement pst = con.prepareStatement("SELECT COUNT(*) FROM student WHERE std_username = ?")) {
            pst.setString(1, username);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error checking username: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }


    void clearForm() {
        txtName.setText("");
        txtUsername.setText("");
        txtAddress.setText("");
        txtGuardianName.setText("");
        txtGuardianNo.setText("");
        txtHostelFee.setText("");
        txtBalance.setText("");
        comboRoomNo.setSelectedIndex(-1);
    }

    void viewStudent() {
        model.setRowCount(0);
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hostelms", "root", "");
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT s.std_id, s.std_name, s.std_username, s.std_address, s.std_guardian_name, s.std_guardian_no, s.std_hostel_fee, s.std_balance, s.std_hostel_status, r.room_no " +
                     "FROM student s JOIN room r ON s.room_id = r.room_id")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("std_id"),
                        rs.getString("std_name"),
                        rs.getString("std_username"),
                        rs.getString("std_address"),
                        rs.getString("std_guardian_name"),
                        // Retrieve as String to display exactly as stored (even if long, convert to string for display)
                        rs.getString("std_guardian_no"),
                        rs.getBigDecimal("std_hostel_fee"),
                        rs.getBigDecimal("std_balance"),
                        rs.getString("std_hostel_status"),
                        rs.getString("room_no")
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Load Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    void editStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int studentId = (int) model.getValueAt(selectedRow, 0);
            AdminUpdateStudent updateDialog = new AdminUpdateStudent(studentId);
            updateDialog.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    viewStudent();
                }
            });
            updateDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a student to edit!", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    void deleteStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int studentId = (int) model.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this student ?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            Connection con = null;
            try {
                con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hostelms", "root", "");
                con.setAutoCommit(false); // Start transaction

                // 1. Delete associated fee records for this student
                // This assumes your 'fee' table has a 'std_id' foreign key.
                PreparedStatement pstDeleteFees = con.prepareStatement("DELETE FROM fee WHERE std_id = ?");
                pstDeleteFees.setInt(1, studentId);
                pstDeleteFees.executeUpdate();
                pstDeleteFees.close();

                // 2. Delete associated hostel log records for this student
                // Assuming your 'hostel_log' table has a 'std_id' foreign key or stores student ID
                // The AdminHostelLogManager.logActivity seems to log it as a String, so adjust table/column name accordingly.
                // If hostel_log just stores string student ID, you might need to reconsider deletion logic.
                // Assuming `hostel_log` has a column `std_id` of INT type for simplicity:
                PreparedStatement pstDeleteLogs = con.prepareStatement("DELETE FROM hostel_log WHERE std_id = ?");
                pstDeleteLogs.setInt(1, studentId);
                pstDeleteLogs.executeUpdate();
                pstDeleteLogs.close();
                // If hostel_log stores student_id as String, you might need:
                // PreparedStatement pstDeleteLogs = con.prepareStatement("DELETE FROM hostel_log WHERE student_id_string_col = ?");
                // pstDeleteLogs.setString(1, String.valueOf(studentId));

                // 3. Delete the student record itself
                PreparedStatement pstDeleteStudent = con.prepareStatement("DELETE FROM student WHERE std_id = ?");
                pstDeleteStudent.setInt(1, studentId);
                pstDeleteStudent.executeUpdate();
                pstDeleteStudent.close();

                con.commit(); // Commit transaction if all successful
                JOptionPane.showMessageDialog(this, "Student deleted successfully!");
                viewStudent(); // Refresh the table

            } catch (SQLException ex) {
                try {
                    if (con != null) con.rollback(); // Rollback on error
                } catch (SQLException rollbackEx) {
                    rollbackEx.printStackTrace(); // Print rollback error
                }
                JOptionPane.showMessageDialog(this, "Delete Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    if (con != null) {
                        con.setAutoCommit(true); // Reset auto-commit
                        con.close(); // Close connection
                    }
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace(); // Print close error
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a student to delete!", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    void checkInStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int studentId = (int) model.getValueAt(selectedRow, 0);
            String currentStatus = (String) model.getValueAt(selectedRow, 8); // Assuming status is at index 8
            if ("Residing".equals(currentStatus)) {
                JOptionPane.showMessageDialog(this, "Student is already checked in.", "Action Not Required", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            updateStudentHostelStatus(studentId, "Residing");
            AdminHostelLogManager.logActivity(String.valueOf(studentId), "Check-in"); // Log activity using String ID
            viewStudent();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a student to check in.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    void checkOutStudent() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int studentId = (int) model.getValueAt(selectedRow, 0);
            String currentStatus = (String) model.getValueAt(selectedRow, 8); // Assuming status is at index 8
            if ("Left".equals(currentStatus)) {
                JOptionPane.showMessageDialog(this, "Student is already checked out.", "Action Not Required", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            updateStudentHostelStatus(studentId, "Left");
            AdminHostelLogManager.logActivity(String.valueOf(studentId), "Check-out"); // Log activity using String ID
            viewStudent();
        } else {
            JOptionPane.showMessageDialog(this, "Please select a student to check out.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateStudentHostelStatus(int studentId, String status) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/hostelms", "root", "");
             PreparedStatement pst = con.prepareStatement("UPDATE student SET std_hostel_status = ? WHERE std_id = ?")) {
            pst.setString(1, status);
            pst.setInt(2, studentId);
            pst.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error updating hostel status: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshAllData() {
        clearForm(); // Clear input fields
        loadRoomData(); // This loads the roomNameToIdMap

        // Correctly update the class-level 'roomNumbers' vector
        roomNumbers = loadRoomNumbers(); // Assuming loadRoomNumbers() returns a Vector<String>

        // Then, set the model for the JComboBox
        comboRoomNo.setModel(new DefaultComboBoxModel<>(roomNumbers));

        viewStudent(); // Reload student table data
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Admin Student Manager");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new AdminStudentManager());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}