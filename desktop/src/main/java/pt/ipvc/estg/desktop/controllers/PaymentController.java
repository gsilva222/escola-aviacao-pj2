package pt.ipvc.estg.desktop.controllers;

import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.services.PaymentService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller para gerenciar Pagamentos na interface Desktop
 */
public class PaymentController {
    
    private final PaymentService paymentService;
    
    public PaymentController() {
        this.paymentService = new PaymentService();
    }
    
    public List<Payment> listarPagamentos() {
        return paymentService.getAllPagamentos();
    }
    
    public Optional<Payment> obterPagamento(Integer id) {
        try {
            return paymentService.getPagamento(id);
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao obter pagamento: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    public List<Payment> obterPagamentosPorEstudante(Integer studentId) {
        return paymentService.getPagamentosPorEstudante(studentId);
    }
    
    public List<Payment> obterPagamentosPorStatus(String status) {
        return paymentService.getPagamentosPorStatus(status);
    }
    
    public void criarPagamento(Student student, String description, Double amount, LocalDate dueDate) {
        try {
            Payment payment = paymentService.criarPagamento(student, description, amount, dueDate);
            System.out.println("Pagamento criado com sucesso: " + payment);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao criar pagamento: " + e.getMessage());
        }
    }
    
    public void registarPagamento(Integer id, LocalDate paidDate, String paymentMethod) {
        try {
            Payment payment = paymentService.registarPagamento(id, paidDate, paymentMethod);
            System.out.println("Pagamento registado com sucesso: " + payment);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao registar pagamento: " + e.getMessage());
        }
    }
    
    public List<Payment> obterPagamentosPendentes(Integer studentId) {
        return paymentService.getPagamentosPendentes(studentId);
    }
    
    public List<Payment> obterPagamentosAtrasados(Integer studentId) {
        return paymentService.getPagamentosAtrasados(studentId);
    }
    
    public void eliminarPagamento(Integer id) {
        try {
            paymentService.eliminarPagamento(id);
            System.out.println("Pagamento eliminado com sucesso");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao eliminar pagamento: " + e.getMessage());
        }
    }
    
    public long obterTotalPagamentos() {
        return paymentService.contarPagamentos();
    }
    
    public Double obterTotalRecebido() {
        return paymentService.calcularTotalRecebido();
    }
    
    public Double obterTotalPendente(Integer studentId) {
        return paymentService.calcularTotalPendente(studentId);
    }
}
