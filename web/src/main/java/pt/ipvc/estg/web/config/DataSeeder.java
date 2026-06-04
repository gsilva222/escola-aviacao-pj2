package pt.ipvc.estg.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.UserAccount;
import pt.ipvc.estg.web.repositories.StudentRepository;
import pt.ipvc.estg.web.repositories.UserAccountRepository;
import pt.ipvc.estg.web.services.AuthService;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserAccountRepository userAccountRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    private final boolean seedEnabled;
    private final boolean seedStudents;
    private final String adminUsername;
    private final String adminPassword;
    private final String studentPassword;

    public DataSeeder(UserAccountRepository userAccountRepository,
                      StudentRepository studentRepository,
                      PasswordEncoder passwordEncoder,
                      @Value("${seed.enabled:true}") boolean seedEnabled,
                      @Value("${seed.students.enabled:true}") boolean seedStudents,
                      @Value("${seed.admin.username:admin}") String adminUsername,
                      @Value("${seed.admin.password:admin123}") String adminPassword,
                      @Value("${seed.student.password:aluno123}") String studentPassword) {
        this.userAccountRepository = userAccountRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.seedEnabled = seedEnabled;
        this.seedStudents = seedStudents;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.studentPassword = studentPassword;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled) {
            return;
        }

        seedAdminIfMissing();
        if (seedStudents) {
            seedStudentAccountsIfMissing();
        }
    }

    private void seedAdminIfMissing() {
        if (userAccountRepository.countByRole(AuthService.ROLE_ADMIN) > 0) {
            return;
        }
        if (userAccountRepository.findByUsernameIgnoreCase(adminUsername).isPresent()) {
            return;
        }

        UserAccount admin = new UserAccount();
        admin.setUsername(adminUsername);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setRole(AuthService.ROLE_ADMIN);
        admin.setActive(true);
        userAccountRepository.save(admin);
    }

    private void seedStudentAccountsIfMissing() {
        List<Student> students = studentRepository.findAll();
        for (Student student : students) {
            if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
                continue;
            }
            if (userAccountRepository.findByStudent_Id(student.getId()).isPresent()) {
                continue;
            }
            if (userAccountRepository.findByUsernameIgnoreCase(student.getEmail()).isPresent()) {
                continue;
            }

            UserAccount account = new UserAccount();
            account.setUsername(student.getEmail());
            account.setPasswordHash(passwordEncoder.encode(studentPassword));
            account.setRole(AuthService.ROLE_STUDENT);
            account.setActive(true);
            account.setStudent(student);
            userAccountRepository.save(account);
        }
    }
}
