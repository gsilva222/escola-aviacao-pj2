package pt.ipvc.estg.dal.mock;

import pt.ipvc.estg.entities.Instructor;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DAO Mock para entidade Instructor
 */
public class InstructorDAOMock {
    
    private static final Map<Integer, Instructor> database = new HashMap<>();
    private static final AtomicInteger idSequence = new AtomicInteger(1);
    
    static {
        inserirInstructorInicial("Capt. António Ferreira", "CPL/IR/FI", "PPL, IR");
        inserirInstructorInicial("Capt. Margarida Lopes", "CPL/IR/FI/IRI", "PPL, CPL");
        inserirInstructorInicial("Capt. José Pereira", "ATPL/FI/TRI", "CPL, ATPL");
    }
    
    private static void inserirInstructorInicial(String name, String license, String specialization) {
        Instructor instructor = new Instructor(name, license, specialization);
        int id = idSequence.getAndIncrement();
        instructor.setId(id);
        database.put(id, instructor);
    }
    
    public Optional<Instructor> findById(Integer id) {
        return Optional.ofNullable(database.get(id));
    }
    
    public Optional<Instructor> findByName(String name) {
        return database.values().stream()
                .filter(i -> i.getName().equalsIgnoreCase(name))
                .findFirst();
    }
    
    public List<Instructor> findAll() {
        return new ArrayList<>(database.values());
    }
    
    public List<Instructor> findByStatus(String status) {
        return database.values().stream()
                .filter(i -> i.getStatus().equals(status))
                .toList();
    }
    
    public Instructor insert(Instructor instructor) {
        int id = idSequence.getAndIncrement();
        instructor.setId(id);
        database.put(id, instructor);
        System.out.println("[MOCK] Instrutor inserido: " + instructor);
        return instructor;
    }
    
    public Instructor update(Instructor instructor) {
        if (!database.containsKey(instructor.getId())) {
            throw new IllegalArgumentException("Instrutor não encontrado");
        }
        database.put(instructor.getId(), instructor);
        System.out.println("[MOCK] Instrutor atualizado: " + instructor);
        return instructor;
    }
    
    public void delete(Integer id) {
        Instructor removed = database.remove(id);
        if (removed != null) {
            System.out.println("[MOCK] Instrutor eliminado: " + removed);
        }
    }
    
    public long count() {
        return database.size();
    }
}
