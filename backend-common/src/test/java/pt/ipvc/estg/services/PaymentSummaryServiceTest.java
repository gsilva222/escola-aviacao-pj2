package pt.ipvc.estg.services;

import org.junit.jupiter.api.Test;
import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.repositories.PaymentRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentSummaryServiceTest {

    @Test
    void summarizeAll_aggregatesByStatus() {
        Student student = new Student("Test", "test@test.com", null);
        student.setId(1);

        Payment pending = new Payment(student, "Propina", 100.0, null);
        pending.setStatus("pending");
        Payment paid = new Payment(student, "Taxa", 50.0, null);
        paid.setStatus("paid");
        Payment overdue = new Payment(student, "Multa", 25.0, null);
        overdue.setStatus("overdue");

        PaymentRepository repo = new PaymentRepository() {
            @Override public Optional<Payment> findById(Integer id) { return Optional.empty(); }
            @Override public List<Payment> findAll() { return List.of(pending, paid, overdue); }
            @Override public pt.ipvc.estg.domain.PageResult<Payment> findAll(pt.ipvc.estg.domain.PageQuery query) { return null; }
            @Override public List<Payment> findByStudent(Integer studentId) { return List.of(); }
            @Override public pt.ipvc.estg.domain.PageResult<Payment> findByStudent(Integer studentId, pt.ipvc.estg.domain.PageQuery query) { return null; }
            @Override public List<Payment> findByStatus(String status) { return List.of(); }
            @Override public pt.ipvc.estg.domain.PageResult<Payment> findByStatus(String status, pt.ipvc.estg.domain.PageQuery query) { return null; }
            @Override public List<Payment> findByStudentAndStatus(Integer studentId, String status) { return List.of(); }
            @Override public long countByStatus(String status) { return 0; }
            @Override public Payment save(Payment payment) { return payment; }
            @Override public void deleteById(Integer id) {}
            @Override public boolean existsById(Integer id) { return false; }
            @Override public long count() { return 0; }
        };

        var summary = new PaymentSummaryService(repo).summarizeAll();

        assertEquals(100.0, summary.totalPending());
        assertEquals(25.0, summary.totalOverdue());
        assertEquals(50.0, summary.totalPaid());
        assertEquals(1, summary.pendingCount());
        assertEquals(1, summary.overdueCount());
        assertEquals(1, summary.paidCount());
    }
}
