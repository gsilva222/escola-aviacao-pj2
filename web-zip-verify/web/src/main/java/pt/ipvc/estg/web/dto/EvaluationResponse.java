package pt.ipvc.estg.web.dto;

import java.time.LocalDate;

public record EvaluationResponse(
        Integer id,
        Integer studentId,
        String studentName,
        Integer courseId,
        String courseName,
        String examName,
        LocalDate evaluationDate,
        Integer score,
        Integer maxScore,
        String status,
        String evaluationType,
        String notes
) {
}
