package pt.ipvc.estg.desktop.views.panels;

import pt.ipvc.estg.desktop.controllers.CourseController;
import pt.ipvc.estg.entities.Course;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Painel para gerenciar Cursos
 */
public class BOCourses extends JPanel {
    
    private final CourseController courseController;
    private JTable tableData;
    private DefaultTableModel tableModel;
    private JTextField textName;
    private JTextField textDuration;
    private JTextField textFlightHours;
    private JTextField textTheoreticalHours;
    private JTextField textPrice;
    
    public BOCourses() {
        this.courseController = new CourseController();
        initializeUI();
        loadData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Painel de Entrada
        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);
        
        // Painel de Tabela
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Painel de Botões
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 4, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Novo Curso"));
        
        panel.add(new JLabel("Nome:"));
        textName = new JTextField();
        panel.add(textName);
        
        panel.add(new JLabel("Duração:"));
        textDuration = new JTextField();
        panel.add(textDuration);
        
        panel.add(new JLabel("Horas Voo:"));
        textFlightHours = new JTextField();
        panel.add(textFlightHours);
        
        panel.add(new JLabel("Horas Teóricas:"));
        textTheoreticalHours = new JTextField();
        panel.add(textTheoreticalHours);
        
        panel.add(new JLabel("Preço:"));
        textPrice = new JTextField();
        panel.add(textPrice);
        
        JButton btnCriar = new JButton("Criar Curso");
        btnCriar.addActionListener(e -> criarCurso());
        panel.add(btnCriar);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Cursos"));
        
        String[] columns = {"ID", "Nome", "Duração", "Horas Voo", "Horas Teóricas", "Preço", "Inscritos", "Concluídos"};
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
        
        JButton btnEditar = new JButton("Editar Selecionado");
        btnEditar.addActionListener(e -> editarSelecionado());
        panel.add(btnEditar);
        
        JButton btnEliminar = new JButton("Eliminar Selecionado");
        btnEliminar.addActionListener(e -> eliminarSelecionado());
        panel.add(btnEliminar);
        
        JButton btnDetalhe = new JButton("Ver Detalhe");
        btnDetalhe.addActionListener(e -> verDetalhe());
        panel.add(btnDetalhe);
        
        return panel;
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Course> cursos = courseController.listarCursos();
        for (Course curso : cursos) {
            Object[] row = {
                curso.getId(),
                curso.getName(),
                curso.getDuration(),
                curso.getFlightHours(),
                curso.getTheoreticalHours(),
                curso.getPrice(),
                curso.getEnrolled(),
                curso.getCompleted()
            };
            tableModel.addRow(row);
        }
    }
    
    private void criarCurso() {
        try {
            String name = textName.getText().trim();
            String duration = textDuration.getText().trim();
            Integer flightHours = Integer.parseInt(textFlightHours.getText().trim());
            Integer theoreticalHours = Integer.parseInt(textTheoreticalHours.getText().trim());
            Double price = Double.parseDouble(textPrice.getText().trim());
            
            courseController.criarCurso(name, duration, flightHours, theoreticalHours, price);
            
            JOptionPane.showMessageDialog(this, "Curso criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparCampos();
            loadData();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos corretamente!", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao criar curso: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editarSelecionado() {
        int selectedRow = tableData.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um curso para editar!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        courseController.obterCurso(id).ifPresent(curso -> {
            textName.setText(curso.getName());
            textDuration.setText(curso.getDuration());
            textFlightHours.setText(curso.getFlightHours().toString());
            textTheoreticalHours.setText(curso.getTheoreticalHours().toString());
            textPrice.setText(curso.getPrice().toString());
        });
    }
    
    private void eliminarSelecionado() {
        int selectedRow = tableData.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um curso para eliminar!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que quer eliminar este curso?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            courseController.eliminarCurso(id);
            JOptionPane.showMessageDialog(this, "Curso eliminado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        }
    }
    
    private void verDetalhe() {
        int selectedRow = tableData.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um curso para ver detalhes!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        courseController.obterCurso(id).ifPresent(curso -> {
            String detalhe = String.format("ID: %d\nNome: %s\nDuração: %s\nHoras Voo: %d\nHoras Teóricas: %d\nPreço: %.2f\nInscritos: %d\nConcluídos: %d",
                curso.getId(), curso.getName(), curso.getDuration(), curso.getFlightHours(), 
                curso.getTheoreticalHours(), curso.getPrice(), curso.getEnrolled(), curso.getCompleted());
            JOptionPane.showMessageDialog(this, detalhe, "Detalhes do Curso", JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    private void limparCampos() {
        textName.setText("");
        textDuration.setText("");
        textFlightHours.setText("");
        textTheoreticalHours.setText("");
        textPrice.setText("");
    }
}
