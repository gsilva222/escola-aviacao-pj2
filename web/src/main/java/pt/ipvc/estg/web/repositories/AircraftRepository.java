package pt.ipvc.estg.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import pt.ipvc.estg.entities.Aircraft;

import java.util.List;
import java.util.Optional;

public interface AircraftRepository extends JpaRepository<Aircraft, Integer> {
	List<Aircraft> findByStatus(String status);
	List<Aircraft> findByStatus(String status, Pageable pageable);
	Optional<Aircraft> findByRegistrationIgnoreCase(String registration);
}
