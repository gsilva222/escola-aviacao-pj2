package pt.ipvc.estg.repositories;

import pt.ipvc.estg.entities.UserAccount;

import java.util.Optional;

public interface UserAccountRepository {
    Optional<UserAccount> findByUsername(String username);
    Optional<UserAccount> findByStudent(Integer studentId);
    UserAccount save(UserAccount account);
}
