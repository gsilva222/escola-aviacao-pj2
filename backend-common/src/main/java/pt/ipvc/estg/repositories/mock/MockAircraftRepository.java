package pt.ipvc.estg.repositories.mock;

import pt.ipvc.estg.dal.mock.AircraftDAOMock;
import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.repositories.AircraftRepository;
import pt.ipvc.estg.util.PageUtils;

import java.util.List;
import java.util.Optional;

public class MockAircraftRepository implements AircraftRepository {
    private final AircraftDAOMock delegate = new AircraftDAOMock();

    @Override
    public Optional<Aircraft> findById(Integer id) {
        return delegate.findById(id);
    }

    @Override
    public Optional<Aircraft> findByRegistration(String registration) {
        return delegate.findByRegistration(registration);
    }

    @Override
    public List<Aircraft> findAll() {
        return delegate.findAll();
    }

    @Override
    public PageResult<Aircraft> findAll(PageQuery query) {
        return PageUtils.paginate(delegate.findAll(), query);
    }

    @Override
    public List<Aircraft> findByStatus(String status) {
        return delegate.findByStatus(status);
    }

    @Override
    public PageResult<Aircraft> findByStatus(String status, PageQuery query) {
        return PageUtils.paginate(delegate.findByStatus(status), query);
    }

    @Override
    public Aircraft save(Aircraft aircraft) {
        return aircraft.getId() == null ? delegate.insert(aircraft) : delegate.update(aircraft);
    }

    @Override
    public void deleteById(Integer id) {
        delegate.delete(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return delegate.findById(id).isPresent();
    }

    @Override
    public long count() {
        return delegate.count();
    }
}
