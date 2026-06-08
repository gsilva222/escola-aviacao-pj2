package pt.ipvc.estg.desktop.security;

import java.util.LinkedHashSet;
import java.util.Set;

public final class RoleMenuPolicy {

    private RoleMenuPolicy() {
    }

    public static Set<String> allowedPages(String uiRole) {
        Set<String> pages = new LinkedHashSet<>();
        pages.add("dashboard");

        if (uiRole == null || uiRole.isBlank()) {
            return allPages();
        }

        switch (uiRole) {
            case "Administrador" -> pages.addAll(allPages());
            case "Secretaria" -> {
                pages.add("students");
                pages.add("courses");
                pages.add("payments");
                pages.add("evaluations");
            }
            case "Gestor Operacional" -> {
                pages.add("flights");
                pages.add("aircraft");
                pages.add("instructors");
                pages.add("maintenance");
                pages.add("evaluations");
                pages.add("reports");
            }
            case "Instrutor" -> {
                pages.add("students");
                pages.add("flights");
                pages.add("evaluations");
            }
            case "Técnico de Manutenção" -> {
                pages.add("aircraft");
                pages.add("maintenance");
            }
            default -> pages.addAll(allPages());
        }
        return pages;
    }

    private static Set<String> allPages() {
        return Set.of(
                "dashboard", "students", "courses", "flights", "aircraft",
                "instructors", "maintenance", "evaluations", "payments", "reports"
        );
    }
}
