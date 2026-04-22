package pt.ipvc.estg.dal.mock;

import pt.ipvc.estg.entities.Maintenance;
import pt.ipvc.estg.entities.Aircraft;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DAO Mock para entidade Maintenance
 */
public class MaintenanceDAOMock {
    
    private static final Map<Integer, Maintenance> database = new HashMap<>();
    private static final AtomicInteger idSequence = new AtomicInteger(1);
    
    public Optional<Maintenance> findById(Integer id) {
        return Optional.ofNullable(database.get(id));
    }
    
    public List<Maintenance> findAll() {
        return new ArrayList<>(database.values());
    }
    
    public List<Maintenance> findByAircraft(Integer aircraftId) {
        return database.values().stream()
                .filter(m -> m.getAircraft().getId().equals(aircraftId))
                .toList();
    }
    
    public List<Maintenance> findByStatus(String status) {
        return database.values().stream()
                .filter(m -> m.getStatus().equals(status))
                .toList();
    }
    
    public List<Maintenance> findByPriority(String priority) {
        return database.values().stream()
                .filter(m -> m.getPriority().equals(priority))
                .toList();
    }
    
    public Maintenance insert(Maintenance maintenance) {
        int id = idSequence.getAndIncrement();
        maintenance.setId(id);
        database.put(id, maintenance);
        System.out.println("[MOCK] Manutenção inserida: " + maintenance);
        return maintenance;
    }
    
    public Maintenance update(Maintenance maintenance) {
        if (!database.containsKey(maintenance.getId())) {
            throw new IllegalArgumentException("Manutenção não encontrada");
        }
        database.put(maintenance.getId(), maintenance);
        System.out.println("[MOCK] Manutenção atualizada: " + maintenance);
        return maintenance;
    }
    
    public void delete(Integer id) {
        Maintenance removed = database.remove(id);
        if (removed != null) {
            System.out.println("[MOCK] Manutenção eliminada: " + removed);
        }
    }
    
    public long count() {
        return database.size();
    }
}
