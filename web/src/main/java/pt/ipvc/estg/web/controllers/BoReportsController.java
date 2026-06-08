package pt.ipvc.estg.web.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ipvc.estg.services.ReportsService;
import pt.ipvc.estg.web.dto.ReportsSummaryResponse;
import pt.ipvc.estg.web.mappers.DomainDtoMapper;

@RestController
@RequestMapping("/bo/reports")
public class BoReportsController {

    private final ReportsService reportsService;

    public BoReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    @GetMapping("/summary")
    public ReportsSummaryResponse summary() {
        return DomainDtoMapper.toResponse(reportsService.buildSummary());
    }
}
