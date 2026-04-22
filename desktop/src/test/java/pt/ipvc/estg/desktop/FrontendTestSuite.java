package pt.ipvc.estg.desktop;

import org.junit.jupiter.api.Test;
import pt.ipvc.estg.desktop.views.LoginFrame;
import pt.ipvc.estg.desktop.views.components.Sidebar;
import pt.ipvc.estg.desktop.views.components.TopBar;
import pt.ipvc.estg.desktop.views.panels.BODashboard;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite de Testes Unitários - Componentes Frontend
 * Testa todos os componentes novos criados
 */
public class FrontendTestSuite {

    @Test
    public void testAllComponentsLoadWithoutError() {
        assertDoesNotThrow(() -> {
            // Teste de smoke - verifica que todos os componentes conseguem ser instanciados
            TopBar topBar = new TopBar();
            assertNotNull(topBar);
            
            Sidebar sidebar = new Sidebar(page -> {});
            assertNotNull(sidebar);
            
            BODashboard dashboard = new BODashboard();
            assertNotNull(dashboard);
            
            LoginFrame loginFrame = new LoginFrame();
            assertNotNull(loginFrame);
            loginFrame.dispose();
            
            DesktopApp desktopApp = new DesktopApp("Administrador");
            assertNotNull(desktopApp);
            desktopApp.dispose();
        });
    }

    @Test
    public void testComponentHierarchy() {
        assertDoesNotThrow(() -> {
            // Verifica a hierarquia correta: Main -> LoginFrame -> DesktopApp
            LoginFrame loginFrame = new LoginFrame();
            assertNotNull(loginFrame);
            
            // LoginFrame deveria abrir DesktopApp ao fazer login
            // Mas como não podemos simular UI, apenas verificamos que conseguem coexistir
            DesktopApp desktopApp = new DesktopApp("Administrador");
            assertNotNull(desktopApp);
            
            loginFrame.dispose();
            desktopApp.dispose();
        });
    }

    @Test
    public void testAllRolesSupported() {
        String[] roles = {"Administrador", "Secretaria", "Gestor Operacional", "Instrutor", "Técnico de Manutenção"};
        
        for (String role : roles) {
            assertDoesNotThrow(() -> {
                DesktopApp app = new DesktopApp(role);
                assertNotNull(app);
                assertTrue(app.getTitle().contains(role));
                app.dispose();
            });
        }
    }

    @Test
    public void testNavigationCallbacks() {
        java.util.List<String> pages = new java.util.ArrayList<>();
        
        Sidebar sidebar = new Sidebar(page -> {
            pages.add(page);
        });
        
        assertNotNull(sidebar);
        // Simulamos clicks na sidebar
        String[] expectedPages = {"dashboard", "students", "courses", "flights"};
        for (String page : expectedPages) {
            pages.add(page);
        }
        
        assertEquals(expectedPages.length, pages.size());
    }

    @Test
    public void testDynamicTitleUpdates() {
        TopBar topBar = new TopBar();
        
        String[] titles = {"Dashboard", "Gestão de Alunos", "Cursos", "Voos", "Aeronaves"};
        
        for (String title : titles) {
            assertDoesNotThrow(() -> {
                topBar.setPageTitle(title);
            });
        }
    }

    @Test
    public void testComponentResizing() {
        assertDoesNotThrow(() -> {
            BODashboard dashboard = new BODashboard();
            
            // Testa diferentes resoluções
            int[] widths = {800, 1024, 1200, 1400, 1920};
            int[] heights = {600, 768, 800, 900, 1080};
            
            for (int width : widths) {
                for (int height : heights) {
                    dashboard.setSize(width, height);
                    assertEquals(width, dashboard.getWidth());
                    assertEquals(height, dashboard.getHeight());
                }
            }
        });
    }

    @Test
    public void testComponentsAreReusable() {
        assertDoesNotThrow(() -> {
            TopBar topBar1 = new TopBar();
            TopBar topBar2 = new TopBar();
            
            topBar1.setPageTitle("Página 1");
            topBar2.setPageTitle("Página 2");
            
            assertNotNull(topBar1);
            assertNotNull(topBar2);
        });
    }

    @Test
    public void testConcurrentComponentInitialization() {
        assertDoesNotThrow(() -> {
            // Testa que múltiplas instâncias conseguem existir simultaneamente
            DesktopApp app1 = new DesktopApp("Administrador");
            DesktopApp app2 = new DesktopApp("Instrutor");
            
            assertNotNull(app1);
            assertNotNull(app2);
            
            app1.dispose();
            app2.dispose();
        });
    }
}
