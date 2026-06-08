package pt.ipvc.estg.desktop.api.dto;

public record InstructorResponse(
        Integer id,
        String name,
        String license,
        String specialization,
        Integer flightHours,
        String status,
        String email,
        String phone
) {
}
