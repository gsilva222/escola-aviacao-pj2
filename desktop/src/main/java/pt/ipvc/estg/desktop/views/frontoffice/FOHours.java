package pt.ipvc.estg.desktop.views.frontoffice;

import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.desktop.controllers.FlightController;

import javax.swing.*;
import java.util.List;

/**
 * Minhas Horas de Voo do Aluno (FrontOffice)
 * Mostra horas de voo realizadas do aluno
 */
public class FOHours extends JPanel {

    private final Student student;
    private final FlightController flightController;

    public FOHours(Student student) {
        this.student = student;
        this.flightController = new FlightController();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new java.awt.BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new java.awt.Color(245, 245, 245));

        // Calcular horas
        List<Flight> myFlights = flightController.obterVoosPorEstudante(student.getId());
        
        double totalHours = myFlights == null ? 0 : myFlights.stream()
                .filter(f -> f.getDuration() != null && "completed".equals(f.getStatus()))
                .mapToDouble(Flight::getDuration)
                .sum();
        
        double practicalHours = myFlights == null ? 0 : myFlights.stream()
                .filter(f -> f.getDuration() != null && "completed".equals(f.getStatus()) 
                        && ("prático".equals(f.getFlightType()) || "practical".equals(f.getFlightType())))
                .mapToDouble(Flight::getDuration)
                .sum();

        // Grid de cards com horas
        JPanel hoursPanel = new JPanel(new java.awt.GridLayout(2, 2, 15, 15));
        hoursPanel.setOpaque(false);
        
        hoursPanel.add(createHoursCard("Horas Totais de Voo", String.format("%.1f h", totalHours), "de 200h", 
                (int)totalHours, 200));
        hoursPanel.add(createHoursCard("Voos Práticos", String.format("%.1f h", practicalHours), "de 150h", 
                (int)practicalHours, 150));
        hoursPanel.add(createHoursCard("Voos Completados", String.valueOf(myFlights == null ? 0 : 
                (int) myFlights.stream().filter(f -> "completed".equals(f.getStatus())).count()), 
                "de 50 voos", 
                (int) (myFlights == null ? 0 : myFlights.stream().filter(f -> "completed".equals(f.getStatus())).count()), 
                50));
        hoursPanel.add(createHoursCard("Voos Agendados", String.valueOf(myFlights == null ? 0 : 
                (int) myFlights.stream().filter(f -> "scheduled".equals(f.getStatus())).count()), 
                "próximos", 
                (int) (myFlights == null ? 0 : myFlights.stream().filter(f -> "scheduled".equals(f.getStatus())).count()), 
                10));

        add(hoursPanel, java.awt.BorderLayout.NORTH);

        // Tabela de breakdown
        String[] columns = {"Data", "Tipo", "Duração", "Instrutor", "Status"};
        
        Object[][] data = new Object[myFlights != null ? myFlights.size() : 0][];
        
        if (myFlights != null) {
            for (int i = 0; i < myFlights.size(); i++) {
                Flight flight = myFlights.get(i);
                data[i] = new Object[]{
                        flight.getFlightDate(),
                        flight.getFlightType() != null ? flight.getFlightType() : "---",
                        flight.getDuration() != null ? String.format("%.1f h", flight.getDuration()) : "---",
                        flight.getInstructor() != null ? flight.getInstructor().getName() : "---",
                        flight.getStatus() != null ? flight.getStatus() : "---"
                };
            }
        }

        JTable table = new JTable(data, columns);
        table.setRowHeight(25);
        table.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel tablePanel = new JPanel(new java.awt.BorderLayout());
        tablePanel.setBackground(java.awt.Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Histórico de Horas"));
        tablePanel.add(scrollPane, java.awt.BorderLayout.CENTER);

        add(tablePanel, java.awt.BorderLayout.CENTER);
    }

    private JPanel createHoursCard(String type, String current, String required, int currentVal, int requiredVal) {
        JPanel card = new JPanel();
        card.setBackground(java.awt.Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(java.awt.Color.LIGHT_GRAY, 1));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel typeLabel = new JLabel(type);
        typeLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        typeLabel.setForeground(new java.awt.Color(15, 35, 68));
        card.add(typeLabel);

        JLabel currentLabel = new JLabel(current);
        currentLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        currentLabel.setForeground(new java.awt.Color(21, 101, 192));
        card.add(currentLabel);

        JLabel requiredLabel = new JLabel(required);
        requiredLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 11));
        requiredLabel.setForeground(new java.awt.Color(150, 150, 150));
        card.add(requiredLabel);

        card.add(Box.createVerticalStrut(10));

        JProgressBar progressBar = new JProgressBar(0, Math.max(requiredVal, currentVal + 1));
        progressBar.setValue(Math.min(currentVal, requiredVal));
        progressBar.setStringPainted(true);
        int percentage = requiredVal > 0 ? Math.round((currentVal * 100.0f / requiredVal)) : 0;
        progressBar.setString(percentage + "%");
        card.add(progressBar);
        
        return card;
    }
}
