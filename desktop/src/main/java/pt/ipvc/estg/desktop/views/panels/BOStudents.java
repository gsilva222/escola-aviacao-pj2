package pt.ipvc.estg.desktop.views.panels;

import pt.ipvc.estg.desktop.controllers.StudentController;
import pt.ipvc.estg.desktop.controllers.CourseController;
import pt.ipvc.estg.desktop.views.dialogs.StudentEditDialog;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.Course;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Painel para gerenciar Estudantes - COM FILTROS AVANÇADOS
 */
public class BOStudents extends JPanel {
    
    private final StudentController studentController;
    private final CourseController courseController;
    private JTable tableData;
    private DefaultTableModel tableModel;
    private JTextField textName;
    private JTextField textEmail;
    private JTextField textPhone;
    private JComboBox<String> comboStatus;
    
    // Filtros
    private JTextField searchField;
    private JComboBox<String> comboCourseFilter;
    private JComboBox<String> comboStatusFilter;
    
    // Paginação
    private int currentPage = 0;
    private int pageSize = 10;
    private List<Student> filteredStudents;
    private JLabel labelPageInfo;
    private JButton btnPrevPage;
    private JButton btnNextPage;
    
    // Estatísticas
    private JLabel labelStats;
    
    public BOStudents() {
        this.studentController = new StudentController();
        this.courseController = new CourseController();
        initializeUI();
        loadData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header com estatísticas
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Filtros
        JPanel filterPanel = createFilterPanel();
        add(filterPanel, BorderLayout.PAGE_START);
        
        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.PAGE_START);
        
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        JPanel paginationPanel = createPaginationPanel();
        add(paginationPanel, BorderLayout.PAGE_END);
        
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        
        labelStats = new JLabel("Carregando...");
        labelStats.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(labelStats);
        
