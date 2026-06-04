package pt.ipvc.estg.web.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.entities.Evaluation;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.web.dto.EvaluationRequest;
import pt.ipvc.estg.web.dto.EvaluationResponse;
import pt.ipvc.estg.web.mappers.EvaluationMapper;
import pt.ipvc.estg.web.repositories.CourseRepository;
import pt.ipvc.estg.web.repositories.EvaluationRepository;
import pt.ipvc.estg.web.repositories.StudentRepository;
import pt.ipvc.estg.web.validation.BusinessRules;

import java.util.List;

@RestController
@RequestMapping("/bo/evaluations")
public class EvaluationController {

    private final EvaluationRepository evaluationRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EvaluationController(EvaluationRepository evaluationRepository,
                                StudentRepository studentRepository,
                                CourseRepository courseRepository) {
        this.evaluationRepository = evaluationRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public Page<EvaluationResponse> getEvaluations(@RequestParam(required = false) Integer studentId,
                                                   @RequestParam(required = false) String status,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "20") int size,
                                                   @RequestParam(required = false) String sort) {
        PageRequest pageable = PageRequest.of(page, size, buildSort(sort));
        if (studentId != null) {
            List<EvaluationResponse> content = evaluationRepository.findByStudent_Id(studentId, pageable)
                    .stream().map(EvaluationMapper::toResponse).toList();
            long total = evaluationRepository.findByStudent_Id(studentId).size();
            return new PageImpl<>(content, pageable, total);
        }
        if (status != null && !status.trim().isEmpty()) {
            List<EvaluationResponse> content = evaluationRepository.findByStatus(status, pageable)
                    .stream().map(EvaluationMapper::toResponse).toList();
            long total = evaluationRepository.findByStatus(status).size();
            return new PageImpl<>(content, pageable, total);
        }
        return evaluationRepository.findAll(pageable).map(EvaluationMapper::toResponse);
    }

    @GetMapping("/{id}")
    public EvaluationResponse getEvaluation(@PathVariable("id") Integer id) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Avaliacao nao encontrada"));
        return EvaluationMapper.toResponse(evaluation);
    }

    @PostMapping
    public EvaluationResponse createEvaluation(@Valid @RequestBody EvaluationRequest request) {
        BusinessRules.requirePositiveOrZero("Pontuacao", request.score());
        BusinessRules.requirePositive("Pontuacao maxima", request.maxScore());
        BusinessRules.validateScore(request.score(), request.maxScore());
        Student student = studentRepository.findById(request.studentId())
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Estudante nao encontrado"));
        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Curso nao encontrado"));

        Evaluation evaluation = new Evaluation(student, course, request.examName());
        if (request.evaluationDate() != null) evaluation.setEvaluationDate(request.evaluationDate());
        if (request.score() != null) evaluation.setScore(request.score());
        if (request.maxScore() != null) evaluation.setMaxScore(request.maxScore());
        if (request.status() != null) evaluation.setStatus(BusinessRules.requireAllowed("Status", request.status(), BusinessRules.EVALUATION_STATUSES));
        if (request.evaluationType() != null) evaluation.setEvaluationType(BusinessRules.requireAllowed("Tipo de avaliacao", request.evaluationType(), BusinessRules.EVALUATION_TYPES));
        if (request.notes() != null) evaluation.setNotes(request.notes());

        Evaluation created = evaluationRepository.save(evaluation);
        return EvaluationMapper.toResponse(created);
    }

    @PutMapping("/{id}")
    public EvaluationResponse updateEvaluation(@PathVariable("id") Integer id, @RequestBody EvaluationRequest request) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Avaliacao nao encontrada"));
        BusinessRules.requirePositiveOrZero("Pontuacao", request.score());
        BusinessRules.requirePositive("Pontuacao maxima", request.maxScore());
        BusinessRules.validateScore(
                request.score() != null ? request.score() : evaluation.getScore(),
                request.maxScore() != null ? request.maxScore() : evaluation.getMaxScore()
        );

        if (request.examName() != null && !request.examName().trim().isEmpty()) {
            evaluation.setExamName(request.examName());
        }
        if (request.evaluationDate() != null) evaluation.setEvaluationDate(request.evaluationDate());
        if (request.score() != null) evaluation.setScore(request.score());
        if (request.maxScore() != null) evaluation.setMaxScore(request.maxScore());
        if (request.status() != null) evaluation.setStatus(BusinessRules.requireAllowed("Status", request.status(), BusinessRules.EVALUATION_STATUSES));
        if (request.evaluationType() != null) evaluation.setEvaluationType(BusinessRules.requireAllowed("Tipo de avaliacao", request.evaluationType(), BusinessRules.EVALUATION_TYPES));
        if (request.notes() != null) evaluation.setNotes(request.notes());

        if (request.studentId() != null) {
            Student student = studentRepository.findById(request.studentId())
                    .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Estudante nao encontrado"));
            evaluation.setStudent(student);
        }

        if (request.courseId() != null) {
            Course course = courseRepository.findById(request.courseId())
                    .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Curso nao encontrado"));
            evaluation.setCourse(course);
        }

        Evaluation updated = evaluationRepository.save(evaluation);
        return EvaluationMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteEvaluation(@PathVariable("id") Integer id) {
        if (!evaluationRepository.existsById(id)) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Avaliacao nao encontrada");
        }
        evaluationRepository.deleteById(id);
    }

    private Sort buildSort(String sort) {
        if (sort == null || sort.trim().isEmpty()) {
            return Sort.unsorted();
        }
        String[] parts = sort.split(",", 2);
        String property = parts[0].trim();
        if (property.isEmpty()) {
            return Sort.unsorted();
        }
        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length == 2 && "desc".equalsIgnoreCase(parts[1].trim())) {
            direction = Sort.Direction.DESC;
        }
        return Sort.by(direction, property);
    }
}
