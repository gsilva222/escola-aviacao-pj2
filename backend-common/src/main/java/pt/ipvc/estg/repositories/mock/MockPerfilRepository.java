package pt.ipvc.estg.repositories.mock;

import pt.ipvc.estg.dal.mock.PerfilDAOMock;
import pt.ipvc.estg.entities.Perfil;
import pt.ipvc.estg.repositories.PerfilRepository;

import java.util.List;
import java.util.Optional;

public class MockPerfilRepository implements PerfilRepository {
    private final PerfilDAOMock delegate = new PerfilDAOMock();

    @Override
    public Optional<Perfil> findById(Integer id) {
        return delegate.findById(id);
    }

    @Override
    public Optional<Perfil> findByNome(String nome) {
        return delegate.findByNome(nome);
    }

    @Override
    public List<Perfil> findAll() {
        return delegate.findAll();
    }

    @Override
    public Perfil save(Perfil perfil) {
        return perfil.getId() == null ? delegate.insert(perfil) : delegate.update(perfil);
    }

    @Override
    public void deleteById(Integer id) {
        delegate.delete(id);
    }

    @Override
    public long count() {
        return delegate.count();
    }
}
