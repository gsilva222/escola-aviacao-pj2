package pt.ipvc.estg.repositories;

import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository {
    Optional<Student> findById(Integer id);
    Optional<Student> findByEmail(String email);
    List<Student> findAll();
    PageResult<Student> findAll(PageQuery query);
    List<Student> findByCourse(Integer courseId);
    PageResult<Student> findByCourse(Integer courseId, PageQuery query);
    List<Student> findByStatus(String status);
    PageResult<Student> findByStatus(String status, PageQuery query);
    Student save(Student student);
    void deleteById(Integer id);
    boolean existsById(Integer id);
    long count();
}
