package pt.ipvc.estg.desktop.services;

import pt.ipvc.estg.desktop.api.ApiClient;
import pt.ipvc.estg.desktop.api.ApiException;
import pt.ipvc.estg.desktop.api.AppConfig;
import pt.ipvc.estg.desktop.api.SessionContext;
import pt.ipvc.estg.desktop.api.dto.AuthLoginRequest;
import pt.ipvc.estg.desktop.api.dto.AuthResponse;
import pt.ipvc.estg.desktop.api.dto.ChangePasswordRequest;

public class DesktopAuthService {

    private final ApiClient apiClient;

    public DesktopAuthService() {
        this.apiClient = new ApiClient();
    }

    public AuthResponse loginBackOffice(String username, String password, String uiRole) {
        AuthResponse response = loginWithCandidates(username, password);
        if (!"ADMIN".equalsIgnoreCase(response.role())) {
            throw new ApiException(403, "Conta sem permissao de BackOffice");
        }
        SessionContext.setSession(response, uiRole);
        return response;
    }

    public AuthResponse loginStudent(String username, String password) {
        AuthResponse response = loginWithCandidates(username, password);
        if (!"STUDENT".equalsIgnoreCase(response.role())) {
            throw new ApiException(403, "Conta sem permissao de aluno");
        }
        SessionContext.setSession(response, "Aluno");
        return response;
    }

    private AuthResponse loginWithCandidates(String input, String password) {
        if (!AppConfig.isApiEnabled()) {
            throw new ApiException("API desativada");
        }
        String trimmed = input == null ? "" : input.trim();
        if (trimmed.isEmpty()) {
            throw new ApiException(401, "Credenciais invalidas");
        }

        java.util.LinkedHashSet<String> candidates = new java.util.LinkedHashSet<>();
        candidates.add(trimmed);
        String normalized = normalizeUsername(trimmed);
        if (!normalized.equals(trimmed)) {
            candidates.add(normalized);
        }

        ApiException lastFailure = null;
        for (String candidate : candidates) {
            try {
                return login(candidate, password);
            } catch (ApiException ex) {
                if (ex.isConnectionError()) {
                    throw ex;
                }
                if (ex.getStatusCode() == 401 || ex.getStatusCode() == 403) {
                    lastFailure = ex;
                } else {
                    throw ex;
                }
            }
        }
        throw lastFailure != null ? lastFailure : new ApiException(401, "Credenciais invalidas");
    }

    private AuthResponse login(String username, String password) {
        return apiClient.postPublic("/auth/login", new AuthLoginRequest(username, password), AuthResponse.class);
    }

    public void changePassword(String currentPassword, String newPassword) {
        if (!AppConfig.isApiEnabled()) {
            throw new ApiException("API desativada");
        }
        apiClient.post("/auth/change-password",
                new ChangePasswordRequest(currentPassword, newPassword),
                Void.class);
    }

    public static void logout() {
        SessionContext.clear();
    }

    public static String normalizeUsername(String input) {
        String value = input == null ? "" : input.trim();
        if (value.contains("@")) {
            return value.substring(0, value.indexOf('@'));
        }
        return value;
    }
}
