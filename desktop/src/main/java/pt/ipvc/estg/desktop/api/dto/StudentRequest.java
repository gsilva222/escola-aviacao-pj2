package pt.ipvc.estg.desktop.api.dto;

import java.time.LocalDate;

public record StudentRequest(
        String name,
        String email,
        String phone,
        String nif,
        LocalDate birthdate,
        String address,
        String nationality,
        Integer courseId,
        Integer instructorId,
        String status,
        LocalDate enrollmentDate,
        Integer progress,
        Double flightHours,
        Double theoreticalHours,
        String paymentStatus
) {
}
