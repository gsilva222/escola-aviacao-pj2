package pt.ipvc.estg.web.security;

public record AuthenticatedUser(String username, String role, Integer studentId, String staffProfile) {
}
