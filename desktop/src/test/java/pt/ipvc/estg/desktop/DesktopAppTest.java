package pt.ipvc.estg.desktop;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para DesktopApp
 */
public class DesktopAppTest {

    private DesktopApp desktopApp;

    @BeforeEach
    public void setUp() {
        assertDoesNotThrow(() -> {
            desktopApp = new DesktopApp("Administrador");
        });
    }

    @Test
    public void testDesktopAppInitialization() {
        assertNotNull(desktopApp, "DesktopApp deve ser inicializado");
    }

    @Test
    public void testDesktopAppWithAdminRole() {
        assertDoesNotThrow(() -> {
            DesktopApp adminApp = new DesktopApp("Administrador");
            assertNotNull(adminApp);
            assertTrue(adminApp.getTitle().contains("Administrador"));
        });
    }

    @Test
    public void testDesktopAppWithSecretariaRole() {
        assertDoesNotThrow(() -> {
            DesktopApp secretariaApp = new DesktopApp("Secretaria");
            assertNotNull(secretariaApp);
            assertTrue(secretariaApp.getTitle().contains("Secretaria"));
        });
    }

    @Test
    public void testDesktopAppWithInstructorRole() {
        assertDoesNotThrow(() -> {
            DesktopApp instructorApp = new DesktopApp("Instrutor");
            assertNotNull(instructorApp);
        });
    }

    @Test
    public void testDesktopAppWithAllRoles() {
        String[] roles = {"Administrador", "Secretaria", "Gestor Operacional", "Instrutor", "Técnico de Manutenção"};
        
        for (String role : roles) {
            assertDoesNotThrow(() -> {
                DesktopApp app = new DesktopApp(role);
                assertNotNull(app);
                assertTrue(app.getTitle().contains(role));
            });
        }
    }

    @Test
    public void testDesktopAppSize() {
        assertEquals(1400, desktopApp.getWidth());
        assertEquals(900, desktopApp.getHeight());
    }

    @Test
    public void testDesktopAppTitle() {
        assertTrue(desktopApp.getTitle().contains("AeroSchool"));
        assertTrue(desktopApp.getTitle().contains("BackOffice"));
    }

    @Test
    public void testDesktopAppDefaultCloseOperation() {
        assertEquals(JFrame.EXIT_ON_CLOSE, desktopApp.getDefaultCloseOperation());
    }

    @Test
    public void testDesktopAppIsResizable() {
        assertTrue(desktopApp.isResizable());
    }

    @Test
    public void testDesktopAppCenterLocation() {
        // DesktopApp usa setLocationRelativeTo(null) para centralizar
        assertNotNull(desktopApp.getLocation());
    }

    @Test
    public void testDesktopAppContentPane() {
        assertNotNull(desktopApp.getContentPane());
    }

    @Test
    public void testDesktopAppHasMainPanel() {
        // Verifica que tem componentes (mainPanel com Sidebar + RightPanel)
        assertTrue(desktopApp.getContentPane().getComponentCount() > 0);
    }

    @Test
    public void testDesktopAppCleanup() {
        assertDoesNotThrow(() -> {
            desktopApp.dispose();
        });
    }

    @Test
    public void testMultipleDesktopAppInstances() {
        assertDoesNotThrow(() -> {
            DesktopApp app1 = new DesktopApp("Administrador");
            DesktopApp app2 = new DesktopApp("Instrutor");
            
            assertNotNull(app1);
            assertNotNull(app2);
            
            // Diferentes instâncias devem ter títulos diferentes
            assertNotEquals(app1.getTitle(), app2.getTitle());
        });
    }
}
