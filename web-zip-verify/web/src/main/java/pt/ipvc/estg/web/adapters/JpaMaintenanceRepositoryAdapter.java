package pt.ipvc.estg.web.adapters;

import org.springframework.stereotype.Component;
import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Maintenance;
import pt.ipvc.estg.repositories.MaintenanceRepository;

import java.util.List;
import java.util.Optional;

@Component
public class JpaMaintenanceRepositoryAdapter implements MaintenanceRepository {

    private final pt.ipvc.estg.web.repositories.MaintenanceRepository jpa;

    public JpaMaintenanceRepositoryAdapter(pt.ipvc.estg.web.repositories.MaintenanceRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Maintenance> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public List<Maintenance> findAll() {
        return jpa.findAll();
    }

    @Override
    public PageResult<Maintenance> findAll(PageQuery query) {
        var page = jpa.findAll(PageAdapter.toPageable(query));
        return new PageResult<>(page.getContent(), page.getTotalElements(), query.page(), query.size());
    }

    @Override
    public List<Maintenance> findByAircraft(Integer aircraftId) {
        return jpa.findByAircraft_Id(aircraftId);
    }

    @Override
    public PageResult<Maintenance> findByAircraft(Integer aircraftId, PageQuery query) {
        var pageable = PageAdapter.toPageable(query);
        List<Maintenance> content = jpa.findByAircraft_Id(aircraftId, pageable);
        long total = jpa.findByAircraft_Id(aircraftId).size();
        return PageAdapter.fromPage(content, total, query);
    }

    @Override
    public List<Maintenance> findByStatus(String status) {
        return jpa.findByStatus(status);
    }

    @Override
    public PageResult<Maintenance> findByStatus(String status, PageQuery query) {
        var pageable = PageAdapter.toPageable(query);
        List<Maintenance> content = jpa.findByStatus(status, pageable);
        long total = jpa.findByStatus(status).size();
        return PageAdapter.fromPage(content, total, query);
    }

    @Override
    public List<Maintenance> findByPriority(String priority) {
        return jpa.findByPriority(priority);
    }

    @Override
    public PageResult<Maintenance> findByPriority(String priority, PageQuery query) {
        var pageable = PageAdapter.toPageable(query);
        List<Maintenance> content = jpa.findByPriority(priority, pageable);
        long total = jpa.findByPriority(priority).size();
        return PageAdapter.fromPage(content, total, query);
    }

    @Override
    public Maintenance save(Maintenance maintenance) {
        return jpa.save(maintenance);
    }

    @Override
    public void deleteById(Integer id) {
        jpa.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return jpa.existsById(id);
    }

    @Override
    public long count() {
        return jpa.count();
    }
}
