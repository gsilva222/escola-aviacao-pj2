package pt.ipvc.estg.desktop.controllers;

import pt.ipvc.estg.desktop.api.ApiException;
import pt.ipvc.estg.desktop.api.BoDataAccess;
import pt.ipvc.estg.desktop.api.FoDataAccess;
import pt.ipvc.estg.desktop.api.bo.BoApiService;
import pt.ipvc.estg.desktop.api.dto.PaymentSummaryResponse;
import pt.ipvc.estg.desktop.services.FoStudentService;
import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.services.PaymentService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class PaymentController {

    private final PaymentService paymentService;
    private final BoApiService boApi;
    private final FoStudentService foStudentService;

    public PaymentController() {
        this.paymentService = new PaymentService();
        this.boApi = new BoApiService();
        this.foStudentService = new FoStudentService();
    }

    public List<Payment> listarPagamentos() {
        if (BoDataAccess.useApi()) {
            return boApi.listPayments();
        }
        return paymentService.getAllPagamentos();
    }

    public Optional<Payment> obterPagamento(Integer id) {
        try {
            if (BoDataAccess.useApi()) {
                return boApi.getPayment(id);
            }
            return paymentService.getPagamento(id);
        } catch (IllegalArgumentException | ApiException e) {
            System.err.println("Erro ao obter pagamento: " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<Payment> obterPagamentosPorEstudante(Integer studentId) {
        if (FoDataAccess.useApi()) {
            return foStudentService.myPayments();
        }
        if (BoDataAccess.useApi()) {
            return boApi.listPaymentsByStudent(studentId);
        }
        return paymentService.getPagamentosPorEstudante(studentId);
    }

    public PaymentSummaryResponse obterResumoPagamentosEstudante(Integer studentId) {
        if (FoDataAccess.useApi()) {
            return foStudentService.getPaymentSummary();
        }
        double totalPaid = obterPagamentosPorEstudante(studentId).stream()
                .filter(p -> "paid".equals(p.getStatus()))
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                .sum();
        double totalPending = obterPagamentosPorEstudante(studentId).stream()
                .filter(p -> "pending".equals(p.getStatus()))
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                .sum();
        double totalOverdue = obterPagamentosPorEstudante(studentId).stream()
                .filter(p -> "overdue".equals(p.getStatus()))
                .mapToDouble(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                .sum();
        return new PaymentSummaryResponse(totalPending, totalOverdue, totalPaid, 0, 0, 0);
    }

    public List<Payment> obterPagamentosPorStatus(String status) {
        if (BoDataAccess.useApi()) {
            return boApi.listPaymentsByStatus(status);
        }
        return paymentService.getPagamentosPorStatus(status);
    }

    public void criarPagamento(Student student, String description, Double amount, LocalDate dueDate) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.createPayment(student, description, amount, dueDate);
                return;
            }
            paymentService.criarPagamento(student, description, amount, dueDate);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao criar pagamento: " + e.getMessage());
        }
    }

    public void registarPagamento(Integer id, LocalDate paidDate, String paymentMethod) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.registerPayment(id, paidDate, paymentMethod);
                return;
            }
            paymentService.registarPagamento(id, paidDate, paymentMethod);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao registar pagamento: " + e.getMessage());
        }
    }

    public List<Payment> obterPagamentosPendentes(Integer studentId) {
        if (BoDataAccess.useApi()) {
            return boApi.listPaymentsByStudent(studentId).stream()
                    .filter(p -> "pending".equals(p.getStatus()))
                    .toList();
        }
        return paymentService.getPagamentosPendentes(studentId);
    }

    public List<Payment> obterPagamentosAtrasados(Integer studentId) {
        if (BoDataAccess.useApi()) {
            return boApi.listPaymentsByStudent(studentId).stream()
                    .filter(p -> "overdue".equals(p.getStatus()))
                    .toList();
        }
        return paymentService.getPagamentosAtrasados(studentId);
    }

    public void eliminarPagamento(Integer id) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.deletePayment(id);
                return;
            }
            paymentService.eliminarPagamento(id);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao eliminar pagamento: " + e.getMessage());
        }
    }

    public long obterTotalPagamentos() {
        return listarPagamentos().size();
    }

    public Double obterTotalRecebido() {
        if (BoDataAccess.useApi()) {
            return listarPagamentos().stream()
                    .filter(p -> "paid".equals(p.getStatus()))
                    .map(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                    .reduce(0.0, Double::sum);
        }
        return paymentService.calcularTotalRecebido();
    }

    public Double obterTotalPendente(Integer studentId) {
        if (BoDataAccess.useApi()) {
            return boApi.listPaymentsByStudent(studentId).stream()
                    .filter(p -> "pending".equals(p.getStatus()) || "overdue".equals(p.getStatus()))
                    .map(p -> p.getAmount() != null ? p.getAmount() : 0.0)
                    .reduce(0.0, Double::sum);
        }
        return paymentService.calcularTotalPendente(studentId);
    }
}
