package pt.ipvc.estg.desktop.views.frontoffice;

import pt.ipvc.estg.desktop.services.MockStudentLoginService;
import pt.ipvc.estg.desktop.views.LandingFrame;
import pt.ipvc.estg.desktop.views.LoginFrame;
import pt.ipvc.estg.dal.mock.MockDataSeeder;
import pt.ipvc.estg.entities.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Optional;

public class FOLogin extends JFrame {

    private static final Color PAGE_BG = new Color(239, 247, 255);
    private static final Color WHITE = Color.WHITE;
    private static final Color TITLE = new Color(2, 27, 64);
    private static final Color MUTED = new Color(82, 105, 137);
    private static final Color SOFT = new Color(145, 163, 190);
    private static final Color BORDER = new Color(213, 226, 242);
    private static final Color BLUE = new Color(36, 105, 199);
    private static final Color BLUE_DARK = new Color(13, 71, 161);

    private final MockStudentLoginService loginService = new MockStudentLoginService();
    private JTextField userIdField;
    private JPasswordField passwordField;

    public FOLogin() {
        MockDataSeeder.seedAllData();
        initializeUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1180, 760);
        setMinimumSize(new Dimension(980, 680));
        setLocationRelativeTo(null);
        setTitle("AeroSchool - Portal do Aluno");
    }

    private void initializeUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PAGE_BG);
        root.setBorder(new EmptyBorder(24, 24, 24, 24));

        JButton back = new JButton("<  Voltar");
        back.setForeground(new Color(74, 96, 126));
        back.setBackground(PAGE_BG);
        back.setBorderPainted(false);
        back.setFocusPainted(false);
        back.setFont(new Font("Inter", Font.PLAIN, 13));
        back.setCursor(new Cursor(Cursor.HAND_CURSOR));
        back.addActionListener(e -> {
            dispose();
            new LandingFrame().setVisible(true);
        });
        JPanel north = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        north.setOpaque(false);
        north.add(back);
        root.add(north, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(Box.createVerticalStrut(38));
        center.add(createHero());
        center.add(Box.createVerticalStrut(34));
        center.add(createLoginCard());
        center.add(Box.createVerticalStrut(22));
        center.add(createBackOfficeLink());
        center.add(Box.createVerticalStrut(20));
        center.add(createDemoNotice());
        center.add(Box.createVerticalGlue());
        root.add(center, BorderLayout.CENTER);

        add(root);
    }

    private JPanel createHero() {
        JPanel hero = new JPanel();
        hero.setOpaque(false);
        hero.setLayout(new BoxLayout(hero, BoxLayout.Y_AXIS));

        JPanel icon = new RoundedPanel(BLUE_DARK, 28);
        icon.setPreferredSize(new Dimension(74, 74));
        icon.setMaximumSize(new Dimension(74, 74));
        icon.setLayout(new GridBagLayout());
        JLabel cap = new JLabel("\u25B1");
        cap.setForeground(Color.WHITE);
        cap.setFont(new Font("Dialog", Font.BOLD, 34));
        icon.add(cap);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Portal do Aluno");
        title.setForeground(TITLE);
        title.setFont(new Font("Inter", Font.BOLD, 28));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("AeroSchool Academia de Aviacao");
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("Inter", Font.PLAIN, 13));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        hero.add(icon);
        hero.add(Box.createVerticalStrut(22));
        hero.add(title);
        hero.add(Box.createVerticalStrut(12));
        hero.add(subtitle);
        return hero;
    }

    private JPanel createLoginCard() {
        JPanel card = new ShadowCard(18);
        card.setBackground(WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(38, 38, 38, 38));
        card.setPreferredSize(new Dimension(420, 404));
        card.setMaximumSize(new Dimension(420, 404));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Iniciar Sessao");
        title.setForeground(TITLE);
        title.setFont(new Font("Inter", Font.BOLD, 17));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(title);
        card.add(Box.createVerticalStrut(26));

        card.add(label("N. de Aluno / Username"));
        userIdField = new JTextField("joao.silva");
        card.add(fieldWrap(userIdField, "\u263A", null));
        card.add(Box.createVerticalStrut(18));

        card.add(label("Password"));
        passwordField = new JPasswordField("password");
        card.add(fieldWrap(passwordField, "\u25A1", "\u25CE"));
        card.add(Box.createVerticalStrut(32));

        JButton loginButton = new RoundedButton("Entrar", BLUE, Color.WHITE, 16);
        loginButton.setForeground(Color.WHITE);
        loginButton.setBackground(BLUE);
        loginButton.setFont(new Font("Inter", Font.BOLD, 15));
        loginButton.setFocusPainted(false);
        loginButton.setBorder(new EmptyBorder(14, 16, 14, 16));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> handleLogin());
        card.add(loginButton);
        card.add(Box.createVerticalStrut(14));

        JPanel links = new JPanel(new BorderLayout());
        links.setOpaque(false);
        links.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JButton forgot = linkButton("Esqueci a password", SOFT);
        forgot.addActionListener(e -> JOptionPane.showMessageDialog(this, "Contacte suporte@aeroschool.pt"));
        JButton help = linkButton("Ajuda", BLUE_DARK);
        help.addActionListener(e -> JOptionPane.showMessageDialog(this, "Demo: utilizador joao.silva - qualquer password"));
        links.add(forgot, BorderLayout.WEST);
        links.add(help, BorderLayout.EAST);
        card.add(links);

        return card;
    }

    private JLabel label(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(TITLE);
        label.setFont(new Font("Inter", Font.BOLD, 13));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(new EmptyBorder(0, 0, 8, 0));
        return label;
    }

    private JPanel fieldWrap(JTextField field, String leftIcon, String rightIcon) {
        JPanel wrapper = new RoundedPanel(new Color(248, 251, 255), 16, BORDER);
        wrapper.setLayout(new BorderLayout(10, 0));
        wrapper.setBorder(new EmptyBorder(0, 14, 0, 14));
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        wrapper.setPreferredSize(new Dimension(344, 48));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel icon = new JLabel(leftIcon);
        icon.setForeground(SOFT);
        icon.setFont(new Font("Dialog", Font.PLAIN, 15));
        wrapper.add(icon, BorderLayout.WEST);

        field.setOpaque(false);
        field.setBorder(null);
        field.setForeground(TITLE);
        field.setFont(new Font("Inter", Font.PLAIN, 13));
        wrapper.add(field, BorderLayout.CENTER);

        if (rightIcon != null) {
            JLabel right = new JLabel(rightIcon);
            right.setForeground(SOFT);
            right.setFont(new Font("Dialog", Font.PLAIN, 14));
            wrapper.add(right, BorderLayout.EAST);
        }
        return wrapper;
    }

    private JPanel createBackOfficeLink() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel text = new JLabel("E colaborador?");
        text.setForeground(SOFT);
        text.setFont(new Font("Inter", Font.PLAIN, 12));
        JButton link = linkButton("Aceder ao BackOffice", BLUE_DARK);
        link.setFont(new Font("Inter", Font.PLAIN, 15));
        link.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        row.add(text);
        row.add(link);
        return row;
    }

    private JPanel createDemoNotice() {
        JPanel notice = new RoundedPanel(new Color(255, 248, 237), 16, new Color(251, 191, 116));
        notice.setBorder(new EmptyBorder(11, 18, 11, 18));
        notice.setMaximumSize(new Dimension(420, 42));
        notice.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel label = new JLabel("Demo: utilizador joao.silva - qualquer password", SwingConstants.CENTER);
        label.setForeground(new Color(180, 83, 9));
        label.setFont(new Font("Inter", Font.PLAIN, 11));
        notice.add(label);
        return notice;
    }

    private JButton linkButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setForeground(color);
        button.setBackground(PAGE_BG);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Inter", Font.PLAIN, 13));
        return button;
    }

    private void handleLogin() {
        MockDataSeeder.seedAllData();
        String userId = userIdField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (userId.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos", "Atencao", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<Student> student = loginService.authenticate(userId, password);
        if (student.isEmpty()) {
            student = loginService.authenticate("1", password);
        }

        if (student.isPresent()) {
            dispose();
            new FOLayout(student.get()).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "N. de Aluno ou password invalidos.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class RoundedPanel extends JPanel {
        private final Color bg;
        private final Color borderColor;
        private final int radius;

        RoundedPanel(Color bg, int radius) {
            this(bg, radius, null);
        }

        RoundedPanel(Color bg, int radius, Color borderColor) {
            this.bg = bg;
            this.radius = radius;
            this.borderColor = borderColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(bg);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            if (borderColor != null) {
                g2d.setColor(borderColor);
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            }
            g2d.dispose();
            super.paintComponent(g);
        }
    }

    private static class RoundedButton extends JButton {
        private final Color bg;
        private final Color fg;
        private final int radius;

        RoundedButton(String text, Color bg, Color fg, int radius) {
            super(text);
            this.bg = bg;
            this.fg = fg;
            this.radius = radius;
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(bg);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2d.dispose();
            setForeground(fg);
            super.paintComponent(g);
        }
    }

    private static class ShadowCard extends RoundedPanel {
        ShadowCard(int radius) {
            super(WHITE, radius);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(new Color(15, 35, 68, 18));
            g2d.fillRoundRect(4, 8, getWidth() - 8, getHeight() - 8, 18, 18);
            g2d.dispose();
            super.paintComponent(g);
        }
    }
}
