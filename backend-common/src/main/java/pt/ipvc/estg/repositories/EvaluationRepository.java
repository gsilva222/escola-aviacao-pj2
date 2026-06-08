package pt.ipvc.estg.repositories;

import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Evaluation;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository {
    Optional<Evaluation> findById(Integer id);
    List<Evaluation> findAll();
    PageResult<Evaluation> findAll(PageQuery query);
    List<Evaluation> findByStudent(Integer studentId);
    PageResult<Evaluation> findByStudent(Integer studentId, PageQuery query);
    List<Evaluation> findByCourse(Integer courseId);
    List<Evaluation> findByStatus(String status);
    PageResult<Evaluation> findByStatus(String status, PageQuery query);
    Evaluation save(Evaluation evaluation);
    void deleteById(Integer id);
    boolean existsById(Integer id);
    long count();
}
