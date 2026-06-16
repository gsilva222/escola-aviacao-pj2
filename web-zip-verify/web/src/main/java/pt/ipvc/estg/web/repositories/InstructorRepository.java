package pt.ipvc.estg.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import pt.ipvc.estg.entities.Instructor;

import java.util.List;

public interface InstructorRepository extends JpaRepository<Instructor, Integer> {
	List<Instructor> findByStatus(String status);
	List<Instructor> findByStatus(String status, Pageable pageable);
}
