package pt.ipvc.estg.desktop.api.dto;

import java.time.LocalDate;

public record StudentResponse(
        Integer id,
        String name,
        String email,
        String phone,
        String nif,
        LocalDate birthdate,
        String address,
        String nationality,
        Integer courseId,
        String courseName,
        Integer instructorId,
        String status,
        LocalDate enrollmentDate,
        Integer progress,
        Double flightHours,
        Double theoreticalHours,
        String paymentStatus,
        String avatar,
        String instructorName
) {
}
