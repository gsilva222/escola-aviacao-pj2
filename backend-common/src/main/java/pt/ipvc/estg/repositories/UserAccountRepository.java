package pt.ipvc.estg.repositories;

import pt.ipvc.estg.entities.UserAccount;

import java.util.List;
import java.util.Optional;

public interface UserAccountRepository {
    Optional<UserAccount> findById(Integer id);
    Optional<UserAccount> findByUsername(String username);
    Optional<UserAccount> findByStudent(Integer studentId);
    List<UserAccount> findAll();
    UserAccount save(UserAccount account);
}
