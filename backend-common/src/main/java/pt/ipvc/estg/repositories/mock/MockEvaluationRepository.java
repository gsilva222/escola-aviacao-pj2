package pt.ipvc.estg.repositories.mock;

import pt.ipvc.estg.dal.mock.EvaluationDAOMock;
import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Evaluation;
import pt.ipvc.estg.repositories.EvaluationRepository;
import pt.ipvc.estg.util.PageUtils;

import java.util.List;
import java.util.Optional;

public class MockEvaluationRepository implements EvaluationRepository {
    private final EvaluationDAOMock delegate = new EvaluationDAOMock();

    @Override
    public Optional<Evaluation> findById(Integer id) {
        return delegate.findById(id);
    }

    @Override
    public List<Evaluation> findAll() {
        return delegate.findAll();
    }

    @Override
    public PageResult<Evaluation> findAll(PageQuery query) {
        return PageUtils.paginate(delegate.findAll(), query);
    }

    @Override
    public List<Evaluation> findByStudent(Integer studentId) {
        return delegate.findByStudent(studentId);
    }

    @Override
    public PageResult<Evaluation> findByStudent(Integer studentId, PageQuery query) {
        return PageUtils.paginate(delegate.findByStudent(studentId), query);
    }

    @Override
    public List<Evaluation> findByCourse(Integer courseId) {
        return delegate.findByCourse(courseId);
    }

    @Override
    public List<Evaluation> findByStatus(String status) {
        return delegate.findByStatus(status);
    }

    @Override
    public PageResult<Evaluation> findByStatus(String status, PageQuery query) {
        return PageUtils.paginate(delegate.findByStatus(status), query);
    }

    @Override
    public Evaluation save(Evaluation evaluation) {
        return evaluation.getId() == null ? delegate.insert(evaluation) : delegate.update(evaluation);
    }

    @Override
    public void deleteById(Integer id) {
        delegate.delete(id);
    }

    @Override
    public boolean existsById(Integer id) {
        return delegate.findById(id).isPresent();
    }

    @Override
    public long count() {
        return delegate.count();
    }
}
