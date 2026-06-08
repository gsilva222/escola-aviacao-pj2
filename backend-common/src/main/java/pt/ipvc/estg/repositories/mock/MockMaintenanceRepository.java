package pt.ipvc.estg.repositories.mock;

import pt.ipvc.estg.dal.mock.MaintenanceDAOMock;
import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Maintenance;
import pt.ipvc.estg.repositories.MaintenanceRepository;
import pt.ipvc.estg.util.PageUtils;

import java.util.List;
import java.util.Optional;

public class MockMaintenanceRepository implements MaintenanceRepository {
    private final MaintenanceDAOMock delegate = new MaintenanceDAOMock();

    @Override
    public Optional<Maintenance> findById(Integer id) {
        return delegate.findById(id);
    }

    @Override
    public List<Maintenance> findAll() {
        return delegate.findAll();
    }

    @Override
    public PageResult<Maintenance> findAll(PageQuery query) {
        return PageUtils.paginate(delegate.findAll(), query);
    }

    @Override
    public List<Maintenance> findByAircraft(Integer aircraftId) {
        return delegate.findByAircraft(aircraftId);
    }

    @Override
    public PageResult<Maintenance> findByAircraft(Integer aircraftId, PageQuery query) {
        return PageUtils.paginate(delegate.findByAircraft(aircraftId), query);
    }

    @Override
    public List<Maintenance> findByStatus(String status) {
        return delegate.findByStatus(status);
    }

    @Override
    public PageResult<Maintenance> findByStatus(String status, PageQuery query) {
        return PageUtils.paginate(delegate.findByStatus(status), query);
    }

    @Override
    public List<Maintenance> findByPriority(String priority) {
        return delegate.findByPriority(priority);
    }

    @Override
    public PageResult<Maintenance> findByPriority(String priority, PageQuery query) {
        return PageUtils.paginate(delegate.findByPriority(priority), query);
    }

    @Override
    public Maintenance save(Maintenance maintenance) {
        return maintenance.getId() == null ? delegate.insert(maintenance) : delegate.update(maintenance);
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
