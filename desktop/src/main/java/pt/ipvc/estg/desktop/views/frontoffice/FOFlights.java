package pt.ipvc.estg.desktop.views.frontoffice;

import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.desktop.controllers.FlightController;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Histórico de Voos do Aluno (FrontOffice)
 * Mostra voos realizados do aluno autenticado
 */
public class FOFlights extends JPanel {

    private final Student student;
    private final FlightController flightController;

    public FOFlights(Student student) {
        this.student = student;
        this.flightController = new FlightController();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 245));

        // Filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setOpaque(false);
        filterPanel.add(new JLabel("Filtrar por período:"));
        JComboBox<?> periodCombo = new JComboBox<>(new String[]{"Todos", "Este mês", "3 últimos meses", "Último ano"});
        filterPanel.add(periodCombo);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);
        northPanel.add(filterPanel, BorderLayout.NORTH);

        add(northPanel, BorderLayout.NORTH);

        // Tabela de voos
        String[] columns = {"Data", "Status", "Duração", "Aeronave", "Instrutor", "Tipo", "Origem", "Destino"};
        
        List<Flight> myFlights = flightController.obterVoosPorEstudante(student.getId());
        Object[][] data = new Object[myFlights != null ? myFlights.size() : 0][];

        if (myFlights != null) {
            for (int i = 0; i < myFlights.size(); i++) {
                Flight flight = myFlights.get(i);
                data[i] = new Object[]{
                        flight.getFlightDate(),
                        flight.getStatus() != null ? flight.getStatus() : "Agendado",
                        flight.getDuration() != null ? String.format("%.1fh", flight.getDuration()) : "---",
                        flight.getAircraft() != null ? flight.getAircraft().getRegistration() : "N/A",
                        flight.getInstructor() != null ? flight.getInstructor().getName() : "N/A",
                        flight.getFlightType() != null ? flight.getFlightType() : "Prático",
                        flight.getOrigin() != null ? flight.getOrigin() : "---",
                        flight.getDestination() != null ? flight.getDestination() : "---"
                };
            }
        }

        JTable table = new JTable(data, columns);
        table.setRowHeight(25);
        table.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Histórico de Voos"));
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);

        // Resumo de horas
        double totalHours = myFlights == null ? 0 : myFlights.stream()
                .filter(f -> f.getDuration() != null && "completed".equals(f.getStatus()))
                .mapToDouble(Flight::getDuration)
                .sum();

        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBackground(new Color(21, 101, 192));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JLabel summaryLabel = new JLabel("Total de Horas Voadas: " + String.format("%.1f h", totalHours) + 
                " | Voos Completados: " + (myFlights == null ? 0 : (int) myFlights.stream()
                .filter(f -> "completed".equals(f.getStatus())).count()));
        summaryLabel.setFont(new Font("Arial", Font.BOLD, 12));
        summaryLabel.setForeground(Color.WHITE);
        summaryPanel.add(summaryLabel, BorderLayout.CENTER);

        add(summaryPanel, BorderLayout.SOUTH);
    }
}
