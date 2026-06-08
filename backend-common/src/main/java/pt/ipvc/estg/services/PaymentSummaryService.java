package pt.ipvc.estg.services;

import pt.ipvc.estg.domain.PaymentSummary;
import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.repositories.PaymentRepository;

import java.util.List;

public class PaymentSummaryService {

    private final PaymentRepository paymentRepository;

    public PaymentSummaryService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentSummaryService() {
        this(pt.ipvc.estg.bootstrap.MockServices.getInstance().paymentRepository());
    }

    public PaymentSummary summarizeAll() {
        return summarize(paymentRepository.findAll());
    }

    public PaymentSummary summarizeForStudent(int studentId) {
        return summarize(paymentRepository.findByStudent(studentId));
    }

    private PaymentSummary summarize(List<Payment> payments) {
        double pending = 0;
        double overdue = 0;
        double paid = 0;
        long pendingCount = 0;
        long overdueCount = 0;
        long paidCount = 0;

        for (Payment payment : payments) {
            double amount = payment.getAmount() != null ? payment.getAmount() : 0.0;
            String status = payment.getStatus() == null ? "pending" : payment.getStatus();
            switch (status) {
                case "paid" -> {
                    paid += amount;
                    paidCount++;
                }
                case "overdue" -> {
                    overdue += amount;
                    overdueCount++;
                }
                default -> {
                    pending += amount;
                    pendingCount++;
                }
            }
        }

        return new PaymentSummary(pending, overdue, paid, pendingCount, overdueCount, paidCount);
    }
}
