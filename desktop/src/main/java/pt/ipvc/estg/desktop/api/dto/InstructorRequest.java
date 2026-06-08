package pt.ipvc.estg.desktop.api.dto;

public record InstructorRequest(
        String name,
        String license,
        String specialization,
        Integer flightHours,
        String status,
        String email,
        String phone
) {
}
