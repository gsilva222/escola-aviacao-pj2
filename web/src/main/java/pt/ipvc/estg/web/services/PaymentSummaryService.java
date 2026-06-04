package pt.ipvc.estg.web.services;

import org.springframework.stereotype.Service;
import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.web.dto.PaymentSummaryResponse;
import pt.ipvc.estg.web.repositories.PaymentRepository;

import java.util.List;

@Service
public class PaymentSummaryService {

    private final PaymentRepository paymentRepository;

    public PaymentSummaryService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public PaymentSummaryResponse summarizeAll() {
        return summarize(paymentRepository.findAll());
    }

    public PaymentSummaryResponse summarizeForStudent(int studentId) {
        return summarize(paymentRepository.findByStudent_Id(studentId));
    }

    private PaymentSummaryResponse summarize(List<Payment> payments) {
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

        return new PaymentSummaryResponse(pending, overdue, paid, pendingCount, overdueCount, paidCount);
    }
}
