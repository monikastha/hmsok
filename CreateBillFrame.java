package Admin;

import java.awt.Font;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;

import javax.swing.JOptionPane;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.File;

public class CreateBillFrame extends JFrame {

    // private JLabel lblSelectedStudentName; // Removed: Replaced by txtStudentName
    private JTextField txtStudentName; // New: JTextField for displaying student name
    private String selectedFeeId; // Store the FEE ID passed to the constructor
    private String currentStudentId; // Store the STUDENT ID retrieved from the fee record

    private JTextField txtBillNo;
    private JTextField txtBillDate;
    private JTextField txtRoomNo;
    private JTextField txtRoomFloorNo;
    private JTextField txtStdHostelFee;
    private JTextField txtFCredit;
    private JTextField txtFRemaining;
    private JButton btnCreateBill, btnDownloadReceiptPdf, btnBack;
    private JTextArea receiptTextArea;
    private JPanel formPanel;

    private AdminFeeManager parentFeeManager;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    // private static AtomicInteger billNumberCounter = new AtomicInteger(0); // Not used, can be removed

    // Colors
    private Color backgroundColor = new Color(89, 91, 94);
    private Color formPanelColor = Color.WHITE;
    private Color labelColor = Color.BLACK;
    private Color textFieldBackgroundColor = Color.WHITE;
    private Color buttonColor = new Color(0, 216, 92);
    private Color buttonTextColor = Color.GRAY;
    private Color errorColor = new Color(255, 69, 0);
    private Color titleColor = new Color(0, 128, 128);

    // Fonts
    private Font labelFont = new Font("Tahoma", Font.PLAIN, 16);
    private Font textFont = new Font("Tahoma", Font.PLAIN, 16);
    private Font buttonFont = new Font("Tahoma", Font.BOLD, 16);
    private Font titleFont = new Font("Tahoma", Font.BOLD, 18);
    private Font receiptFont = new Font("Monospaced", Font.PLAIN, 12);

    // iTextPDF Fonts
    private static com.itextpdf.text.Font pdfTitleFont;
    private static com.itextpdf.text.Font pdfRegularFont;

