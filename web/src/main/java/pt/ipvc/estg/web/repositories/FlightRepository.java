package pt.ipvc.estg.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import pt.ipvc.estg.entities.Flight;

import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Integer> {
    List<Flight> findByStudent_Id(Integer studentId);
    List<Flight> findByStudent_Id(Integer studentId, Pageable pageable);
    List<Flight> findByStatus(String status);
    List<Flight> findByStatus(String status, Pageable pageable);
}
