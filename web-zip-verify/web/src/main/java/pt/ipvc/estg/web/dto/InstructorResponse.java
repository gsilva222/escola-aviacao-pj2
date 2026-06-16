package pt.ipvc.estg.web.dto;

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
