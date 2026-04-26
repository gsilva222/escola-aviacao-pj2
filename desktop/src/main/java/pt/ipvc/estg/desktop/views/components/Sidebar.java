package pt.ipvc.estg.desktop.views.components;

import pt.ipvc.estg.desktop.views.LoginFrame;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class Sidebar extends JPanel {

    private static final Color DARK_BG = new Color(15, 35, 68);      // #0F2344
    private static final Color ACTIVE_BG = new Color(30, 77, 140);   // #1E4D8C
    private static final Color HOVER_BG = new Color(23, 52, 95);
    private static final Color LIGHT_TEXT = new Color(147, 169, 200); // #93A9C8
    private static final Color BLUE_PRIMARY = new Color(21, 101, 192); // #1565C0
    private static final Color DIVIDER = new Color(255, 255, 255, 18);

    public interface NavigationListener {
        void navigateTo(String page);
    }

    private final NavigationListener listener;
    private final Map<String, NavButton> navButtons = new LinkedHashMap<>();
    private String activePage = "dashboard";

    public Sidebar(NavigationListener listener) {
        this.listener = listener;

        setLayout(new BorderLayout());
        setBackground(DARK_BG);
        setPreferredSize(new Dimension(220, Integer.MAX_VALUE));
        setMinimumSize(new Dimension(220, 0));
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, DIVIDER));

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createNavPanel(), BorderLayout.CENTER);
        add(createFooterPanel(), BorderLayout.SOUTH);

        setActivePage("dashboard");
    }

    public void setActivePage(String page) {
        this.activePage = page;
        navButtons.forEach((key, btn) -> btn.setActive(key.equals(page)));
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(9, 0));
        panel.setBackground(DARK_BG);
        panel.setBorder(new EmptyBorder(18, 16, 18, 16));

        JPanel logoBox = new JPanel(new GridBagLayout());
        logoBox.setPreferredSize(new Dimension(30, 30));
        logoBox.setBackground(BLUE_PRIMARY);
        logoBox.setBorder(new EmptyBorder(0, 0, 0, 0));
        JLabel plane = new JLabel("\u2708");
        plane.setForeground(Color.WHITE);
        plane.setFont(new Font("Dialog", Font.PLAIN, 13));
        logoBox.add(plane);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel brand = new JLabel("AeroSchool");
        brand.setFont(new Font("Inter", Font.BOLD, 13));
        brand.setForeground(Color.WHITE);
        JLabel sub = new JLabel("BACKOFFICE");
        sub.setFont(new Font("Inter", Font.BOLD, 8));
        sub.setForeground(new Color(96, 165, 250));
        text.add(brand);
        text.add(sub);

        panel.add(logoBox, BorderLayout.WEST);
        panel.add(text, BorderLayout.CENTER);

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER),
                new EmptyBorder(12, 14, 12, 14)
        ));
        return panel;
    }

    private JPanel createNavPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(DARK_BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(14, 8, 12, 8));

        JLabel navLabel = new JLabel("NAVEGACAO");
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        navLabel.setBorder(new EmptyBorder(0, 10, 8, 0));
        navLabel.setForeground(new Color(74, 111, 165));
        navLabel.setFont(new Font("Inter", Font.BOLD, 8));
        panel.add(navLabel);

        addNavButton(panel, "dashboard", "Dashboard", "\u2318");
        addNavButton(panel, "students", "Alunos", "\u25CE");
        addNavButton(panel, "courses", "Cursos", "\u25A7");
        addNavButton(panel, "flights", "Agendamento de Voos", "\u2708");
        addNavButton(panel, "aircraft", "Aeronaves", "\u25CC");
        addNavButton(panel, "maintenance", "Manutencao", "\u2699");
        addNavButton(panel, "evaluations", "Avaliacoes", "\u25A4");
        addNavButton(panel, "payments", "Pagamentos", "\u25AC");
        addNavButton(panel, "reports", "Relatorios", "\u25E7");

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private void addNavButton(JPanel panel, String page, String label, String shortIcon) {
        NavButton button = new NavButton(page, label, shortIcon);
        navButtons.put(page, button);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(button);
        panel.add(Box.createVerticalStrut(4));
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, DIVIDER),
                new EmptyBorder(12, 10, 12, 10)
        ));

        JPanel userCard = new JPanel(new BorderLayout(10, 0));
        userCard.setBackground(new Color(255, 255, 255, 12));
        userCard.setBorder(new EmptyBorder(10, 10, 10, 10));
        userCard.setPreferredSize(new Dimension(200, 48));

        JLabel avatar = new JLabel("AD", SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(28, 28));
        avatar.setOpaque(true);
        avatar.setBackground(BLUE_PRIMARY);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Inter", Font.BOLD, 11));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel name = new JLabel("Admin. Geral");
        name.setForeground(Color.WHITE);
        name.setFont(new Font("Inter", Font.BOLD, 10));
        JLabel role = new JLabel("Administrador");
        role.setForeground(new Color(96, 165, 250));
        role.setFont(new Font("Inter", Font.PLAIN, 9));
        text.add(name);
        text.add(role);

        JButton logoutBtn = new JButton("Sair");
        logoutBtn.setFont(new Font("Inter", Font.PLAIN, 11));
        logoutBtn.setForeground(new Color(96, 165, 250));
        logoutBtn.setBackground(new Color(0, 0, 0, 0));
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addActionListener(e -> logout());

        userCard.add(avatar, BorderLayout.WEST);
        userCard.add(text, BorderLayout.CENTER);
        userCard.add(logoutBtn, BorderLayout.EAST);

        panel.add(userCard, BorderLayout.CENTER);
        return panel;
    }

    private void logout() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Deseja terminar sessao?",
                "Confirmacao",
                JOptionPane.YES_NO_OPTION
        );
        if (result == JOptionPane.YES_OPTION) {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.dispose();
            }
            SwingUtilities.invokeLater(() -> {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
            });
        }
    }

    private class NavButton extends JButton {
        private final String page;
        private final JLabel dot;
        private final JLabel icon;
        private final JLabel text;
        private boolean active;
        private boolean hovered;

        NavButton(String page, String label, String shortIcon) {
            this.page = page;

            setLayout(new BorderLayout(8, 0));
            setBorder(new EmptyBorder(8, 10, 8, 10));
            setPreferredSize(new Dimension(204, 34));
            setMinimumSize(new Dimension(204, 34));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);

            JPanel left = new JPanel(new BorderLayout(9, 0));
            left.setOpaque(false);

            icon = new JLabel(shortIcon, SwingConstants.CENTER);
            icon.setPreferredSize(new Dimension(18, 18));
            icon.setForeground(LIGHT_TEXT);
            icon.setFont(new Font("Dialog", Font.PLAIN, 12));

            text = new JLabel(label);
            text.setForeground(LIGHT_TEXT);
            text.setFont(new Font("Inter", Font.PLAIN, 11));

            left.add(icon, BorderLayout.WEST);
            left.add(text, BorderLayout.CENTER);

            dot = new JLabel("\u25CF");
            dot.setForeground(new Color(96, 165, 250));
            dot.setFont(new Font("Dialog", Font.PLAIN, 8));
            dot.setVisible(false);

            add(left, BorderLayout.CENTER);
            add(dot, BorderLayout.EAST);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });

            addActionListener(e -> {
                setActivePage(this.page);
                if (listener != null) {
                    listener.navigateTo(this.page);
                }
            });
        }

        void setActive(boolean active) {
            this.active = active;
            Color foreground = active ? Color.WHITE : LIGHT_TEXT;
            icon.setForeground(foreground);
            text.setForeground(foreground);
            text.setFont(new Font("Inter", active ? Font.BOLD : Font.PLAIN, 11));
            dot.setVisible(active);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (active || hovered) {
                g2d.setColor(active ? ACTIVE_BG : HOVER_BG);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
            g2d.dispose();
            super.paintComponent(g);
        }
    }
}
