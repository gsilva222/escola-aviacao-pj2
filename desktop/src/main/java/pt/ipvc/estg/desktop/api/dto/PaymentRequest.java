package pt.ipvc.estg.desktop.api.dto;

import java.time.LocalDate;

public record PaymentRequest(
        Integer studentId,
        String description,
        Double amount,
        LocalDate dueDate,
        LocalDate paidDate,
        String status,
        String paymentMethod,
        String notes
) {
}
