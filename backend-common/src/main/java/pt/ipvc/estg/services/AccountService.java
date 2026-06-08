package pt.ipvc.estg.services;

import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.UserAccount;
import pt.ipvc.estg.exception.ConflictException;
import pt.ipvc.estg.exception.EntityNotFoundException;
import pt.ipvc.estg.repositories.StudentRepository;
import pt.ipvc.estg.repositories.UserAccountRepository;

import java.util.List;

public class AccountService {

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_STUDENT = "STUDENT";
    public static final String DEFAULT_STAFF_PROFILE = "Administrador";

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
        return prepareRegistration(username, passwordHash, role, studentId, null);
    }

    public UserAccount prepareRegistration(String username, String passwordHash, String role,
                                           Integer studentId, String staffProfile) {
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
            account.setStaffProfile(normalizeStaffProfile(staffProfile));
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
            account.setStaffProfile(null);
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

    public UserAccount requireAccount(Integer id) {
        return userAccountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Conta nao encontrada"));
    }

    public List<UserAccount> listAccounts() {
        return userAccountRepository.findAll();
    }

    public UserAccount setActive(Integer id, boolean active) {
        UserAccount account = requireAccount(id);
        account.setActive(active);
        return userAccountRepository.save(account);
    }

    public UserAccount updateStaffProfile(Integer id, String staffProfile) {
        UserAccount account = requireAccount(id);
        if (!ROLE_ADMIN.equals(account.getRole())) {
            throw new IllegalArgumentException("Perfil de staff apenas para contas ADMIN");
        }
        account.setStaffProfile(normalizeStaffProfile(staffProfile));
        return userAccountRepository.save(account);
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

    public static String normalizeStaffProfile(String staffProfile) {
        if (staffProfile == null || staffProfile.trim().isEmpty()) {
            return DEFAULT_STAFF_PROFILE;
        }
        return staffProfile.trim();
    }

    public static String effectiveStaffProfile(UserAccount account) {
        if (account == null || !ROLE_ADMIN.equals(account.getRole())) {
            return null;
        }
        return normalizeStaffProfile(account.getStaffProfile());
    }
}
