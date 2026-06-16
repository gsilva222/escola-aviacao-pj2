package pt.ipvc.estg.desktop.api.dto;

public record ChangePasswordRequest(String currentPassword, String newPassword) {
}
