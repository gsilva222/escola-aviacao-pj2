package pt.ipvc.estg.dal.mock;

import pt.ipvc.estg.entities.Evaluation;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.Course;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DAO Mock para entidade Evaluation
 */
public class EvaluationDAOMock {
    
    private static final Map<Integer, Evaluation> database = new HashMap<>();
    private static final AtomicInteger idSequence = new AtomicInteger(1);
    
    public Optional<Evaluation> findById(Integer id) {
        return Optional.ofNullable(database.get(id));
    }
    
    public List<Evaluation> findAll() {
        return new ArrayList<>(database.values());
    }
    
    public List<Evaluation> findByStudent(Integer studentId) {
        return database.values().stream()
                .filter(e -> e.getStudent().getId().equals(studentId))
                .toList();
    }
    
    public List<Evaluation> findByCourse(Integer courseId) {
        return database.values().stream()
                .filter(e -> e.getCourse().getId().equals(courseId))
                .toList();
    }
    
    public List<Evaluation> findByStatus(String status) {
        return database.values().stream()
                .filter(e -> e.getStatus().equals(status))
                .toList();
    }
    
    public Evaluation insert(Evaluation evaluation) {
        int id = idSequence.getAndIncrement();
        evaluation.setId(id);
        database.put(id, evaluation);
        System.out.println("[MOCK] Avaliação inserida: " + evaluation);
        return evaluation;
    }
    
    public Evaluation update(Evaluation evaluation) {
        if (!database.containsKey(evaluation.getId())) {
            throw new IllegalArgumentException("Avaliação não encontrada");
        }
        database.put(evaluation.getId(), evaluation);
        System.out.println("[MOCK] Avaliação atualizada: " + evaluation);
        return evaluation;
    }
    
    public void delete(Integer id) {
        Evaluation removed = database.remove(id);
        if (removed != null) {
            System.out.println("[MOCK] Avaliação eliminada: " + removed);
        }
    }
    
    public long count() {
        return database.size();
    }
}
