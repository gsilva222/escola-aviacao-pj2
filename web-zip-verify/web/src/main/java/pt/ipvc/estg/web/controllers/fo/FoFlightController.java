package pt.ipvc.estg.web.controllers.fo;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.repositories.FlightRepository;
import pt.ipvc.estg.services.FoDashboardService;
import pt.ipvc.estg.util.FlightHoursCalculator;
import pt.ipvc.estg.web.dto.FlightResponse;
import pt.ipvc.estg.web.dto.FoHoursSummaryResponse;
import pt.ipvc.estg.web.mappers.DomainDtoMapper;
import pt.ipvc.estg.web.mappers.FlightMapper;
import pt.ipvc.estg.web.services.StudentScopeService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/fo")
public class FoFlightController {

    private final StudentScopeService studentScopeService;
    private final FlightRepository flightRepository;
    private final FoDashboardService foDashboardService;

    public FoFlightController(StudentScopeService studentScopeService,
                              FlightRepository flightRepository,
                              FoDashboardService foDashboardService) {
        this.studentScopeService = studentScopeService;
        this.flightRepository = flightRepository;
        this.foDashboardService = foDashboardService;
    }

    @Transactional(readOnly = true)
    @GetMapping("/flights")
    public Page<FlightResponse> getFlights(@RequestParam(value = "status", required = false) String status,
                                           @RequestParam(value = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "size", defaultValue = "20") int size) {
        int studentId = studentScopeService.requireCurrentStudent().getId();
        PageQuery query = PageQuery.of(page, size);
        var result = status != null && !status.isBlank()
                ? flightRepository.findByStudentAndStatus(studentId, status, query)
                : flightRepository.findByStudent(studentId, query);
        return DomainDtoMapper.toSpringPage(result, FlightMapper::toResponse, null);
    }

    @Transactional(readOnly = true)
    @GetMapping("/schedule")
    public List<FlightResponse> getSchedule(@RequestParam(value = "from", required = false) LocalDate from,
                                            @RequestParam(value = "to", required = false) LocalDate to) {
        int studentId = studentScopeService.requireCurrentStudent().getId();
        LocalDate start = from != null ? from : LocalDate.now();
        LocalDate end = to != null ? to : start.plusDays(30);
        return flightRepository.findByStudentAndFlightDateBetween(studentId, start, end).stream()
                .filter(f -> "scheduled".equals(FlightHoursCalculator.normalizeStatus(f)))
                .map(FlightMapper::toResponse)
                .toList();
    }

    @GetMapping("/hours")
    public FoHoursSummaryResponse getHours() {
        return DomainDtoMapper.toResponse(foDashboardService.buildHoursSummary());
    }
}
