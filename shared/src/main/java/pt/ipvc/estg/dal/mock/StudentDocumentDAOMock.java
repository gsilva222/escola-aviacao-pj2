package pt.ipvc.estg.dal.mock;

import pt.ipvc.estg.entities.StudentDocument;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DAO Mock para entidade StudentDocument.
 */
public class StudentDocumentDAOMock {

    private static final Map<Integer, StudentDocument> database = new HashMap<>();
    private static final AtomicInteger idSequence = new AtomicInteger(1);

    public Optional<StudentDocument> findById(Integer id) {
        return Optional.ofNullable(database.get(id));
    }

    public Optional<StudentDocument> findByIdAndStudent(Integer id, Integer studentId) {
        return findById(id).filter(doc -> doc.getStudent() != null && studentId.equals(doc.getStudent().getId()));
    }

    public List<StudentDocument> findByStudentOrderByUploadedAtDesc(Integer studentId) {
        return database.values().stream()
                .filter(doc -> doc.getStudent() != null && studentId.equals(doc.getStudent().getId()))
                .sorted(Comparator.comparing(StudentDocument::getUploadedAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    public List<StudentDocument> findAll() {
        return new ArrayList<>(database.values());
    }

    public StudentDocument insert(StudentDocument document) {
        int id = idSequence.getAndIncrement();
        document.setId(id);
        database.put(id, document);
        System.out.println("[MOCK] Documento inserido: " + document.getFileName());
        return document;
    }

    public StudentDocument update(StudentDocument document) {
        if (!database.containsKey(document.getId())) {
            throw new IllegalArgumentException("Documento nao encontrado");
        }
        database.put(document.getId(), document);
        return document;
    }

    public StudentDocument save(StudentDocument document) {
        return document.getId() == null ? insert(document) : update(document);
    }

    public void delete(Integer id) {
        database.remove(id);
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
