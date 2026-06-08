package pt.ipvc.estg.repositories;

import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Course;

import java.util.List;
import java.util.Optional;

public interface CourseRepository {
    Optional<Course> findById(Integer id);
    Optional<Course> findByName(String name);
    List<Course> findAll();
    PageResult<Course> findAll(PageQuery query);
    Course save(Course course);
    void deleteById(Integer id);
    boolean existsById(Integer id);
    long count();
}
