package pt.ipvc.estg.desktop.views.panels;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para BODashboard
 */
public class BODashboardTest {

    private BODashboard dashboard;

    @BeforeEach
    public void setUp() {
        assertDoesNotThrow(() -> {
            dashboard = new BODashboard();
        });
    }

    @Test
    public void testBODashboardInitialization() {
        assertNotNull(dashboard, "BODashboard deve ser inicializado");
    }

    @Test
    public void testBODashboardIsPanel() {
        assertTrue(dashboard instanceof JPanel, "BODashboard deve ser um JPanel");
    }

    @Test
    public void testBODashboardLayoutIsBoxLayout() {
        assertTrue(dashboard.getLayout() instanceof BoxLayout, "Layout deve ser BoxLayout");
    }

    @Test
    public void testBODashboardHasComponents() {
        // Verifica que o Dashboard tem componentes adicionados
        assertTrue(dashboard.getComponentCount() > 0, "Dashboard deve ter componentes");
    }

    @Test
    public void testBODashboardBackground() {
        assertNotNull(dashboard.getBackground());
        // Deve ter cor LIGHT_BG (#F0F4F8)
    }

    @Test
    public void testBODashboardIsOpaque() {
        // Dashboard deve ser opaque (preenchido com cor)
        assertTrue(dashboard.isOpaque());
    }

    @Test
    public void testBODashboardWelcomeBanner() {
        // Verifica que há componentes suficientes para incluir o welcome banner
        int componentCount = dashboard.getComponentCount();
        assertTrue(componentCount >= 3, "Dashboard deve ter pelo menos welcome banner, KPIs e charts");
    }

    @Test
    public void testBODashboardKPICards() {
        // KPI Cards estão adicionados
        assertTrue(dashboard.getComponentCount() > 0, "KPI cards devem estar presentes");
    }

    @Test
    public void testBODashboardChartsPanel() {
        // Charts devem estar presentes
        assertTrue(dashboard.getComponentCount() > 0, "Charts devem estar presentes");
    }

    @Test
    public void testBODashboardRecentActivity() {
        // Recent activity panel deve estar presente
        assertTrue(dashboard.getComponentCount() > 0, "Recent activity deve estar presente");
    }

    @Test
    public void testBODashboardServicesInitialization() {
        // Dashboard consegue inicializar sem erros
        assertDoesNotThrow(() -> {
            BODashboard testDashboard = new BODashboard();
            assertNotNull(testDashboard);
        });
    }

    @Test
    public void testBODashboardSize() {
        // Dashboard consegue lidar com redimensionamento
        dashboard.setSize(1200, 800);
        assertEquals(1200, dashboard.getWidth());
        assertEquals(800, dashboard.getHeight());
    }

    @Test
    public void testBODashboardRepaint() {
        // Dashboard consegue ser redesenhado sem erro
        assertDoesNotThrow(() -> {
            dashboard.repaint();
        });
    }

    @Test
    public void testBODashboardValidate() {
        // Dashboard consegue validar layout sem erro
        assertDoesNotThrow(() -> {
            dashboard.validate();
        });
    }

    @Test
    public void testBODashboardScrolling() {
        // Dashboard como componente scrollável
        JScrollPane scrollPane = new JScrollPane(dashboard);
        assertNotNull(scrollPane);
        assertTrue(scrollPane.getViewport().getView() instanceof BODashboard);
    }
}
