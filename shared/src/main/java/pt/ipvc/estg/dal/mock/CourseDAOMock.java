package pt.ipvc.estg.dal.mock;

import pt.ipvc.estg.entities.Course;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * DAO Mock para entidade Course
 */
public class CourseDAOMock {
    
    private static final Map<Integer, Course> database = new HashMap<>();
    private static final AtomicInteger idSequence = new AtomicInteger(1);
    
    static {
        // Dados iniciais
        inserirCursoInicial("PPL – Piloto Privado", "12 meses", 45, 100, 8500.0);
        inserirCursoInicial("CPL – Piloto Comercial", "18 meses", 200, 250, 28000.0);
        inserirCursoInicial("IR – Habilitação por Instrumentos", "6 meses", 50, 150, 12000.0);
        inserirCursoInicial("ATPL – Transporte Aéreo", "36 meses", 500, 650, 75000.0);
    }
    
    private static void inserirCursoInicial(String name, String duration, Integer flightHours, 
                                           Integer theoreticalHours, Double price) {
        Course course = new Course(name, duration, flightHours, theoreticalHours, price);
        int id = idSequence.getAndIncrement();
        course.setId(id);
        database.put(id, course);
    }
    
    public Optional<Course> findById(Integer id) {
        return Optional.ofNullable(database.get(id));
    }
    
    public Optional<Course> findByName(String name) {
        return database.values().stream()
                .filter(c -> c.getName().equalsIgnoreCase(name))
                .findFirst();
    }
    
    public List<Course> findAll() {
        return new ArrayList<>(database.values());
    }
    
    public Course insert(Course course) {
        int id = idSequence.getAndIncrement();
        course.setId(id);
        database.put(id, course);
        System.out.println("[MOCK] Curso inserido: " + course);
        return course;
    }
    
    public Course update(Course course) {
        if (!database.containsKey(course.getId())) {
            throw new IllegalArgumentException("Curso não encontrado");
        }
        database.put(course.getId(), course);
        System.out.println("[MOCK] Curso atualizado: " + course);
        return course;
    }
    
    public void delete(Integer id) {
        Course removed = database.remove(id);
        if (removed != null) {
            System.out.println("[MOCK] Curso eliminado: " + removed);
        }
    }
    
    public long count() {
        return database.size();
    }
}
