package pt.ipvc.estg.services;

import pt.ipvc.estg.dal.PerfilDAOMock;
import pt.ipvc.estg.entities.Perfil;

import java.util.List;
import java.util.Optional;

/**
 * Serviço de Perfil com suporte a Mock
 * Pode ser configurado para usar PerfilDAO real ou PerfilDAOMock
 */
public class PerfilServiceMock {
    
    private final PerfilDAOMock perfilDAO;
    
    public PerfilServiceMock() {
        this.perfilDAO = new PerfilDAOMock();
    }
    
    /**
     * Procura um perfil pelo ID
     */
    public Optional<Perfil> getPerfil(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        return perfilDAO.findById(id);
    }
    
    /**
     * Procura um perfil pelo nome
     */
    public Optional<Perfil> getPerfilPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome deve ser válido");
        }
        return perfilDAO.findByNome(nome);
    }
    
    /**
     * Retorna todos os perfis
     */
    public List<Perfil> getAllPerfis() {
        return perfilDAO.findAll();
    }
    
    /**
     * Cria um novo perfil
     */
    public Perfil criarPerfil(String nome, String descricao) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        
        // Verifica se o nome já existe
        if (perfilDAO.findByNome(nome).isPresent()) {
            throw new IllegalArgumentException("Já existe um perfil com esse nome");
        }
        
        Perfil perfil = new Perfil(nome, descricao);
        return perfilDAO.insert(perfil);
    }
    
    /**
     * Atualiza um perfil existente
     */
    public Perfil atualizarPerfil(Integer id, String nome, String descricao) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        
        Optional<Perfil> opt = perfilDAO.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Perfil não encontrado");
        }
        
        Perfil perfil = opt.get();
        
        if (nome != null && !nome.trim().isEmpty()) {
            // Verifica se o novo nome já existe noutro perfil
            Optional<Perfil> existente = perfilDAO.findByNome(nome);
            if (existente.isPresent() && !existente.get().getId().equals(id)) {
                throw new IllegalArgumentException("Já existe outro perfil com esse nome");
            }
            perfil.setNome(nome);
        }
        
        if (descricao != null) {
            perfil.setDescricao(descricao);
        }
        
        return perfilDAO.update(perfil);
    }
    
    /**
     * Elimina um perfil
     */
    public void eliminarPerfil(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        
        if (perfilDAO.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Perfil não encontrado");
        }
        
        perfilDAO.delete(id);
    }
    
    /**
     * Retorna o total de perfis
     */
    public long contarPerfis() {
        return perfilDAO.count();
    }
}
