package pt.ipvc.estg.desktop.views.panels;

import pt.ipvc.estg.desktop.controllers.InstructorController;
import pt.ipvc.estg.entities.Instructor;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Painel para gerenciar Instrutores
 */
public class BOInstructors extends JPanel {

    private static final Color PAGE_BG = new Color(238, 242, 247);
    private static final Color TITLE = new Color(15, 35, 68);
    private static final Color MUTED = new Color(100, 116, 139);

    private final InstructorController instructorController;
    private JTable tableData;
    private DefaultTableModel tableModel;
    private JTextField textName;
    private JTextField textEmail;
    private JTextField textPhone;
    private JComboBox comboLicense;
    
    public BOInstructors() {
        this.instructorController = new InstructorController();
        initializeUI();
        loadData();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(PAGE_BG);
        setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("Gestao de Instrutores");
        title.setForeground(TITLE);
        title.setFont(new Font("Inter", Font.BOLD, 22));
        JLabel subtitle = new JLabel("Licencas, contactos e estado operacional");
        subtitle.setForeground(MUTED);
        subtitle.setFont(new Font("Inter", Font.PLAIN, 13));
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        titles.add(title);
        titles.add(subtitle);
        header.add(titles, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        JPanel body = new JPanel(new BorderLayout(10, 10));
        body.setOpaque(false);

        JPanel inputPanel = createInputPanel();
        body.add(inputPanel, BorderLayout.NORTH);
        
        JPanel tablePanel = createTablePanel();
        body.add(tablePanel, BorderLayout.CENTER);

        JPanel buttonPanel = createButtonPanel();
        body.add(buttonPanel, BorderLayout.SOUTH);
        add(body, BorderLayout.CENTER);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Novo Instrutor"));
        
        panel.add(new JLabel("Nome:"));
        textName = new JTextField();
        panel.add(textName);
        
        panel.add(new JLabel("Email:"));
        textEmail = new JTextField();
        panel.add(textEmail);
        
        panel.add(new JLabel("Telemóvel:"));
        textPhone = new JTextField();
        panel.add(textPhone);
        
        panel.add(new JLabel("Licença:"));
        String[] licenses = {"CPL", "IR", "FI", "CFII", "ATPL"};
        comboLicense = new JComboBox(licenses);
        panel.add(comboLicense);
        
        JButton btnCriar = new JButton("Criar Instrutor");
        btnCriar.addActionListener(e -> criarInstrutor());
        panel.add(btnCriar);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Instrutores"));
        
        String[] columns = {"ID", "Nome", "Licença", "Email", "Telemóvel", "Horas Voo", "Estado", "Especialização"};
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
        
        JButton btnStatus = new JButton("Mudar Status");
        btnStatus.addActionListener(e -> mudarStatus());
        panel.add(btnStatus);
        
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarSelecionado());
        panel.add(btnEliminar);
        
        return panel;
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Instructor> instrutores = instructorController.listarInstrutores();
        for (Instructor instrutor : instrutores) {
            Object[] row = {
                instrutor.getId(),
                instrutor.getName(),
                instrutor.getLicense(),
                instrutor.getEmail(),
                instrutor.getPhone(),
                instrutor.getFlightHours(),
                instrutor.getStatus(),
                instrutor.getSpecialization()
            };
            tableModel.addRow(row);
        }
    }
    
    private void criarInstrutor() {
        try {
            String name = textName.getText().trim();
            String license = comboLicense.getSelectedItem().toString();
            String specialization = "General";
            
            instructorController.criarInstrutor(name, license, specialization);
            
            JOptionPane.showMessageDialog(this, "Instrutor criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparCampos();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao criar instrutor: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editarSelecionado() {
        int selectedRow = tableData.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um instrutor para editar!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        instructorController.obterInstrutor(id).ifPresent(instrutor -> {
            textName.setText(instrutor.getName());
            textEmail.setText(instrutor.getEmail() != null ? instrutor.getEmail() : "");
            textPhone.setText(instrutor.getPhone() != null ? instrutor.getPhone() : "");
            comboLicense.setSelectedItem(instrutor.getLicense());
        });
    }
    
    private void mudarStatus() {
        int selectedRow = tableData.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um instrutor!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String[] statusOptions = {"active", "inactive"};
        int choice = JOptionPane.showOptionDialog(this, "Escolha o novo status:", "Status",
            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, statusOptions, statusOptions[0]);
        
        if (choice >= 0) {
            instructorController.atualizarStatus(id, statusOptions[choice]);
            JOptionPane.showMessageDialog(this, "Status atualizado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        }
    }
    
    private void eliminarSelecionado() {
        int selectedRow = tableData.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um instrutor para eliminar!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Confirma eliminação?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            instructorController.eliminarInstrutor(id);
            JOptionPane.showMessageDialog(this, "Instrutor eliminado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        }
    }
    
    private void limparCampos() {
        textName.setText("");
        textEmail.setText("");
        textPhone.setText("");
        comboLicense.setSelectedIndex(0);
    }
}
