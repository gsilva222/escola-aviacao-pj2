package pt.ipvc.estg.web.controllers.fo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ipvc.estg.services.FoDashboardService;
import pt.ipvc.estg.web.dto.FoDashboardResponse;
import pt.ipvc.estg.web.mappers.DomainDtoMapper;

@RestController
@RequestMapping("/fo/dashboard")
public class FoDashboardController {

    private final FoDashboardService foDashboardService;

    public FoDashboardController(FoDashboardService foDashboardService) {
        this.foDashboardService = foDashboardService;
    }

    @GetMapping
    public FoDashboardResponse dashboard() {
        return DomainDtoMapper.toResponse(foDashboardService.buildDashboard());
    }
}
