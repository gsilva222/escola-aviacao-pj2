package pt.ipvc.estg.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import pt.ipvc.estg.entities.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Integer> {
    List<Student> findByCourse_Id(Integer courseId);
    List<Student> findByCourse_Id(Integer courseId, Pageable pageable);
    List<Student> findByStatus(String status);
    List<Student> findByStatus(String status, Pageable pageable);
    Optional<Student> findByEmailIgnoreCase(String email);
}
