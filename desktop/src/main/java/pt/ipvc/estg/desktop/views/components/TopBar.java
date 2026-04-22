package pt.ipvc.estg.desktop.views.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TopBar extends JPanel {
    private static final Color LIGHT_BG = new Color(240, 244, 248);    // #F0F4F8
    private static final Color WHITE = Color.WHITE;
    private static final Color GRAY_TEXT = new Color(100, 116, 139);   // #64748B
    private static final Color BORDER_COLOR = new Color(226, 232, 240); // #E2E8F0
    private static final Color BLUE_PRIMARY = new Color(21, 101, 192); // #1565C0

    private JLabel pageTitle;

    public TopBar() {
        setBackground(WHITE);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        setPreferredSize(new Dimension(Integer.MAX_VALUE, 70));

        // Left section - Page title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        leftPanel.setOpaque(false);
        leftPanel.setBorder(new EmptyBorder(0, 30, 0, 0));

        pageTitle = new JLabel("Dashboard");
        pageTitle.setFont(new Font("Inter", Font.BOLD, 24));
        pageTitle.setForeground(new Color(15, 35, 68)); // Dark
        leftPanel.add(pageTitle);

        add(leftPanel, BorderLayout.WEST);

        // Right section - Search, notifications, profile
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(0, 0, 0, 20));

        // Search input
        JTextField searchField = new JTextField("Buscar...");
        searchField.setFont(new Font("Inter", Font.PLAIN, 12));
        searchField.setBackground(new Color(248, 250, 252)); // #F8FAFC
        searchField.setForeground(GRAY_TEXT);
        searchField.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        searchField.setPreferredSize(new Dimension(300, 35));
        rightPanel.add(searchField);

        // Notifications button
        JButton notificationsBtn = new JButton("🔔");
        notificationsBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        notificationsBtn.setBackground(new Color(0, 0, 0, 0));
        notificationsBtn.setBorderPainted(false);
        notificationsBtn.setFocusPainted(false);
        notificationsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        notificationsBtn.setPreferredSize(new Dimension(40, 40));
        rightPanel.add(notificationsBtn);

        // Profile button
        JButton profileBtn = new JButton("👤");
        profileBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        profileBtn.setBackground(new Color(0, 0, 0, 0));
        profileBtn.setBorderPainted(false);
        profileBtn.setFocusPainted(false);
        profileBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profileBtn.setPreferredSize(new Dimension(40, 40));
        rightPanel.add(profileBtn);

        // Settings button
        JButton settingsBtn = new JButton("⚙");
        settingsBtn.setFont(new Font("Arial", Font.PLAIN, 14));
        settingsBtn.setBackground(new Color(0, 0, 0, 0));
        settingsBtn.setBorderPainted(false);
        settingsBtn.setFocusPainted(false);
        settingsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settingsBtn.setPreferredSize(new Dimension(40, 40));
        rightPanel.add(settingsBtn);

        add(rightPanel, BorderLayout.EAST);
    }

    public void setPageTitle(String title) {
        pageTitle.setText(title);
        repaint();
    }
}
