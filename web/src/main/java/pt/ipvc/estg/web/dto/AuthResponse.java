package pt.ipvc.estg.web.dto;

public record AuthResponse(
        String token,
        String username,
        String role,
        Integer studentId
) {
}
