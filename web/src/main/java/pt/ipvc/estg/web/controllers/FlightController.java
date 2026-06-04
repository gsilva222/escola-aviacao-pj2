package pt.ipvc.estg.web.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Instructor;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.web.dto.FlightRequest;
import pt.ipvc.estg.web.dto.FlightResponse;
import pt.ipvc.estg.web.mappers.FlightMapper;
import pt.ipvc.estg.web.repositories.AircraftRepository;
import pt.ipvc.estg.web.repositories.FlightRepository;
import pt.ipvc.estg.web.repositories.InstructorRepository;
import pt.ipvc.estg.web.repositories.StudentRepository;
import pt.ipvc.estg.web.validation.BusinessRules;

import java.util.List;

@RestController
@RequestMapping("/bo/flights")
public class FlightController {

    private final FlightRepository flightRepository;
    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;
    private final AircraftRepository aircraftRepository;

    public FlightController(FlightRepository flightRepository,
                            StudentRepository studentRepository,
                            InstructorRepository instructorRepository,
                            AircraftRepository aircraftRepository) {
        this.flightRepository = flightRepository;
        this.studentRepository = studentRepository;
        this.instructorRepository = instructorRepository;
        this.aircraftRepository = aircraftRepository;
    }

    @GetMapping
    public Page<FlightResponse> getFlights(@RequestParam(value = "studentId", required = false) Integer studentId,
                                           @RequestParam(value = "status", required = false) String status,
                                           @RequestParam(value = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "size", defaultValue = "20") int size,
                                           @RequestParam(value = "sort", required = false) String sort) {
        PageRequest pageable = PageRequest.of(page, size, buildSort(sort));
        if (studentId != null) {
            List<FlightResponse> content = flightRepository.findByStudent_Id(studentId, pageable)
                    .stream().map(FlightMapper::toResponse).toList();
            long total = flightRepository.findByStudent_Id(studentId).size();
            return new PageImpl<>(content, pageable, total);
        }
        if (status != null && !status.trim().isEmpty()) {
            List<FlightResponse> content = flightRepository.findByStatus(status, pageable)
                    .stream().map(FlightMapper::toResponse).toList();
            long total = flightRepository.findByStatus(status).size();
            return new PageImpl<>(content, pageable, total);
        }
        return flightRepository.findAll(pageable).map(FlightMapper::toResponse);
    }

    @GetMapping("/{id}")
    public FlightResponse getFlight(@PathVariable("id") Integer id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Voo nao encontrado"));
        return FlightMapper.toResponse(flight);
    }

    @PostMapping
    public FlightResponse createFlight(@Valid @RequestBody FlightRequest request) {
        BusinessRules.requirePositive("Duracao", request.duration());
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Estudante nao encontrado"));
        Instructor instructor = instructorRepository.findById(request.instructorId())
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Instrutor nao encontrado"));
        Aircraft aircraft = aircraftRepository.findById(request.aircraftId())
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Aeronave nao encontrada"));

        Flight flight = new Flight(request.flightDate(), student, instructor, aircraft);
        if (request.flightTime() != null) flight.setFlightTime(request.flightTime());
        if (request.duration() != null) flight.setDuration(request.duration());
        if (request.origin() != null) flight.setOrigin(request.origin());
        if (request.destination() != null) flight.setDestination(request.destination());
        if (request.flightType() != null) flight.setFlightType(request.flightType());
        if (request.status() != null) flight.setStatus(BusinessRules.requireAllowed("Status", request.status(), BusinessRules.FLIGHT_STATUSES));
        if (request.objectives() != null) flight.setObjectives(request.objectives());
        if (request.notes() != null) flight.setNotes(request.notes());
        if (request.grade() != null) flight.setGrade(request.grade());

        Flight created = flightRepository.save(flight);
        return FlightMapper.toResponse(created);
    }

    @PutMapping("/{id}")
    public FlightResponse updateFlight(@PathVariable("id") Integer id, @RequestBody FlightRequest request) {
        BusinessRules.requirePositive("Duracao", request.duration());
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Voo nao encontrado"));

        if (request.flightDate() != null) flight.setFlightDate(request.flightDate());
        if (request.flightTime() != null) flight.setFlightTime(request.flightTime());
        if (request.duration() != null) flight.setDuration(request.duration());
        if (request.origin() != null) flight.setOrigin(request.origin());
        if (request.destination() != null) flight.setDestination(request.destination());
        if (request.flightType() != null) flight.setFlightType(request.flightType());
        if (request.status() != null) flight.setStatus(BusinessRules.requireAllowed("Status", request.status(), BusinessRules.FLIGHT_STATUSES));
        if (request.objectives() != null) flight.setObjectives(request.objectives());
        if (request.notes() != null) flight.setNotes(request.notes());
        if (request.grade() != null) flight.setGrade(request.grade());

        if (request.studentId() != null) {
            Student student = studentRepository.findById(request.studentId())
                    .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Estudante nao encontrado"));
            flight.setStudent(student);
        }

        if (request.instructorId() != null) {
            Instructor instructor = instructorRepository.findById(request.instructorId())
                    .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Instrutor nao encontrado"));
            flight.setInstructor(instructor);
        }

        if (request.aircraftId() != null) {
            Aircraft aircraft = aircraftRepository.findById(request.aircraftId())
                    .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Aeronave nao encontrada"));
            flight.setAircraft(aircraft);
        }

        Flight updated = flightRepository.save(flight);
        return FlightMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteFlight(@PathVariable("id") Integer id) {
        if (!flightRepository.existsById(id)) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Voo nao encontrado");
        }
        flightRepository.deleteById(id);
    }

    private Sort buildSort(String sort) {
        if (sort == null || sort.trim().isEmpty()) {
            return Sort.unsorted();
        }
        String[] parts = sort.split(",", 2);
        String property = parts[0].trim();
        if (property.isEmpty()) {
            return Sort.unsorted();
        }
        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length == 2 && "desc".equalsIgnoreCase(parts[1].trim())) {
            direction = Sort.Direction.DESC;
        }
        return Sort.by(direction, property);
    }
}
