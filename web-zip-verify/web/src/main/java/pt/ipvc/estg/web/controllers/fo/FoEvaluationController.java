package pt.ipvc.estg.web.controllers.fo;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.services.EvaluationService;
import pt.ipvc.estg.web.dto.EvaluationResponse;
import pt.ipvc.estg.web.mappers.DomainDtoMapper;
import pt.ipvc.estg.web.mappers.EvaluationMapper;
import pt.ipvc.estg.web.services.StudentScopeService;

@RestController
@RequestMapping("/fo/evaluations")
public class FoEvaluationController {

    private final StudentScopeService studentScopeService;
    private final EvaluationService evaluationService;

    public FoEvaluationController(StudentScopeService studentScopeService,
                                  EvaluationService evaluationService) {
        this.studentScopeService = studentScopeService;
        this.evaluationService = evaluationService;
    }

    @GetMapping
    public Page<EvaluationResponse> getEvaluations(@RequestParam(value = "page", defaultValue = "0") int page,
                                                   @RequestParam(value = "size", defaultValue = "20") int size) {
        int studentId = studentScopeService.requireCurrentStudent().getId();
        PageQuery query = PageQuery.of(page, size, "evaluationDate", true);
        var result = evaluationService.listAvaliacoes(query, studentId, null);
        return DomainDtoMapper.toSpringPage(result, EvaluationMapper::toResponse, "evaluationDate,desc");
    }
}
