package pt.ipvc.estg.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import pt.ipvc.estg.entities.Payment;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByStudent_Id(Integer studentId);
    List<Payment> findByStudent_Id(Integer studentId, Pageable pageable);
    List<Payment> findByStatus(String status);
    List<Payment> findByStatus(String status, Pageable pageable);
    List<Payment> findByStudent_IdAndStatus(Integer studentId, String status);
    long countByStatus(String status);
}
