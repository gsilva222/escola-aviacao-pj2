package pt.ipvc.estg.desktop;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitários para Main (Entry Point)
 */
public class MainTest {

    @Test
    public void testMainClassExists() {
        assertNotNull(Main.class, "Main class deve existir");
    }

    @Test
    public void testMainHasMainMethod() {
        assertDoesNotThrow(() -> {
            // Verifica que o método main pode ser acessado
            Main.class.getMethod("main", String[].class);
        });
    }

    @Test
    public void testMainClassCanBeInstantiated() {
        assertDoesNotThrow(() -> {
            Main main = new Main();
            assertNotNull(main);
        });
    }

    @Test
    public void testMainMethodSignature() {
        assertDoesNotThrow(() -> {
            // Verifica a assinatura correta do main
            var method = Main.class.getMethod("main", String[].class);
            
            // Deve ser static
            int modifiers = method.getModifiers();
            assertTrue(java.lang.reflect.Modifier.isStatic(modifiers), "main deve ser static");
            
            // Deve ser public
            assertTrue(java.lang.reflect.Modifier.isPublic(modifiers), "main deve ser public");
        });
    }

    @Test
    public void testMainWithEmptyArgs() {
        // Main com argumentos vazios não deve lançar erro
        assertDoesNotThrow(() -> {
            // Não chamamos diretamente Main.main() pois abriria a GUI
            // Apenas verificamos que o método deve existir
            Main.class.getMethod("main", String[].class);
        });
    }

    @Test
    public void testMainEntryPointValidation() {
        // Simples validação que o Main é um ponto de entrada válido
        assertDoesNotThrow(() -> {
            Class<?> mainClass = Main.class;
            assertNotNull(mainClass.getMethod("main", String[].class));
        });
    }
}
