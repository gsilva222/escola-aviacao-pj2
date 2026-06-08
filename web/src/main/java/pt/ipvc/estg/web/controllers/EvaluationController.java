package pt.ipvc.estg.web.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.estg.services.EvaluationService;
import pt.ipvc.estg.util.PageQueryParser;
import pt.ipvc.estg.web.dto.EvaluationRequest;
import pt.ipvc.estg.web.dto.EvaluationResponse;
import pt.ipvc.estg.web.mappers.DomainDtoMapper;
import pt.ipvc.estg.web.mappers.EvaluationMapper;

@RestController
@RequestMapping("/bo/evaluations")
public class EvaluationController {

    private final EvaluationService evaluationService;

    public EvaluationController(EvaluationService evaluationService) {
        this.evaluationService = evaluationService;
    }

    @GetMapping
    public Page<EvaluationResponse> getEvaluations(@RequestParam(value = "studentId", required = false) Integer studentId,
                                                   @RequestParam(value = "status", required = false) String status,
                                                   @RequestParam(value = "page", defaultValue = "0") int page,
                                                   @RequestParam(value = "size", defaultValue = "20") int size,
                                                   @RequestParam(value = "sort", required = false) String sort) {
        var result = evaluationService.listAvaliacoes(PageQueryParser.parse(page, size, sort), studentId, status);
        return DomainDtoMapper.toSpringPage(result, EvaluationMapper::toResponse, sort);
    }

    @GetMapping("/{id}")
    public EvaluationResponse getEvaluation(@PathVariable("id") Integer id) {
        return EvaluationMapper.toResponse(evaluationService.requireAvaliacao(id));
    }

    @PostMapping
    public EvaluationResponse createEvaluation(@Valid @RequestBody EvaluationRequest request) {
        return EvaluationMapper.toResponse(evaluationService.saveAvaliacao(EvaluationMapper.toEntity(request)));
    }

    @PutMapping("/{id}")
    public EvaluationResponse updateEvaluation(@PathVariable("id") Integer id, @RequestBody EvaluationRequest request) {
        return EvaluationMapper.toResponse(evaluationService.updateAvaliacao(id, EvaluationMapper.toEntity(request)));
    }

    @DeleteMapping("/{id}")
    public void deleteEvaluation(@PathVariable("id") Integer id) {
        evaluationService.eliminarAvaliacao(id);
    }
}
