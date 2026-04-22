package pt.ipvc.estg.desktop.views.panels;

import pt.ipvc.estg.desktop.controllers.StudentController;
import pt.ipvc.estg.desktop.controllers.CourseController;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.Course;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Painel para gerenciar Estudantes
 */
public class BOStudents extends JPanel {
    
    private final StudentController studentController;
    private final CourseController courseController;
    private JTable tableData;
    private DefaultTableModel tableModel;
    private JTextField textName;
    private JTextField textEmail;
    private JTextField textPhone;
    private JComboBox comboStatus;
    
    public BOStudents() {
        this.studentController = new StudentController();
        this.courseController = new CourseController();
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
        panel.setBorder(BorderFactory.createTitledBorder("Novo Estudante"));
        
        panel.add(new JLabel("Nome:"));
        textName = new JTextField();
        panel.add(textName);
        
        panel.add(new JLabel("Email:"));
        textEmail = new JTextField();
        panel.add(textEmail);
        
        panel.add(new JLabel("Telemóvel:"));
        textPhone = new JTextField();
        panel.add(textPhone);
        
        panel.add(new JLabel("Estado:"));
        String[] status = {"active", "suspended", "completed"};
        comboStatus = new JComboBox(status);
        panel.add(comboStatus);
        
        JButton btnCriar = new JButton("Criar Estudante");
        btnCriar.addActionListener(e -> criarEstudante());
        panel.add(btnCriar);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Estudantes"));
        
        String[] columns = {"ID", "Nome", "Email", "Telemóvel", "Estado", "Progresso %", "Horas Voo", "Pagamento"};
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
        
        JButton btnEditar = new JButton("Editar");
        btnEditar.addActionListener(e -> editarSelecionado());
        panel.add(btnEditar);
        
        JButton btnProgresso = new JButton("Atualizar Progresso");
        btnProgresso.addActionListener(e -> atualizarProgresso());
        panel.add(btnProgresso);
        
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarSelecionado());
        panel.add(btnEliminar);
        
        return panel;
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Student> estudantes = studentController.listarEstudantes();
        for (Student estudante : estudantes) {
            Object[] row = {
                estudante.getId(),
                estudante.getName(),
                estudante.getEmail(),
                estudante.getPhone(),
                estudante.getStatus(),
                estudante.getProgress(),
                estudante.getFlightHours(),
                estudante.getPaymentStatus()
            };
            tableModel.addRow(row);
        }
    }
    
    private void criarEstudante() {
        try {
            String name = textName.getText().trim();
            String email = textEmail.getText().trim();
            
            List<Course> cursos = courseController.listarCursos();
            if (cursos.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nenhum curso disponível!", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Course cursoSelecionado = cursos.get(0);
            studentController.criarEstudante(name, email, cursoSelecionado);
            
            JOptionPane.showMessageDialog(this, "Estudante criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparCampos();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao criar estudante: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editarSelecionado() {
        int selectedRow = tableData.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um estudante para editar!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        studentController.obterEstudante(id).ifPresent(estudante -> {
            textName.setText(estudante.getName());
            textEmail.setText(estudante.getEmail());
            textPhone.setText(estudante.getPhone() != null ? estudante.getPhone() : "");
            comboStatus.setSelectedItem(estudante.getStatus());
        });
    }
    
    private void atualizarProgresso() {
        int selectedRow = tableData.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um estudante!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String progresso = JOptionPane.showInputDialog(this, "Novo progresso (0-100):", "50");
        
        try {
            Integer novoProgresso = Integer.parseInt(progresso);
            studentController.atualizarProgresso(id, novoProgresso);
            JOptionPane.showMessageDialog(this, "Progresso atualizado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valor inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarSelecionado() {
        int selectedRow = tableData.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um estudante para eliminar!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Confirma eliminação?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            studentController.eliminarEstudante(id);
            JOptionPane.showMessageDialog(this, "Estudante eliminado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        }
    }
    
    private void limparCampos() {
        textName.setText("");
        textEmail.setText("");
        textPhone.setText("");
        comboStatus.setSelectedIndex(0);
    }
}
