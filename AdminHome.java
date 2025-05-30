package Admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class AdminHome extends JPanel {

    private JLabel roomsCountLabel;
    private JLabel studentsCountLabel;
    private JLabel refreshLogo;

    public AdminHome() {
        setLayout(new BorderLayout());
        setBackground(new Color(235, 245, 255)); // Light background

        // -------- TOP PANEL --------
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(235, 245, 255));
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10)); // Padding around

        // === Left Panel for the count boxes aligned left ===
        JPanel leftStatsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        leftStatsPanel.setBackground(new Color(235, 245, 255));

        leftStatsPanel.add(createCountPanel("Total Rooms", new Color(100, 180, 250), new Color(50, 130, 200), true));
        leftStatsPanel.add(createCountPanel("Total Students", new Color(120, 220, 120), new Color(60, 180, 60), false));

        topPanel.add(leftStatsPanel, BorderLayout.WEST);

        // === Top-Right: Refresh Icon ===
        ImageIcon rawIcon = new ImageIcon("lib/refresh.png");
        Image img = rawIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(img);

        refreshLogo = new JLabel(scaledIcon);
        refreshLogo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshLogo.setToolTipText("Click to refresh data");
        refreshLogo.setBorder(new EmptyBorder(10, 10, 10, 10));
        refreshLogo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                refreshData();
            }
        });

        JPanel topRightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        topRightPanel.setBackground(new Color(235, 245, 255));
        topRightPanel.add(refreshLogo);

        topPanel.add(topRightPanel, BorderLayout.EAST);

        // -------- CENTER PANEL --------
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(235, 245, 255));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel welcomeLabel = new JLabel("Welcome to the Admin Dashboard!");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(25, 25, 112));
        centerPanel.add(welcomeLabel, gbc);

        gbc.gridy++;

        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);

        refreshData();
    }

    private JPanel createCountPanel(String title, Color startColor, Color endColor, boolean isRoomPanel) {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, startColor, 0, h, endColor);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, w, h, 20, 20);
            }
        };

        panel.setPreferredSize(new Dimension(200, 100));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(180, 180, 180), 1, true),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, gbc);

        gbc.gridy++;
        JLabel countLabel = new JLabel("...", SwingConstants.CENTER);
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        countLabel.setForeground(Color.WHITE);
        panel.add(countLabel, gbc);

        if (isRoomPanel) {
            this.roomsCountLabel = countLabel;
        } else {
            this.studentsCountLabel = countLabel;
        }

        return panel;
    }

    private void refreshData() {
        roomsCountLabel.setText(String.valueOf(getTotalRooms()));
        studentsCountLabel.setText(String.valueOf(getTotalStudents()));
    }

    private Connection getDatabaseConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/hostelms", "root", "");
    }

    private int getTotalRooms() {
        try (Connection con = getDatabaseConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total_rooms FROM room")) {
            if (rs.next()) {
                return rs.getInt("total_rooms");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching total rooms: " + ex.getMessage());
        }
        return 0;
    }

    private int getTotalStudents() {
        try (Connection con = getDatabaseConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS total_students FROM student")) {
            if (rs.next()) {
                return rs.getInt("total_students");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error fetching total students: " + ex.getMessage());
        }
        return 0;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            JFrame frame = new JFrame("Admin Home");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);
            frame.setContentPane(new AdminHome());
            frame.setVisible(true);
        });
    }
}
