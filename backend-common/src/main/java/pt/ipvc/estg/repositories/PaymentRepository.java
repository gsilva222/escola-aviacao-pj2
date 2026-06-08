package pt.ipvc.estg.repositories;

import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Optional<Payment> findById(Integer id);
    List<Payment> findAll();
    PageResult<Payment> findAll(PageQuery query);
    List<Payment> findByStudent(Integer studentId);
    PageResult<Payment> findByStudent(Integer studentId, PageQuery query);
    List<Payment> findByStatus(String status);
    PageResult<Payment> findByStatus(String status, PageQuery query);
    List<Payment> findByStudentAndStatus(Integer studentId, String status);
    long countByStatus(String status);
    Payment save(Payment payment);
    void deleteById(Integer id);
    boolean existsById(Integer id);
    long count();
}
