package pt.ipvc.estg.desktop.views.components;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para TopBar
 */
public class TopBarTest {

    private TopBar topBar;

    @BeforeEach
    public void setUp() {
        assertDoesNotThrow(() -> {
            topBar = new TopBar();
        });
    }

    @Test
    public void testTopBarInitialization() {
        assertNotNull(topBar, "TopBar deve ser inicializado");
    }

    @Test
    public void testSetPageTitle() {
        assertDoesNotThrow(() -> {
            topBar.setPageTitle("Dashboard");
            topBar.setPageTitle("Gestão de Alunos");
            topBar.setPageTitle("Cursos e Módulos");
        });
    }

    @Test
    public void testPageTitleUpdates() {
        // Teste múltiplas mudanças de título
        String[] titles = {"Dashboard", "Alunos", "Cursos", "Voos", "Aeronaves", "Manutenção"};
        
        for (String title : titles) {
            assertDoesNotThrow(() -> {
                topBar.setPageTitle(title);
            });
        }
    }

    @Test
    public void testTopBarHasPreferredSize() {
        assertNotNull(topBar.getPreferredSize());
        assertEquals(70, topBar.getPreferredSize().height, "TopBar height deve ser 70");
    }

    @Test
    public void testTopBarIsPanel() {
        assertTrue(topBar instanceof JPanel, "TopBar deve ser um JPanel");
    }

    @Test
    public void testEmptyTitleHandling() {
        assertDoesNotThrow(() -> {
            topBar.setPageTitle("");
        });
    }

    @Test
    public void testNullTitleHandling() {
        // Não deve lançar NullPointerException
        assertDoesNotThrow(() -> {
            topBar.setPageTitle(null);
        });
    }

    @Test
    public void testLongTitleHandling() {
        String longTitle = "Este é um título muito longo que testaria a renderização do TopBar com muito texto";
        assertDoesNotThrow(() -> {
            topBar.setPageTitle(longTitle);
        });
    }
}
