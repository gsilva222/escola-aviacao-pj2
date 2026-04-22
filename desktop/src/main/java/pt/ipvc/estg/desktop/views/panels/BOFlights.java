package pt.ipvc.estg.desktop.views.panels;

import pt.ipvc.estg.desktop.controllers.FlightController;
import pt.ipvc.estg.desktop.controllers.StudentController;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Student;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Painel para gerenciar Voos
 */
public class BOFlights extends JPanel {
    
    private final FlightController flightController;
    private final StudentController studentController;
    private JTable tableData;
    private DefaultTableModel tableModel;
    private JComboBox comboStudent;
    private JComboBox comboFlightType;
    private JTextField textDuration;
    private JComboBox comboStatus;
    
    public BOFlights() {
        this.flightController = new FlightController();
        this.studentController = new StudentController();
        initializeUI();
        loadData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);
        
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Novo Voo"));
        
        panel.add(new JLabel("Estudante:"));
        comboStudent = new JComboBox();
        carregarEstudantes();
        panel.add(comboStudent);
        
        panel.add(new JLabel("Tipo de Voo:"));
        String[] types = {"Local", "Navigation", "IFR"};
        comboFlightType = new JComboBox(types);
        panel.add(comboFlightType);
        
        panel.add(new JLabel("Duração (horas):"));
        textDuration = new JTextField();
        panel.add(textDuration);
        
        panel.add(new JLabel("Estado:"));
        String[] status = {"scheduled", "in-progress", "completed", "cancelled"};
        comboStatus = new JComboBox(status);
        panel.add(comboStatus);
        
        JButton btnCriar = new JButton("Criar Voo");
        btnCriar.addActionListener(e -> criarVoo());
        panel.add(btnCriar);
        
        return panel;
    }
    
    private void carregarEstudantes() {
        comboStudent.removeAllItems();
        List<Student> estudantes = studentController.listarEstudantes();
        for (Student e : estudantes) {
            comboStudent.addItem(e.getName() + " (ID: " + e.getId() + ")");
        }
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Voos"));
        
        String[] columns = {"ID", "Estudante", "Instrutor", "Data", "Duração", "Tipo", "Estado", "Nota"};
        tableModel = new DefaultTableModel(columns, 0);
        tableData = new JTable(tableModel);
        tableData.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JScrollPane scrollPane = new JScrollPane(tableData);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton btnRecarregar = new JButton("Recarregar");
        btnRecarregar.addActionListener(e -> loadData());
        panel.add(btnRecarregar);
        
        JButton btnConcluir = new JButton("Marcar Concluído");
        btnConcluir.addActionListener(e -> marcarConcluido());
        panel.add(btnConcluir);
        
        JButton btnDetalhes = new JButton("Ver Detalhes");
        btnDetalhes.addActionListener(e -> verDetalhes());
        panel.add(btnDetalhes);
        
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarSelecionado());
        panel.add(btnEliminar);
        
        return panel;
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Flight> voos = flightController.listarVoos();
        for (Flight voo : voos) {
            Object[] row = {
                voo.getId(),
                voo.getStudent() != null ? voo.getStudent().getName() : "N/A",
                voo.getInstructor() != null ? voo.getInstructor().getName() : "N/A",
                voo.getFlightDate(),
                voo.getDuration(),
                voo.getFlightType(),
                voo.getStatus(),
                voo.getGrade()
            };
            tableModel.addRow(row);
        }
    }
    
    private void criarVoo() {
        try {
            String studentStr = comboStudent.getSelectedItem().toString();
            Integer studentId = Integer.parseInt(studentStr.substring(studentStr.lastIndexOf(":") + 2, studentStr.length() - 1));
            
            Student student = studentController.obterEstudante(studentId).orElse(null);
            if (student == null) {
                JOptionPane.showMessageDialog(this, "Estudante não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String type = comboFlightType.getSelectedItem().toString();
            
            flightController.criarVoo(LocalDate.now(), student, null, null);
            
            JOptionPane.showMessageDialog(this, "Voo criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao criar voo: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void marcarConcluido() {
        int selectedRow = tableData.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um voo!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String duracao = JOptionPane.showInputDialog(this, "Duração (em horas):", "1.5");
        String nota = JOptionPane.showInputDialog(this, "Nota (A/B/C/D/F):", "A");
        
        try {
            Double duration = Double.parseDouble(duracao);
            flightController.marcarComoConcluido(id, duration, nota);
            JOptionPane.showMessageDialog(this, "Voo marcado como concluído!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void verDetalhes() {
        int selectedRow = tableData.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um voo!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        flightController.obterVoo(id).ifPresent(voo -> {
            String detalhes = String.format("ID: %d\nEstudante: %s\nInstrutor: %s\nData: %s\nTipo: %s\nDuração: %.2f h\nEstado: %s\nNota: %s",
                voo.getId(),
                voo.getStudent() != null ? voo.getStudent().getName() : "N/A",
                voo.getInstructor() != null ? voo.getInstructor().getName() : "N/A",
                voo.getFlightDate(),
                voo.getFlightType(),
                voo.getDuration() != null ? voo.getDuration() : 0,
                voo.getStatus(),
                voo.getGrade() != null ? voo.getGrade() : "Sem nota");
            JOptionPane.showMessageDialog(this, detalhes, "Detalhes do Voo", JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    private void eliminarSelecionado() {
        int selectedRow = tableData.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um voo para eliminar!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Confirma eliminação?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            flightController.eliminarVoo(id);
            JOptionPane.showMessageDialog(this, "Voo eliminado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        }
    }
}
