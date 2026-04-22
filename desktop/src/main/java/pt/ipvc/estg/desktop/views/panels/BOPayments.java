package pt.ipvc.estg.desktop.views.panels;

import pt.ipvc.estg.desktop.controllers.PaymentController;
import pt.ipvc.estg.desktop.controllers.StudentController;
import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.entities.Student;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Painel para gerenciar Pagamentos
 */
public class BOPayments extends JPanel {
    
    private final PaymentController paymentController;
    private final StudentController studentController;
    private JTable tableData;
    private DefaultTableModel tableModel;
    private JComboBox comboStudent;
    private JTextField textDescription;
    private JTextField textAmount;
    private JComboBox comboPaymentMethod;
    
    public BOPayments() {
        this.paymentController = new PaymentController();
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
        panel.setBorder(BorderFactory.createTitledBorder("Novo Pagamento"));
        
        panel.add(new JLabel("Estudante:"));
        comboStudent = new JComboBox();
        carregarEstudantes();
        panel.add(comboStudent);
        
        panel.add(new JLabel("Descrição:"));
        textDescription = new JTextField();
        panel.add(textDescription);
        
        panel.add(new JLabel("Valor (€):"));
        textAmount = new JTextField();
        panel.add(textAmount);
        
        panel.add(new JLabel("Método:"));
        String[] methods = {"cash", "card", "transfer", "check"};
        comboPaymentMethod = new JComboBox(methods);
        panel.add(comboPaymentMethod);
        
        JButton btnCriar = new JButton("Criar Pagamento");
        btnCriar.addActionListener(e -> criarPagamento());
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
        panel.setBorder(BorderFactory.createTitledBorder("Pagamentos"));
        
        String[] columns = {"ID", "Estudante", "Descrição", "Valor", "Data Vencimento", "Data Pagamento", "Estado", "Método"};
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
        
        JButton btnRegistar = new JButton("Registar Pagamento");
        btnRegistar.addActionListener(e -> registarPagamento());
        panel.add(btnRegistar);
        
        JButton btnPendentes = new JButton("Pagamentos Pendentes");
        btnPendentes.addActionListener(e -> mostrarPendentes());
        panel.add(btnPendentes);
        
        JButton btnAtrasados = new JButton("Pagamentos Atrasados");
        btnAtrasados.addActionListener(e -> mostrarAtrasados());
        panel.add(btnAtrasados);
        
        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> eliminarSelecionado());
        panel.add(btnEliminar);
        
        return panel;
    }
    
    private void loadData() {
        tableModel.setRowCount(0);
        List<Payment> pagamentos = paymentController.listarPagamentos();
        for (Payment pag : pagamentos) {
            Object[] row = {
                pag.getId(),
                pag.getStudent() != null ? pag.getStudent().getName() : "N/A",
                pag.getDescription(),
                String.format("%.2f €", pag.getAmount()),
                pag.getDueDate(),
                pag.getPaidDate(),
                pag.getStatus(),
                pag.getPaymentMethod()
            };
            tableModel.addRow(row);
        }
    }
    
    private void criarPagamento() {
        try {
            String studentStr = comboStudent.getSelectedItem().toString();
            Integer studentId = Integer.parseInt(studentStr.substring(studentStr.lastIndexOf(":") + 2, studentStr.length() - 1));
            
            Student student = studentController.obterEstudante(studentId).orElse(null);
            String description = textDescription.getText().trim();
            Double amount = Double.parseDouble(textAmount.getText().trim());
            
            if (student == null) {
                JOptionPane.showMessageDialog(this, "Estudante não encontrado!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            LocalDate dueDate = LocalDate.now().plusDays(15);
            paymentController.criarPagamento(student, description, amount, dueDate);
            
            JOptionPane.showMessageDialog(this, "Pagamento criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            textDescription.setText("");
            textAmount.setText("");
            loadData();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Valores inválidos!", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao criar pagamento: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void registarPagamento() {
        int selectedRow = tableData.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pagamento!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        String[] methods = {"cash", "card", "transfer", "check"};
        int choice = JOptionPane.showOptionDialog(this, "Escolha o método de pagamento:", "Registar Pagamento",
            JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, methods, methods[0]);
        
        if (choice >= 0) {
            paymentController.registarPagamento(id, LocalDate.now(), methods[choice]);
            JOptionPane.showMessageDialog(this, "Pagamento registado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        }
    }
    
    private void mostrarPendentes() {
        int selectedRow = tableData.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um estudante!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String studentStr = comboStudent.getSelectedItem().toString();
        Integer studentId = Integer.parseInt(studentStr.substring(studentStr.lastIndexOf(":") + 2, studentStr.length() - 1));
        
        List<Payment> pendentes = paymentController.obterPagamentosPendentes(studentId);
        StringBuilder msg = new StringBuilder("Pagamentos Pendentes:\n");
        for (Payment p : pendentes) {
            msg.append(String.format("%s - %.2f €\n", p.getDescription(), p.getAmount()));
        }
        JOptionPane.showMessageDialog(this, msg.toString(), "Pendentes", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarAtrasados() {
        int selectedRow = tableData.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um estudante!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String studentStr = comboStudent.getSelectedItem().toString();
        Integer studentId = Integer.parseInt(studentStr.substring(studentStr.lastIndexOf(":") + 2, studentStr.length() - 1));
        
        List<Payment> atrasados = paymentController.obterPagamentosAtrasados(studentId);
        StringBuilder msg = new StringBuilder("Pagamentos Atrasados:\n");
        for (Payment p : atrasados) {
            msg.append(String.format("%s - %.2f € (Venc.: %s)\n", p.getDescription(), p.getAmount(), p.getDueDate()));
        }
        JOptionPane.showMessageDialog(this, msg.toString(), "Atrasados", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void eliminarSelecionado() {
        int selectedRow = tableData.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um pagamento para eliminar!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Integer id = (Integer) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, "Confirma eliminação?", "Confirmação", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            paymentController.eliminarPagamento(id);
            JOptionPane.showMessageDialog(this, "Pagamento eliminado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            loadData();
        }
    }
}
