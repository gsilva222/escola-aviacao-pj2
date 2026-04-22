package pt.ipvc.estg.desktop.controllers;

import pt.ipvc.estg.entities.Perfil;
import pt.ipvc.estg.services.PerfilServiceMock;

import java.util.List;
import java.util.Optional;

/**
 * Controller para gerenciar Perfis na interface Desktop
 */
public class PerfilController {
    
    private final PerfilServiceMock perfilService;
    
    public PerfilController() {
        this.perfilService = new PerfilServiceMock();
    }
    
    /**
     * Retorna todos os perfis para exibir na tabela
     */
    public List<Perfil> listarPerfis() {
        return perfilService.getAllPerfis();
    }
    
    /**
     * Procura um perfil pelo ID
     */
    public Optional<Perfil> obterPerfil(Integer id) {
        try {
            return perfilService.getPerfil(id);
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao obter perfil: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Cria um novo perfil
     */
    public void criarPerfil(String nome, String descricao) {
        try {
            Perfil perfil = perfilService.criarPerfil(nome, descricao);
            System.out.println("Perfil criado com sucesso: " + perfil);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao criar perfil: " + e.getMessage());
        }
    }
    
    /**
     * Atualiza um perfil
     */
    public void atualizarPerfil(Integer id, String nome, String descricao) {
        try {
            Perfil perfil = perfilService.atualizarPerfil(id, nome, descricao);
            System.out.println("Perfil atualizado com sucesso: " + perfil);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao atualizar perfil: " + e.getMessage());
        }
    }
    
    /**
     * Elimina um perfil
     */
    public void eliminarPerfil(Integer id) {
        try {
            perfilService.eliminarPerfil(id);
            System.out.println("Perfil eliminado com sucesso");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao eliminar perfil: " + e.getMessage());
        }
    }
    
    /**
     * Retorna o total de perfis
     */
    public long obterTotalPerfis() {
        return perfilService.contarPerfis();
    }
}
