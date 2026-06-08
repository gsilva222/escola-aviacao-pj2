package pt.ipvc.estg.repositories.mock;

import pt.ipvc.estg.dal.mock.CourseDAOMock;
import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.repositories.CourseRepository;
import pt.ipvc.estg.util.PageUtils;

import java.util.List;
import java.util.Optional;

public class MockCourseRepository implements CourseRepository {
    private final CourseDAOMock delegate = new CourseDAOMock();

    @Override
    public Optional<Course> findById(Integer id) {
        return delegate.findById(id);
    }

    @Override
    public Optional<Course> findByName(String name) {
        return delegate.findByName(name);
    }

    @Override
    public List<Course> findAll() {
        return delegate.findAll();
    }

    @Override
    public PageResult<Course> findAll(PageQuery query) {
        return PageUtils.paginate(delegate.findAll(), query);
    }

    @Override
    public Course save(Course course) {
        return course.getId() == null ? delegate.insert(course) : delegate.update(course);
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
