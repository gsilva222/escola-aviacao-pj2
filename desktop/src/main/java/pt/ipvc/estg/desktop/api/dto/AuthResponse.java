package pt.ipvc.estg.desktop.api.dto;

public record AuthResponse(String token, String username, String role, Integer studentId, String staffProfile) {
}
