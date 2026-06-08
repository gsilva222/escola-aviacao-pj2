package pt.ipvc.estg.desktop.api;

import org.junit.jupiter.api.Test;
import pt.ipvc.estg.desktop.security.RoleMenuPolicy;
import pt.ipvc.estg.desktop.services.DesktopAuthService;

import static org.junit.jupiter.api.Assertions.*;

class DesktopAuthServiceTest {

    @Test
    void shouldNormalizeEmailToUsername() {
        assertEquals("admin", DesktopAuthService.normalizeUsername("admin@aeroschool.pt"));
        assertEquals("joao.silva", DesktopAuthService.normalizeUsername("joao.silva"));
    }

    @Test
    void shouldRestrictSecretariaMenu() {
        var pages = RoleMenuPolicy.allowedPages("Secretaria");
        assertTrue(pages.contains("students"));
        assertTrue(pages.contains("payments"));
        assertFalse(pages.contains("maintenance"));
        assertFalse(pages.contains("instructors"));
    }

    @Test
    void shouldIncludeInstructorsForAdministrator() {
        var pages = RoleMenuPolicy.allowedPages("Administrador");
        assertTrue(pages.contains("instructors"));
    }
}
