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
        setPreferredSize(new Dimension(256, Integer.MAX_VALUE));
        setMinimumSize(new Dimension(256, 0));
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
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(DARK_BG);
        panel.setBorder(new EmptyBorder(18, 16, 18, 16));

        JPanel logoBox = new JPanel(new GridBagLayout());
        logoBox.setPreferredSize(new Dimension(38, 38));
        logoBox.setBackground(BLUE_PRIMARY);
        logoBox.setBorder(new EmptyBorder(0, 0, 0, 0));
        JLabel plane = new JLabel("\u2708");
        plane.setForeground(Color.WHITE);
        plane.setFont(new Font("Dialog", Font.PLAIN, 18));
        logoBox.add(plane);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel brand = new JLabel("AeroSchool");
        brand.setFont(new Font("Inter", Font.BOLD, 17));
        brand.setForeground(Color.WHITE);
        JLabel sub = new JLabel("BACKOFFICE");
        sub.setFont(new Font("Inter", Font.PLAIN, 10));
        sub.setForeground(new Color(96, 165, 250));
        text.add(brand);
        text.add(sub);

        panel.add(logoBox, BorderLayout.WEST);
        panel.add(text, BorderLayout.CENTER);

        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, DIVIDER),
                new EmptyBorder(14, 16, 14, 16)
        ));
        return panel;
    }

    private JPanel createNavPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(DARK_BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(14, 10, 12, 10));

        JLabel navLabel = new JLabel("NAVEGACAO");
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        navLabel.setBorder(new EmptyBorder(0, 10, 8, 0));
        navLabel.setForeground(new Color(74, 111, 165));
        navLabel.setFont(new Font("Inter", Font.BOLD, 10));
        panel.add(navLabel);

        addNavButton(panel, "dashboard", "Dashboard", "DB");
        addNavButton(panel, "students", "Alunos", "AL");
        addNavButton(panel, "courses", "Cursos", "CU");
        addNavButton(panel, "flights", "Agendamento de Voos", "VO");
        addNavButton(panel, "aircraft", "Aeronaves", "AR");
        addNavButton(panel, "maintenance", "Manutencao", "MN");
        addNavButton(panel, "evaluations", "Avaliacoes", "AV");
        addNavButton(panel, "payments", "Pagamentos", "PG");
        addNavButton(panel, "reports", "Relatorios", "RL");

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
        userCard.setPreferredSize(new Dimension(236, 54));

        JLabel avatar = new JLabel("AD", SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(32, 32));
        avatar.setOpaque(true);
        avatar.setBackground(BLUE_PRIMARY);
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Inter", Font.BOLD, 11));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel name = new JLabel("Admin. Geral");
        name.setForeground(Color.WHITE);
        name.setFont(new Font("Inter", Font.BOLD, 12));
        JLabel role = new JLabel("Administrador");
        role.setForeground(new Color(96, 165, 250));
        role.setFont(new Font("Inter", Font.PLAIN, 10));
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

            setLayout(new BorderLayout(10, 0));
            setBorder(new EmptyBorder(10, 10, 10, 10));
            setPreferredSize(new Dimension(236, 42));
            setMinimumSize(new Dimension(236, 42));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setOpaque(false);

            JPanel left = new JPanel(new BorderLayout(10, 0));
            left.setOpaque(false);

            icon = new JLabel(shortIcon, SwingConstants.CENTER);
            icon.setPreferredSize(new Dimension(24, 20));
            icon.setForeground(LIGHT_TEXT);
            icon.setFont(new Font("Inter", Font.BOLD, 10));

            text = new JLabel(label);
            text.setForeground(LIGHT_TEXT);
            text.setFont(new Font("Inter", Font.PLAIN, 13));

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
            text.setFont(new Font("Inter", active ? Font.BOLD : Font.PLAIN, 13));
            dot.setVisible(active);
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            if (active || hovered) {
                g2d.setColor(active ? ACTIVE_BG : HOVER_BG);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            }
            g2d.dispose();
            super.paintComponent(g);
        }
    }
}
