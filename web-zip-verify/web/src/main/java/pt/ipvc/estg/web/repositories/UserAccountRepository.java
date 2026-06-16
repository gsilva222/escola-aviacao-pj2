package pt.ipvc.estg.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.ipvc.estg.entities.UserAccount;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Integer> {
    Optional<UserAccount> findByUsernameIgnoreCase(String username);
    Optional<UserAccount> findByStudent_Id(Integer studentId);
    long countByRole(String role);
}
