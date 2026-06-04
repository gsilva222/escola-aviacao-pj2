package pt.ipvc.estg.web.security;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import pt.ipvc.estg.web.services.AuthService;

public final class SecurityUtils {
    private SecurityUtils() {}

    public static AuthenticatedUser requireAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Nao autenticado");
        }
        return user;
    }

    public static int requireStudentId() {
        AuthenticatedUser user = requireAuthenticated();
        if (!AuthService.ROLE_STUDENT.equals(user.role()) || user.studentId() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso reservado a alunos");
        }
        return user.studentId();
    }

    public static void requireAdmin() {
        AuthenticatedUser user = requireAuthenticated();
        if (!AuthService.ROLE_ADMIN.equals(user.role())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso reservado a administradores");
        }
    }
}
