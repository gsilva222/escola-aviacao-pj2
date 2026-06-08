package pt.ipvc.estg.web.adapters;

import org.springframework.stereotype.Component;
import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.repositories.FlightRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class JpaFlightRepositoryAdapter implements FlightRepository {

    private final pt.ipvc.estg.web.repositories.FlightRepository jpa;

    public JpaFlightRepositoryAdapter(pt.ipvc.estg.web.repositories.FlightRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Flight> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public List<Flight> findAll() {
        return jpa.findAll();
    }

    @Override
    public PageResult<Flight> findAll(PageQuery query) {
        var page = jpa.findAll(PageAdapter.toPageable(query));
        return new PageResult<>(page.getContent(), page.getTotalElements(), query.page(), query.size());
    }

    @Override
    public List<Flight> findByStudent(Integer studentId) {
        return jpa.findByStudent_Id(studentId);
    }

    @Override
    public PageResult<Flight> findByStudent(Integer studentId, PageQuery query) {
        var pageable = PageAdapter.toPageable(query);
        List<Flight> content = jpa.findByStudent_Id(studentId, pageable);
        long total = jpa.findByStudent_Id(studentId).size();
        return PageAdapter.fromPage(content, total, query);
    }

    @Override
    public List<Flight> findByStudentAndStatus(Integer studentId, String status) {
        return jpa.findByStudent_IdAndStatus(studentId, status);
    }

    @Override
    public PageResult<Flight> findByStudentAndStatus(Integer studentId, String status, PageQuery query) {
        var pageable = PageAdapter.toPageable(query);
        List<Flight> content = jpa.findByStudent_IdAndStatus(studentId, status, pageable);
        long total = jpa.findByStudent_IdAndStatus(studentId, status).size();
        return PageAdapter.fromPage(content, total, query);
    }

    @Override
    public List<Flight> findByStudentAndFlightDateBetween(Integer studentId, LocalDate from, LocalDate to) {
        return jpa.findByStudent_IdAndFlightDateBetweenOrderByFlightDateAscFlightTimeAsc(studentId, from, to);
    }

    @Override
    public List<Flight> findByInstructor(Integer instructorId) {
        return jpa.findAll().stream()
                .filter(f -> f.getInstructor() != null && instructorId.equals(f.getInstructor().getId()))
                .toList();
    }

    @Override
    public List<Flight> findByStatus(String status) {
        return jpa.findByStatus(status);
    }

    @Override
    public PageResult<Flight> findByStatus(String status, PageQuery query) {
        var pageable = PageAdapter.toPageable(query);
        List<Flight> content = jpa.findByStatus(status, pageable);
        long total = jpa.findByStatus(status).size();
        return PageAdapter.fromPage(content, total, query);
    }

    @Override
    public long countByStatus(String status) {
        return jpa.countByStatus(status);
    }

    @Override
    public Flight save(Flight flight) {
        return jpa.save(flight);
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
