package pt.ipvc.estg.services;

import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.entities.Evaluation;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.exception.EntityNotFoundException;
import pt.ipvc.estg.repositories.CourseRepository;
import pt.ipvc.estg.repositories.EvaluationRepository;
import pt.ipvc.estg.repositories.StudentRepository;
import pt.ipvc.estg.validation.BusinessRules;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EvaluationService(EvaluationRepository evaluationRepository,
                             StudentRepository studentRepository,
                             CourseRepository courseRepository) {
        this.evaluationRepository = evaluationRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    public EvaluationService() {
        this(pt.ipvc.estg.bootstrap.MockServices.getInstance().evaluationRepository(),
                pt.ipvc.estg.bootstrap.MockServices.getInstance().studentRepository(),
                pt.ipvc.estg.bootstrap.MockServices.getInstance().courseRepository());
    }

    public Optional<Evaluation> getAvaliacao(Integer id) {
        validateId(id);
        return evaluationRepository.findById(id);
    }

    public Evaluation requireAvaliacao(Integer id) {
        return getAvaliacao(id).orElseThrow(() -> new EntityNotFoundException("Avaliacao nao encontrada"));
    }

    public List<Evaluation> getAllAvaliacoes() {
        return evaluationRepository.findAll();
    }

    public PageResult<Evaluation> listAvaliacoes(PageQuery query, Integer studentId, String status) {
        if (studentId != null) {
            return evaluationRepository.findByStudent(studentId, query);
        }
        if (status != null && !status.trim().isEmpty()) {
            return evaluationRepository.findByStatus(status, query);
        }
        return evaluationRepository.findAll(query);
    }

    public List<Evaluation> getAvaliacoesPorEstudante(Integer studentId) {
        validateId(studentId);
        return evaluationRepository.findByStudent(studentId);
    }

    public List<Evaluation> getAvaliacoesPorCurso(Integer courseId) {
        validateId(courseId);
        return evaluationRepository.findByCourse(courseId);
    }

    public List<Evaluation> getAvaliacoesPorStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status deve ser valido");
        }
        return evaluationRepository.findByStatus(status);
    }

    public Evaluation criarAvaliacao(Student student, Course course, String examName) {
        if (student == null) throw new IllegalArgumentException("Estudante e obrigatorio");
        if (course == null) throw new IllegalArgumentException("Curso e obrigatorio");
        validateAlunoCourseAndStatus(student, course);
        if (examName == null || examName.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do exame e obrigatorio");
        }
        Evaluation evaluation = new Evaluation(student, course, examName);
        return evaluationRepository.save(evaluation);
    }

    public Evaluation saveAvaliacao(Evaluation evaluation) {
        validateEvaluationFields(evaluation);
        resolveRelations(evaluation);
        validateAlunoCourseAndStatus(evaluation.getStudent(), evaluation.getCourse());
        return evaluationRepository.save(evaluation);
    }

    public Evaluation updateAvaliacao(Integer id, Evaluation updates) {
        Evaluation evaluation = requireAvaliacao(id);
        validateEvaluationFields(updates, evaluation);
        if (updates.getExamName() != null && !updates.getExamName().trim().isEmpty()) {
            evaluation.setExamName(updates.getExamName());
        }
        if (updates.getEvaluationDate() != null) evaluation.setEvaluationDate(updates.getEvaluationDate());
        if (updates.getScore() != null) evaluation.setScore(updates.getScore());
        if (updates.getMaxScore() != null) evaluation.setMaxScore(updates.getMaxScore());
        if (updates.getStatus() != null) {
            evaluation.setStatus(BusinessRules.requireAllowed("Status", updates.getStatus(), BusinessRules.EVALUATION_STATUSES));
        }
        if (updates.getEvaluationType() != null) {
            evaluation.setEvaluationType(BusinessRules.requireAllowed(
                    "Tipo de avaliacao", updates.getEvaluationType(), BusinessRules.EVALUATION_TYPES));
        }
        if (updates.getNotes() != null) evaluation.setNotes(updates.getNotes());
        if (updates.getStudent() != null && updates.getStudent().getId() != null) {
            Student student = studentRepository.findById(updates.getStudent().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Estudante nao encontrado"));
            evaluation.setStudent(student);
        }
        if (updates.getCourse() != null && updates.getCourse().getId() != null) {
            Course course = courseRepository.findById(updates.getCourse().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Curso nao encontrado"));
            evaluation.setCourse(course);
        }

        // Regra do dominio: avaliacao tem de corresponder ao curso do aluno
        validateAlunoCourseAndStatus(evaluation.getStudent(), evaluation.getCourse());
        return evaluationRepository.save(evaluation);
    }

    public Evaluation registarResultado(Integer id, Integer score, String evaluationType, String notes) {
        if (score == null || score < 0 || score > 100) {
            throw new IllegalArgumentException("Pontuacao deve estar entre 0-100");
        }
        Evaluation evaluation = requireAvaliacao(id);
        evaluation.setScore(score);
        evaluation.setStatus(score >= 50 ? "passed" : "failed");
        if (evaluationType != null) evaluation.setEvaluationType(evaluationType);
        if (notes != null) evaluation.setNotes(notes);
        evaluation.setEvaluationDate(LocalDate.now());
        return evaluationRepository.save(evaluation);
    }

    public void eliminarAvaliacao(Integer id) {
        validateId(id);
        if (!evaluationRepository.existsById(id)) {
            throw new EntityNotFoundException("Avaliacao nao encontrada");
        }
        evaluationRepository.deleteById(id);
    }

    public long contarAvaliacoes() {
        return evaluationRepository.count();
    }

    public double calcularTaxaAprovacao() {
        List<Evaluation> todas = getAllAvaliacoes();
        if (todas.isEmpty()) return 0;
        long aprovadas = todas.stream().filter(e -> "passed".equals(e.getStatus())).count();
        return (aprovadas * 100.0) / todas.size();
    }

    private void validateEvaluationFields(Evaluation evaluation) {
        validateEvaluationFields(evaluation, evaluation);
    }

    private void validateEvaluationFields(Evaluation source, Evaluation current) {
        BusinessRules.requirePositiveOrZero("Pontuacao", source.getScore());
        BusinessRules.requirePositive("Pontuacao maxima", source.getMaxScore());
        BusinessRules.validateScore(
                source.getScore() != null ? source.getScore() : current.getScore(),
                source.getMaxScore() != null ? source.getMaxScore() : current.getMaxScore()
        );
        if (source.getStatus() != null) {
            source.setStatus(BusinessRules.requireAllowed("Status", source.getStatus(), BusinessRules.EVALUATION_STATUSES));
        }
        if (source.getEvaluationType() != null) {
            source.setEvaluationType(BusinessRules.requireAllowed(
                    "Tipo de avaliacao", source.getEvaluationType(), BusinessRules.EVALUATION_TYPES));
        }
    }

    private void resolveRelations(Evaluation evaluation) {
        if (evaluation.getStudent() != null && evaluation.getStudent().getId() != null) {
            Student student = studentRepository.findById(evaluation.getStudent().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Estudante nao encontrado"));
            evaluation.setStudent(student);
        }
        if (evaluation.getCourse() != null && evaluation.getCourse().getId() != null) {
            Course course = courseRepository.findById(evaluation.getCourse().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Curso nao encontrado"));
            evaluation.setCourse(course);
        }
    }

    private void validateAlunoCourseAndStatus(Student student, Course course) {
        if (student == null || course == null) {
            return;
        }
        if (student.getStatus() != null && "suspended".equalsIgnoreCase(student.getStatus())) {
            throw new IllegalArgumentException("Aluno suspenso: nao e permitido criar/atualizar avaliacao");
        }
        if (student.getCourse() == null || student.getCourse().getId() == null || course.getId() == null) {
            return;
        }
        if (!student.getCourse().getId().equals(course.getId())) {
            throw new IllegalArgumentException("Aluno nao esta neste curso");
        }
    }

    private static void validateId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser valido");
        }
    }
}
