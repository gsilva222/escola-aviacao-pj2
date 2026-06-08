package pt.ipvc.estg.web.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import pt.ipvc.estg.entities.UserAccount;
import pt.ipvc.estg.services.AccountService;
import pt.ipvc.estg.web.dto.*;
import pt.ipvc.estg.web.security.JwtService;
import pt.ipvc.estg.web.security.SecurityUtils;

@Service
public class AuthService {

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final boolean allowPublicAdminRegister;

    public AuthService(AccountService accountService,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       @Value("${auth.allow-public-admin-register:false}") boolean allowPublicAdminRegister) {
        this.accountService = accountService;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.allowPublicAdminRegister = allowPublicAdminRegister;
    }

    public AuthResponse login(AuthLoginRequest request) {
        UserAccount user;
        try {
            user = accountService.requireActiveUser(request.username());
        } catch (IllegalArgumentException ex) {
            if ("Conta desativada".equals(ex.getMessage())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
            }
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ex.getMessage());
        }

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais invalidas");
        }

        return toAuthResponse(user);
    }

    public AuthResponse register(AuthRegisterRequest request) {
        String normalizedRole = accountService.normalizeRole(request.role());

        if (AccountService.ROLE_ADMIN.equals(normalizedRole)) {
            if (!allowPublicAdminRegister) {
                var current = SecurityUtils.currentUser();
                if (current.isEmpty() || !AccountService.ROLE_ADMIN.equals(current.get().role())) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                            "Registo de administrador requer autenticacao de admin");
                }
            }
        }

        try {
            UserAccount account = accountService.prepareRegistration(
                    request.username(),
                    passwordEncoder.encode(request.password()),
                    normalizedRole,
                    request.studentId()
            );
            UserAccount created = accountService.save(account);
            return toAuthResponse(created);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        } catch (pt.ipvc.estg.exception.ConflictException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        } catch (pt.ipvc.estg.exception.EntityNotFoundException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
        }
    }

    public MeResponse me() {
        var user = SecurityUtils.currentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Nao autenticado"));
        return new MeResponse(user.username(), user.role(), user.studentId());
    }

    public void changePassword(ChangePasswordRequest request) {
        var current = SecurityUtils.currentUser()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Nao autenticado"));
        try {
            accountService.changePassword(
                    current.username(),
                    request.currentPassword(),
                    passwordEncoder.encode(request.newPassword()),
                    passwordEncoder::matches
            );
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    private AuthResponse toAuthResponse(UserAccount user) {
        Integer studentId = user.getStudent() != null ? user.getStudent().getId() : null;
        String token = jwtService.generateToken(user.getUsername(), user.getRole(), studentId);
        return new AuthResponse(token, user.getUsername(), user.getRole(), studentId);
    }
}
