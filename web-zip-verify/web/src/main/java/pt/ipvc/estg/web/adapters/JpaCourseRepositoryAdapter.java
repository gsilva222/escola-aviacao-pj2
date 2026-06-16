package pt.ipvc.estg.web.adapters;

import org.springframework.stereotype.Component;
import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.repositories.CourseRepository;

import java.util.List;
import java.util.Optional;

@Component
public class JpaCourseRepositoryAdapter implements CourseRepository {

    private final pt.ipvc.estg.web.repositories.CourseRepository jpa;

    public JpaCourseRepositoryAdapter(pt.ipvc.estg.web.repositories.CourseRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Course> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Course> findByName(String name) {
        return jpa.findByNameIgnoreCase(name);
    }

    @Override
    public List<Course> findAll() {
        return jpa.findAll();
    }

    @Override
    public PageResult<Course> findAll(PageQuery query) {
        var page = jpa.findAll(PageAdapter.toPageable(query));
        return new PageResult<>(page.getContent(), page.getTotalElements(), query.page(), query.size());
    }

    @Override
    public Course save(Course course) {
        return jpa.save(course);
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
