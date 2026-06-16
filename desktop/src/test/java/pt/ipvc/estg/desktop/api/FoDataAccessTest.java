package pt.ipvc.estg.desktop.api;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pt.ipvc.estg.desktop.api.dto.AuthResponse;

import static org.junit.jupiter.api.Assertions.*;

class FoDataAccessTest {

    @AfterEach
    void cleanup() {
        SessionContext.clear();
    }

    @Test
    void shouldUseApiOnlyForStudentSession() {
        assertFalse(FoDataAccess.useApi());

        SessionContext.setSession(new AuthResponse("token", "aluno", "STUDENT", 1, null), "Aluno");
        assertTrue(FoDataAccess.useApi());

        SessionContext.clear();
        SessionContext.setSession(new AuthResponse("token", "admin", "ADMIN", null, "Administrador"), "Administrador");
        assertFalse(FoDataAccess.useApi());
    }
}
