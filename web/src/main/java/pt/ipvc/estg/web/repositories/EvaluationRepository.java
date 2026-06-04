package pt.ipvc.estg.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import pt.ipvc.estg.entities.Evaluation;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Integer> {
    List<Evaluation> findByStudent_Id(Integer studentId);
    List<Evaluation> findByStudent_Id(Integer studentId, Pageable pageable);
    List<Evaluation> findByStatus(String status);
    List<Evaluation> findByStatus(String status, Pageable pageable);
}