    // Modified constructor to accept feeId
    public CreateBillFrame(AdminFeeManager parent, String feeId) {
        this.parentFeeManager = parent;
        this.selectedFeeId = feeId; // Store the FEE ID

        // Initialize iTextPDF fonts
        try {
            BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            pdfTitleFont = new com.itextpdf.text.Font(baseFont, 18, com.itextpdf.text.Font.BOLD);
            BaseFont regularBaseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
            pdfRegularFont = new com.itextpdf.text.Font(regularBaseFont, 12);
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading fonts for PDF generation. PDF download may not work correctly.", "Font Error", JOptionPane.ERROR_MESSAGE);
        }

        setTitle("Create New Hostel Bill");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(backgroundColor);
        add(mainPanel);

        formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(formPanelColor);

        addFormComponents();

        mainPanel.add(formPanel, BorderLayout.CENTER);

        receiptTextArea = new JTextArea();
        receiptTextArea.setFont(receiptFont);
        receiptTextArea.setEditable(false);
        JScrollPane receiptScrollPane = new JScrollPane(receiptTextArea);
        receiptScrollPane.setPreferredSize(new Dimension(350, 250));
        mainPanel.add(receiptScrollPane, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(backgroundColor);

        btnCreateBill = new JButton("Create Bill");
        btnCreateBill.setFont(buttonFont);
        btnCreateBill.setBackground(buttonColor);
        btnCreateBill.setForeground(buttonTextColor);

        btnDownloadReceiptPdf = new JButton("Download Receipt as PDF");
        btnDownloadReceiptPdf.setFont(buttonFont);
        btnDownloadReceiptPdf.setBackground(buttonColor);
        btnDownloadReceiptPdf.setForeground(buttonTextColor);

        btnBack = new JButton("Back");
        btnBack.setFont(buttonFont);
        btnBack.setBackground(buttonColor);
        btnBack.setForeground(buttonTextColor);

        buttonPanel.add(btnBack);
        buttonPanel.add(btnCreateBill);
        buttonPanel.add(btnDownloadReceiptPdf);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        getNextBillNumber();
        txtFCredit.addActionListener(e -> calculateRemainingAmount());
        btnCreateBill.addActionListener(e -> {
            if (createBill()) {
                generateReceipt();
            }
        });
        btnDownloadReceiptPdf.addActionListener(e -> downloadReceiptAsPdf());
        btnBack.addActionListener(e -> dispose());

        // Load details for the pre-selected fee ID
        if (selectedFeeId != null && !selectedFeeId.isEmpty()) {
            loadStudentDetails(selectedFeeId); // Now passing feeId
        } else {
            JOptionPane.showMessageDialog(this, "No fee ID provided to create bill.", "Error", JOptionPane.ERROR_MESSAGE);
            clearDisplayFields();
        }

        setVisible(true);
    }

    private void addFormComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridwidth = 1;

        JLabel lblStudentName = createLabel("Student Name:");
        // Replaced lblSelectedStudentName with txtStudentName
        txtStudentName = createTextField(false); // Student name should not be editable by user

        JLabel lblBillNo = createLabel("Bill Number:");
        JLabel lblBillDate = createLabel("Bill Date (YYYY-MM-DD):");
        JLabel lblRoomNo = createLabel("Room No:");
        JLabel lblRoomFloorNo = createLabel("Room Floor No:");
        JLabel lblStdHostelFee = createLabel("Hostel Fee (Rs):");
        JLabel lblFCredit = createLabel("Paid Amount (Rs):");
        JLabel lblFRemaining = createLabel("Remaining (Rs):");

        txtBillNo = createTextField(false); // Bill number should not be editable by user
        txtBillDate = createTextField();
        txtRoomNo = createTextField(false);
        txtRoomFloorNo = createTextField(false);
        txtStdHostelFee = createTextField(false);
        txtFCredit = createTextField(true); // Paid amount *should* be editable for input
        txtFRemaining = createTextField(false);

        addComponent(formPanel, lblStudentName, 0, 0, gbc);
        addComponent(formPanel ,  txtStudentName, 1, 0, gbc); // Add txtStudentName here

        addComponent(formPanel, lblBillNo, 0, 1, gbc);
        addComponent(formPanel, txtBillNo, 1, 1, gbc);

        addComponent(formPanel, lblBillDate, 0, 2, gbc);
        addComponent(formPanel, txtBillDate, 1, 2, gbc);

        addComponent(formPanel, lblRoomNo, 0, 3, gbc);
        addComponent(formPanel, txtRoomNo, 1, 3, gbc);

        addComponent(formPanel, lblRoomFloorNo, 0, 4, gbc);
        addComponent(formPanel, txtRoomFloorNo, 1, 4, gbc);

        addComponent(formPanel, lblStdHostelFee, 0, 5, gbc);
        addComponent(formPanel, txtStdHostelFee, 1, 5, gbc);

        addComponent(formPanel, lblFCredit, 0, 6, gbc);
        addComponent(formPanel, txtFCredit, 1, 6, gbc);

        addComponent(formPanel, lblFRemaining, 0, 7, gbc);
        addComponent(formPanel, txtFRemaining, 1, 7, gbc);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(labelFont);
        label.setForeground(labelColor);
        return label;
    }

