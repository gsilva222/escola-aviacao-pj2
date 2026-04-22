package pt.ipvc.estg.desktop.views.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class Sidebar extends JPanel {
    private JPanel navPanel;
    private List<SidebarButton> buttons = new ArrayList<>();
    private boolean isCollapsed = false;
    private static final Color DARK_BG = new Color(15, 35, 68);      // #0F2344
    private static final Color BLUE_PRIMARY = new Color(21, 101, 192); // #1565C0
    private static final Color LIGHT_TEXT = new Color(147, 169, 200); // #93A9C8
    private static final int EXPANDED_WIDTH = 256;
    private static final int COLLAPSED_WIDTH = 72;

    public interface NavigationListener {
        void navigateTo(String page);
    }

    private NavigationListener listener;

    public Sidebar(NavigationListener listener) {
        this.listener = listener;
        setBackground(DARK_BG);
        setPreferredSize(new Dimension(EXPANDED_WIDTH, Integer.MAX_VALUE));
        setLayout(new BorderLayout());

        // Header with logo
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Navigation
        navPanel = createNavPanel();
        add(navPanel, BorderLayout.CENTER);

        // Footer
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(DARK_BG);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 15));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Logo icon
        JLabel logoIcon = new JLabel("✈");
        logoIcon.setFont(new Font("Arial", Font.BOLD, 20));
        logoIcon.setForeground(BLUE_PRIMARY);
        panel.add(logoIcon);

        // Logo text (shown when expanded)
        logoTextLabel = new JLabel("AeroSchool");
        logoTextLabel.setFont(new Font("Inter", Font.BOLD, 14));
        logoTextLabel.setForeground(Color.WHITE);
        panel.add(logoTextLabel);

        return panel;
    }

    private JLabel logoTextLabel;

    private JPanel createNavPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(DARK_BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(15, 10, 15, 10));

        String[] navItems = {
                "📊 Dashboard",
                "👥 Alunos",
                "📚 Cursos",
                "✈ Voos",
                "🛩 Aeronaves",
                "🔧 Manutenção",
                "📋 Avaliações",
                "💳 Pagamentos",
                "📈 Relatórios"
        };

        String[] pages = {
                "dashboard",
                "students",
                "courses",
                "flights",
                "aircraft",
                "maintenance",
                "evaluations",
                "payments",
                "reports"
        };

        for (int i = 0; i < navItems.length; i++) {
            SidebarButton btn = new SidebarButton(navItems[i], pages[i]);
            buttons.add(btn);
            panel.add(btn);
            if (i < navItems.length - 1) {
                panel.add(Box.createVerticalStrut(5));
            }
        }

        panel.add(Box.createVerticalGlue());

        // Collapse/Expand button
        JButton collapseBtn = new JButton("<<");
        collapseBtn.setFont(new Font("Inter", Font.BOLD, 12));
        collapseBtn.setBackground(new Color(30, 77, 140));
        collapseBtn.setForeground(Color.WHITE);
        collapseBtn.setBorderPainted(false);
        collapseBtn.setFocusPainted(false);
        collapseBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        collapseBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        collapseBtn.addActionListener(e -> toggleCollapse());
        panel.add(collapseBtn);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(DARK_BG);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(255, 255, 255, 20)));

        JButton settingsBtn = new JButton("⚙");
        settingsBtn.setFont(new Font("Arial", Font.BOLD, 16));
        settingsBtn.setBackground(new Color(30, 77, 140));
        settingsBtn.setForeground(Color.WHITE);
        settingsBtn.setBorderPainted(false);
        settingsBtn.setFocusPainted(false);
        settingsBtn.setPreferredSize(new Dimension(40, 40));
        panel.add(settingsBtn);

        JButton logoutBtn = new JButton("🚪");
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 16));
        logoutBtn.setBackground(new Color(239, 68, 68));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setPreferredSize(new Dimension(40, 40));
        logoutBtn.addActionListener(e -> logout());
        panel.add(logoutBtn);

        return panel;
    }

    private void toggleCollapse() {
        isCollapsed = !isCollapsed;
        int newWidth = isCollapsed ? COLLAPSED_WIDTH : EXPANDED_WIDTH;
        setPreferredSize(new Dimension(newWidth, getHeight()));

        // Toggle text visibility
        logoTextLabel.setVisible(!isCollapsed);

        for (SidebarButton btn : buttons) {
            btn.setLabelVisible(!isCollapsed);
        }

        revalidate();
        repaint();
    }

    private void logout() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Tem a certeza que deseja sair?",
                "Confirmar Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            // Return to login - close the desktop app window
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (frame != null) {
                frame.dispose();
                // TODO: Show login again
            }
        }
    }

    private class SidebarButton extends JButton {
        private String page;
        private String fullLabel;
        private JLabel textLabel;

        SidebarButton(String label, String page) {
            super();
            this.page = page;
            this.fullLabel = label;

            setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
            setBackground(new Color(0, 0, 0, 0));
            setForeground(LIGHT_TEXT);
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

            // Icon and text
            add(new JLabel(label.substring(0, 1))); // Icon
            textLabel = new JLabel(label.substring(2));
            textLabel.setFont(new Font("Inter", Font.PLAIN, 13));
            add(textLabel);

            addActionListener(e -> {
                if (listener != null) {
                    listener.navigateTo(page);
                }
            });
        }

        void setLabelVisible(boolean visible) {
            textLabel.setVisible(visible);
        }
    }
}
