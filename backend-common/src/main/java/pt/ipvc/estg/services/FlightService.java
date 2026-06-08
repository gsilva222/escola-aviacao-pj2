package pt.ipvc.estg.services;

import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Instructor;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.exception.EntityNotFoundException;
import pt.ipvc.estg.repositories.AircraftRepository;
import pt.ipvc.estg.repositories.FlightRepository;
import pt.ipvc.estg.repositories.InstructorRepository;
import pt.ipvc.estg.repositories.StudentRepository;
import pt.ipvc.estg.validation.BusinessRules;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class FlightService {

    private final FlightRepository flightRepository;
    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;
    private final AircraftRepository aircraftRepository;

    public FlightService(FlightRepository flightRepository,
                         StudentRepository studentRepository,
                         InstructorRepository instructorRepository,
                         AircraftRepository aircraftRepository) {
        this.flightRepository = flightRepository;
        this.studentRepository = studentRepository;
        this.instructorRepository = instructorRepository;
        this.aircraftRepository = aircraftRepository;
    }

    public FlightService() {
        this(pt.ipvc.estg.bootstrap.MockServices.getInstance().flightRepository(),
                pt.ipvc.estg.bootstrap.MockServices.getInstance().studentRepository(),
                pt.ipvc.estg.bootstrap.MockServices.getInstance().instructorRepository(),
                pt.ipvc.estg.bootstrap.MockServices.getInstance().aircraftRepository());
    }

    public Optional<Flight> getVoo(Integer id) {
        validateId(id);
        return flightRepository.findById(id);
    }

    public Flight requireVoo(Integer id) {
        return getVoo(id).orElseThrow(() -> new EntityNotFoundException("Voo nao encontrado"));
    }

    public List<Flight> getAllVoos() {
        return flightRepository.findAll();
    }

    public PageResult<Flight> listVoos(PageQuery query, Integer studentId, String status) {
        if (studentId != null) {
            return flightRepository.findByStudent(studentId, query);
        }
        if (status != null && !status.trim().isEmpty()) {
            return flightRepository.findByStatus(status, query);
        }
        return flightRepository.findAll(query);
    }

    public List<Flight> getVoosPorEstudante(Integer studentId) {
        validateId(studentId);
        return flightRepository.findByStudent(studentId);
    }

    public List<Flight> getVoosPorInstrutor(Integer instructorId) {
        validateId(instructorId);
        return flightRepository.findByInstructor(instructorId);
    }

    public List<Flight> getVoosPorStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status deve ser valido");
        }
        return flightRepository.findByStatus(status);
    }

    public Flight criarVoo(LocalDate flightDate, Student student, Instructor instructor, Aircraft aircraft) {
        if (flightDate == null) throw new IllegalArgumentException("Data do voo e obrigatoria");
        if (student == null) throw new IllegalArgumentException("Estudante e obrigatorio");
        if (instructor == null) throw new IllegalArgumentException("Instrutor e obrigatorio");
        if (aircraft == null) throw new IllegalArgumentException("Aviao e obrigatorio");
        Flight flight = new Flight(flightDate, student, instructor, aircraft);
        return flightRepository.save(flight);
    }

    public Flight saveVoo(Flight flight) {
        resolveRelations(flight);
        if (flight.getStatus() != null) {
            flight.setStatus(BusinessRules.requireAllowed("Status", flight.getStatus(), BusinessRules.FLIGHT_STATUSES));
        }
        return flightRepository.save(flight);
    }

    public Flight atualizarVoo(Integer id, Double duration, String origin, String destination,
                               String flightType, String objectives, String grade) {
        Flight flight = requireVoo(id);
        if (duration != null && duration > 0) flight.setDuration(duration);
        if (origin != null && !origin.trim().isEmpty()) flight.setOrigin(origin);
        if (destination != null && !destination.trim().isEmpty()) flight.setDestination(destination);
        if (flightType != null) flight.setFlightType(flightType);
        if (objectives != null) flight.setObjectives(objectives);
        if (grade != null) flight.setGrade(grade);
        return flightRepository.save(flight);
    }

    public void marcarComoConcluido(Integer id, Double duration, String grade) {
        Flight flight = requireVoo(id);
        flight.setStatus("completed");
        if (duration != null) flight.setDuration(duration);
        if (grade != null) flight.setGrade(grade);
        flightRepository.save(flight);
    }

    public void eliminarVoo(Integer id) {
        validateId(id);
        if (!flightRepository.existsById(id)) {
            throw new EntityNotFoundException("Voo nao encontrado");
        }
        flightRepository.deleteById(id);
    }

    public long contarVoos() {
        return flightRepository.count();
    }

    private void resolveRelations(Flight flight) {
        if (flight.getStudent() != null && flight.getStudent().getId() != null) {
            Student student = studentRepository.findById(flight.getStudent().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Estudante nao encontrado"));
            flight.setStudent(student);
        }
        if (flight.getInstructor() != null && flight.getInstructor().getId() != null) {
            Instructor instructor = instructorRepository.findById(flight.getInstructor().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Instrutor nao encontrado"));
            flight.setInstructor(instructor);
        }
        if (flight.getAircraft() != null && flight.getAircraft().getId() != null) {
            Aircraft aircraft = aircraftRepository.findById(flight.getAircraft().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Aviao nao encontrado"));
            flight.setAircraft(aircraft);
        }
    }

    private static void validateId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser valido");
        }
    }
}
