package pt.ipvc.estg.web.mappers;

import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.entities.Evaluation;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.web.dto.EvaluationResponse;

public final class EvaluationMapper {
    private EvaluationMapper() {}

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
