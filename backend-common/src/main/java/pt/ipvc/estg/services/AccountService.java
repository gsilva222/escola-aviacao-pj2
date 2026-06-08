package pt.ipvc.estg.services;

import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.UserAccount;
import pt.ipvc.estg.exception.ConflictException;
import pt.ipvc.estg.exception.EntityNotFoundException;
import pt.ipvc.estg.repositories.StudentRepository;
import pt.ipvc.estg.repositories.UserAccountRepository;

public class AccountService {

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_STUDENT = "STUDENT";

    private final UserAccountRepository userAccountRepository;
    private final StudentRepository studentRepository;

    public AccountService(UserAccountRepository userAccountRepository, StudentRepository studentRepository) {
        this.userAccountRepository = userAccountRepository;
        this.studentRepository = studentRepository;
    }

    public String normalizeRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role e obrigatorio");
        }
        String normalized = role.trim().toUpperCase();
        if (!ROLE_ADMIN.equals(normalized) && !ROLE_STUDENT.equals(normalized)) {
            throw new IllegalArgumentException("Role invalido");
        }
        return normalized;
    }

    public void validateUsernameAvailable(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username e obrigatorio");
        }
        userAccountRepository.findByUsername(username).ifPresent(existing -> {
            throw new ConflictException("Username ja existe");
        });
    }

    public UserAccount prepareRegistration(String username, String passwordHash, String role, Integer studentId) {
        String normalizedRole = normalizeRole(role);
        validateUsernameAvailable(username);

        UserAccount account = new UserAccount();
        account.setUsername(username);
        account.setPasswordHash(passwordHash);
        account.setRole(normalizedRole);
        account.setActive(true);

        if (ROLE_ADMIN.equals(normalizedRole)) {
            if (studentId != null) {
                throw new IllegalArgumentException("Admin nao pode ter studentId");
            }
        } else if (ROLE_STUDENT.equals(normalizedRole)) {
            if (studentId == null) {
                throw new IllegalArgumentException("StudentId e obrigatorio para aluno");
            }
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new EntityNotFoundException("Estudante nao encontrado"));
            userAccountRepository.findByStudent(student.getId()).ifPresent(existing -> {
                throw new ConflictException("Aluno ja possui conta");
            });
            account.setStudent(student);
        }
        return account;
    }

    public UserAccount requireActiveUser(String username) {
        UserAccount user = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Credenciais invalidas"));
        if (!user.isActive()) {
            throw new IllegalArgumentException("Conta desativada");
        }
        return user;
    }

    public UserAccount save(UserAccount account) {
        return userAccountRepository.save(account);
    }

    public void changePassword(String username, String currentPlainPassword, String newPasswordHash,
                               java.util.function.BiPredicate<String, String> passwordMatches) {
        UserAccount user = requireActiveUser(username);
        if (!passwordMatches.test(currentPlainPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Password atual incorreta");
        }
        user.setPasswordHash(newPasswordHash);
        userAccountRepository.save(user);
    }
}
