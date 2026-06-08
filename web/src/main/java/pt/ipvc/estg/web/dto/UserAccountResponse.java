package pt.ipvc.estg.web.dto;

public record UserAccountResponse(
        Integer id,
        String username,
        String role,
        boolean active,
        Integer studentId,
        String studentName,
        String staffProfile
) {
}
