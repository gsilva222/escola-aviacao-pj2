package pt.ipvc.estg.dal.mock;

import pt.ipvc.estg.entities.Aircraft;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DAO Mock para entidade Aircraft
 */
public class AircraftDAOMock {
    
    private static final Map<Integer, Aircraft> database = new HashMap<>();
    private static final AtomicInteger idSequence = new AtomicInteger(1);
    
    static {
        inserirAviãoInicial("CS-AER", "Cessna 172 Skyhawk", "Single Engine", 2018, "operational");
        inserirAviãoInicial("CS-FLY", "Piper PA-28 Cherokee", "Single Engine", 2016, "operational");
        inserirAviãoInicial("CS-NAV", "Cessna 172 Skyhawk", "Single Engine", 2020, "maintenance");
        inserirAviãoInicial("CS-IFR", "Diamond DA42 Twin Star", "Multi Engine", 2019, "operational");
        inserirAviãoInicial("CS-SIM", "Beechcraft Bonanza G36", "Single Engine", 2017, "grounded");
    }
    
    private static void inserirAviãoInicial(String registration, String model, String type, 
                                           Integer year, String status) {
        Aircraft aircraft = new Aircraft(registration, model, type);
        aircraft.setManufYear(year);
        aircraft.setStatus(status);
        aircraft.setLastMaintenance(LocalDate.now().minusMonths(1));
        aircraft.setNextMaintenance(LocalDate.now().plusMonths(2));
        int id = idSequence.getAndIncrement();
        aircraft.setId(id);
        database.put(id, aircraft);
    }
    
    public Optional<Aircraft> findById(Integer id) {
        return Optional.ofNullable(database.get(id));
    }
    
    public Optional<Aircraft> findByRegistration(String registration) {
        return database.values().stream()
                .filter(a -> a.getRegistration().equalsIgnoreCase(registration))
                .findFirst();
    }
    
    public List<Aircraft> findAll() {
        return new ArrayList<>(database.values());
    }
    
    public List<Aircraft> findByStatus(String status) {
        return database.values().stream()
                .filter(a -> a.getStatus().equals(status))
                .toList();
    }
    
    public Aircraft insert(Aircraft aircraft) {
        int id = idSequence.getAndIncrement();
        aircraft.setId(id);
        database.put(id, aircraft);
        System.out.println("[MOCK] Avião inserido: " + aircraft);
        return aircraft;
    }
    
    public Aircraft update(Aircraft aircraft) {
        if (!database.containsKey(aircraft.getId())) {
            throw new IllegalArgumentException("Avião não encontrado");
        }
        database.put(aircraft.getId(), aircraft);
        System.out.println("[MOCK] Avião atualizado: " + aircraft);
        return aircraft;
    }
    
    public void delete(Integer id) {
        Aircraft removed = database.remove(id);
        if (removed != null) {
            System.out.println("[MOCK] Avião eliminado: " + removed);
        }
    }
    
    public long count() {
        return database.size();
    }
}