    private JTextField createTextField() {
        JTextField textField = new JTextField();
        textField.setFont(textFont);
        textField.setBackground(textFieldBackgroundColor);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                new EmptyBorder(5, 10, 5, 10)));
        return textField;
    }

    private JTextField createTextField(boolean editable) {
        JTextField textField = new JTextField();
        textField.setFont(textFont);
        textField.setEditable(editable);
        textField.setBackground(editable ? textFieldBackgroundColor : Color.LIGHT_GRAY);
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                new EmptyBorder(5, 10, 5, 10)));
        return textField;
    }

    private void addComponent(JPanel panel, Component component, int gridx, int gridy, GridBagConstraints gbc) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        panel.add(component, gbc);
    }

    private Connection getDatabaseConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/hostelms", "root", "");
    }

    // Modified to load student details based on feeId
    private void loadStudentDetails(String feeId) {
        if (feeId != null && !feeId.isEmpty()) {
            try (Connection conn = getDatabaseConnection()) {
                // Query to fetch student, room, and fee details using fee_id
                String query = "SELECT s.std_id, s.std_name, r.room_no, r.room_floor_no, s.std_hostel_fee, f.f_credit, f.f_remaining "
                        + "FROM fee f "
                        + "JOIN student s ON f.std_id = s.std_id "
                        + "LEFT JOIN room r ON s.room_id = r.room_id " // Use LEFT JOIN in case room_id is NULL
                        + "WHERE f.f_id = ?";
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, feeId); // Set the feeId
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    this.currentStudentId = rs.getString("std_id"); // Store student ID
                    String studentName = rs.getString("std_name");
                    String roomNo = rs.getString("room_no");
                    String roomFloorNo = rs.getString("room_floor_no");
                    float stdHostelFee = rs.getFloat("std_hostel_fee");
                    float fCredit = rs.getFloat("f_credit");
                    float fRemaining = rs.getFloat("f_remaining"); // Also fetch remaining from fee table

                    txtStudentName.setText(studentName); // Set the student name JTextField
                    txtRoomNo.setText(roomNo != null ? roomNo : "N/A"); // Handle potential null room data
                    txtRoomFloorNo.setText(roomFloorNo != null ? roomFloorNo : "N/A");
                    txtStdHostelFee.setText(String.valueOf(stdHostelFee));
                    txtFCredit.setText(String.valueOf(fCredit));
                    txtFRemaining.setText(String.valueOf(fRemaining)); // Set remaining directly from DB
                    // No need to call calculateRemainingAmount() here if f_remaining is fetched directly
                } else {
                    JOptionPane.showMessageDialog(this, "Fee record or associated student/room details not found for Fee ID: " + feeId, "Error", JOptionPane.ERROR_MESSAGE);
                    clearDisplayFields();
                    txtStudentName.setText("Not Found"); // Update JTextField
                    this.currentStudentId = null; // Clear student ID if not found
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Error loading details for Fee ID: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                clearDisplayFields();
                txtStudentName.setText("Error Loading"); // Update JTextField
                this.currentStudentId = null; // Clear student ID on error
                ex.printStackTrace();
            }
        } else {
            clearDisplayFields();
            txtStudentName.setText("No Fee Selected"); // Update JTextField
            this.currentStudentId = null; // Clear student ID if no fee selected
        }
    }

    private void calculateRemainingAmount() {
        try {
            float hostelFee = Float.parseFloat(txtStdHostelFee.getText().isEmpty() ? "0" : txtStdHostelFee.getText());
            float paidAmount = Float.parseFloat(txtFCredit.getText().isEmpty() ? "0" : txtFCredit.getText());
            float remainingAmount = hostelFee - paidAmount;
            txtFRemaining.setText(String.valueOf(remainingAmount));
        } catch (NumberFormatException e) {
            txtFRemaining.setText("Error");
        }
    }

    private void clearDisplayFields() {
        txtRoomNo.setText("");
        txtRoomFloorNo.setText("");
        txtStdHostelFee.setText("");
        txtFCredit.setText("");
        txtFRemaining.setText("");
    }

    private boolean createBill() {
        String studentName = txtStudentName.getText(); // Get name from JTextField
        String billNo = txtBillNo.getText();
        String billDateStr = txtBillDate.getText();
        String paidAmountStr = txtFCredit.getText().trim();

        // Use the stored currentStudentId and selectedFeeId
        if (currentStudentId == null || currentStudentId.isEmpty() || selectedFeeId == null || selectedFeeId.isEmpty() || billNo.isEmpty() || billDateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please ensure a student's fee record is selected and all essential fields are filled.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!billDateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Please use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            java.util.Date billDate = dateFormat.parse(billDateStr);
            java.sql.Date sqlBillDate = new java.sql.Date(billDate.getTime());
            float paidAmount = Float.parseFloat(paidAmountStr.isEmpty() ? "0" : paidAmountStr);

            try (Connection conn = getDatabaseConnection()) {
                // Check if bill number already exists
                String checkQuery = "SELECT bill_no FROM bill WHERE bill_no = ?";
                PreparedStatement checkPs = conn.prepareStatement(checkQuery);
                checkPs.setString(1, billNo);
                ResultSet checkRs = checkPs.executeQuery();
                if (checkRs.next()) {
                    JOptionPane.showMessageDialog(this, "Bill number already exists. Generating a new one.", "Error", JOptionPane.ERROR_MESSAGE);
                    getNextBillNumber();
                    return false;
                }

                // Insert into bill table
                String insertBillQuery = "INSERT INTO bill (bill_no, bill_date, std_id, f_id, created_at, updated_at) "
                        + "VALUES (?, ?, ?, ?, NOW(), NOW())";
                PreparedStatement insertBillPs = conn.prepareStatement(insertBillQuery);
                insertBillPs.setString(1, billNo);
                insertBillPs.setDate(2, sqlBillDate);
                insertBillPs.setString(3, currentStudentId); // Use currentStudentId
                insertBillPs.setString(4, selectedFeeId); // Use selectedFeeId directly

                int billResult = insertBillPs.executeUpdate();

                if (billResult > 0) {
                    // Update fee table
                    String updateFeeQuery = "UPDATE fee SET f_credit = ?, f_remaining = ? WHERE f_id = ?"; // Update by f_id
                    PreparedStatement updateFeePs = conn.prepareStatement(updateFeeQuery);
                    updateFeePs.setFloat(1, paidAmount);
                    updateFeePs.setFloat(2, Float.parseFloat(txtFRemaining.getText()));
                    updateFeePs.setString(3, selectedFeeId); // Use selectedFeeId for update
                    int feeResult = updateFeePs.executeUpdate();

                    if (feeResult >= 0) {
                        JOptionPane.showMessageDialog(this, "Bill created and fee details updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                        return true;
                    } else {
                        JOptionPane.showMessageDialog(this, "Bill created, but failed to update fee details.", "Warning", JOptionPane.WARNING_MESSAGE);
                        return false;
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create bill.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        } catch (ParseException | SQLException | NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    private void clearBillForm() {
        txtBillNo.setText("");
        txtBillDate.setText("");
        txtStudentName.setText("No Fee Selected"); // Reset student name JTextField
        clearDisplayFields();
        getNextBillNumber();
        this.currentStudentId = null; // Clear stored student ID
        this.selectedFeeId = null; // Clear stored fee ID
    }

    private void getNextBillNumber() {
        try (Connection conn = getDatabaseConnection()) {
            String query = "SELECT bill_no FROM bill ORDER BY bill_no DESC LIMIT 1";
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String lastBillNo = rs.getString("bill_no");
                String numericPart = lastBillNo.replaceAll("[^0-9]", "");
                int billNumber = 0;
                if (!numericPart.isEmpty()) {
                    billNumber = Integer.parseInt(numericPart);
                }
                billNumber++;

                String newBillNo = String.format("HS-%03d", billNumber);
                txtBillNo.setText(newBillNo);
            } else {
                txtBillNo.setText("HS-001");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error getting next bill number: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            txtBillNo.setText("HS-001");
            e.printStackTrace();
        }
    }

    private void generateReceipt() {
        String studentName = txtStudentName.getText(); // Get from JTextField
        String billNo = txtBillNo.getText();
        String billDate = txtBillDate.getText();
        String roomNo = txtRoomNo.getText();
        String roomFloorNo = txtRoomFloorNo.getText();
        String stdHostelFee = txtStdHostelFee.getText();
        String fCredit = txtFCredit.getText();
        String fRemaining = txtFRemaining.getText();

        StringBuilder receipt = new StringBuilder();
        receipt.append("\n======================== Hostel Bill Receipt =======================\n");
        receipt.append("  Bill Number             : ").append(String.format("%-10s", billNo)).append("\n");
        receipt.append("  Bill Date               : ").append(String.format("%-10s", billDate)).append("\n");
        receipt.append("  Student Name            : ").append(String.format("%-10s", studentName)).append("\n");
        receipt.append("  Room No                 : ").append(String.format("%-10s", roomNo)).append("\n");
        receipt.append("  Room Floor No           : ").append(String.format("%-10s", roomFloorNo)).append("\n");
        receipt.append("  Hostel Fee (Rs)         : ").append(String.format("%-10s", stdHostelFee)).append("\n");
        receipt.append("  Paid Amount (Rs)        : ").append(String.format("%-10s", fCredit)).append("\n");
        receipt.append("  Remaining Amount (Rs)   : ").append(String.format("%-10s", fRemaining)).append("\n");
        receipt.append("===================================================================\n");

        receiptTextArea.setText(receipt.toString());
    }

    private void downloadReceiptAsPdf() {
        String studentName = txtStudentName.getText(); // Get from JTextField
        String billNo = txtBillNo.getText();
        String billDate = txtBillDate.getText();
        String roomNo = txtRoomNo.getText();
        String roomFloorNo = txtRoomFloorNo.getText();
        String stdHostelFee = txtStdHostelFee.getText();
        String fCredit = txtFCredit.getText();
        String fRemaining = txtFRemaining.getText();

        if (currentStudentId == null || currentStudentId.isEmpty() || selectedFeeId == null || selectedFeeId.isEmpty() || billNo.isEmpty() || billDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please ensure a student's fee record is selected and essential bill fields are filled before downloading.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Receipt as PDF");

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }

            Document document = new Document();
            try {
                PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();

                // Add metadata (optional)
                document.addTitle("Hostel Bill Receipt");
                document.addSubject("Hostel Fee Bill");
                document.addAuthor("Hostel Management System");

                // Add content
                Paragraph title = new Paragraph("Hostel Bill Receipt\n\n", pdfTitleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);

                Paragraph content = new Paragraph();
                content.add(new Chunk("Bill Number: ", pdfRegularFont));
                content.add(new Chunk(billNo + "\n", pdfRegularFont));
                content.add(new Chunk("Bill Date: ", pdfRegularFont));
                content.add(new Chunk(billDate + "\n", pdfRegularFont));
                content.add(new Chunk("Student Name: ", pdfRegularFont));
                content.add(new Chunk(studentName + "\n", pdfRegularFont));
                content.add(new Chunk("Room No: ", pdfRegularFont));
                content.add(new Chunk(roomNo + "\n", pdfRegularFont));
                content.add(new Chunk("Room Floor No: ", pdfRegularFont));
                content.add(new Chunk(roomFloorNo + "\n", pdfRegularFont));
                content.add(new Chunk("Hostel Fee (Rs): ", pdfRegularFont));
                content.add(new Chunk(stdHostelFee + "\n", pdfRegularFont));
                content.add(new Chunk("Paid Amount (Rs): ", pdfRegularFont));
                content.add(new Chunk(fCredit + "\n", pdfRegularFont));
                content.add(new Chunk("Remaining Amount (Rs): ", pdfRegularFont));
                content.add(new Chunk(fRemaining + "\n\n", pdfRegularFont));
                content.setAlignment(Element.ALIGN_LEFT);
                document.add(content);

                Paragraph footer = new Paragraph("===================================================================", pdfRegularFont);
                footer.setAlignment(Element.ALIGN_CENTER);
                document.add(footer);

                document.close();
                JOptionPane.showMessageDialog(this, "Receipt saved as PDF successfully to " + filePath, "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (DocumentException | IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving receipt as PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                AdminFeeManager parent = new AdminFeeManager(); // This parent might need a mock if not fully functional
                // For testing, provide a valid FEE ID from your database
                // You'll need to find an f_id that exists in your 'fee' table
                // and is linked to a student and optionally a room.
                String testFeeId = "1"; // Replace with an actual f_id from your 'fee' table
                CreateBillFrame frame = new CreateBillFrame(parent, testFeeId);
                frame.setVisible(true);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException |
                     UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
        });
    }
}
