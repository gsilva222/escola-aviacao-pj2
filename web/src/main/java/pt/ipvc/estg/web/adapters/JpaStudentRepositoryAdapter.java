package pt.ipvc.estg.web.adapters;

import org.springframework.stereotype.Component;
import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.repositories.StudentRepository;

import java.util.List;
import java.util.Optional;

@Component
public class JpaStudentRepositoryAdapter implements StudentRepository {

    private final pt.ipvc.estg.web.repositories.StudentRepository jpa;

    public JpaStudentRepositoryAdapter(pt.ipvc.estg.web.repositories.StudentRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Student> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<Student> findByEmail(String email) {
        return jpa.findByEmailIgnoreCase(email);
    }

    @Override
    public List<Student> findAll() {
        return jpa.findAll();
    }

    @Override
    public PageResult<Student> findAll(PageQuery query) {
        var page = jpa.findAll(PageAdapter.toPageable(query));
        return new PageResult<>(page.getContent(), page.getTotalElements(), query.page(), query.size());
    }

    @Override
    public List<Student> findByCourse(Integer courseId) {
        return jpa.findByCourse_Id(courseId);
    }

    @Override
    public PageResult<Student> findByCourse(Integer courseId, PageQuery query) {
        var pageable = PageAdapter.toPageable(query);
        List<Student> content = jpa.findByCourse_Id(courseId, pageable);
        long total = jpa.findByCourse_Id(courseId).size();
        return PageAdapter.fromPage(content, total, query);
    }

    @Override
    public List<Student> findByStatus(String status) {
        return jpa.findByStatus(status);
    }

    @Override
    public PageResult<Student> findByStatus(String status, PageQuery query) {
        var pageable = PageAdapter.toPageable(query);
        List<Student> content = jpa.findByStatus(status, pageable);
        long total = jpa.findByStatus(status).size();
        return PageAdapter.fromPage(content, total, query);
    }

    @Override
    public Student save(Student student) {
        return jpa.save(student);
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
