package pt.ipvc.estg.services;

import pt.ipvc.estg.dal.mock.PaymentDAOMock;
import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.entities.Student;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Serviço de Payment - Lógica de negócio para Pagamentos
 */
public class PaymentService {
    
    private final PaymentDAOMock paymentDAO;
    
    public PaymentService() {
        this.paymentDAO = new PaymentDAOMock();
    }
    
    public Optional<Payment> getPagamento(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        return paymentDAO.findById(id);
    }
    
    public List<Payment> getAllPagamentos() {
        return paymentDAO.findAll();
    }
    
    public List<Payment> getPagamentosPorEstudante(Integer studentId) {
        if (studentId == null || studentId <= 0) {
            throw new IllegalArgumentException("Student ID deve ser válido");
        }
        return paymentDAO.findByStudent(studentId);
    }
    
    public List<Payment> getPagamentosPorStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status deve ser válido");
        }
        return paymentDAO.findByStatus(status);
    }
    
    public Payment criarPagamento(Student student, String description, Double amount, LocalDate dueDate) {
        if (student == null) {
            throw new IllegalArgumentException("Estudante é obrigatório");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que 0");
        }
        
        Payment payment = new Payment(student, description, amount, dueDate);
        return paymentDAO.insert(payment);
    }
    
    public Payment registarPagamento(Integer id, LocalDate paidDate, String paymentMethod) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        
        Optional<Payment> opt = paymentDAO.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Pagamento não encontrado");
        }
        
        Payment payment = opt.get();
        payment.setStatus("paid");
        payment.setPaidDate(paidDate != null ? paidDate : LocalDate.now());
        if (paymentMethod != null) payment.setPaymentMethod(paymentMethod);
        
        return paymentDAO.update(payment);
    }
    
    public List<Payment> getPagamentosPendentes(Integer studentId) {
        return paymentDAO.findByStudentAndStatus(studentId, "pending");
    }
    
    public List<Payment> getPagamentosAtrasados(Integer studentId) {
        return paymentDAO.findByStudentAndStatus(studentId, "overdue");
    }
    
    public Double calcularTotalPendente(Integer studentId) {
        List<Payment> pendentes = getPagamentosPendentes(studentId);
        return pendentes.stream()
                .mapToDouble(Payment::getAmount)
                .sum();
    }
    
    public Double calcularTotalRecebido() {
        return getAllPagamentos().stream()
                .filter(p -> "paid".equals(p.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();
    }
    
    public void eliminarPagamento(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        if (paymentDAO.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Pagamento não encontrado");
        }
        paymentDAO.delete(id);
    }
    
    public long contarPagamentos() {
        return paymentDAO.count();
    }
}
