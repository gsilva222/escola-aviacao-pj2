package pt.ipvc.estg.services;

import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.exception.EntityNotFoundException;
import pt.ipvc.estg.repositories.PaymentRepository;
import pt.ipvc.estg.validation.BusinessRules;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentService() {
        this(pt.ipvc.estg.bootstrap.MockServices.getInstance().paymentRepository());
    }

    public Optional<Payment> getPagamento(Integer id) {
        validateId(id);
        return paymentRepository.findById(id);
    }

    public Payment requirePagamento(Integer id) {
        return getPagamento(id).orElseThrow(() -> new EntityNotFoundException("Pagamento nao encontrado"));
    }

    public List<Payment> getAllPagamentos() {
        return paymentRepository.findAll();
    }

    public PageResult<Payment> listPagamentos(PageQuery query, Integer studentId, String status) {
        if (studentId != null) {
            return paymentRepository.findByStudent(studentId, query);
        }
        if (status != null && !status.trim().isEmpty()) {
            return paymentRepository.findByStatus(status, query);
        }
        return paymentRepository.findAll(query);
    }

    public List<Payment> getPagamentosPorEstudante(Integer studentId) {
        validateId(studentId);
        return paymentRepository.findByStudent(studentId);
    }

    public List<Payment> getPagamentosPorStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status deve ser valido");
        }
        return paymentRepository.findByStatus(status);
    }

    public Payment criarPagamento(Student student, String description, Double amount, LocalDate dueDate) {
        if (student == null) throw new IllegalArgumentException("Estudante e obrigatorio");
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Descricao e obrigatoria");
        }
        BusinessRules.requirePositive("Valor", amount);
        Payment payment = new Payment(student, description, amount, dueDate);
        return paymentRepository.save(payment);
    }

    public Payment savePagamento(Payment payment) {
        if (payment.getStatus() != null) {
            payment.setStatus(BusinessRules.requireAllowed("Status", payment.getStatus(), BusinessRules.PAYMENT_STATUSES));
        }
        BusinessRules.requirePositive("Valor", payment.getAmount());
        return paymentRepository.save(payment);
    }

    public Payment registarPagamento(Integer id, LocalDate paidDate, String paymentMethod) {
        Payment payment = requirePagamento(id);
        payment.setStatus("paid");
        payment.setPaidDate(paidDate != null ? paidDate : LocalDate.now());
        if (paymentMethod != null) payment.setPaymentMethod(paymentMethod);
        return paymentRepository.save(payment);
    }

    public List<Payment> getPagamentosPendentes(Integer studentId) {
        return paymentRepository.findByStudentAndStatus(studentId, "pending");
    }

    public List<Payment> getPagamentosAtrasados(Integer studentId) {
        return paymentRepository.findByStudentAndStatus(studentId, "overdue");
    }

    public Double calcularTotalPendente(Integer studentId) {
        return getPagamentosPendentes(studentId).stream().mapToDouble(Payment::getAmount).sum();
    }

    public Double calcularTotalRecebido() {
        return getAllPagamentos().stream()
                .filter(p -> "paid".equals(p.getStatus()))
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public void eliminarPagamento(Integer id) {
        validateId(id);
        if (!paymentRepository.existsById(id)) {
            throw new EntityNotFoundException("Pagamento nao encontrado");
        }
        paymentRepository.deleteById(id);
    }

    public long contarPagamentos() {
        return paymentRepository.count();
    }

    private static void validateId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser valido");
        }
    }
}
