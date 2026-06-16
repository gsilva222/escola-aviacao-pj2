package pt.ipvc.estg.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ipvc.estg.entities.Perfil;

import java.util.Optional;

public interface PerfilJpaRepository extends JpaRepository<Perfil, Integer> {
    Optional<Perfil> findByNomeIgnoreCase(String nome);
}
