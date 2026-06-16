package pt.ipvc.estg.desktop.controllers;

import pt.ipvc.estg.desktop.api.BoDataAccess;
import pt.ipvc.estg.desktop.api.bo.BoApiService;
import pt.ipvc.estg.desktop.api.dto.UserAccountResponse;

import java.util.Collections;
import java.util.List;

/**
 * Gestão de utilizadores (contas de acesso). Só está disponível quando o
 * desktop está ligado à API como ADMIN; não existe equivalente em modo
 * demo/offline (os dados de utilizadores vivem no backend/PostgreSQL).
 */
public class UserController {

    private final BoApiService boApi = new BoApiService();

    public boolean isAvailable() {
        return BoDataAccess.useApi();
    }

    public List<UserAccountResponse> listarUtilizadores() {
        if (!isAvailable()) {
            return Collections.emptyList();
        }
        return boApi.listUsers();
    }

    public UserAccountResponse criarUtilizador(String username, String password, String role,
                                               Integer studentId, String staffProfile) {
        return boApi.createUser(username, password, role, studentId, staffProfile);
    }

    public UserAccountResponse definirAtivo(Integer id, boolean active) {
        return boApi.setUserActive(id, active);
    }

    public UserAccountResponse alterarPerfil(Integer id, String staffProfile) {
        return boApi.updateUserStaffProfile(id, staffProfile);
    }
}
