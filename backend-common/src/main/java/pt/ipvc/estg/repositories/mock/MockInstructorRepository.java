package pt.ipvc.estg.repositories.mock;

import pt.ipvc.estg.dal.mock.InstructorDAOMock;
import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Instructor;
import pt.ipvc.estg.repositories.InstructorRepository;
import pt.ipvc.estg.util.PageUtils;

import java.util.List;
import java.util.Optional;

public class MockInstructorRepository implements InstructorRepository {
    private final InstructorDAOMock delegate = new InstructorDAOMock();

    @Override
    public Optional<Instructor> findById(Integer id) {
        return delegate.findById(id);
    }

    @Override
    public Optional<Instructor> findByName(String name) {
        return delegate.findByName(name);
    }

    @Override
    public List<Instructor> findAll() {
        return delegate.findAll();
    }

    @Override
    public PageResult<Instructor> findAll(PageQuery query) {
        return PageUtils.paginate(delegate.findAll(), query);
    }

    @Override
    public List<Instructor> findByStatus(String status) {
        return delegate.findByStatus(status);
    }

    @Override
    public PageResult<Instructor> findByStatus(String status, PageQuery query) {
        return PageUtils.paginate(delegate.findByStatus(status), query);
    }

    @Override
    public Instructor save(Instructor instructor) {
        return instructor.getId() == null ? delegate.insert(instructor) : delegate.update(instructor);
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
