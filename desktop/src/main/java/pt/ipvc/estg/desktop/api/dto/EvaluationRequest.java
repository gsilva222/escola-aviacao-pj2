package pt.ipvc.estg.desktop.api.dto;

import java.time.LocalDate;

public record EvaluationRequest(
        Integer studentId,
        Integer courseId,
        String examName,
        LocalDate evaluationDate,
        Integer score,
        Integer maxScore,
        String status,
        String evaluationType,
        String notes
) {
}
