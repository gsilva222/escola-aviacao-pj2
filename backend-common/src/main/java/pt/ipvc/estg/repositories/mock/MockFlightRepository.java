package pt.ipvc.estg.repositories.mock;

import pt.ipvc.estg.dal.mock.FlightDAOMock;
import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.repositories.FlightRepository;
import pt.ipvc.estg.util.FlightHoursCalculator;
import pt.ipvc.estg.util.PageUtils;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class MockFlightRepository implements FlightRepository {
    private final FlightDAOMock delegate = new FlightDAOMock();

    @Override
    public Optional<Flight> findById(Integer id) {
        return delegate.findById(id);
    }

    @Override
    public List<Flight> findAll() {
        return delegate.findAll();
    }

    @Override
    public PageResult<Flight> findAll(PageQuery query) {
        return PageUtils.paginate(delegate.findAll(), query);
    }

    @Override
    public List<Flight> findByStudent(Integer studentId) {
        return delegate.findByStudent(studentId);
    }

    @Override
    public PageResult<Flight> findByStudent(Integer studentId, PageQuery query) {
        return PageUtils.paginate(delegate.findByStudent(studentId), query);
    }

    @Override
    public List<Flight> findByStudentAndStatus(Integer studentId, String status) {
        return delegate.findByStudent(studentId).stream()
                .filter(f -> status.equals(FlightHoursCalculator.normalizeStatus(f)))
                .toList();
    }

    @Override
    public PageResult<Flight> findByStudentAndStatus(Integer studentId, String status, PageQuery query) {
        return PageUtils.paginate(findByStudentAndStatus(studentId, status), query);
    }

    @Override
    public List<Flight> findByStudentAndFlightDateBetween(Integer studentId, LocalDate from, LocalDate to) {
        return delegate.findByStudent(studentId).stream()
                .filter(f -> f.getFlightDate() != null
                        && !f.getFlightDate().isBefore(from)
                        && !f.getFlightDate().isAfter(to))
                .sorted(Comparator.comparing(Flight::getFlightDate, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Flight::getFlightTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();
    }

    @Override
    public List<Flight> findByInstructor(Integer instructorId) {
        return delegate.findByInstructor(instructorId);
    }

    @Override
    public List<Flight> findByStatus(String status) {
        return delegate.findByStatus(status);
    }

    @Override
    public PageResult<Flight> findByStatus(String status, PageQuery query) {
        return PageUtils.paginate(delegate.findByStatus(status), query);
    }

    @Override
    public long countByStatus(String status) {
        return delegate.findByStatus(status).size();
    }

    @Override
    public Flight save(Flight flight) {
        return flight.getId() == null ? delegate.insert(flight) : delegate.update(flight);
    }

    @Override
    public void deleteById(Integer id) {
        delegate.delete(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return delegate.findById(id).isPresent();
    }

    @Override
    public long count() {
        return delegate.count();
    }
}
