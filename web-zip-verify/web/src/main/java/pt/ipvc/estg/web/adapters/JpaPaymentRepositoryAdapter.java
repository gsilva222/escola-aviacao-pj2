package pt.ipvc.estg.web.adapters;

import org.springframework.stereotype.Component;
import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Payment;
import pt.ipvc.estg.repositories.PaymentRepository;

import java.util.List;
import java.util.Optional;

@Component
public class JpaPaymentRepositoryAdapter implements PaymentRepository {

    private final pt.ipvc.estg.web.repositories.PaymentRepository jpa;

    public JpaPaymentRepositoryAdapter(pt.ipvc.estg.web.repositories.PaymentRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<Payment> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public List<Payment> findAll() {
        return jpa.findAll();
    }

    @Override
    public PageResult<Payment> findAll(PageQuery query) {
        var page = jpa.findAll(PageAdapter.toPageable(query));
        return new PageResult<>(page.getContent(), page.getTotalElements(), query.page(), query.size());
    }

    @Override
    public List<Payment> findByStudent(Integer studentId) {
        return jpa.findByStudent_Id(studentId);
    }

    @Override
    public PageResult<Payment> findByStudent(Integer studentId, PageQuery query) {
        var pageable = PageAdapter.toPageable(query);
        List<Payment> content = jpa.findByStudent_Id(studentId, pageable);
        long total = jpa.findByStudent_Id(studentId).size();
        return PageAdapter.fromPage(content, total, query);
    }

    @Override
    public List<Payment> findByStatus(String status) {
        return jpa.findByStatus(status);
    }

    @Override
    public PageResult<Payment> findByStatus(String status, PageQuery query) {
        var pageable = PageAdapter.toPageable(query);
        List<Payment> content = jpa.findByStatus(status, pageable);
        long total = jpa.findByStatus(status).size();
        return PageAdapter.fromPage(content, total, query);
    }

    @Override
    public List<Payment> findByStudentAndStatus(Integer studentId, String status) {
        return jpa.findByStudent_IdAndStatus(studentId, status);
    }

    @Override
    public long countByStatus(String status) {
        return jpa.countByStatus(status);
    }

    @Override
    public Payment save(Payment payment) {
        return jpa.save(payment);
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
