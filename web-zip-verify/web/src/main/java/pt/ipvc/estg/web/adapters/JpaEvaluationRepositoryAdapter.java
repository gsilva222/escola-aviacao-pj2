package pt.ipvc.estg.web.adapters;

import org.springframework.stereotype.Component;
import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Evaluation;
import pt.ipvc.estg.repositories.EvaluationRepository;

import java.util.List;
import java.util.Optional;

@Component
public class JpaEvaluationRepositoryAdapter implements EvaluationRepository {

    private final pt.ipvc.estg.web.repositories.EvaluationRepository jpa;

    public JpaEvaluationRepositoryAdapter(pt.ipvc.estg.web.repositories.EvaluationRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Evaluation> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public List<Evaluation> findAll() {
        return jpa.findAll();
    }

    @Override
    public PageResult<Evaluation> findAll(PageQuery query) {
        var page = jpa.findAll(PageAdapter.toPageable(query));
        return new PageResult<>(page.getContent(), page.getTotalElements(), query.page(), query.size());
    }

    @Override
    public List<Evaluation> findByStudent(Integer studentId) {
        return jpa.findByStudent_Id(studentId);
    }

    @Override
    public PageResult<Evaluation> findByStudent(Integer studentId, PageQuery query) {
        var pageable = PageAdapter.toPageable(query);
        List<Evaluation> content = jpa.findByStudent_Id(studentId, pageable);
        long total = jpa.findByStudent_Id(studentId).size();
        return PageAdapter.fromPage(content, total, query);
    }

    @Override
    public List<Evaluation> findByCourse(Integer courseId) {
        return jpa.findAll().stream()
                .filter(e -> e.getCourse() != null && courseId.equals(e.getCourse().getId()))
                .toList();
    }

    @Override
    public List<Evaluation> findByStatus(String status) {
        return jpa.findByStatus(status);
    }

    @Override
    public PageResult<Evaluation> findByStatus(String status, PageQuery query) {
        var pageable = PageAdapter.toPageable(query);
        List<Evaluation> content = jpa.findByStatus(status, pageable);
        long total = jpa.findByStatus(status).size();
        return PageAdapter.fromPage(content, total, query);
    }

    @Override
    public Evaluation save(Evaluation evaluation) {
        return jpa.save(evaluation);
    }

    @Override
    public void deleteById(Integer id) {
        jpa.deleteById(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return jpa.existsById(id);
    }

    @Override
    public long count() {
        return jpa.count();
    }
}
