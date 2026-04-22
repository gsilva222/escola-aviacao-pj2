package pt.ipvc.estg.dal.mock;

import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.entities.Student;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DAO Mock para entidade Payment
 */
public class PaymentDAOMock {
    
    private static final Map<Integer, Payment> database = new HashMap<>();
    private static final AtomicInteger idSequence = new AtomicInteger(1);
    
    public Optional<Payment> findById(Integer id) {
        return Optional.ofNullable(database.get(id));
    }
    
    public List<Payment> findAll() {
        return new ArrayList<>(database.values());
    }
    
    public List<Payment> findByStudent(Integer studentId) {
        return database.values().stream()
                .filter(p -> p.getStudent().getId().equals(studentId))
                .toList();
    }
    
    public List<Payment> findByStatus(String status) {
        return database.values().stream()
                .filter(p -> p.getStatus().equals(status))
                .toList();
    }
    
    public List<Payment> findByStudentAndStatus(Integer studentId, String status) {
        return database.values().stream()
                .filter(p -> p.getStudent().getId().equals(studentId) && p.getStatus().equals(status))
                .toList();
    }
    
    public Payment insert(Payment payment) {
        int id = idSequence.getAndIncrement();
        payment.setId(id);
        database.put(id, payment);
        System.out.println("[MOCK] Pagamento inserido: " + payment);
        return payment;
    }
    
    public Payment update(Payment payment) {
        if (!database.containsKey(payment.getId())) {
            throw new IllegalArgumentException("Pagamento não encontrado");
        }
        database.put(payment.getId(), payment);
        System.out.println("[MOCK] Pagamento atualizado: " + payment);
        return payment;
    }
    
    public void delete(Integer id) {
        Payment removed = database.remove(id);
        if (removed != null) {
            System.out.println("[MOCK] Pagamento eliminado: " + removed);
        }
    }
    
    public long count() {
        return database.size();
    }
}
