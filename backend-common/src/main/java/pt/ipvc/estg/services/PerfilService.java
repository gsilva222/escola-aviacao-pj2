package pt.ipvc.estg.services;

import pt.ipvc.estg.entities.Perfil;
import pt.ipvc.estg.exception.ConflictException;
import pt.ipvc.estg.exception.EntityNotFoundException;
import pt.ipvc.estg.repositories.PerfilRepository;

import java.util.List;
import java.util.Optional;

public class PerfilService {

    private final PerfilRepository perfilRepository;

    public PerfilService(PerfilRepository perfilRepository) {
        this.perfilRepository = perfilRepository;
    }

    public PerfilService() {
        this(pt.ipvc.estg.bootstrap.MockServices.getInstance().perfilRepository());
    }

    public Optional<Perfil> getPerfil(Integer id) {
        validateId(id);
        return perfilRepository.findById(id);
    }

    public Optional<Perfil> getPerfilPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome deve ser valido");
        }
        return perfilRepository.findByNome(nome);
    }

    public List<Perfil> getAllPerfis() {
        return perfilRepository.findAll();
    }

    public Perfil criarPerfil(String nome, String descricao) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome e obrigatorio");
        }
        if (perfilRepository.findByNome(nome).isPresent()) {
            throw new ConflictException("Ja existe um perfil com esse nome");
        }
        Perfil perfil = new Perfil(nome, descricao);
        return perfilRepository.save(perfil);
    }

    public Perfil atualizarPerfil(Integer id, String nome, String descricao) {
        Perfil perfil = getPerfil(id).orElseThrow(() -> new EntityNotFoundException("Perfil nao encontrado"));
        if (nome != null && !nome.trim().isEmpty()) {
            perfilRepository.findByNome(nome)
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new ConflictException("Ja existe outro perfil com esse nome");
                    });
            perfil.setNome(nome);
        }
        if (descricao != null) perfil.setDescricao(descricao);
        return perfilRepository.save(perfil);
    }

    public void eliminarPerfil(Integer id) {
        validateId(id);
        if (perfilRepository.findById(id).isEmpty()) {
            throw new EntityNotFoundException("Perfil nao encontrado");
        }
        perfilRepository.deleteById(id);
    }

    public long contarPerfis() {
        return perfilRepository.count();
    }

    private static void validateId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser valido");
        }
    }
}
