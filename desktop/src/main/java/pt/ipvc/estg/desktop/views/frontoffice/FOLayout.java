package pt.ipvc.estg.desktop.views.frontoffice;

import pt.ipvc.estg.entities.Student;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Layout principal do FrontOffice inspirado no mockup web.
 */
public class FOLayout extends JFrame {

    private static final Color PAGE_BG = new Color(240, 246, 255);    // #F0F6FF
    private static final Color WHITE = Color.WHITE;
    private static final Color TITLE = new Color(15, 35, 68);         // #0F2344
    private static final Color MUTED = new Color(100, 116, 139);      // #64748B
    private static final Color BORDER = new Color(226, 232, 240);     // #E2E8F0
    private static final Color BLUE = new Color(21, 101, 192);        // #1565C0

    private final Student currentStudent;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final Map<String, JButton> navButtons = new LinkedHashMap<>();
    private String activePage = "dashboard";

    public FOLayout(Student student) {
        this.currentStudent = student;
        initializeUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);
        setTitle("AeroSchool - Portal do Aluno");
    }

    private void initializeUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(PAGE_BG);

        root.add(createHeader(), BorderLayout.NORTH);

        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(24, 24, 24, 24));
        contentPanel.add(new FODashboard(currentStudent), "dashboard");
        contentPanel.add(new FOSchedule(currentStudent), "schedule");
        contentPanel.add(new FOFlights(currentStudent), "flights");
        contentPanel.add(new FOHours(currentStudent), "hours");
        contentPanel.add(new FOEvaluations(currentStudent), "evaluations");
        contentPanel.add(new FODocuments(currentStudent), "documents");
        contentPanel.add(new FOPayments(currentStudent), "payments");
        contentPanel.add(new FOProfile(currentStudent), "profile");

        cardLayout.show(contentPanel, "dashboard");
        updateNavState();

        root.add(contentPanel, BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        header.setPreferredSize(new Dimension(Integer.MAX_VALUE, 68));

        header.add(createBrandPanel(), BorderLayout.WEST);
        header.add(createNavPanel(), BorderLayout.CENTER);
        header.add(createActionsPanel(), BorderLayout.EAST);
        return header;
    }

    private JPanel createBrandPanel() {
        JPanel brand = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 16));
        brand.setOpaque(false);
        brand.setBorder(new EmptyBorder(0, 16, 0, 0));

        JPanel iconBox = new JPanel(new GridBagLayout());
        iconBox.setPreferredSize(new Dimension(32, 32));
        iconBox.setBackground(TITLE);
        JLabel plane = new JLabel("\u2708");
        plane.setForeground(Color.WHITE);
        plane.setFont(new Font("Dialog", Font.PLAIN, 14));
        iconBox.add(plane);

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel brandTitle = new JLabel("AeroSchool");
        brandTitle.setFont(new Font("Inter", Font.BOLD, 13));
        brandTitle.setForeground(TITLE);
        JLabel subtitle = new JLabel("Portal do Aluno");
        subtitle.setFont(new Font("Inter", Font.PLAIN, 10));
        subtitle.setForeground(new Color(148, 163, 184));
        text.add(brandTitle);
        text.add(subtitle);

        brand.add(iconBox);
        brand.add(text);
        return brand;
    }

    private JPanel createNavPanel() {
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 14));
        nav.setOpaque(false);

        addNavButton(nav, "dashboard", "Dashboard");
        addNavButton(nav, "schedule", "Horario");
        addNavButton(nav, "flights", "Voos");
        addNavButton(nav, "hours", "Horas de Voo");
        addNavButton(nav, "evaluations", "Avaliacoes");
        addNavButton(nav, "documents", "Documentos");
        addNavButton(nav, "payments", "Pagamentos");
        addNavButton(nav, "profile", "Perfil");

        return nav;
    }

    private void addNavButton(JPanel nav, String page, String label) {
        JButton btn = new JButton(label);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFont(new Font("Inter", Font.PLAIN, 12));
        btn.setBorder(new EmptyBorder(8, 12, 8, 12));
        btn.addActionListener(e -> navigateToPage(page));
        navButtons.put(page, btn);
        nav.add(btn);
    }

    private JPanel createActionsPanel() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        actions.setOpaque(false);
        actions.setBorder(new EmptyBorder(0, 0, 0, 16));

        JButton bellBtn = new JButton("\uD83D\uDD14");
        bellBtn.setFocusPainted(false);
        bellBtn.setBorderPainted(false);
        bellBtn.setBackground(new Color(0, 0, 0, 0));
        bellBtn.setForeground(MUTED);
        bellBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actions.add(bellBtn);

        JButton profileBtn = new JButton(buildProfileText());
        profileBtn.setFocusPainted(false);
        profileBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profileBtn.setForeground(TITLE);
        profileBtn.setFont(new Font("Inter", Font.PLAIN, 12));
        profileBtn.setBackground(WHITE);
        profileBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(241, 245, 249), 1),
                new EmptyBorder(7, 10, 7, 10)
        ));
        profileBtn.addActionListener(e -> navigateToPage("profile"));
        actions.add(profileBtn);

        JButton logoutBtn = new JButton("Sair");
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setFont(new Font("Inter", Font.PLAIN, 12));
        logoutBtn.setForeground(new Color(239, 68, 68));
        logoutBtn.setBackground(WHITE);
        logoutBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(7, 10, 7, 10)
        ));
        logoutBtn.addActionListener(e -> handleLogout());
        actions.add(logoutBtn);

        return actions;
    }

    private String buildProfileText() {
        String name = currentStudent.getName() != null ? currentStudent.getName() : "Aluno";
        String firstName = name.contains(" ") ? name.substring(0, name.indexOf(' ')) : name;
        int progress = currentStudent.getProgress() != null ? currentStudent.getProgress() : 0;
        return firstName + " · " + progress + "%";
    }

    public void navigateToPage(String page) {
        String targetPage = normalizePage(page);
        if (!navButtons.containsKey(targetPage)) {
            targetPage = "dashboard";
        }

        activePage = targetPage;
        cardLayout.show(contentPanel, targetPage);
        updateNavState();
    }

    private String normalizePage(String page) {
        if (page == null || page.isBlank()) {
            return "dashboard";
        }

        return switch (page.trim().toLowerCase(Locale.ROOT)) {
            case "dashboard" -> "dashboard";
            case "schedule", "horario" -> "schedule";
            case "flights", "voos" -> "flights";
            case "hours", "horas" -> "hours";
            case "evaluations", "avaliacoes" -> "evaluations";
            case "documents", "documentos" -> "documents";
            case "payments", "pagamentos" -> "payments";
            case "profile", "perfil" -> "profile";
            default -> "dashboard";
        };
    }

    private void updateNavState() {
        navButtons.forEach((page, btn) -> {
            boolean active = page.equals(activePage);
            btn.setOpaque(true);
            btn.setBackground(active ? new Color(239, 246, 255) : new Color(0, 0, 0, 0));
            btn.setForeground(active ? BLUE : MUTED);
            btn.setFont(new Font("Inter", active ? Font.BOLD : Font.PLAIN, 12));
        });
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Deseja sair do Portal do Aluno?",
                "Confirmacao",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new FOLogin().setVisible(true);
        }
    }

    public Student getCurrentStudent() {
        return currentStudent;
    }
}
