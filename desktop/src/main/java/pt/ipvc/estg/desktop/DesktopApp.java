package pt.ipvc.estg.desktop;

import pt.ipvc.estg.desktop.views.panels.*;
import javax.swing.*;

/**
 * Aplicação principal do Desktop - Escola de Aviação
 * Integra todos os módulos (Cursos, Estudantes, Instrutores, Aviões, Voos, Avaliações, Pagamentos, Manutenções)
 */
public class DesktopApp extends JFrame {
    
    public DesktopApp() {
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("Escola de Aviação - Gestão");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        
        // Criar abas para cada módulo
        JTabbedPane tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("Cursos", new BOCourses());
        tabbedPane.addTab("Estudantes", new BOStudents());
        tabbedPane.addTab("Instrutores", new BOInstructors());
        tabbedPane.addTab("Aviões", new BOAircraft());
        tabbedPane.addTab("Voos", new BOFlights());
        tabbedPane.addTab("Avaliações", new BOEvaluations());
        tabbedPane.addTab("Pagamentos", new BOPayments());
        tabbedPane.addTab("Manutenções", new BOMaintenance());
        
        add(tabbedPane);
        setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DesktopApp());
    }
}
