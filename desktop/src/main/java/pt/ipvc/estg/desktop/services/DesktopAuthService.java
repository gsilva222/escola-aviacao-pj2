package pt.ipvc.estg.desktop.services;

import pt.ipvc.estg.desktop.api.ApiClient;
import pt.ipvc.estg.desktop.api.ApiException;
import pt.ipvc.estg.desktop.api.AppConfig;
import pt.ipvc.estg.desktop.api.SessionContext;
import pt.ipvc.estg.desktop.api.dto.AuthLoginRequest;
import pt.ipvc.estg.desktop.api.dto.AuthResponse;

public class DesktopAuthService {

    private final ApiClient apiClient;

    public DesktopAuthService() {
        this.apiClient = new ApiClient();
    }

    public AuthResponse loginBackOffice(String username, String password, String uiRole) {
        AuthResponse response = login(username, password);
        if (!"ADMIN".equalsIgnoreCase(response.role())) {
            throw new ApiException(403, "Conta sem permissao de BackOffice");
        }
        SessionContext.setSession(response, uiRole);
        return response;
    }

    public AuthResponse loginStudent(String username, String password) {
        AuthResponse response = login(username, password);
        if (!"STUDENT".equalsIgnoreCase(response.role())) {
            throw new ApiException(403, "Conta sem permissao de aluno");
        }
        SessionContext.setSession(response, "Aluno");
        return response;
    }

    private AuthResponse login(String username, String password) {
        if (!AppConfig.isApiEnabled()) {
            throw new ApiException("API desativada");
        }
        return apiClient.postPublic("/auth/login", new AuthLoginRequest(username, password), AuthResponse.class);
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
