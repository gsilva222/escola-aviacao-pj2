package pt.ipvc.estg.desktop.api.dto;

public record CreateUserRequest(
        String username,
        String password,
        String role,
        Integer studentId,
        String staffProfile
) {
}
