package pt.ipvc.estg.web.controllers.fo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.estg.web.dto.EvaluationResponse;
import pt.ipvc.estg.web.mappers.EvaluationMapper;
import pt.ipvc.estg.web.repositories.EvaluationRepository;
import pt.ipvc.estg.web.services.StudentScopeService;

import java.util.List;

@RestController
@RequestMapping("/fo/evaluations")
public class FoEvaluationController {

    private final StudentScopeService studentScopeService;
    private final EvaluationRepository evaluationRepository;

    public FoEvaluationController(StudentScopeService studentScopeService,
                                  EvaluationRepository evaluationRepository) {
        this.studentScopeService = studentScopeService;
        this.evaluationRepository = evaluationRepository;
    }

    @GetMapping
    public Page<EvaluationResponse> getEvaluations(@RequestParam(value = "page", defaultValue = "0") int page,
                                                   @RequestParam(value = "size", defaultValue = "20") int size) {
        int studentId = studentScopeService.requireCurrentStudent().getId();
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "evaluationDate"));
        List<EvaluationResponse> content = evaluationRepository.findByStudent_Id(studentId, pageable)
                .stream().map(EvaluationMapper::toResponse).toList();
        long total = evaluationRepository.findByStudent_Id(studentId).size();
        return new PageImpl<>(content, pageable, total);
    }
}
