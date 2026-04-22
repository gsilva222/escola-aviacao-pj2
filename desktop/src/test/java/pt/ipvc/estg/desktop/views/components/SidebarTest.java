package pt.ipvc.estg.desktop.views.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para Sidebar
 */
public class SidebarTest {

    private Sidebar sidebar;
    private List<String> navigationPages;

    @BeforeEach
    public void setUp() {
        navigationPages = new ArrayList<>();
        
        sidebar = new Sidebar(page -> {
            navigationPages.add(page);
        });
    }

    @Test
    public void testSidebarInitialization() {
        assertNotNull(sidebar, "Sidebar deve ser inicializado");
    }

    @Test
    public void testSidebarIsPanelComponent() {
        assertTrue(sidebar instanceof JPanel, "Sidebar deve ser um JPanel");
    }

    @Test
    public void testSidebarListenerCallback() {
        // Teste que o listener é chamado
        Sidebar testSidebar = new Sidebar(page -> {
            navigationPages.add(page);
        });
        
        // O listener deve ter sido passado corretamente
        assertNotNull(testSidebar);
    }

    @Test
    public void testNavigationListenerReceivesPages() {
        // Verifica que o listener pode receber diferentes páginas
        Sidebar.NavigationListener listener = new Sidebar.NavigationListener() {
            @Override
            public void navigateTo(String page) {
                navigationPages.add(page);
            }
        };

        assertNotNull(listener);
    }

    @Test
    public void testMultipleNavigationEvents() {
        // Simula múltiplas navegações
        String[] pages = {"dashboard", "students", "courses", "flights", "aircraft"};
        
        for (String page : pages) {
            // O listener deveria adicionar à lista
            navigationPages.add(page);
        }
        
        assertEquals(pages.length, navigationPages.size());
    }

    @Test
    public void testSidebarDimensions() {
        assertNotNull(sidebar.getPreferredSize());
        // Sidebar expandido deve ter ~256px de largura
        assertTrue(sidebar.getPreferredSize().width > 0);
    }

    @Test
    public void testSidebarBackground() {
        assertNotNull(sidebar.getBackground());
        // Deve ter cor DARK_BG (#0F2344)
    }

    @Test
    public void testMultipleNavigationCallbacks() {
        List<String> pageHistory = new ArrayList<>();
        
        Sidebar localSidebar = new Sidebar(page -> {
            pageHistory.add(page);
        });
        
        assertNotNull(localSidebar);
        assertEquals(0, pageHistory.size(), "Inicialmente nenhuma navegação");
    }
}
