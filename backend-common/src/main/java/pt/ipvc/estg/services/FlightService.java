package pt.ipvc.estg.services;

import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.entities.Maintenance;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Instructor;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.exception.EntityNotFoundException;
import pt.ipvc.estg.repositories.AircraftRepository;
import pt.ipvc.estg.repositories.MaintenanceRepository;
import pt.ipvc.estg.repositories.FlightRepository;
import pt.ipvc.estg.repositories.InstructorRepository;
import pt.ipvc.estg.repositories.StudentRepository;
import pt.ipvc.estg.validation.BusinessRules;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class FlightService {

    private final FlightRepository flightRepository;
    private final StudentRepository studentRepository;
    private final InstructorRepository instructorRepository;
    private final AircraftRepository aircraftRepository;
    private final MaintenanceRepository maintenanceRepository;

    public FlightService(FlightRepository flightRepository,
                         StudentRepository studentRepository,
                         InstructorRepository instructorRepository,
                         AircraftRepository aircraftRepository,
                         MaintenanceRepository maintenanceRepository) {
        this.flightRepository = flightRepository;
        this.studentRepository = studentRepository;
        this.instructorRepository = instructorRepository;
        this.aircraftRepository = aircraftRepository;
        this.maintenanceRepository = maintenanceRepository;
    }

    public FlightService() {
        this(pt.ipvc.estg.bootstrap.MockServices.getInstance().flightRepository(),
                pt.ipvc.estg.bootstrap.MockServices.getInstance().studentRepository(),
                pt.ipvc.estg.bootstrap.MockServices.getInstance().instructorRepository(),
                pt.ipvc.estg.bootstrap.MockServices.getInstance().aircraftRepository(),
                pt.ipvc.estg.bootstrap.MockServices.getInstance().maintenanceRepository());
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

    public Flight criarVoo(LocalDate flightDate, LocalTime flightTime, Double duration,
                           Student student, Instructor instructor, Aircraft aircraft,
                           String origin, String destination, String flightType) {
        if (flightDate == null) throw new IllegalArgumentException("Data do voo e obrigatoria");
        if (flightTime == null) throw new IllegalArgumentException("Hora do voo e obrigatoria");
        if (duration == null || duration <= 0) throw new IllegalArgumentException("Duracao deve ser superior a zero");
        if (student == null) throw new IllegalArgumentException("Estudante e obrigatorio");
        if (instructor == null) throw new IllegalArgumentException("Instrutor e obrigatorio");
        if (aircraft == null) throw new IllegalArgumentException("Aviao e obrigatorio");

        Flight flight = new Flight(flightDate, student, instructor, aircraft);
        flight.setFlightTime(flightTime);
        flight.setDuration(duration);
        flight.setOrigin(origin != null && !origin.isBlank() ? origin.trim().toUpperCase() : "LPPT");
        flight.setDestination(destination != null && !destination.isBlank() ? destination.trim().toUpperCase() : "LPPT");
        flight.setFlightType(flightType != null && !flightType.isBlank() ? flightType.trim() : "training");
        flight.setStatus("scheduled");
        return saveVoo(flight);
    }

    public Flight saveVoo(Flight flight) {
        resolveRelations(flight);
        validateSchedulingRules(flight);
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
        return saveVoo(flight);
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

    private void validateSchedulingRules(Flight flight) {
        // Aplica regras apenas quando o objetivo é agendar (scheduled). Para completed/cancelled, não bloqueamos.
        boolean isScheduling = flight.getStatus() == null || "scheduled".equalsIgnoreCase(flight.getStatus());
        if (!isScheduling) {
            return;
        }

        if (flight.getInstructor() != null && flight.getInstructor().getStatus() != null) {
            // Regra: instrutor inativo nao pode agendar voos.
            if ("inactive".equalsIgnoreCase(flight.getInstructor().getStatus())) {
                throw new IllegalArgumentException("Instrutor inativo: nao e permitido agendar voos");
            }
        }

        if (flight.getStudent() != null && "suspended".equalsIgnoreCase(flight.getStudent().getStatus())) {
            throw new IllegalArgumentException("Aluno suspenso: nao e permitido criar voos");
        }

        validateNotInPast(flight);
        validateAircraftMaintenanceWindow(flight);
        validateScheduleConflicts(flight);
    }

    private void validateNotInPast(Flight flight) {
        if (flight.getFlightDate() == null) {
            return;
        }
        LocalDate today = LocalDate.now();
        if (flight.getFlightDate().isBefore(today)) {
            throw new IllegalArgumentException("Nao e possivel agendar voos para datas anteriores a hoje");
        }
        if (flight.getFlightDate().equals(today) && flight.getFlightTime() != null) {
            LocalDateTime start = LocalDateTime.of(flight.getFlightDate(), flight.getFlightTime());
            if (start.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Nao e possivel agendar voos para horarios ja passados");
            }
        }
    }

    private void validateScheduleConflicts(Flight candidate) {
        requireScheduleSlot(candidate);

        LocalDateTime candidateStart = toStartDateTime(candidate);
        LocalDateTime candidateEnd = toEndDateTime(candidateStart, candidate.getDuration());

        for (Flight existing : flightRepository.findAll()) {
            if (existing == null || isSameRecord(candidate, existing)) {
                continue;
            }
            if (!isActiveSchedule(existing)) {
                continue;
            }
            if (!sameDay(candidate, existing)) {
                continue;
            }

            if (sameStudent(candidate, existing)) {
                ensureNoTimeConflict(candidateStart, candidateEnd, existing,
                        "Conflito de agendamento: o aluno ja tem aula agendada neste horario");
            }
            if (sameInstructor(candidate, existing)) {
                ensureNoTimeConflict(candidateStart, candidateEnd, existing,
                        "Conflito de agendamento: o instrutor ja tem voo agendado neste horario");
            }
            if (sameAircraft(candidate, existing)) {
                ensureNoTimeConflict(candidateStart, candidateEnd, existing,
                        "Conflito de agendamento: a aeronave ja tem voo agendado neste horario");
            }
        }
    }

    private void requireScheduleSlot(Flight flight) {
        if (flight.getFlightTime() == null) {
            throw new IllegalArgumentException("Hora do voo e obrigatoria para agendamento");
        }
        if (flight.getDuration() == null || flight.getDuration() <= 0) {
            throw new IllegalArgumentException("Duracao deve ser superior a zero para agendamento");
        }
    }

    private boolean hasScheduleSlot(Flight flight) {
        return flight.getFlightTime() != null
                && flight.getDuration() != null
                && flight.getDuration() > 0;
    }

    private void ensureNoTimeConflict(LocalDateTime candidateStart, LocalDateTime candidateEnd,
                                      Flight existing, String message) {
        if (!hasScheduleSlot(existing)) {
            throw new IllegalArgumentException(message);
        }
        LocalDateTime existingStart = toStartDateTime(existing);
        LocalDateTime existingEnd = toEndDateTime(existingStart, existing.getDuration());
        if (intervalsOverlap(candidateStart, candidateEnd, existingStart, existingEnd)) {
            throw new IllegalArgumentException(message);
        }
    }

    private static boolean intervalsOverlap(LocalDateTime aStart, LocalDateTime aEnd,
                                            LocalDateTime bStart, LocalDateTime bEnd) {
        return aStart.isBefore(bEnd) && bStart.isBefore(aEnd);
    }

    private static boolean isSameRecord(Flight candidate, Flight existing) {
        return existing.getId() != null && candidate.getId() != null
                && existing.getId().equals(candidate.getId());
    }

    private static boolean isActiveSchedule(Flight flight) {
        String status = flight.getStatus() != null ? flight.getStatus() : "";
        return "scheduled".equalsIgnoreCase(status);
    }

    private static boolean sameDay(Flight a, Flight b) {
        return a.getFlightDate() != null && a.getFlightDate().equals(b.getFlightDate());
    }

    private static boolean sameStudent(Flight a, Flight b) {
        return a.getStudent() != null && b.getStudent() != null
                && a.getStudent().getId() != null && b.getStudent().getId() != null
                && a.getStudent().getId().equals(b.getStudent().getId());
    }

    private static boolean sameInstructor(Flight a, Flight b) {
        return a.getInstructor() != null && b.getInstructor() != null
                && a.getInstructor().getId() != null && b.getInstructor().getId() != null
                && a.getInstructor().getId().equals(b.getInstructor().getId());
    }

    private static boolean sameAircraft(Flight a, Flight b) {
        return a.getAircraft() != null && b.getAircraft() != null
                && a.getAircraft().getId() != null && b.getAircraft().getId() != null
                && a.getAircraft().getId().equals(b.getAircraft().getId());
    }

    private void validateAircraftMaintenanceWindow(Flight flight) {
        if (flight.getAircraft() == null || flight.getAircraft().getId() == null) {
            return;
        }
        if (flight.getFlightDate() == null) {
            return;
        }

        List<Maintenance> maintenances = maintenanceRepository.findByAircraft(flight.getAircraft().getId());
        for (Maintenance m : maintenances) {
            String status = m.getStatus() != null ? m.getStatus() : "";
            if ("completed".equalsIgnoreCase(status)) {
                continue;
            }
            if (m.getEstimatedEndDate() == null) {
                // Sem prazo estimado, não conseguimos validar "após o prazo"; por consistência bloqueamos.
                throw new IllegalArgumentException("Aeronave em manutencao sem prazo estimado");
            }
            // Regra: só permitir agendar depois do prazo estimado (estrito: flightDate > estimatedEndDate).
            if (!flight.getFlightDate().isAfter(m.getEstimatedEndDate())) {
                throw new IllegalArgumentException("Aeronave em manutencao ate " + m.getEstimatedEndDate() + " (agendamento bloqueado)");
            }
        }
    }

    private LocalDateTime toStartDateTime(Flight f) {
        return LocalDateTime.of(f.getFlightDate(), f.getFlightTime());
    }

    private LocalDateTime toEndDateTime(LocalDateTime start, double durationHours) {
        long nanos = Math.round(durationHours * 3600d * 1_000_000_000d);
        return start.plusNanos(nanos);
    }

    private static void validateId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser valido");
        }
    }
}
