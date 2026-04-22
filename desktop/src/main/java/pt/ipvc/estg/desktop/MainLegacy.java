package pt.ipvc.estg.desktop;

import pt.ipvc.estg.entities.Perfil;
import pt.ipvc.estg.services.PerfilServiceMock;

import java.util.List;

/**
 * Classe de teste ORIGINAL (Main.java antigo)
 * Mantida como referência histórica
 * 
 * Para testes: mostra como usar o PerfilService com Mocks
 * (sem necessidade de BD)
 */
public class MainLegacy {
    
    public static void main(String[] args) {
        System.out.println("=== Teste do Sistema de Perfis (MOCK) ===\n");
        
        // Usa o serviço com dados em memória
        PerfilServiceMock service = new PerfilServiceMock();
        
        System.out.println("📋 Listando todos os perfis:\n");
        
        List<Perfil> perfis = service.getAllPerfis();
        for (Perfil p : perfis) {
            System.out.println("  " + p);
        }
        
        System.out.println("\n📊 Total de perfis: " + service.contarPerfis());
        
        System.out.println("\n✅ Teste concluído com sucesso!");
        System.out.println("\nNota: Este é o código original Main.java refatorizado para usar Mocks");
        System.out.println("Para usar a BD real, mude para: PerfilService (em vez de PerfilServiceMock)");
    }
}
