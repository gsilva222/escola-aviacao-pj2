package pt.ipvc.estg.desktop.views.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class TopBar extends JPanel {

    private static final Color WHITE = Color.WHITE;
    private static final Color DARK_TEXT = new Color(15, 35, 68);
    private static final Color MUTED_TEXT = new Color(148, 163, 184);
    private static final Color ICON_TEXT = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color BLUE_PRIMARY = new Color(21, 101, 192);
    private static final Color HOVER_BG = new Color(248, 250, 252);

    private final JLabel pageTitleLabel;
    private final JLabel subtitleLabel;
    private JLabel avatarLabel;
    private JLabel profileNameLabel;
    private JLabel profileRoleLabel;

    public TopBar() {
        this("Admin. Geral", "Administrador", null);
    }

    public TopBar(String displayName, String roleLabel, Runnable onLogout) {
        setBackground(WHITE);
        setLayout(new BorderLayout(16, 0));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        setPreferredSize(new Dimension(Integer.MAX_VALUE, 56));
        setMinimumSize(new Dimension(900, 56));

        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(new EmptyBorder(7, 18, 7, 0));

        pageTitleLabel = new JLabel("Dashboard");
        pageTitleLabel.setFont(new Font("Inter", Font.BOLD, 13));
        pageTitleLabel.setForeground(DARK_TEXT);

        subtitleLabel = new JLabel("AeroSchool BackOffice - " + formatToday());
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 9));
        subtitleLabel.setForeground(MUTED_TEXT);

        leftPanel.add(pageTitleLabel);
        leftPanel.add(Box.createVerticalStrut(3));
        leftPanel.add(subtitleLabel);

        add(leftPanel, BorderLayout.WEST);

        add(createRightPanel(onLogout), BorderLayout.EAST);
        setUserInfo(displayName, roleLabel);
    }

    public void setUserInfo(String displayName, String roleLabel) {
        String name = displayName != null && !displayName.isBlank() ? displayName : "Utilizador";
        String role = roleLabel != null && !roleLabel.isBlank() ? roleLabel : "Administrador";
        profileNameLabel.setText(name.length() > 18 ? name.substring(0, 18) : name);
        profileRoleLabel.setText(role.length() > 22 ? role.substring(0, 22) : role);
        avatarLabel.setText(initials(name));
    }

    private static String initials(String name) {
        if (name == null || name.isBlank()) {
            return "US";
        }
        String trimmed = name.trim();
        if (trimmed.length() >= 2) {
            return trimmed.substring(0, 2).toUpperCase();
        }
        return trimmed.toUpperCase();
    }

    public void setPageTitle(String title) {
        pageTitleLabel.setText(title);
        subtitleLabel.setText("AeroSchool BackOffice - " + formatToday());
    }

    private String formatToday() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("pt", "PT"));
        return LocalDate.now().format(formatter);
    }

    private JComponent createRightPanel(Runnable onLogout) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 0, 16));

        panel.add(createNotificationButton());
        panel.add(createProfileChip());
        if (onLogout != null) {
            panel.add(createLogoutButton(onLogout));
        }
        return panel;
    }

    private JComponent createLogoutButton(Runnable onLogout) {
        JButton logoutBtn = new JButton("Sair");
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setFont(new Font("Inter", Font.PLAIN, 11));
        logoutBtn.setForeground(new Color(239, 68, 68));
        logoutBtn.setBackground(WHITE);
        logoutBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(6, 12, 6, 12)
        ));
        logoutBtn.addActionListener(e -> onLogout.run());
        return logoutBtn;
    }

    private JComponent createNotificationButton() {
        JPanel wrapper = new JPanel(null);
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(30, 30));

        JButton bell = new JButton("\u25CF");
        bell.setBounds(0, 0, 30, 30);
        bell.setFocusPainted(false);
        bell.setBorderPainted(false);
        bell.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bell.setBackground(HOVER_BG);
        bell.setFont(new Font("Dialog", Font.PLAIN, 13));
        bell.setForeground(ICON_TEXT);

        JPanel dot = new JPanel();
        dot.setBackground(new Color(239, 68, 68));
        dot.setBounds(21, 5, 7, 7);

        wrapper.add(bell);
        wrapper.add(dot);
        return wrapper;
    }

    private JComponent createProfileChip() {
        JPanel chip = new JPanel(new BorderLayout(8, 0));
        chip.setOpaque(true);
        chip.setBackground(WHITE);
        chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 245, 249), 1),
                new EmptyBorder(6, 8, 6, 8)
        ));
        chip.setPreferredSize(new Dimension(160, 38));

        avatarLabel = new JLabel("AD", SwingConstants.CENTER);
        avatarLabel.setPreferredSize(new Dimension(26, 26));
        avatarLabel.setOpaque(true);
        avatarLabel.setBackground(BLUE_PRIMARY);
        avatarLabel.setForeground(Color.WHITE);
        avatarLabel.setFont(new Font("Inter", Font.BOLD, 10));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        profileNameLabel = new JLabel("Admin. Geral");
        profileNameLabel.setForeground(DARK_TEXT);
        profileNameLabel.setFont(new Font("Inter", Font.BOLD, 10));
        profileRoleLabel = new JLabel("Administrador");
        profileRoleLabel.setForeground(MUTED_TEXT);
        profileRoleLabel.setFont(new Font("Inter", Font.PLAIN, 9));
        text.add(profileNameLabel);
        text.add(profileRoleLabel);

        JLabel chevron = new JLabel("\u25BE");
        chevron.setForeground(MUTED_TEXT);
        chevron.setFont(new Font("Dialog", Font.PLAIN, 11));

        chip.add(avatarLabel, BorderLayout.WEST);
        chip.add(text, BorderLayout.CENTER);
        chip.add(chevron, BorderLayout.EAST);
        return chip;
    }
}
