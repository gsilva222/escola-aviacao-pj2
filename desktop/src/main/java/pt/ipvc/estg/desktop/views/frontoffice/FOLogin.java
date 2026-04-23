package pt.ipvc.estg.desktop.views.frontoffice;

import pt.ipvc.estg.desktop.services.MockStudentLoginService;
import pt.ipvc.estg.entities.Student;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.util.Optional;

/**
 * Login Frame para Portal do Aluno (FrontOffice)
 * Design simplificado com gradiente azul
 * Usa MockStudentLoginService para autenticação real
 */
public class FOLogin extends JFrame {

    private JTextField userIdField;
    private JPasswordField passwordField;
    private final MockStudentLoginService loginService;

    public FOLogin() {
        this.loginService = new MockStudentLoginService();
        initializeUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, 500, 600, 20, 20));
    }

    private void initializeUI() {
        // Main panel com gradient
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Gradiente azul claro
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(240, 246, 255),  // #F0F6FF
                        getWidth(), getHeight(), new Color(232, 244, 253)  // #E8F4FD
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        // Card branco centralizado
        JPanel cardPanel = createCardPanel();
        mainPanel.add(Box.createVerticalStrut(50));
        mainPanel.add(cardPanel);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel);
    }

    private JPanel createCardPanel() {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setMaximumSize(new Dimension(350, 400));
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Logo e título
        JLabel logoLabel = new JLabel("✈️");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 48));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(logoLabel);

        JLabel titleLabel = new JLabel("Portal do Aluno");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(15, 35, 68));
        card.add(titleLabel);

        card.add(Box.createVerticalStrut(30));

        // Campo Nº de Aluno / Username
        JLabel userIdLabel = new JLabel("Nº de Aluno / Username:");
        userIdLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        card.add(userIdLabel);

        userIdField = new JTextField();
        userIdField.setText("1");
        userIdField.setFont(new Font("Arial", Font.PLAIN, 14));
        userIdField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        card.add(userIdField);

        card.add(Box.createVerticalStrut(15));

        // Campo Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        card.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        card.add(passwordField);

        // Toggle show/hide password
        JCheckBox showPassword = new JCheckBox("Mostrar password");
        showPassword.setFont(new Font("Arial", Font.PLAIN, 11));
        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });
        card.add(showPassword);

        card.add(Box.createVerticalStrut(20));

        // Botão Entrar
        JButton loginButton = new JButton("ENTRAR");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(new Color(21, 101, 192));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        loginButton.addActionListener(e -> handleLogin());
        card.add(loginButton);

        card.add(Box.createVerticalStrut(15));

        // Links
        JPanel linksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        linksPanel.setOpaque(false);

        JButton forgotPassword = new JButton("Esqueci a password");
        forgotPassword.setContentAreaFilled(false);
        forgotPassword.setBorderPainted(false);
        forgotPassword.setFont(new Font("Arial", Font.PLAIN, 11));
        forgotPassword.setForeground(new Color(21, 101, 192));
        forgotPassword.addActionListener(e -> handleForgotPassword());
        linksPanel.add(forgotPassword);

        JLabel separator = new JLabel("|");
        separator.setForeground(Color.LIGHT_GRAY);
        linksPanel.add(separator);

        JButton help = new JButton("Ajuda");
        help.setContentAreaFilled(false);
        help.setBorderPainted(false);
        help.setFont(new Font("Arial", Font.PLAIN, 11));
        help.setForeground(new Color(21, 101, 192));
        help.addActionListener(e -> handleHelp());
        linksPanel.add(help);

        card.add(linksPanel);

        // Demo notice
        JLabel demoLabel = new JLabel("Demo: Use IDs 1-24 · qualquer password");
        demoLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        demoLabel.setForeground(new Color(150, 150, 150));
        demoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(Box.createVerticalStrut(15));
        card.add(demoLabel);

        // Link para BackOffice
        JPanel boLinkPanel = new JPanel();
        boLinkPanel.setOpaque(false);
        JLabel boQuestion = new JLabel("É colaborador?");
        boQuestion.setFont(new Font("Arial", Font.PLAIN, 11));
        JButton boLink = new JButton("Aceder ao BackOffice");
        boLink.setContentAreaFilled(false);
        boLink.setBorderPainted(false);
        boLink.setFont(new Font("Arial", Font.PLAIN, 11));
        boLink.setForeground(new Color(21, 101, 192));
        boLink.addActionListener(e -> handleBackofficeLink());
        boLinkPanel.add(boQuestion);
        boLinkPanel.add(boLink);
        card.add(boLinkPanel);

        return card;
    }

    private void handleLogin() {
        String userId = userIdField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (userId.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Preencha todos os campos",
                    "Atenção",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Usar MockStudentLoginService para autenticar
        Optional<Student> student = loginService.authenticate(userId, password);
        
        if (student.isPresent()) {
            // Autenticação bem-sucedida - abrir FOLayout com o aluno autenticado
            dispose();
            FOLayout layout = new FOLayout(student.get());
            layout.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Nº de Aluno ou password inválidos\n\nPara teste, use IDs: 1-24",
                    "Erro de Autenticação",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    private void handleForgotPassword() {
        JOptionPane.showMessageDialog(this,
                "Funcionalidade em desenvolvimento.\nPor favor contacte suporte@aeroschool.pt",
                "Reset de Password",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleHelp() {
        JOptionPane.showMessageDialog(this,
                "Suporte disponível 24/7\nEmail: suporte@aeroschool.pt\nTelefone: +351 XXX XXX XXX",
                "Ajuda",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleBackofficeLink() {
        // Abrir BackOffice LoginFrame
        dispose();
        new pt.ipvc.estg.desktop.views.LoginFrame().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FOLogin().setVisible(true));
    }
}
