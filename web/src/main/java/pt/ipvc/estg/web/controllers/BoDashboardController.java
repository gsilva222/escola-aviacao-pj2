package pt.ipvc.estg.web.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ipvc.estg.domain.BoDashboardSummary;
import pt.ipvc.estg.services.BoDashboardService;
import pt.ipvc.estg.web.dto.BoDashboardResponse;
import pt.ipvc.estg.web.mappers.DomainDtoMapper;

@RestController
@RequestMapping("/bo/dashboard")
public class BoDashboardController {

    private final BoDashboardService boDashboardService;

    public BoDashboardController(BoDashboardService boDashboardService) {
        this.boDashboardService = boDashboardService;
    }

    @GetMapping
    public BoDashboardResponse dashboard() {
        BoDashboardSummary summary = boDashboardService.buildDashboard();
        return new BoDashboardResponse(
                DomainDtoMapper.toResponse(summary.reports()),
                summary.flightsToday(),
                summary.completedFlightsToday(),
                summary.scheduledFlightsToday(),
                summary.flightHoursThisMonth(),
                summary.revenueThisMonth(),
                summary.recentActivity().stream()
                        .map(a -> new BoDashboardResponse.BoActivityItemResponse(
                                a.icon(), a.title(), a.subtitle(), a.type()))
                        .toList()
        );
    }
}
