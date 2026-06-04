package pt.ipvc.estg.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import pt.ipvc.estg.entities.Flight;

import java.time.LocalDate;
import java.util.List;

public interface FlightRepository extends JpaRepository<Flight, Integer> {
    List<Flight> findByStudent_Id(Integer studentId);
    List<Flight> findByStudent_Id(Integer studentId, Pageable pageable);
    List<Flight> findByStudent_IdAndStatus(Integer studentId, String status);
    List<Flight> findByStudent_IdAndStatus(Integer studentId, String status, Pageable pageable);
    List<Flight> findByStudent_IdAndFlightDateBetweenOrderByFlightDateAscFlightTimeAsc(
            Integer studentId, LocalDate from, LocalDate to);
    List<Flight> findByStatus(String status);
    List<Flight> findByStatus(String status, Pageable pageable);
    long countByStatus(String status);
}
