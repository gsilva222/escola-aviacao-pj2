package pt.ipvc.estg.desktop.views.panels;

import pt.ipvc.estg.desktop.controllers.EvaluationController;
import pt.ipvc.estg.desktop.controllers.StudentController;
import pt.ipvc.estg.desktop.controllers.CourseController;
import pt.ipvc.estg.entities.Evaluation;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.Course;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Painel para gerenciar Avaliações
 */
public class BOEvaluations extends JPanel {
    
    private final EvaluationController evaluationController;
    private final StudentController studentController;
    private final CourseController courseController;
    private JTable tableData;
    private DefaultTableModel tableModel;
    private JComboBox comboStudent;
    private JComboBox comboCourse;
    private JTextField textExamName;
    private JComboBox comboEvalType;
    
    public BOEvaluations() {
        this.evaluationController = new EvaluationController();
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
        panel.setBorder(BorderFactory.createTitledBorder("Nova Avaliação"));
        
        panel.add(new JLabel("Estudante:"));
        comboStudent = new JComboBox();
        carregarEstudantes();
        panel.add(comboStudent);
        
        panel.add(new JLabel("Curso:"));
        comboCourse = new JComboBox();
        carregarCursos();
        panel.add(comboCourse);
        
        panel.add(new JLabel("Nome do Exame:"));
        textExamName = new JTextField();
        panel.add(textExamName);
        
        panel.add(new JLabel("Tipo Avaliação:"));
        String[] types = {"theoretical", "practical", "simulator"};
        comboEvalType = new JComboBox(types);
        panel.add(comboEvalType);
        
        JButton btnCriar = new JButton("Criar Avaliação");
        btnCriar.addActionListener(e -> criarAvaliacao());
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
    
    private void carregarCursos() {
        comboCourse.removeAllItems();
        List<Course> cursos = courseController.listarCursos();
        for (Course c : cursos) {
            comboCourse.addItem(c.getName() + " (ID: " + c.getId() + ")");
        }
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Avaliações"));
        
        String[] columns = {"ID", "Estudante", "Curso", "Exame", "Data", "Nota", "Estado", "Tipo"};
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
        
        JButton btnRegistar = new JButton("Registar Resultado");
        btnRegistar.addActionListener(e -> registarResultado());
        panel.add(btnRegistar);
        
        JButton btnTaxaAprovacao = new JButton("Taxa Aprovação");
        btnTaxaAprovacao.addActionListener(e -> mostrarTaxaAprovacao());
        panel.add(btnTaxaAprovacao);
        
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarSelecionado());
        panel.add(btnEliminar);
        
        return panel;
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Evaluation> avaliacoes = evaluationController.listarAvaliacoes();
        for (Evaluation aval : avaliacoes) {
            Object[] row = {
                aval.getId(),
                aval.getStudent() != null ? aval.getStudent().getName() : "N/A",
                aval.getCourse() != null ? aval.getCourse().getName() : "N/A",
                aval.getExamName(),
                aval.getEvaluationDate(),
                aval.getScore(),
                aval.getStatus(),
                aval.getEvaluationType()
            };
            tableModel.addRow(row);
        }
    }
    
    private void criarAvaliacao() {
        try {
            String studentStr = comboStudent.getSelectedItem().toString();
            Integer studentId = Integer.parseInt(studentStr.substring(studentStr.lastIndexOf(":") + 2, studentStr.length() - 1));
            
            String courseStr = comboCourse.getSelectedItem().toString();
            Integer courseId = Integer.parseInt(courseStr.substring(courseStr.lastIndexOf(":") + 2, courseStr.length() - 1));
            
            Student student = studentController.obterEstudante(studentId).orElse(null);
            Course course = courseController.obterCurso(courseId).orElse(null);
            String examName = textExamName.getText().trim();
            
            if (student == null || course == null) {
                JOptionPane.showMessageDialog(this, "Estudante ou Curso não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            evaluationController.criarAvaliacao(student, course, examName);
            
            JOptionPane.showMessageDialog(this, "Avaliação criada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            textExamName.setText("");
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao criar avaliação: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void registarResultado() {
        int selectedRow = tableData.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma avaliação!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String score = JOptionPane.showInputDialog(this, "Nota (0-100):", "75");
        String type = JOptionPane.showInputDialog(this, "Tipo (theoretical/practical/simulator):", "theoretical");
        String notes = JOptionPane.showInputDialog(this, "Notas (opcional):", "");
        
        try {
            Integer skor = Integer.parseInt(score);
            evaluationController.registarResultado(id, skor, type, notes);
            JOptionPane.showMessageDialog(this, "Resultado registado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Nota inválida!", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void mostrarTaxaAprovacao() {
        double taxa = evaluationController.obterTaxaAprovacao();
        JOptionPane.showMessageDialog(this, String.format("Taxa de aprovação: %.2f%%", taxa), "Taxa de Aprovação", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void eliminarSelecionado() {
        int selectedRow = tableData.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma avaliação para eliminar!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Confirma eliminação?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            evaluationController.eliminarAvaliacao(id);
            JOptionPane.showMessageDialog(this, "Avaliação eliminada!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        }
    }
}
