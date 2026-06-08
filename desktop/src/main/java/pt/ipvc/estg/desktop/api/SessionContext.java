package pt.ipvc.estg.desktop.api;

import pt.ipvc.estg.desktop.api.dto.AuthResponse;

public final class SessionContext {

    private static String token;
    private static String username;
    private static String role;
    private static Integer studentId;
    private static String displayRole;

    private SessionContext() {
    }

    public static void setSession(AuthResponse auth, String uiRole) {
        token = auth.token();
        username = auth.username();
        role = auth.role();
        studentId = auth.studentId();
        displayRole = uiRole;
    }

    public static void clear() {
        token = null;
        username = null;
        role = null;
        studentId = null;
        displayRole = null;
    }

    public static boolean isAuthenticated() {
        return token != null && !token.isBlank();
    }

    public static String getToken() {
        return token;
    }

    public static String getUsername() {
        return username;
    }

    public static String getRole() {
        return role;
    }

    public static Integer getStudentId() {
        return studentId;
    }

    public static String getDisplayRole() {
        return displayRole != null ? displayRole : "Utilizador";
    }

    public static boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public static boolean isStudent() {
        return "STUDENT".equalsIgnoreCase(role);
    }
}
