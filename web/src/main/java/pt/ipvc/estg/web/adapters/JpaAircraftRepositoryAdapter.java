package pt.ipvc.estg.web.adapters;

import org.springframework.stereotype.Component;
import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.repositories.AircraftRepository;

import java.util.List;
import java.util.Optional;

@Component
public class JpaAircraftRepositoryAdapter implements AircraftRepository {

    private final pt.ipvc.estg.web.repositories.AircraftRepository jpa;

    public JpaAircraftRepositoryAdapter(pt.ipvc.estg.web.repositories.AircraftRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Aircraft> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Aircraft> findByRegistration(String registration) {
        return jpa.findByRegistrationIgnoreCase(registration);
    }

    @Override
    public List<Aircraft> findAll() {
        return jpa.findAll();
    }

    @Override
    public PageResult<Aircraft> findAll(PageQuery query) {
        var page = jpa.findAll(PageAdapter.toPageable(query));
        return new PageResult<>(page.getContent(), page.getTotalElements(), query.page(), query.size());
    }

    @Override
    public List<Aircraft> findByStatus(String status) {
        return jpa.findByStatus(status);
    }

    @Override
    public PageResult<Aircraft> findByStatus(String status, PageQuery query) {
        var pageable = PageAdapter.toPageable(query);
        List<Aircraft> content = jpa.findByStatus(status, pageable);
        long total = jpa.findByStatus(status).size();
        return PageAdapter.fromPage(content, total, query);
    }

    @Override
    public Aircraft save(Aircraft aircraft) {
        return jpa.save(aircraft);
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
