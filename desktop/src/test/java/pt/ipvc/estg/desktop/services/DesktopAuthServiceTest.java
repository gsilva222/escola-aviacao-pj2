package pt.ipvc.estg.desktop.services;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import pt.ipvc.estg.desktop.api.BoDataAccess;
import pt.ipvc.estg.desktop.api.FoDataAccess;
import pt.ipvc.estg.desktop.api.SessionContext;
import pt.ipvc.estg.desktop.api.dto.AuthResponse;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DesktopAuthServiceTest {

    @AfterEach
    void cleanup() {
        DesktopAuthService.logout();
    }

    @Test
    void logoutShouldClearSessionAndDisableApiAccess() {
        SessionContext.setSession(new AuthResponse("token", "admin", "ADMIN", null), "Administrador");
        assertTrue(BoDataAccess.useApi());

        DesktopAuthService.logout();

        assertFalse(SessionContext.isAuthenticated());
        assertFalse(BoDataAccess.useApi());
        assertFalse(FoDataAccess.useApi());
    }
}
