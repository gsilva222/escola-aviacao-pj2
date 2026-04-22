package pt.ipvc.estg.desktop.views;

import pt.ipvc.estg.desktop.DesktopApp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Arrays;

public class LoginFrame extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private JButton loginButton;
    private DesktopApp desktopApp;

    // Color scheme from Figma
    private static final Color DARK_BG = new Color(15, 35, 68);      // #0F2344
    private static final Color BLUE_PRIMARY = new Color(21, 101, 192); // #1565C0
    private static final Color LIGHT_BG = new Color(240, 244, 248);    // #F0F4F8
    private static final Color WHITE = Color.WHITE;
    private static final Color GRAY_TEXT = new Color(100, 116, 139);   // #64748B
    private static final Color BORDER_COLOR = new Color(226, 232, 240); // #E2E8F0
    private static final Color INPUT_BG = new Color(248, 250, 252);    // #F8FAFC

    public LoginFrame() {
        setTitle("AeroSchool BackOffice - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 1400, 800, 20, 20));
        setBackground(LIGHT_BG);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(LIGHT_BG);

        // Left Panel - Image section
        JPanel leftPanel = createLeftPanel();
        mainPanel.add(leftPanel, BorderLayout.WEST);

        // Right Panel - Form section
        JPanel rightPanel = createRightPanel();
        mainPanel.add(rightPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Gradient overlay: Dark navy -> Brighter blue
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(10, 25, 68),
                        getWidth(), getHeight(), new Color(10, 25, 68, 200)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Try to add subtle cockpit background effect
                int opacity = 80;
                g2d.setColor(new Color(255, 255, 255, opacity));
                g2d.setStroke(new BasicStroke(2));
                for (int i = 0; i < 5; i++) {
                    g2d.drawRect(20 + i * 40, 50 + i * 30, 150, 100);
                }
            }
        };

        panel.setPreferredSize(new Dimension(600, 800));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(60, 40, 60, 40));

        // Back button
        JButton backButton = createStyledButton("← Voltar ao início", 12);
        backButton.setForeground(new Color(147, 197, 253)); // blue-300
        backButton.addActionListener(e -> System.exit(0));
        backButton.setMaximumSize(new Dimension(200, 30));
        backButton.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(backButton);

        panel.add(Box.createVerticalStrut(80));

        // Logo and title
        JLabel logoLabel = new JLabel("✈ AeroSchool");
        logoLabel.setFont(new Font("Inter", Font.BOLD, 28));
        logoLabel.setForeground(WHITE);
        logoLabel.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(logoLabel);

        JLabel subtitleLabel = new JLabel("ACADEMIA DE AVIAÇÃO");
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        subtitleLabel.setForeground(new Color(147, 197, 253));
        subtitleLabel.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(subtitleLabel);

        panel.add(Box.createVerticalStrut(30));

        JLabel titleLabel = new JLabel("<html>Sistema de Gestão<br/>BackOffice</html>");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 32));
        titleLabel.setForeground(WHITE);
        panel.add(titleLabel);

        panel.add(Box.createVerticalStrut(20));

        JLabel descLabel = new JLabel("<html>Plataforma integrada para<br/>administração, gestão operacional,<br/>instrutores e manutenção.</html>");
        descLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        descLabel.setForeground(new Color(147, 197, 253)); // blue-300
        panel.add(descLabel);

        panel.add(Box.createVerticalStrut(40));

        // Stats grid
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(400, 200));

        String[][] stats = {
                {"40+", "Alunos Ativos"},
                {"5", "Aeronaves"},
                {"3", "Instrutores"},
                {"EASA", "Aprovado ATO"}
        };

        for (String[] stat : stats) {
            JPanel statCard = new JPanel();
            statCard.setLayout(new BoxLayout(statCard, BoxLayout.Y_AXIS));
            statCard.setOpaque(false);
            statCard.setBorder(new EmptyBorder(15, 15, 15, 15));

            JLabel statValue = new JLabel(stat[0]);
            statValue.setFont(new Font("Inter", Font.BOLD, 20));
            statValue.setForeground(WHITE);
            statCard.add(statValue);

            JLabel statLabel = new JLabel(stat[1]);
            statLabel.setFont(new Font("Inter", Font.PLAIN, 12));
            statLabel.setForeground(new Color(147, 197, 253));
            statCard.add(statLabel);

            statsPanel.add(statCard);
        }

        panel.add(statsPanel);
        panel.add(Box.createVerticalGlue());

        JLabel copyrightLabel = new JLabel("© 2025 AeroSchool · EASA Part-FCL ATO");
        copyrightLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        copyrightLabel.setForeground(new Color(147, 197, 253));
        copyrightLabel.setAlignmentX(LEFT_ALIGNMENT);
        panel.add(copyrightLabel);

        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(LIGHT_BG);
        panel.setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel();
        formPanel.setBackground(WHITE);
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        formPanel.setMaximumSize(new Dimension(420, 600));

        // Set rounded borders
        formPanel.setBorder(new RoundedBorder(20, new EmptyBorder(40, 40, 40, 40)));

        // Title
        JLabel titleLabel = new JLabel("Iniciar Sessão");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 28));
        titleLabel.setForeground(DARK_BG);
        formPanel.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Acesso exclusivo para colaboradores da AeroSchool");
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 13));
        subtitleLabel.setForeground(GRAY_TEXT);
        formPanel.add(subtitleLabel);

        formPanel.add(Box.createVerticalStrut(30));

        // Role selector
        formPanel.add(createLabel("Perfil de Acesso"));
        String[] roles = {"Administrador", "Secretaria", "Gestor Operacional", "Instrutor", "Técnico de Manutenção"};
        roleCombo = new JComboBox<>(roles);
        roleCombo.setSelectedIndex(0);
        formPanel.add(createStyledComboBox(roleCombo));

        formPanel.add(Box.createVerticalStrut(15));

        // Email field
        formPanel.add(createLabel("Email"));
        emailField = new JTextField("admin@aeroschool.pt");
        emailField.setFont(new Font("Inter", Font.PLAIN, 13));
        formPanel.add(createStyledTextField(emailField));

        formPanel.add(Box.createVerticalStrut(15));

        // Password field
        formPanel.add(createLabel("Senha"));
        passwordField = new JPasswordField("••••••••");
        passwordField.setFont(new Font("Inter", Font.PLAIN, 13));
        formPanel.add(createStyledTextField(passwordField));

        formPanel.add(Box.createVerticalStrut(30));

        // Login button
        loginButton = new JButton("Entrar");
        loginButton.setFont(new Font("Inter", Font.BOLD, 14));
        loginButton.setBackground(BLUE_PRIMARY);
        loginButton.setForeground(WHITE);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setPreferredSize(new Dimension(400, 45));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        loginButton.addActionListener(this::handleLogin);
        formPanel.add(loginButton);

        panel.add(formPanel, new GridBagConstraints());

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Inter", Font.BOLD, 12));
        label.setForeground(DARK_BG);
        label.setAlignmentX(LEFT_ALIGNMENT);
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        return label;
    }

    private JPanel createStyledTextField(JTextField field) {
        field.setBackground(INPUT_BG);
        field.setForeground(DARK_BG);
        field.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 2));
        field.setFont(new Font("Inter", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(400, 40));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        wrapper.add(field);

        return wrapper;
    }

    private JPanel createStyledComboBox(JComboBox<String> combo) {
        combo.setBackground(INPUT_BG);
        combo.setForeground(DARK_BG);
        combo.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 2));
        combo.setFont(new Font("Inter", Font.PLAIN, 13));
        combo.setPreferredSize(new Dimension(400, 40));
        combo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        wrapper.add(combo);

        return wrapper;
    }

    private JButton createStyledButton(String text, int fontSize) {
        JButton button = new JButton(text);
        button.setFont(new Font("Inter", Font.PLAIN, fontSize));
        button.setForeground(new Color(147, 197, 253)); // blue-300
        button.setBackground(new Color(0, 0, 0, 0));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void handleLogin(ActionEvent e) {
        String selectedRole = (String) roleCombo.getSelectedItem();
        setVisible(false);
        dispose();

        desktopApp = new DesktopApp(selectedRole);
        desktopApp.setVisible(true);
    }

    // Custom border with rounded corners
    private static class RoundedBorder extends EmptyBorder {
        private int radius;

        RoundedBorder(int radius, EmptyBorder border) {
            super(border.top, border.left, border.bottom, border.right);
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2d.setColor(new Color(0, 0, 0, 6));
            g2d.fillRoundRect(x, y, width, height, radius, radius);

            g2d.setColor(BORDER_COLOR);
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }
}
