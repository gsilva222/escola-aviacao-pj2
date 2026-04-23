package pt.ipvc.estg.desktop.views.frontoffice;

import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.desktop.controllers.PaymentController;

import javax.swing.*;
import java.util.List;

/**
 * Pagamentos do Aluno (FrontOffice)
 * Mostra histórico de pagamentos do aluno autenticado
 */
public class FOPayments extends JPanel {

    private final Student student;
    private final PaymentController paymentController;

    public FOPayments(Student student) {
        this.student = student;
        this.paymentController = new PaymentController();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new java.awt.BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new java.awt.Color(245, 245, 245));

        // Calcular saldo devido
        List<Payment> pendingPayments = paymentController.obterPagamentosPendentes(student.getId());
        List<Payment> overduePayments = paymentController.obterPagamentosAtrasados(student.getId());
        
        double pendingAmount = pendingPayments == null ? 0 : pendingPayments.stream()
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0)
                .sum();
        
        double overdueAmount = overduePayments == null ? 0 : overduePayments.stream()
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0)
                .sum();
        
        double totalDue = pendingAmount + overdueAmount;

        // Saldo geral
        JPanel balancePanel = new JPanel();
        balancePanel.setBackground(totalDue > 0 ? new java.awt.Color(192, 40, 40) : new java.awt.Color(21, 101, 192));
        balancePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        balancePanel.setLayout(new BoxLayout(balancePanel, BoxLayout.Y_AXIS));

        JLabel balanceTitle = new JLabel(totalDue > 0 ? "Saldo em Atraso" : "Saldo Atual");
        balanceTitle.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        balanceTitle.setForeground(java.awt.Color.WHITE);
        balancePanel.add(balanceTitle);

        JLabel balanceValue = new JLabel("€ " + String.format("%.2f", totalDue));
        balanceValue.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 32));
        balanceValue.setForeground(java.awt.Color.WHITE);
        balancePanel.add(balanceValue);

        JLabel balanceSubtitle = new JLabel(totalDue > 0 ? "Pagamento urgente" : "A pagar até fim do mês");
        balanceSubtitle.setFont(new java.awt.Font("Arial", java.awt.Font.ITALIC, 12));
        balanceSubtitle.setForeground(java.awt.Color.WHITE);
        balancePanel.add(balanceSubtitle);

        add(balancePanel, java.awt.BorderLayout.NORTH);

        // Tabela de faturas
        String[] columns = {"Data", "Conceito", "Valor", "Status", "Método", "Data Pagamento"};
        
        List<Payment> allPayments = paymentController.obterPagamentosPorEstudante(student.getId());
        Object[][] data = new Object[allPayments != null ? allPayments.size() : 0][];

        if (allPayments != null) {
            for (int i = 0; i < allPayments.size(); i++) {
                Payment payment = allPayments.get(i);
                data[i] = new Object[]{
                        payment.getDueDate(),
                        payment.getDescription() != null ? payment.getDescription() : "Mensalidade",
                        "€" + String.format("%.2f", payment.getAmount() != null ? payment.getAmount() : 0),
                        payment.getStatus() != null ? payment.getStatus() : "Pendente",
                        payment.getPaymentMethod() != null ? payment.getPaymentMethod() : "---",
                        payment.getPaidDate() != null ? payment.getPaidDate() : "---"
                };
            }
        }

        JTable table = new JTable(data, columns);
        table.setRowHeight(25);
        table.setEnabled(false);
        JScrollPane scrollPane = new JScrollPane(table);

        JPanel tablePanel = new JPanel(new java.awt.BorderLayout());
        tablePanel.setBackground(java.awt.Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Histórico de Pagamentos"));
        tablePanel.add(scrollPane, java.awt.BorderLayout.CENTER);

        add(tablePanel, java.awt.BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 10));
        buttonPanel.setOpaque(false);
        JButton payBtn = new JButton("💳 Efetuar Pagamento");
        payBtn.addActionListener(e -> JOptionPane.showMessageDialog(this, 
                "Pagamento Online\n\nFuncionalidade em desenvolvimento.\n\nPara pagar, entre em contato com o administrador."));
        buttonPanel.add(payBtn);

        add(buttonPanel, java.awt.BorderLayout.SOUTH);
    }
}
