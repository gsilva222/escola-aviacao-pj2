package pt.ipvc.estg.dal.mock;

import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.Course;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * DAO Mock para entidade Student
 */
public class StudentDAOMock {
    
    private static final Map<Integer, Student> database = new HashMap<>();
    private static final AtomicInteger idSequence = new AtomicInteger(1);
    
    static {
        // Dados iniciais removidos - usar MockDataSeeder.seedStudents() na aplicacao
    }
    
    public Optional<Student> findById(Integer id) {
        return Optional.ofNullable(database.get(id));
    }
    
    public Optional<Student> findByEmail(String email) {
        return database.values().stream()
                .filter(s -> s.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
    
    public List<Student> findByCourse(Integer courseId) {
        return database.values().stream()
                .filter(s -> s.getCourse().getId().equals(courseId))
                .collect(Collectors.toList());
    }
    
    public List<Student> findByStatus(String status) {
        return database.values().stream()
                .filter(s -> s.getStatus().equals(status))
                .collect(Collectors.toList());
    }
    
    public List<Student> findAll() {
        return new ArrayList<>(database.values());
    }
    
    public Student insert(Student student) {
        int id = idSequence.getAndIncrement();
        student.setId(id);
        database.put(id, student);
        System.out.println("[MOCK] Estudante inserido: " + student);
        return student;
    }
    
    public Student update(Student student) {
        if (!database.containsKey(student.getId())) {
            throw new IllegalArgumentException("Estudante não encontrado");
        }
        database.put(student.getId(), student);
        System.out.println("[MOCK] Estudante atualizado: " + student);
        return student;
    }
    
    public void delete(Integer id) {
        Student removed = database.remove(id);
        if (removed != null) {
            System.out.println("[MOCK] Estudante eliminado: " + removed);
        }
    }
    
    public long count() {
        return database.size();
    }

    /** Limpa estado em memoria (uso em testes). */
    public static void reset() {
        database.clear();
        idSequence.set(1);
    }
}
