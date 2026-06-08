package pt.ipvc.estg.repositories;

import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Instructor;

import java.util.List;
import java.util.Optional;

public interface InstructorRepository {
    Optional<Instructor> findById(Integer id);
    Optional<Instructor> findByName(String name);
    List<Instructor> findAll();
    PageResult<Instructor> findAll(PageQuery query);
    List<Instructor> findByStatus(String status);
    PageResult<Instructor> findByStatus(String status, PageQuery query);
    Instructor save(Instructor instructor);
    void deleteById(Integer id);
    boolean existsById(Integer id);
    long count();
}
