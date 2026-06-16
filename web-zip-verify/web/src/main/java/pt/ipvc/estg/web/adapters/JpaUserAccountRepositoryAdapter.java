package pt.ipvc.estg.web.adapters;

import org.springframework.stereotype.Component;
import pt.ipvc.estg.entities.UserAccount;
import pt.ipvc.estg.repositories.UserAccountRepository;

import java.util.List;
import java.util.Optional;

@Component
public class JpaUserAccountRepositoryAdapter implements UserAccountRepository {

    private final pt.ipvc.estg.web.repositories.UserAccountRepository jpa;

    public JpaUserAccountRepositoryAdapter(pt.ipvc.estg.web.repositories.UserAccountRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<UserAccount> findById(Integer id) {
        return jpa.findById(id);
    }

    @Override
    public Optional<UserAccount> findByUsername(String username) {
        return jpa.findByUsernameIgnoreCase(username);
    }

    @Override
    public Optional<UserAccount> findByStudent(Integer studentId) {
        return jpa.findByStudent_Id(studentId);
    }

    @Override
    public List<UserAccount> findAll() {
        return jpa.findAll();
    }

    @Override
    public UserAccount save(UserAccount account) {
        return jpa.save(account);
    }
}