        return panel;
    }
    
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 3, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("🔍 Filtros Avançados"));
        
        // Search bar
        panel.add(new JLabel("Pesquisar (Nome/Email):"));
        searchField = new JTextField();
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void removeUpdate(DocumentEvent e) { applyFilters(); }
            @Override
            public void changedUpdate(DocumentEvent e) { applyFilters(); }
        });
        panel.add(searchField);
        
        // Filtro por Curso
        panel.add(new JLabel("Curso:"));
        comboCourseFilter = new JComboBox<>();
        comboCourseFilter.addItem("-- Todos os Cursos --");
        comboCourseFilter.addActionListener(e -> applyFilters());
        populateCourseFilter();
        panel.add(comboCourseFilter);
        
        // Filtro por Status
        panel.add(new JLabel("Estado:"));
        String[] statusOptions = {"-- Todos os Estados --", "active", "suspended", "completed"};
        comboStatusFilter = new JComboBox<>(statusOptions);
        comboStatusFilter.addActionListener(e -> applyFilters());
        panel.add(comboStatusFilter);
        
        return panel;
    }
    
    private JPanel createPaginationPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        
        btnPrevPage = new JButton("← Anterior");
        btnPrevPage.addActionListener(e -> previousPage());
        panel.add(btnPrevPage);
        
        labelPageInfo = new JLabel("Página 1 de 1");
        labelPageInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        panel.add(labelPageInfo);
        
        btnNextPage = new JButton("Próxima →");
        btnNextPage.addActionListener(e -> nextPage());
        panel.add(btnNextPage);
        
        return panel;
    }
    
    private void populateCourseFilter() {
        List<Course> courses = courseController.listarCursos();
        for (Course course : courses) {
            comboCourseFilter.addItem(course.getName());
        }
    }
    
    private void applyFilters() {
        currentPage = 0;
        String searchText = searchField.getText().toLowerCase();
        String courseFilter = (String) comboCourseFilter.getSelectedItem();
        String statusFilter = (String) comboStatusFilter.getSelectedItem();
        
        List<Student> allStudents = studentController.listarEstudantes();
        
        // Aplicar filtros
        filteredStudents = allStudents.stream()
            .filter(s -> {
                boolean matchesSearch = searchText.isEmpty() ||
                    s.getName().toLowerCase().contains(searchText) ||
                    s.getEmail().toLowerCase().contains(searchText);
                
                boolean matchesCourse = courseFilter.equals("-- Todos os Cursos --") ||
                    (s.getCourse() != null && s.getCourse().getName().equals(courseFilter));
                
                boolean matchesStatus = statusFilter.equals("-- Todos os Estados --") ||
                    s.getStatus().equals(statusFilter);
                
                return matchesSearch && matchesCourse && matchesStatus;
            })
            .collect(Collectors.toList());
        
        updateStats();
        displayPage();
    }
    
    private void updateStats() {
        List<Student> allStudents = studentController.listarEstudantes();
        long activeCount = allStudents.stream().filter(s -> "active".equals(s.getStatus())).count();
        labelStats.setText(String.format("📊 %d alunos registados · %d ativos", allStudents.size(), activeCount));
    }
    
    private void displayPage() {
        tableModel.setRowCount(0);
        
        int startIdx = currentPage * pageSize;
        int endIdx = Math.min(startIdx + pageSize, filteredStudents.size());
        
        if (startIdx < filteredStudents.size()) {
            for (int i = startIdx; i < endIdx; i++) {
                Student estudante = filteredStudents.get(i);
                Object[] row = {
                    estudante.getId(),
                    estudante.getName(),
                    estudante.getEmail(),
                    estudante.getPhone(),
                    estudante.getStatus(),
                    estudante.getProgress(),
                    String.format("%.1f h", estudante.getFlightHours()),
                    estudante.getPaymentStatus()
                };
                tableModel.addRow(row);
            }
        }
        
        // Atualizar paginação
        int totalPages = (int) Math.ceil((double) filteredStudents.size() / pageSize);
        if (totalPages == 0) totalPages = 1;
        
        labelPageInfo.setText(String.format("Página %d de %d (%d registos)", 
            currentPage + 1, totalPages, filteredStudents.size()));
        
        btnPrevPage.setEnabled(currentPage > 0);
        btnNextPage.setEnabled(currentPage < totalPages - 1);
    }
    
    private void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            displayPage();
        }
    }
    
    private void nextPage() {
        int totalPages = (int) Math.ceil((double) filteredStudents.size() / pageSize);
        if (currentPage < totalPages - 1) {
            currentPage++;
            displayPage();
        }
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Estudantes"));
        
        String[] columns = {"ID", "Nome", "Email", "Telemóvel", "Estado", "Progresso %", "Horas Voo", "Pagamento"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableData = new JTable(tableModel);
        tableData.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        tableData.setRowHeight(25);
        
        // Custom renderers para badges coloridos
        tableData.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer()); // Estado
        tableData.getColumnModel().getColumn(7).setCellRenderer(new PaymentBadgeRenderer()); // Pagamento
        
        // Adicionar duplo-clique para abrir ficha do aluno
        tableData.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = tableData.getSelectedRow();
                    if (selectedRow >= 0) {
                        Integer studentId = (Integer) tableModel.getValueAt(selectedRow, 0);
                        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(BOStudents.this);
                        new BOStudentFile(parentFrame, studentId).setVisible(true);
                    }
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(tableData);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Custom renderer para Status badges
    private static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
            
            JLabel label = new JLabel(String.valueOf(value));
            label.setOpaque(true);
            label.setHorizontalAlignment(JLabel.CENTER);
            
            String status = String.valueOf(value);
            switch (status) {
                case "active":
                    label.setBackground(new Color(220, 252, 231)); // Verde claro
                    label.setForeground(new Color(22, 163, 74)); // Verde escuro
                    label.setText("✓ Ativo");
                    break;
                case "suspended":
                    label.setBackground(new Color(254, 243, 199)); // Laranja claro
                    label.setForeground(new Color(217, 119, 6)); // Laranja escuro
                    label.setText("⊘ Suspenso");
                    break;
                case "completed":
                    label.setBackground(new Color(219, 234, 254)); // Azul claro
                    label.setForeground(new Color(29, 78, 216)); // Azul escuro
                    label.setText("✓ Concluído");
                    break;
                default:
                    label.setBackground(Color.WHITE);
                    label.setForeground(Color.BLACK);
            }
            
            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            }
            
            return label;
        }
    }
    
    // Custom renderer para Payment badges
    private static class PaymentBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
            boolean isSelected, boolean hasFocus, int row, int column) {
            
            JLabel label = new JLabel(String.valueOf(value));
            label.setOpaque(true);
            label.setHorizontalAlignment(JLabel.CENTER);
            
            String payment = String.valueOf(value);
            switch (payment) {
                case "up_to_date":
                    label.setBackground(new Color(220, 252, 231)); // Verde claro
                    label.setForeground(new Color(22, 163, 74)); // Verde escuro
                    label.setText("✓ Regularizado");
                    break;
                case "pending":
                    label.setBackground(new Color(254, 243, 199)); // Laranja claro
                    label.setForeground(new Color(217, 119, 6)); // Laranja escuro
                    label.setText("⚠ Pendente");
                    break;
                case "overdue":
                    label.setBackground(new Color(254, 226, 226)); // Vermelho claro
                    label.setForeground(new Color(220, 38, 38)); // Vermelho escuro
                    label.setText("✕ Em Atraso");
                    break;
                default:
                    label.setBackground(Color.WHITE);
                    label.setForeground(Color.BLACK);
            }
            
            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            }
            
            return label;
        }
    }
    
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("➕ Novo Estudante"));
        
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
        comboStatus = new JComboBox<>(status);
        panel.add(comboStatus);
        
        JButton btnCriar = new JButton("Criar Estudante");
        btnCriar.addActionListener(e -> criarEstudante());
        panel.add(btnCriar);
        
        JButton btnExport = new JButton("Exportar PDF");
        btnExport.addActionListener(e -> exportarPDF());
        panel.add(btnExport);
        
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
        filteredStudents = studentController.listarEstudantes();
        currentPage = 0;
        updateStats();
        displayPage();
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
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(BOStudents.this);
            StudentEditDialog editDialog = new StudentEditDialog(parentFrame, estudante);
            editDialog.setVisible(true);
            
            // Recarregar dados se foi salvo
            if (editDialog.isSaved()) {
                loadData();
            }
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
    
    private void exportarPDF() {
        JOptionPane.showMessageDialog(this, "Funcionalidade de exportação PDF será implementada.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void limparCampos() {
        textName.setText("");
        textEmail.setText("");
        textPhone.setText("");
        comboStatus.setSelectedIndex(0);
    }
}
