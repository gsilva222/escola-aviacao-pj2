package pt.ipvc.estg.web.dto;

import java.time.LocalDate;

public record PaymentResponse(
        Integer id,
        Integer studentId,
        String studentName,
        String description,
        Double amount,
        LocalDate dueDate,
        LocalDate paidDate,
        String status,
        String paymentMethod,
        String notes
) {
}
