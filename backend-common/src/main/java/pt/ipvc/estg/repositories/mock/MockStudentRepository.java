package pt.ipvc.estg.repositories.mock;

import pt.ipvc.estg.dal.mock.StudentDAOMock;
import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.repositories.StudentRepository;
import pt.ipvc.estg.util.PageUtils;

import java.util.List;
import java.util.Optional;

public class MockStudentRepository implements StudentRepository {
    private final StudentDAOMock delegate = new StudentDAOMock();

    @Override
    public Optional<Student> findById(Integer id) {
        return delegate.findById(id);
    }

    @Override
    public Optional<Student> findByEmail(String email) {
        return delegate.findByEmail(email);
    }

    @Override
    public List<Student> findAll() {
        return delegate.findAll();
    }

    @Override
    public PageResult<Student> findAll(PageQuery query) {
        return PageUtils.paginate(delegate.findAll(), query);
    }

    @Override
    public List<Student> findByCourse(Integer courseId) {
        return delegate.findByCourse(courseId);
    }

    @Override
    public PageResult<Student> findByCourse(Integer courseId, PageQuery query) {
        return PageUtils.paginate(delegate.findByCourse(courseId), query);
    }

    @Override
    public List<Student> findByStatus(String status) {
        return delegate.findByStatus(status);
    }

    @Override
    public PageResult<Student> findByStatus(String status, PageQuery query) {
        return PageUtils.paginate(delegate.findByStatus(status), query);
    }

    @Override
    public Student save(Student student) {
        if (student.getId() == null) {
            return delegate.insert(student);
        }
        return delegate.update(student);
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
