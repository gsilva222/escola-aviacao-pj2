package pt.ipvc.estg.repositories;

import pt.ipvc.estg.entities.Perfil;

import java.util.List;
import java.util.Optional;

public interface PerfilRepository {
    Optional<Perfil> findById(Integer id);
    Optional<Perfil> findByNome(String nome);
    List<Perfil> findAll();
    Perfil save(Perfil perfil);
    void deleteById(Integer id);
    long count();
}
