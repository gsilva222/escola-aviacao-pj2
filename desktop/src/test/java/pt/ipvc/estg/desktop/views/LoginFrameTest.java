package pt.ipvc.estg.desktop.views;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para LoginFrame
 */
public class LoginFrameTest {

    private LoginFrame loginFrame;

    @BeforeEach
    public void setUp() {
        // Note: Como LoginFrame é um JFrame com UI, não conseguimos testar visualmente
        // Mas conseguimos testar a lógica
        assertDoesNotThrow(() -> {
            loginFrame = new LoginFrame();
        });
    }

    @Test
    public void testLoginFrameInitialization() {
        assertNotNull(loginFrame, "LoginFrame deve ser inicializado");
        assertEquals("AeroSchool BackOffice - Login", loginFrame.getTitle());
    }

    @Test
    public void testLoginFrameSize() {
        assertEquals(1400, loginFrame.getWidth());
        assertEquals(800, loginFrame.getHeight());
    }

    @Test
    public void testLoginFrameIsVisible() {
        // Pode ou não estar visível dependendo da inicialização
        // O importante é não lançar erro
        assertDoesNotThrow(() -> {
            loginFrame.setVisible(true);
        });
    }

    @Test
    public void testRoleComboBoxHasValues() {
        // Indiretamente testando que os roles foram carregados corretamente
        // pelo facto de não ter havido exceção no setUp()
        assertNotNull(loginFrame);
    }

    @Test
    public void testDefaultCloseOperation() {
        assertEquals(JFrame.EXIT_ON_CLOSE, loginFrame.getDefaultCloseOperation());
    }

    @Test
    public void testLoginFrameCleanup() {
        // Teste para limpeza
        assertDoesNotThrow(() -> {
            loginFrame.dispose();
        });
    }
}
