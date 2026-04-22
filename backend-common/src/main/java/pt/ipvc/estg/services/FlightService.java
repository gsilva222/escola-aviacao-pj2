package pt.ipvc.estg.services;

import pt.ipvc.estg.dal.mock.FlightDAOMock;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.Instructor;
import pt.ipvc.estg.entities.Aircraft;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Serviço de Flight - Lógica de negócio para Voos
 */
public class FlightService {
    
    private final FlightDAOMock flightDAO;
    
    public FlightService() {
        this.flightDAO = new FlightDAOMock();
    }
    
    public Optional<Flight> getVoo(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        return flightDAO.findById(id);
    }
    
    public List<Flight> getAllVoos() {
        return flightDAO.findAll();
    }
    
    public List<Flight> getVoosPorEstudante(Integer studentId) {
        if (studentId == null || studentId <= 0) {
            throw new IllegalArgumentException("Student ID deve ser válido");
        }
        return flightDAO.findByStudent(studentId);
    }
    
    public List<Flight> getVoosPorInstrutor(Integer instructorId) {
        if (instructorId == null || instructorId <= 0) {
            throw new IllegalArgumentException("Instructor ID deve ser válido");
        }
        return flightDAO.findByInstructor(instructorId);
    }
    
    public List<Flight> getVoosPorStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status deve ser válido");
        }
        return flightDAO.findByStatus(status);
    }
    
    public Flight criarVoo(LocalDate flightDate, Student student, Instructor instructor, Aircraft aircraft) {
        if (flightDate == null) {
            throw new IllegalArgumentException("Data do voo é obrigatória");
        }
        if (student == null) {
            throw new IllegalArgumentException("Estudante é obrigatório");
        }
        if (instructor == null) {
            throw new IllegalArgumentException("Instrutor é obrigatório");
        }
        if (aircraft == null) {
            throw new IllegalArgumentException("Avião é obrigatório");
        }
        
        Flight flight = new Flight(flightDate, student, instructor, aircraft);
        return flightDAO.insert(flight);
    }
    
    public Flight atualizarVoo(Integer id, Double duration, String origin, String destination, 
                             String flightType, String objectives, String grade) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        
        Optional<Flight> opt = flightDAO.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Voo não encontrado");
        }
        
        Flight flight = opt.get();
        
        if (duration != null && duration > 0) {
            flight.setDuration(duration);
        }
        if (origin != null && !origin.trim().isEmpty()) {
            flight.setOrigin(origin);
        }
        if (destination != null && !destination.trim().isEmpty()) {
            flight.setDestination(destination);
        }
        if (flightType != null) {
            flight.setFlightType(flightType);
        }
        if (objectives != null) {
            flight.setObjectives(objectives);
        }
        if (grade != null) {
            flight.setGrade(grade);
        }
        
        return flightDAO.update(flight);
    }
    
    public void marcarComoConcluido(Integer id, Double duration, String grade) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        
        Optional<Flight> opt = flightDAO.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Voo não encontrado");
        }
        
        Flight flight = opt.get();
        flight.setStatus("completed");
        if (duration != null) flight.setDuration(duration);
        if (grade != null) flight.setGrade(grade);
        
        flightDAO.update(flight);
    }
    
    public void eliminarVoo(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        if (flightDAO.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Voo não encontrado");
        }
        flightDAO.delete(id);
    }
    
    public long contarVoos() {
        return flightDAO.count();
    }
}
