package pt.ipvc.estg.desktop.views.frontoffice;

import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.desktop.views.components.Sidebar;
import pt.ipvc.estg.desktop.views.components.TopBar;

import javax.swing.*;
import java.awt.*;

/**
 * Layout principal do FrontOffice (Perspectiva do Aluno)
 * Similar ao DesktopApp mas com menu simplificado
 * Mantém contexto do aluno autenticado
 */
public class FOLayout extends JFrame {

    private Student currentStudent;
    private CardLayout cardLayout;
    private JPanel contentPanel;

    public FOLayout(Student student) {
        this.currentStudent = student;
        initializeUI();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
    }

    private void initializeUI() {
        // Main layout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // TopBar
        String displayName = currentStudent.getName() != null ? currentStudent.getName() : "Aluno";
        TopBar topBar = new TopBar("Portal do Aluno - " + displayName);
        mainPanel.add(topBar, BorderLayout.NORTH);

        // Sidebar + Content
        JPanel contentAreaPanel = new JPanel(new BorderLayout());

        // Sidebar para FrontOffice
        JPanel sidebar = createFrontOfficeSidebar();
        contentAreaPanel.add(sidebar, BorderLayout.WEST);

        // Content area com CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);

        // Adicionar painéis ao content com contexto do aluno
        contentPanel.add(new FODashboard(currentStudent), "dashboard");
        contentPanel.add(new FOProfile(currentStudent), "profile");
        contentPanel.add(new FOSchedule(currentStudent), "schedule");
        contentPanel.add(new FOFlights(currentStudent), "flights");
        contentPanel.add(new FOEvaluations(currentStudent), "evaluations");
        contentPanel.add(new FOPayments(currentStudent), "payments");
        contentPanel.add(new FODocuments(currentStudent), "documents");
        contentPanel.add(new FOHours(currentStudent), "hours");

        // Mostrar dashboard por defeito
        cardLayout.show(contentPanel, "dashboard");

        contentAreaPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(contentAreaPanel, BorderLayout.CENTER);

        add(mainPanel);
        setTitle("AeroSchool - Portal do Aluno");
    }

    private JPanel createFrontOfficeSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(15, 35, 68));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Menu items
        String[] menuItems = {
                "📊 Dashboard",
                "👤 Meu Perfil",
                "📅 Meus Agendamentos",
                "✈️ Histórico de Voos",
                "📋 Minhas Avaliações",
                "💳 Pagamentos",
                "📄 Documentos",
                "⏱️ Minhas Horas"
        };

        String[] panelNames = {
                "dashboard",
                "profile",
                "schedule",
                "flights",
                "evaluations",
                "payments",
                "documents",
                "hours"
        };

        for (int i = 0; i < menuItems.length; i++) {
            JButton btn = new JButton(menuItems[i]);
            btn.setAlignmentX(Component.LEFT_ALIGNMENT);
            btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
            btn.setBackground(new Color(21, 101, 192));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            String panelName = panelNames[i];
            btn.addActionListener(e -> {
                cardLayout.show(contentPanel, panelName);
            });

            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(5));
        }

        sidebar.add(Box.createVerticalGlue());

        // Logout button
        JButton logoutBtn = new JButton("🚪 Sair");
        logoutBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        logoutBtn.setBackground(new Color(220, 50, 50));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        logoutBtn.addActionListener(e -> handleLogout());

        sidebar.add(logoutBtn);

        return sidebar;
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja sair do Portal do Aluno?",
                "Confirmação",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new FOLogin().setVisible(true);
        }
    }
    
    public Student getCurrentStudent() {
        return currentStudent;
    }
}
