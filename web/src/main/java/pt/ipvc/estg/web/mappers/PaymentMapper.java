package pt.ipvc.estg.web.mappers;

import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.web.dto.PaymentResponse;

public final class PaymentMapper {
    private PaymentMapper() {}

    public static PaymentResponse toResponse(Payment payment) {
        if (payment == null) {
            return null;
        }

        Student student = payment.getStudent();

        return new PaymentResponse(
                payment.getId(),
                student != null ? student.getId() : null,
                student != null ? student.getName() : null,
                payment.getDescription(),
                payment.getAmount(),
                payment.getDueDate(),
                payment.getPaidDate(),
                payment.getStatus(),
                payment.getPaymentMethod(),
                payment.getNotes()
        );
    }
}
