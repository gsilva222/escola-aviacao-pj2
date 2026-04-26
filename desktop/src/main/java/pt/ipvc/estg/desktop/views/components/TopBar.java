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
    private static final Color INPUT_BG = new Color(241, 245, 249);
    private static final Color BLUE_PRIMARY = new Color(21, 101, 192);
    private static final Color HOVER_BG = new Color(248, 250, 252);

    private final JLabel pageTitleLabel;
    private final JLabel subtitleLabel;

    public TopBar() {
        setBackground(WHITE);
        setLayout(new BorderLayout(16, 0));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));
        setPreferredSize(new Dimension(Integer.MAX_VALUE, 74));
        setMinimumSize(new Dimension(900, 74));

        JPanel leftPanel = new JPanel();
        leftPanel.setOpaque(false);
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBorder(new EmptyBorder(10, 24, 10, 0));

        pageTitleLabel = new JLabel("Dashboard");
        pageTitleLabel.setFont(new Font("Inter", Font.BOLD, 17));
        pageTitleLabel.setForeground(DARK_TEXT);

        subtitleLabel = new JLabel("AeroSchool BackOffice - " + formatToday());
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        subtitleLabel.setForeground(MUTED_TEXT);

        leftPanel.add(pageTitleLabel);
        leftPanel.add(Box.createVerticalStrut(3));
        leftPanel.add(subtitleLabel);

        add(leftPanel, BorderLayout.WEST);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(0, 8, 0, 8));
        centerPanel.add(createSearchField(), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        add(createRightPanel(), BorderLayout.EAST);
    }

    public void setPageTitle(String title) {
        pageTitleLabel.setText(title);
        subtitleLabel.setText("AeroSchool BackOffice - " + formatToday());
    }

    private String formatToday() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", new Locale("pt", "PT"));
        return LocalDate.now().format(formatter);
    }

    private JComponent createSearchField() {
        JPanel wrapper = new JPanel(new BorderLayout(8, 0));
        wrapper.setBackground(INPUT_BG);
        wrapper.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                new EmptyBorder(7, 10, 7, 10)
        ));
        wrapper.setPreferredSize(new Dimension(360, 40));
        wrapper.setMinimumSize(new Dimension(240, 40));

        JLabel icon = new JLabel("\u2315");
        icon.setForeground(MUTED_TEXT);
        icon.setFont(new Font("Dialog", Font.PLAIN, 14));

        JTextField field = new JTextField("Pesquisar alunos, voos, aeronaves...");
        field.setBorder(null);
        field.setOpaque(false);
        field.setForeground(ICON_TEXT);
        field.setFont(new Font("Inter", Font.PLAIN, 12));

        wrapper.add(icon, BorderLayout.WEST);
        wrapper.add(field, BorderLayout.CENTER);
        return wrapper;
    }

    private JComponent createRightPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(0, 0, 0, 16));

        panel.add(createNotificationButton());
        panel.add(createProfileChip());
        return panel;
    }

    private JComponent createNotificationButton() {
        JPanel wrapper = new JPanel(null);
        wrapper.setOpaque(false);
        wrapper.setPreferredSize(new Dimension(36, 36));

        JButton bell = new JButton("\u25CF");
        bell.setBounds(0, 0, 36, 36);
        bell.setFocusPainted(false);
        bell.setBorderPainted(false);
        bell.setCursor(new Cursor(Cursor.HAND_CURSOR));
        bell.setBackground(HOVER_BG);
        bell.setFont(new Font("Dialog", Font.PLAIN, 13));
        bell.setForeground(ICON_TEXT);

        JPanel dot = new JPanel();
        dot.setBackground(new Color(239, 68, 68));
        dot.setBounds(23, 7, 8, 8);

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
        chip.setPreferredSize(new Dimension(176, 42));

        JLabel avatar = new JLabel("AD", SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(28, 28));
        avatar.setOpaque(true);
        avatar.setBackground(BLUE_PRIMARY);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Inter", Font.BOLD, 10));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel name = new JLabel("Admin. Geral");
        name.setForeground(DARK_TEXT);
        name.setFont(new Font("Inter", Font.BOLD, 11));
        JLabel role = new JLabel("Administrador");
        role.setForeground(MUTED_TEXT);
        role.setFont(new Font("Inter", Font.PLAIN, 10));
        text.add(name);
        text.add(role);

        JLabel chevron = new JLabel("\u25BE");
        chevron.setForeground(MUTED_TEXT);
        chevron.setFont(new Font("Dialog", Font.PLAIN, 11));

        chip.add(avatar, BorderLayout.WEST);
        chip.add(text, BorderLayout.CENTER);
        chip.add(chevron, BorderLayout.EAST);
        return chip;
    }
}
