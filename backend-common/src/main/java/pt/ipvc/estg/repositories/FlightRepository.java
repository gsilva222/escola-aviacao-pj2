package pt.ipvc.estg.repositories;

import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Flight;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface FlightRepository {
    Optional<Flight> findById(Integer id);
    List<Flight> findAll();
    PageResult<Flight> findAll(PageQuery query);
    List<Flight> findByStudent(Integer studentId);
    PageResult<Flight> findByStudent(Integer studentId, PageQuery query);
    List<Flight> findByStudentAndStatus(Integer studentId, String status);
    PageResult<Flight> findByStudentAndStatus(Integer studentId, String status, PageQuery query);
    List<Flight> findByStudentAndFlightDateBetween(Integer studentId, LocalDate from, LocalDate to);
    List<Flight> findByInstructor(Integer instructorId);
    List<Flight> findByStatus(String status);
    PageResult<Flight> findByStatus(String status, PageQuery query);
    long countByStatus(String status);
    Flight save(Flight flight);
    void deleteById(Integer id);
    boolean existsById(Integer id);
    long count();
}
