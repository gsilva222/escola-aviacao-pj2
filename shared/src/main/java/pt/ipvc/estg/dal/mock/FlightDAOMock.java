package pt.ipvc.estg.dal.mock;

import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.Instructor;
import pt.ipvc.estg.entities.Aircraft;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DAO Mock para entidade Flight
 */
public class FlightDAOMock {
    
    private static final Map<Integer, Flight> database = new HashMap<>();
    private static final AtomicInteger idSequence = new AtomicInteger(1);
    
    public Optional<Flight> findById(Integer id) {
        return Optional.ofNullable(database.get(id));
    }
    
    public List<Flight> findAll() {
        return new ArrayList<>(database.values());
    }
    
    public List<Flight> findByStudent(Integer studentId) {
        return database.values().stream()
                .filter(f -> f.getStudent().getId().equals(studentId))
                .toList();
    }
    
    public List<Flight> findByInstructor(Integer instructorId) {
        return database.values().stream()
                .filter(f -> f.getInstructor().getId().equals(instructorId))
                .toList();
    }
    
    public List<Flight> findByStatus(String status) {
        return database.values().stream()
                .filter(f -> f.getStatus().equals(status))
                .toList();
    }
    
    public Flight insert(Flight flight) {
        int id = idSequence.getAndIncrement();
        flight.setId(id);
        database.put(id, flight);
        System.out.println("[MOCK] Voo inserido: " + flight);
        return flight;
    }
    
    public Flight update(Flight flight) {
        if (!database.containsKey(flight.getId())) {
            throw new IllegalArgumentException("Voo não encontrado");
        }
        database.put(flight.getId(), flight);
        System.out.println("[MOCK] Voo atualizado: " + flight);
        return flight;
    }
    
    public void delete(Integer id) {
        Flight removed = database.remove(id);
        if (removed != null) {
            System.out.println("[MOCK] Voo eliminado: " + removed);
        }
    }
    
    public long count() {
        return database.size();
    }
}
