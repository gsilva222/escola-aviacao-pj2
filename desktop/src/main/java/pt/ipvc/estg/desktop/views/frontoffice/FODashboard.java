package pt.ipvc.estg.desktop.views.frontoffice;

import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Evaluation;
import pt.ipvc.estg.desktop.controllers.StudentController;
import pt.ipvc.estg.desktop.controllers.FlightController;
import pt.ipvc.estg.desktop.controllers.EvaluationController;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Dashboard para o Aluno (FrontOffice)
 * Mostra progresso, voos, avaliações e agendamentos do aluno
 */
public class FODashboard extends JPanel {

    private final Student student;
    private final StudentController studentController;
    private final FlightController flightController;
    private final EvaluationController evaluationController;

    public FODashboard(Student student) {
        this.student = student;
        this.studentController = new StudentController();
        this.flightController = new FlightController();
        this.evaluationController = new EvaluationController();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 245));

        // Welcome panel
        JPanel welcomePanel = createWelcomePanel();
        add(welcomePanel, BorderLayout.NORTH);

        // Stats panel
        JPanel statsPanel = createStatsPanel();
        add(statsPanel, BorderLayout.CENTER);
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(21, 101, 192));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel welcomeLabel = new JLabel("Bem-vindo de volta, " + student.getName() + "! ✈️");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        panel.add(welcomeLabel);

        // Calcular progresso do aluno
        Integer progress = student.getProgress() != null ? student.getProgress() : 0;
        String courseInfo = student.getCourse() != null ? student.getCourse().getName() : "Curso não definido";
        
        JLabel statusLabel = new JLabel("Progresso: " + progress + "% completo · Curso: " + courseInfo);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(Color.WHITE);
        panel.add(statusLabel);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 15, 15));
        panel.setOpaque(false);

        // Card 1: Horas Totais
        double totalHours = calculateTotalFlightHours();
        JPanel card1 = createStatCard("Horas Totais", String.format("%.1f h", totalHours), "de 200h necessárias");
        panel.add(card1);

        // Card 2: Voos Realizados
        List<Flight> myFlights = flightController.obterVoosPorEstudante(student.getId());
        int completedFlights = myFlights == null ? 0 : (int) myFlights.stream()
                .filter(f -> "completed".equals(f.getStatus()))
                .count();
        JPanel card2 = createStatCard("Voos Realizados", String.valueOf(completedFlights), 
                "de voos planejados");
        panel.add(card2);

        // Card 3: Avaliações
        List<Evaluation> myEvaluations = evaluationController.obterAvaliaçõesPorEstudante(student.getId());
        int passedEvaluations = myEvaluations == null ? 0 : (int) myEvaluations.stream()
                .filter(e -> "passed".equals(e.getStatus()))
                .count();
        JPanel card3 = createStatCard("Avaliações Passadas", String.valueOf(passedEvaluations), 
                "avaliações concluídas");
        panel.add(card3);

        // Card 4: Taxa de Aprovação
        double approvalRate = myEvaluations == null || myEvaluations.isEmpty() ? 0 : 
                (passedEvaluations * 100.0) / myEvaluations.size();
        JPanel card4 = createStatCard("Taxa de Aprovação", String.format("%.0f%%", approvalRate), 
                student.getProgress() != null && student.getProgress() > 75 ? "acima da média" : "na média");
        panel.add(card4);

        // Progress bar
        JPanel progressPanel = new JPanel();
        progressPanel.setBackground(Color.WHITE);
        progressPanel.setBorder(BorderFactory.createTitledBorder("Progresso do Curso: " + courseInfo));
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setValue(student.getProgress() != null ? student.getProgress() : 0);
        progressBar.setStringPainted(true);
        progressBar.setString(student.getProgress() + "% (" + String.format("%.1f", totalHours) + "h / 200h)");
        progressBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        progressPanel.add(progressBar);

        panel.add(progressPanel);

        // Próximos voos agendados
        JPanel schedulePanel = createUpcomingFlightsPanel();
        panel.add(schedulePanel);

        return panel;
    }

    private JPanel createUpcomingFlightsPanel() {
        JPanel schedulePanel = new JPanel(new BorderLayout());
        schedulePanel.setBackground(Color.WHITE);
        schedulePanel.setBorder(BorderFactory.createTitledBorder("Próximos Voos Agendados"));

        List<Flight> myFlights = flightController.obterVoosPorEstudante(student.getId());
        List<Flight> scheduledFlights = myFlights == null ? List.of() : myFlights.stream()
                .filter(f -> "scheduled".equals(f.getStatus()))
                .sorted((a, b) -> a.getFlightDate().compareTo(b.getFlightDate()))
                .limit(3)
                .toList();

        String[] columns = {"Data", "Hora", "Instrutor", "Tipo", "Origem", "Destino"};
        Object[][] data = new Object[scheduledFlights.size()][];

        for (int i = 0; i < scheduledFlights.size(); i++) {
            Flight flight = scheduledFlights.get(i);
            data[i] = new Object[]{
                    flight.getFlightDate(),
                    "09:00", // Hora exemplo
                    flight.getInstructor() != null ? flight.getInstructor().getName() : "N/A",
                    flight.getFlightType() != null ? flight.getFlightType() : "Prático",
                    flight.getOrigin() != null ? flight.getOrigin() : "OPO",
                    flight.getDestination() != null ? flight.getDestination() : "---"
            };
        }

        JTable table = new JTable(data, columns);
        table.setEnabled(false);
        table.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(table);
        schedulePanel.add(scrollPane, BorderLayout.CENTER);

        return schedulePanel;
    }

    private double calculateTotalFlightHours() {
        List<Flight> myFlights = flightController.obterVoosPorEstudante(student.getId());
        if (myFlights == null) return 0.0;
        
        return myFlights.stream()
                .filter(f -> f.getDuration() != null)
                .mapToDouble(Flight::getDuration)
                .sum();
    }

    private JPanel createStatCard(String title, String value, String subtitle) {
        JPanel card = new JPanel();
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(100, 100, 100));
        card.add(titleLabel);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(new Color(21, 101, 192));
        card.add(valueLabel);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        subtitleLabel.setForeground(new Color(150, 150, 150));
        card.add(subtitleLabel);

        return card;
    }
}
        titleLabel.setForeground(new Color(100, 100, 100));
        card.add(titleLabel);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(new Color(21, 101, 192));
        card.add(valueLabel);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        subtitleLabel.setForeground(new Color(150, 150, 150));
        card.add(subtitleLabel);

        return card;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setOpaque(false);

        JButton btnScheduleFlight = new JButton("Agendar Novo Voo");
        btnScheduleFlight.addActionListener(e -> JOptionPane.showMessageDialog(this, "Funcionalidade em desenvolvimento"));
        panel.add(btnScheduleFlight);

        JButton btnViewMore = new JButton("Ver Mais");
        btnViewMore.addActionListener(e -> JOptionPane.showMessageDialog(this, "Funcionalidade em desenvolvimento"));
        panel.add(btnViewMore);

        return panel;
    }
}
