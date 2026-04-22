package pt.ipvc.estg.desktop.views.panels;

import pt.ipvc.estg.desktop.controllers.MaintenanceController;
import pt.ipvc.estg.desktop.controllers.AircraftController;
import pt.ipvc.estg.entities.Maintenance;
import pt.ipvc.estg.entities.Aircraft;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Painel para gerenciar Manutenções
 */
public class BOMaintenance extends JPanel {
    
    private final MaintenanceController maintenanceController;
    private final AircraftController aircraftController;
    private JTable tableData;
    private DefaultTableModel tableModel;
    private JComboBox comboAircraft;
    private JComboBox comboMaintenanceType;
    private JTextField textDescription;
    private JComboBox comboPriority;
    
    public BOMaintenance() {
        this.maintenanceController = new MaintenanceController();
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
        panel.setBorder(BorderFactory.createTitledBorder("Nova Manutenção"));
        
        panel.add(new JLabel("Avião:"));
        comboAircraft = new JComboBox();
        carregarAvioes();
        panel.add(comboAircraft);
        
        panel.add(new JLabel("Tipo de Manutenção:"));
        String[] types = {"Preventiva", "Corretiva", "Inspeção", "Revisão"};
        comboMaintenanceType = new JComboBox(types);
        panel.add(comboMaintenanceType);
        
        panel.add(new JLabel("Descrição:"));
        textDescription = new JTextField();
        panel.add(textDescription);
        
        panel.add(new JLabel("Prioridade:"));
        String[] priorities = {"low", "medium", "high", "critical"};
        comboPriority = new JComboBox(priorities);
        panel.add(comboPriority);
        
        JButton btnCriar = new JButton("Criar Manutenção");
        btnCriar.addActionListener(e -> criarManutencao());
        panel.add(btnCriar);
        
        return panel;
    }
    
    private void carregarAvioes() {
        comboAircraft.removeAllItems();
        List<Aircraft> avioes = aircraftController.listarAvioes();
        for (Aircraft a : avioes) {
            comboAircraft.addItem(a.getRegistration() + " (ID: " + a.getId() + ")");
        }
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Manutenções"));
        
        String[] columns = {"ID", "Avião", "Tipo", "Data Início", "Data Fim (est.)", "Estado", "Prioridade", "Custo"};
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
        
        JButton btnConcluir = new JButton("Marcar Concluída");
        btnConcluir.addActionListener(e -> marcarConcluida());
        panel.add(btnConcluir);
        
        JButton btnPendentes = new JButton("Manutenções Pendentes");
        btnPendentes.addActionListener(e -> mostrarPendentes());
        panel.add(btnPendentes);
        
        JButton btnCustoTotal = new JButton("Custo Total");
        btnCustoTotal.addActionListener(e -> mostrarCustoTotal());
        panel.add(btnCustoTotal);
        
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarSelecionado());
        panel.add(btnEliminar);
        
        return panel;
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Maintenance> manutencoes = maintenanceController.listarManutenções();
        for (Maintenance m : manutencoes) {
            Object[] row = {
                m.getId(),
                m.getAircraft() != null ? m.getAircraft().getRegistration() : "N/A",
                m.getMaintenanceType(),
                m.getStartDate(),
                m.getEstimatedEndDate(),
                m.getStatus(),
                m.getPriority(),
                m.getCost() != null ? String.format("%.2f €", m.getCost()) : "0.00 €"
            };
            tableModel.addRow(row);
        }
    }
    
    private void criarManutencao() {
        try {
            String aircraftStr = comboAircraft.getSelectedItem().toString();
            Integer aircraftId = Integer.parseInt(aircraftStr.substring(aircraftStr.lastIndexOf(":") + 2, aircraftStr.length() - 1));
            
            Aircraft aircraft = aircraftController.obterAviao(aircraftId).orElse(null);
            String maintenanceType = comboMaintenanceType.getSelectedItem().toString();
            String description = textDescription.getText().trim();
            
            if (aircraft == null) {
                JOptionPane.showMessageDialog(this, "Avião não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            maintenanceController.criarManutencao(aircraft, maintenanceType, description);
            
            JOptionPane.showMessageDialog(this, "Manutenção criada com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            textDescription.setText("");
            loadData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao criar manutenção: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void marcarConcluida() {
        int selectedRow = tableData.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma manutenção!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Marcar como concluída?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            maintenanceController.marcarComoConcluida(id, LocalDate.now());
            JOptionPane.showMessageDialog(this, "Manutenção marcada como concluída!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        }
    }
    
    private void mostrarPendentes() {
        List<Maintenance> pendentes = maintenanceController.obterManutencoesPendentes();
        StringBuilder msg = new StringBuilder("Manutenções Pendentes:\n\n");
        for (Maintenance m : pendentes) {
            msg.append(String.format("%s (%s)\nAvião: %s\nPrioridade: %s\n\n",
                m.getMaintenanceType(),
                m.getStatus(),
                m.getAircraft() != null ? m.getAircraft().getRegistration() : "N/A",
                m.getPriority()));
        }
        JOptionPane.showMessageDialog(this, msg.toString(), "Manutenções Pendentes", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarCustoTotal() {
        Double custo = maintenanceController.obterCustoTotal();
        JOptionPane.showMessageDialog(this, String.format("Custo total de manutenção: %.2f €", custo), "Custo Total", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void eliminarSelecionado() {
        int selectedRow = tableData.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma manutenção para eliminar!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Confirma eliminação?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            maintenanceController.eliminarManutencao(id);
            JOptionPane.showMessageDialog(this, "Manutenção eliminada!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        }
    }
}
