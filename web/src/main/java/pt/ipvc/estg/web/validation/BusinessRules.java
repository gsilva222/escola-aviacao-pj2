package pt.ipvc.estg.web.validation;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;

public final class BusinessRules {

    public static final Set<String> AIRCRAFT_STATUSES = Set.of("operational", "maintenance", "grounded");
    public static final Set<String> EVALUATION_STATUSES = Set.of("passed", "failed", "scheduled");
    public static final Set<String> EVALUATION_TYPES = Set.of("theoretical", "practical", "simulator");
    public static final Set<String> FLIGHT_STATUSES = Set.of("scheduled", "completed", "cancelled");
    public static final Set<String> INSTRUCTOR_STATUSES = Set.of("active", "inactive");
    public static final Set<String> MAINTENANCE_PRIORITIES = Set.of("low", "medium", "high");
    public static final Set<String> MAINTENANCE_STATUSES = Set.of("scheduled", "in_progress", "waiting_parts", "completed");
    public static final Set<String> PAYMENT_STATUSES = Set.of("paid", "pending", "overdue");
    public static final Set<String> STUDENT_PAYMENT_STATUSES = Set.of("up_to_date", "pending", "overdue");
    public static final Set<String> STUDENT_STATUSES = Set.of("active", "suspended", "completed");

    private BusinessRules() {
    }

    public static String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return normalized.isEmpty() ? null : normalized;
    }

    public static String requireAllowed(String field, String value, Set<String> allowedValues) {
        String normalized = normalizeOptional(value);
        if (normalized == null) {
            return null;
        }
        if (!allowedValues.contains(normalized)) {
            throw new IllegalArgumentException(field + " invalido. Valores permitidos: " + String.join(", ", allowedValues));
        }
        return normalized;
    }

    public static void validatePortugueseNif(String nif) {
        if (nif == null || nif.trim().isEmpty()) {
            return;
        }
        String value = nif.trim();
        if (!value.matches("\\d{9}")) {
            throw new IllegalArgumentException("NIF deve ter exatamente 9 digitos");
        }
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            sum += Character.digit(value.charAt(i), 10) * (9 - i);
        }
        int checkDigit = 11 - (sum % 11);
        if (checkDigit >= 10) {
            checkDigit = 0;
        }
        if (checkDigit != Character.digit(value.charAt(8), 10)) {
            throw new IllegalArgumentException("NIF invalido");
        }
    }

    public static void validateAdultBirthdate(LocalDate birthdate) {
        if (birthdate == null) {
            return;
        }
        if (birthdate.isAfter(LocalDate.now().minusYears(18))) {
            throw new IllegalArgumentException("Aluno deve ter pelo menos 18 anos");
        }
    }

    public static void validateDateOrder(String endField, LocalDate start, LocalDate end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new IllegalArgumentException(endField + " nao pode ser anterior a data inicial");
        }
    }

    public static void validateScore(Integer score, Integer maxScore) {
        if (score != null && maxScore != null && score > maxScore) {
            throw new IllegalArgumentException("Pontuacao nao pode exceder a pontuacao maxima");
        }
    }

    public static void requirePositive(String field, Number value) {
        if (value != null && value.doubleValue() <= 0) {
            throw new IllegalArgumentException(field + " deve ser superior a zero");
        }
    }

    public static void requirePositiveOrZero(String field, Number value) {
        if (value != null && value.doubleValue() < 0) {
            throw new IllegalArgumentException(field + " deve ser positivo");
        }
    }

    public static void requirePercent(String field, Integer value) {
        if (value != null && (value < 0 || value > 100)) {
            throw new IllegalArgumentException(field + " deve estar entre 0 e 100");
        }
    }
}
