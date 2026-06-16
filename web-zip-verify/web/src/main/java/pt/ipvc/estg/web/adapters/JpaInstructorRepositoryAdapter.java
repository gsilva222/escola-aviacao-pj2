package pt.ipvc.estg.web.adapters;

import org.springframework.stereotype.Component;
import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Instructor;
import pt.ipvc.estg.repositories.InstructorRepository;

import java.util.List;
import java.util.Optional;

@Component
public class JpaInstructorRepositoryAdapter implements InstructorRepository {

    private final pt.ipvc.estg.web.repositories.InstructorRepository jpa;

    public JpaInstructorRepositoryAdapter(pt.ipvc.estg.web.repositories.InstructorRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Instructor> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Instructor> findByName(String name) {
        return jpa.findAll().stream()
                .filter(i -> i.getName() != null && i.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    @Override
    public List<Instructor> findAll() {
        return jpa.findAll();
    }

    @Override
    public PageResult<Instructor> findAll(PageQuery query) {
        var page = jpa.findAll(PageAdapter.toPageable(query));
        return new PageResult<>(page.getContent(), page.getTotalElements(), query.page(), query.size());
    }

    @Override
    public List<Instructor> findByStatus(String status) {
        return jpa.findByStatus(status);
    }

    @Override
    public PageResult<Instructor> findByStatus(String status, PageQuery query) {
        var pageable = PageAdapter.toPageable(query);
        List<Instructor> content = jpa.findByStatus(status, pageable);
        long total = jpa.findByStatus(status).size();
        return PageAdapter.fromPage(content, total, query);
    }

    @Override
    public Instructor save(Instructor instructor) {
        return jpa.save(instructor);
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
