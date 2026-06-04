package pt.ipvc.estg.web.services;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.UserAccount;
import pt.ipvc.estg.web.dto.AuthLoginRequest;
import pt.ipvc.estg.web.dto.AuthRegisterRequest;
import pt.ipvc.estg.web.dto.AuthResponse;
import pt.ipvc.estg.web.repositories.StudentRepository;
import pt.ipvc.estg.web.repositories.UserAccountRepository;
import pt.ipvc.estg.web.security.JwtService;

@Service
public class AuthService {

    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_STUDENT = "STUDENT";

    private final UserAccountRepository userAccountRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserAccountRepository userAccountRepository,
                       StudentRepository studentRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userAccountRepository = userAccountRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse login(AuthLoginRequest request) {
        UserAccount user = userAccountRepository.findByUsernameIgnoreCase(request.username())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais invalidas"));

        if (!user.isActive()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Conta desativada");
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais invalidas");
        }

        Integer studentId = user.getStudent() != null ? user.getStudent().getId() : null;
        String token = jwtService.generateToken(user.getUsername(), user.getRole(), studentId);
        return new AuthResponse(token, user.getUsername(), user.getRole(), studentId);
    }

    public AuthResponse register(AuthRegisterRequest request) {
        String role = normalizeRole(request.role());

        if (userAccountRepository.findByUsernameIgnoreCase(request.username()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username ja existe");
        }

        UserAccount account = new UserAccount();
        account.setUsername(request.username());
        account.setPasswordHash(passwordEncoder.encode(request.password()));
        account.setRole(role);
        account.setActive(true);

        if (ROLE_ADMIN.equals(role)) {
            if (request.studentId() != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Admin nao pode ter studentId");
            }
            if (userAccountRepository.countByRole(ROLE_ADMIN) > 0) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin ja existe");
            }
        } else if (ROLE_STUDENT.equals(role)) {
            if (request.studentId() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "StudentId e obrigatorio para aluno");
            }
            Student student = studentRepository.findById(request.studentId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudante nao encontrado"));
            if (userAccountRepository.findByStudent_Id(student.getId()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Aluno ja possui conta");
            }
            account.setStudent(student);
        }

        UserAccount created = userAccountRepository.save(account);
        Integer studentId = created.getStudent() != null ? created.getStudent().getId() : null;
        String token = jwtService.generateToken(created.getUsername(), created.getRole(), studentId);
        return new AuthResponse(token, created.getUsername(), created.getRole(), studentId);
    }

    private String normalizeRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role e obrigatorio");
        }
        String normalized = role.trim().toUpperCase();
        if (!ROLE_ADMIN.equals(normalized) && !ROLE_STUDENT.equals(normalized)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role invalido");
        }
        return normalized;
    }
}
