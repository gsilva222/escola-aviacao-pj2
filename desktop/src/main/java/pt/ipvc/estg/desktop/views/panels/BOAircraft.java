package pt.ipvc.estg.desktop.views.panels;

import pt.ipvc.estg.desktop.controllers.AircraftController;
import pt.ipvc.estg.entities.Aircraft;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Painel para gerenciar Aviões
 */
public class BOAircraft extends JPanel {
    
    private final AircraftController aircraftController;
    private JTable tableData;
    private DefaultTableModel tableModel;
    private JTextField textRegistration;
    private JTextField textModel;
    private JComboBox comboType;
    private JComboBox comboStatus;
    
    public BOAircraft() {
        this.aircraftController = new AircraftController();
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
        panel.setBorder(BorderFactory.createTitledBorder("Novo Avião"));
        
        panel.add(new JLabel("Matrícula:"));
        textRegistration = new JTextField();
        panel.add(textRegistration);
        
        panel.add(new JLabel("Modelo:"));
        textModel = new JTextField();
        panel.add(textModel);
        
        panel.add(new JLabel("Tipo:"));
        String[] types = {"Single Engine", "Multi Engine", "Helicopter", "Sim"};
        comboType = new JComboBox(types);
        panel.add(comboType);
        
        panel.add(new JLabel("Estado:"));
        String[] status = {"operational", "maintenance", "grounded"};
        comboStatus = new JComboBox(status);
        panel.add(comboStatus);
        
        JButton btnCriar = new JButton("Criar Avião");
        btnCriar.addActionListener(e -> criarAviao());
        panel.add(btnCriar);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Aviões"));
        
        String[] columns = {"ID", "Matrícula", "Modelo", "Tipo", "Estado", "Horas Voo", "Combustível %", "Localização"};
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
        
        JButton btnOperacionais = new JButton("Aviões Operacionais");
        btnOperacionais.addActionListener(e -> mostrarOperacionais());
        panel.add(btnOperacionais);
        
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarSelecionado());
        panel.add(btnEliminar);
        
        return panel;
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Aircraft> avioes = aircraftController.listarAvioes();
        for (Aircraft aviao : avioes) {
            Object[] row = {
                aviao.getId(),
                aviao.getRegistration(),
                aviao.getModel(),
                aviao.getType(),
                aviao.getStatus(),
                aviao.getFlightHours(),
                aviao.getFuelLevel(),
                aviao.getLocation()
            };
            tableModel.addRow(row);
        }
    }
    
    private void criarAviao() {
        try {
            String registration = textRegistration.getText().trim();
            String model = textModel.getText().trim();
            String type = comboType.getSelectedItem().toString();
            
            aircraftController.criarAviao(registration, model, type);
            
            JOptionPane.showMessageDialog(this, "Avião criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            limparCampos();
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao criar avião: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void editarSelecionado() {
        int selectedRow = tableData.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um avião para editar!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        aircraftController.obterAviao(id).ifPresent(aviao -> {
            textRegistration.setText(aviao.getRegistration());
            textModel.setText(aviao.getModel());
            comboType.setSelectedItem(aviao.getType());
            comboStatus.setSelectedItem(aviao.getStatus());
        });
    }
    
    private void mudarStatus() {
        int selectedRow = tableData.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um avião!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String[] statusOptions = {"operational", "maintenance", "grounded"};
        int choice = JOptionPane.showOptionDialog(this, "Escolha o novo status:", "Status",
            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, statusOptions, statusOptions[0]);
        
        if (choice >= 0) {
            aircraftController.atualizarStatus(id, statusOptions[choice]);
            JOptionPane.showMessageDialog(this, "Status atualizado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        }
    }
    
    private void mostrarOperacionais() {
        long total = aircraftController.obterTotalAviõesOperacionais();
        JOptionPane.showMessageDialog(this, "Aviões operacionais: " + total, "Aviões Operacionais", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void eliminarSelecionado() {
        int selectedRow = tableData.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um avião para eliminar!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Confirma eliminação?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            aircraftController.eliminarAviao(id);
            JOptionPane.showMessageDialog(this, "Avião eliminado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        }
    }
    
    private void limparCampos() {
        textRegistration.setText("");
        textModel.setText("");
        comboType.setSelectedIndex(0);
        comboStatus.setSelectedIndex(0);
    }
}
