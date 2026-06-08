package pt.ipvc.estg.validation;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BusinessRulesTest {

    @Test
    void validatePortugueseNif_acceptsValidNif() {
        assertDoesNotThrow(() -> BusinessRules.validatePortugueseNif("123456789"));
    }

    @Test
    void validatePortugueseNif_rejectsInvalidCheckDigit() {
        assertThrows(IllegalArgumentException.class,
                () -> BusinessRules.validatePortugueseNif("123456788"));
    }

    @Test
    void validateAdultBirthdate_rejectsMinor() {
        assertThrows(IllegalArgumentException.class,
                () -> BusinessRules.validateAdultBirthdate(LocalDate.now().minusYears(17)));
    }

    @Test
    void requireAllowed_normalizesStatus() {
        String status = BusinessRules.requireAllowed("Status", "ACTIVE", BusinessRules.STUDENT_STATUSES);
        assertEquals("active", status);
    }
}
