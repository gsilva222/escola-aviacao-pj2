package pt.ipvc.estg.repositories.mock;

import pt.ipvc.estg.dal.mock.UserAccountDAOMock;
import pt.ipvc.estg.entities.UserAccount;
import pt.ipvc.estg.repositories.UserAccountRepository;

import java.util.List;
import java.util.Optional;

public class MockUserAccountRepository implements UserAccountRepository {
    private final UserAccountDAOMock delegate = new UserAccountDAOMock();

    @Override
    public Optional<UserAccount> findById(Integer id) {
        return delegate.findById(id);
    }

    @Override
    public Optional<UserAccount> findByUsername(String username) {
        return delegate.findByUsername(username);
    }

    @Override
    public Optional<UserAccount> findByStudent(Integer studentId) {
        return delegate.findByStudent(studentId);
    }

    @Override
    public List<UserAccount> findAll() {
        return delegate.findAll();
    }

    @Override
    public UserAccount save(UserAccount account) {
        return delegate.save(account);
    }
}
