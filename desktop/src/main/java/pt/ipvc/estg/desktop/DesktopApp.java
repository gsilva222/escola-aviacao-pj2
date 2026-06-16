package pt.ipvc.estg.desktop;

import pt.ipvc.estg.desktop.api.BoDataAccess;
import pt.ipvc.estg.desktop.api.SessionContext;
import pt.ipvc.estg.dal.mock.MockDataSeeder;
import pt.ipvc.estg.desktop.views.components.Sidebar;
import pt.ipvc.estg.desktop.views.components.TopBar;
import pt.ipvc.estg.desktop.views.components.UITheme;
import pt.ipvc.estg.desktop.views.panels.*;

import javax.swing.*;
import java.awt.*;

/**
 * Aplicação principal do Desktop - Escola de Aviação (BackOffice)
 * Com novo layout: Sidebar colapsável + TopBar + Conteúdo dinâmico
 * Replicando design Figma em Java/Swing
 */
public class DesktopApp extends JFrame {
    private String userRole;
    private TopBar topBar;
    private Sidebar sidebar;
    private JPanel contentPanel;
    private CardLayout cardLayout;

    private String activePageKey = "dashboard";

    // Instances of panels we want to control from TopBar search.
    private BOStudents boStudents;
    private BOEvaluations boEvaluations;
    private BOMaintenance boMaintenance;
    private BOPayments boPayments;

    public DesktopApp(String role) {
        this.userRole = role;
        if (!SessionContext.isAuthenticated()) {
            MockDataSeeder.seedAllData();
        }
        initializeUI();
    }

    private void initializeUI() {
        setTitle("AeroSchool BackOffice - " + userRole);
        setSize(1440, 820);
        setMinimumSize(new Dimension(1180, 720));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setUndecorated(false);

        // Main panel with BorderLayout (Sidebar | TopBar + Content)
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(238, 242, 247));

        // When authenticated against the API, page permissions follow the
        // account's real staff profile (like the web). The login combo is only
        // a fallback for demo/offline mode where there is no real account.
        String effectiveRole = resolveEffectiveRole();
        java.util.Set<String> allowedPages = pt.ipvc.estg.desktop.security.RoleMenuPolicy.allowedPages(effectiveRole);

        // Sidebar (LEFT)
        String sessionUser = SessionContext.getUsername() != null ? SessionContext.getUsername() : userRole;
        sidebar = new Sidebar(page -> navigateToPage(page), effectiveRole, sessionUser);
        mainPanel.add(sidebar, BorderLayout.WEST);

        // Right panel: TopBar (NORTH) + Content (CENTER)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(new Color(238, 242, 247));

        // TopBar
        topBar = new TopBar();
        rightPanel.add(topBar, BorderLayout.NORTH);

        // Content panel with CardLayout for switching between pages
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(new Color(238, 242, 247));

        // Add all pages to CardLayout
        contentPanel.add(new BODashboard(), "dashboard");

        boStudents = new BOStudents();
        contentPanel.add(boStudents, "students");

        contentPanel.add(new BOCourses(), "courses");
        contentPanel.add(new BOFlights(), "flights");
        contentPanel.add(new BOAircraft(), "aircraft");
        contentPanel.add(new BOInstructors(), "instructors");
        boMaintenance = new BOMaintenance();
        contentPanel.add(boMaintenance, "maintenance");

        boEvaluations = new BOEvaluations();
        contentPanel.add(boEvaluations, "evaluations");

        boPayments = new BOPayments();
        contentPanel.add(boPayments, "payments");

        contentPanel.add(new BOReports(), "reports");

        // User management only when connected to the API and the profile allows it.
        if (BoDataAccess.useApi() && allowedPages.contains("users")) {
            contentPanel.add(new BOUsers(), "users");
        }

        rightPanel.add(contentPanel, BorderLayout.CENTER);

        mainPanel.add(rightPanel, BorderLayout.CENTER);

        add(mainPanel);
        UITheme.polishTree(mainPanel);
        setVisible(true);

        // Show dashboard by default
        navigateToPage("dashboard");
    }

    private String resolveEffectiveRole() {
        if (SessionContext.isAuthenticated() && SessionContext.isAdmin()) {
            String staffProfile = SessionContext.getStaffProfile();
            if (staffProfile != null && !staffProfile.isBlank()) {
                return staffProfile;
            }
        }
        return userRole;
    }

    private void navigateToPage(String page) {
        // Update title dynamically
        String pageTitle = switch (page) {
            case "dashboard" -> "Dashboard";
            case "students" -> "Gestão de Alunos";
            case "courses" -> "Cursos e Módulos";
            case "flights" -> "Agendamento de Voos";
            case "aircraft" -> "Gestão de Aeronaves";
            case "instructors" -> "Instrutores";
            case "maintenance" -> "Manutenção";
            case "evaluations" -> "Avaliações e Exames";
            case "payments" -> "Pagamentos";
            case "reports" -> "Relatórios";
            case "users" -> "Gestão de Utilizadores";
            default -> "Dashboard";
        };

        if (topBar != null) {
            topBar.setPageTitle(pageTitle);
        }
        if (sidebar != null) {
            sidebar.setActivePage(page);
        }

        activePageKey = page;

        // Switch to page in CardLayout
        cardLayout.show(contentPanel, page);
    }

    private JPanel createPlaceholder(String name) {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(240, 244, 248));
        JLabel label = new JLabel(name + " - Em desenvolvimento");
        label.setFont(new Font("Inter", Font.PLAIN, 18));
        panel.add(label);
        return panel;
    }
}

