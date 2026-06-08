package pt.ipvc.estg.repositories.mock;

import pt.ipvc.estg.dal.mock.PaymentDAOMock;
import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.repositories.PaymentRepository;
import pt.ipvc.estg.util.PageUtils;

import java.util.List;
import java.util.Optional;

public class MockPaymentRepository implements PaymentRepository {
    private final PaymentDAOMock delegate = new PaymentDAOMock();

    @Override
    public Optional<Payment> findById(Integer id) {
        return delegate.findById(id);
    }

    @Override
    public List<Payment> findAll() {
        return delegate.findAll();
    }

    @Override
    public PageResult<Payment> findAll(PageQuery query) {
        return PageUtils.paginate(delegate.findAll(), query);
    }

    @Override
    public List<Payment> findByStudent(Integer studentId) {
        return delegate.findByStudent(studentId);
    }

    @Override
    public PageResult<Payment> findByStudent(Integer studentId, PageQuery query) {
        return PageUtils.paginate(delegate.findByStudent(studentId), query);
    }

    @Override
    public List<Payment> findByStatus(String status) {
        return delegate.findByStatus(status);
    }

    @Override
    public PageResult<Payment> findByStatus(String status, PageQuery query) {
        return PageUtils.paginate(delegate.findByStatus(status), query);
    }

    @Override
    public List<Payment> findByStudentAndStatus(Integer studentId, String status) {
        return delegate.findByStudentAndStatus(studentId, status);
    }

    @Override
    public long countByStatus(String status) {
        return delegate.findByStatus(status).size();
    }

    @Override
    public Payment save(Payment payment) {
        return payment.getId() == null ? delegate.insert(payment) : delegate.update(payment);
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
