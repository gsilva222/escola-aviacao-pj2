package pt.ipvc.estg.web.adapters;

import org.springframework.stereotype.Component;
import pt.ipvc.estg.entities.Perfil;
import pt.ipvc.estg.repositories.PerfilRepository;

import java.util.List;
import java.util.Optional;

@Component
public class JpaPerfilRepositoryAdapter implements PerfilRepository {

    private final pt.ipvc.estg.web.repositories.PerfilJpaRepository jpa;

    public JpaPerfilRepositoryAdapter(pt.ipvc.estg.web.repositories.PerfilJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Perfil> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Perfil> findByNome(String nome) {
        return jpa.findByNomeIgnoreCase(nome);
    }

    @Override
    public List<Perfil> findAll() {
        return jpa.findAll();
    }

    @Override
    public Perfil save(Perfil perfil) {
        return jpa.save(perfil);
    }

    @Override
    public void deleteById(Integer id) {
        jpa.deleteById(id);
    }

    @Override
    public long count() {
        return jpa.count();
    }
}
