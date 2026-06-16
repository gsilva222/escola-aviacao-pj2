package pt.ipvc.estg.web.mappers;

import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.entities.Evaluation;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.validation.BusinessRules;
import pt.ipvc.estg.web.dto.EvaluationRequest;
import pt.ipvc.estg.web.dto.EvaluationResponse;

public final class EvaluationMapper {
    private EvaluationMapper() {}

    public static Evaluation toEntity(EvaluationRequest request) {
        Student student = new Student();
        student.setId(request.studentId());
        Course course = new Course();
        course.setId(request.courseId());
        Evaluation evaluation = new Evaluation(student, course, request.examName());
        evaluation.setEvaluationDate(request.evaluationDate());
        evaluation.setScore(request.score());
        evaluation.setMaxScore(request.maxScore());
        if (request.status() != null) {
            evaluation.setStatus(BusinessRules.requireAllowed("Status", request.status(), BusinessRules.EVALUATION_STATUSES));
        }
        if (request.evaluationType() != null) {
            evaluation.setEvaluationType(BusinessRules.requireAllowed(
                    "Tipo de avaliacao", request.evaluationType(), BusinessRules.EVALUATION_TYPES));
        }
        evaluation.setNotes(request.notes());
        return evaluation;
    }

    public static EvaluationResponse toResponse(Evaluation evaluation) {
        if (evaluation == null) {
            return null;
        }

        Student student = evaluation.getStudent();
        Course course = evaluation.getCourse();

        return new EvaluationResponse(
                evaluation.getId(),
                student != null ? student.getId() : null,
                student != null ? student.getName() : null,
                course != null ? course.getId() : null,
                course != null ? course.getName() : null,
                evaluation.getExamName(),
                evaluation.getEvaluationDate(),
                evaluation.getScore(),
                evaluation.getMaxScore(),
                evaluation.getStatus(),
                evaluation.getEvaluationType(),
                evaluation.getNotes()
        );
    }
}
