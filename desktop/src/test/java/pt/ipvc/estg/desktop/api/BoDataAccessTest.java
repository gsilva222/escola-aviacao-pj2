package pt.ipvc.estg.desktop.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pt.ipvc.estg.desktop.api.dto.AuthResponse;

import static org.junit.jupiter.api.Assertions.*;

class BoDataAccessTest {

    @AfterEach
    void cleanup() {
        SessionContext.clear();
    }

    @Test
    void shouldUseApiOnlyWhenAdminSessionActive() {
        assertFalse(BoDataAccess.useApi());

        SessionContext.setSession(new AuthResponse("token", "admin", "ADMIN", null), "Administrador");
        assertTrue(BoDataAccess.useApi());

        SessionContext.clear();
        SessionContext.setSession(new AuthResponse("token", "aluno", "STUDENT", 1), "Aluno");
        assertFalse(BoDataAccess.useApi());
    }
}
