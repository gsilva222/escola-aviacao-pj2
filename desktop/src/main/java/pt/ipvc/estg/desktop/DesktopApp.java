package pt.ipvc.estg.desktop;

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

    public DesktopApp(String role) {
        this.userRole = role;
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

        // Sidebar (LEFT)
        sidebar = new Sidebar(page -> navigateToPage(page));
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
        contentPanel.add(new BOStudents(), "students");
        contentPanel.add(new BOCourses(), "courses");
        contentPanel.add(new BOFlights(), "flights");
        contentPanel.add(new BOAircraft(), "aircraft");
        contentPanel.add(new BOMaintenance(), "maintenance");
        contentPanel.add(new BOEvaluations(), "evaluations");
        contentPanel.add(new BOPayments(), "payments");
        contentPanel.add(new BOReports(), "reports");

        rightPanel.add(contentPanel, BorderLayout.CENTER);

        mainPanel.add(rightPanel, BorderLayout.CENTER);

        add(mainPanel);
        UITheme.polishTree(mainPanel);
        setVisible(true);

        // Show dashboard by default
        navigateToPage("dashboard");
    }

    private void navigateToPage(String page) {
        // Update title dynamically
        String pageTitle = switch (page) {
            case "dashboard" -> "Dashboard";
            case "students" -> "Gestão de Alunos";
            case "courses" -> "Cursos e Módulos";
            case "flights" -> "Agendamento de Voos";
            case "aircraft" -> "Gestão de Aeronaves";
            case "maintenance" -> "Manutenção";
            case "evaluations" -> "Avaliações e Exames";
            case "payments" -> "Pagamentos";
            case "reports" -> "Relatórios";
            default -> "Dashboard";
        };

        if (topBar != null) {
            topBar.setPageTitle(pageTitle);
        }
        if (sidebar != null) {
            sidebar.setActivePage(page);
        }

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

