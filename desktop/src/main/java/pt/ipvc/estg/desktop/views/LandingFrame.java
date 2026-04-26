package pt.ipvc.estg.desktop.views;

import pt.ipvc.estg.desktop.views.frontoffice.FOLogin;
import pt.ipvc.estg.dal.mock.MockDataSeeder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LandingFrame extends JFrame {

    private static final Color WHITE = Color.WHITE;
    private static final Color BLUE = new Color(38, 107, 199);
    private static final Color BLUE_LIGHT = new Color(147, 197, 253);
    private static final Color GLASS = new Color(255, 255, 255, 44);

    public LandingFrame() {
        setTitle("AeroSchool");
        setSize(1280, 760);
        setMinimumSize(new Dimension(980, 680));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(createRoot());
    }

    private JPanel createRoot() {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(12, 35, 78), getWidth(), getHeight(), new Color(19, 101, 192));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(new Color(255, 255, 255, 16));
                g2d.setFont(new Font("Dialog", Font.PLAIN, Math.max(220, getWidth() / 4)));
                g2d.drawString("\u2708", getWidth() / 2 - 230, getHeight() / 2 + 80);

                g2d.setColor(new Color(3, 15, 38, 82));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        root.setBorder(new EmptyBorder(18, 24, 14, 24));

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createCenter(), BorderLayout.CENTER);
        root.add(createFooter(), BorderLayout.SOUTH);
        return root;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        brand.setOpaque(false);
        JPanel logo = new RoundedPanel(new Color(255, 255, 255, 36), 12);
        logo.setPreferredSize(new Dimension(34, 34));
        logo.setLayout(new GridBagLayout());
        JLabel plane = new JLabel("\u2708");
        plane.setForeground(WHITE);
        plane.setFont(new Font("Dialog", Font.PLAIN, 16));
        logo.add(plane);
        brand.add(logo);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel name = new JLabel("AeroSchool");
        name.setForeground(WHITE);
        name.setFont(new Font("Inter", Font.BOLD, 18));
        JLabel sub = new JLabel("ACADEMIA DE AVIACAO");
        sub.setForeground(new Color(170, 213, 255));
        sub.setFont(new Font("Inter", Font.PLAIN, 11));
        text.add(name);
        text.add(sub);
        brand.add(text);
        header.add(brand, BorderLayout.WEST);

        JLabel place = new JLabel("Aerodromo de Lisboa-Cascais - LPPT");
        place.setForeground(new Color(210, 232, 255));
        place.setFont(new Font("Inter", Font.PLAIN, 13));
        header.add(place, BorderLayout.EAST);
        return header;
    }

    private JPanel createCenter() {
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.add(Box.createVerticalGlue());

        JLabel badge = new JLabel("\u2708  Sistema de Gestao Academica", SwingConstants.CENTER);
        badge.setOpaque(true);
        badge.setBackground(new Color(255, 255, 255, 28));
        badge.setForeground(new Color(218, 238, 255));
        badge.setBorder(new EmptyBorder(8, 18, 8, 18));
        badge.setFont(new Font("Inter", Font.PLAIN, 13));
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(badge);
        center.add(Box.createVerticalStrut(24));

        JLabel title = new JLabel("Bem-vindo a AeroSchool");
        title.setForeground(WHITE);
        title.setFont(new Font("Inter", Font.BOLD, 48));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(title);
        center.add(Box.createVerticalStrut(18));

        JLabel subtitle = new JLabel("<html><div style='text-align:center'>Plataforma integrada de gestao para a academia de aviacao.<br/>Selecione o seu perfil de acesso.</div></html>");
        subtitle.setForeground(new Color(215, 234, 255));
        subtitle.setFont(new Font("Inter", Font.PLAIN, 17));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        center.add(subtitle);
        center.add(Box.createVerticalStrut(58));

        JPanel cards = new JPanel(new GridLayout(1, 2, 22, 0));
        cards.setOpaque(false);
        cards.setMaximumSize(new Dimension(640, 300));
        cards.add(createAccessCard("BackOffice", "\u26E8", "Para administradores, secretaria, instrutores e tecnicos de manutencao. Gestao completa da academia.", "Administrador   Secretaria   Instrutor   Manutencao", true));
        cards.add(createAccessCard("Portal do Aluno", "\u25B1", "Area exclusiva para alunos. Consulte voos, horarios, avaliacoes, documentos e pagamentos.", "Voos   Horarios   Avaliacoes   Documentos   Pagamentos", false));
        center.add(cards);
        center.add(Box.createVerticalGlue());
        return center;
    }

    private JPanel createAccessCard(String title, String iconText, String desc, String tags, boolean backoffice) {
        JPanel card = new RoundedPanel(GLASS, 14);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 70), 1),
                new EmptyBorder(28, 28, 24, 28)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JPanel icon = new RoundedPanel(backoffice ? new Color(21, 101, 192, 90) : new Color(255, 255, 255, 50), 18);
        icon.setPreferredSize(new Dimension(54, 54));
        icon.setMaximumSize(new Dimension(54, 54));
        icon.setLayout(new GridBagLayout());
        JLabel glyph = new JLabel(iconText);
        glyph.setForeground(WHITE);
        glyph.setFont(new Font("Dialog", Font.BOLD, 24));
        icon.add(glyph);
        icon.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(icon);
        card.add(Box.createVerticalStrut(26));

        JLabel heading = new JLabel(title);
        heading.setForeground(WHITE);
        heading.setFont(new Font("Inter", Font.BOLD, 22));
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(heading);
        card.add(Box.createVerticalStrut(12));

        JLabel copy = new JLabel("<html><body style='width:250px'>" + desc + "</body></html>");
        copy.setForeground(new Color(202, 226, 252));
        copy.setFont(new Font("Inter", Font.BOLD, 13));
        copy.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(copy);
        card.add(Box.createVerticalStrut(18));

        JLabel tagLabel = new JLabel("<html><body style='width:260px'>" + tags + "</body></html>");
        tagLabel.setForeground(new Color(226, 239, 255));
        tagLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        tagLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(tagLabel);
        card.add(Box.createVerticalGlue());

        JButton access = new JButton((backoffice ? "Aceder ao BackOffice" : "Aceder ao Portal do Aluno") + "   >");
        access.setForeground(new Color(201, 224, 249));
        access.setBackground(new Color(0, 0, 0, 0));
        access.setBorderPainted(false);
        access.setContentAreaFilled(false);
        access.setFocusPainted(false);
        access.setCursor(new Cursor(Cursor.HAND_CURSOR));
        access.setFont(new Font("Inter", Font.BOLD, 13));
        access.setAlignmentX(Component.LEFT_ALIGNMENT);
        access.addActionListener(e -> {
            MockDataSeeder.seedAllData();
            dispose();
            if (backoffice) {
                new LoginFrame().setVisible(true);
            } else {
                new FOLogin().setVisible(true);
            }
        });
        card.add(access);
        return card;
    }

    private JLabel createFooter() {
        JLabel footer = new JLabel("© 2025 AeroSchool Academia de Aviacao - Todos os direitos reservados - EASA Part-FCL Approved ATO", SwingConstants.CENTER);
        footer.setForeground(new Color(76, 164, 255));
        footer.setFont(new Font("Inter", Font.PLAIN, 11));
        return footer;
    }

    private static class RoundedPanel extends JPanel {
        private final Color bg;
        private final int radius;

        RoundedPanel(Color bg, int radius) {
            this.bg = bg;
            this.radius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(bg);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2d.dispose();
            super.paintComponent(g);
        }
    }
}
