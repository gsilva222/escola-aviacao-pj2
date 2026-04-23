package pt.ipvc.estg.desktop.views.frontoffice;

import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.desktop.controllers.FlightController;

import javax.swing.*;
import java.util.List;

/**
 * Calendário de Agendamentos do Aluno (FrontOffice)
 * Mostra voos agendados do aluno
 */
public class FOSchedule extends JPanel {

    private final Student student;
    private final FlightController flightController;

    public FOSchedule(Student student) {
        this.student = student;
        this.flightController = new FlightController();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new java.awt.BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new java.awt.Color(245, 245, 245));

        // Tabela de agendamentos
        String[] columns = {"Data", "Status", "Tipo Voo", "Instrutor", "Aeronave", "Duração Planejada"};
        
        List<Flight> scheduledFlights = flightController.obterVoosPorEstudante(student.getId());
        Object[][] data = new Object[scheduledFlights != null ? scheduledFlights.size() : 0][];

        if (scheduledFlights != null) {
            for (int i = 0; i < scheduledFlights.size(); i++) {
                Flight flight = scheduledFlights.get(i);
                data[i] = new Object[]{
                        flight.getFlightDate(),
                        flight.getStatus() != null ? flight.getStatus() : "Agendado",
                        flight.getFlightType() != null ? flight.getFlightType() : "Prático",
                        flight.getInstructor() != null ? flight.getInstructor().getName() : "A designar",
                        flight.getAircraft() != null ? flight.getAircraft().getRegistration() : "A designar",
                        flight.getDuration() != null ? String.format("%.1fh", flight.getDuration()) : "1.5h"
                };
            }
        }

        JTable table = new JTable(data, columns);
        table.setRowHeight(25);
        table.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel tablePanel = new JPanel(new java.awt.BorderLayout());
        tablePanel.setBackground(java.awt.Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Meus Agendamentos"));
        tablePanel.add(scrollPane, java.awt.BorderLayout.CENTER);

        add(tablePanel, java.awt.BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);
        JButton scheduleBtn = new JButton("+ Agendar Novo");
        scheduleBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, 
                "Agendamento de Voo\n\nFuncionalidade em desenvolvimento.\n\nPara agendar, entre em contato com o instrutor."));
        buttonPanel.add(scheduleBtn);

        add(buttonPanel, java.awt.BorderLayout.SOUTH);
    }
}
