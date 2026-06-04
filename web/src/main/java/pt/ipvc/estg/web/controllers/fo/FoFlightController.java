package pt.ipvc.estg.web.controllers.fo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.web.dto.FlightResponse;
import pt.ipvc.estg.web.dto.FoHoursSummaryResponse;
import pt.ipvc.estg.web.mappers.FlightMapper;
import pt.ipvc.estg.web.repositories.FlightRepository;
import pt.ipvc.estg.web.services.FlightHoursCalculator;
import pt.ipvc.estg.web.services.FoDashboardService;
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
        PageRequest pageable = PageRequest.of(page, size);
        List<FlightResponse> content;
        long total;
        if (status != null && !status.isBlank()) {
            content = flightRepository.findByStudent_IdAndStatus(studentId, status, pageable)
                    .stream().map(FlightMapper::toResponse).toList();
            total = flightRepository.findByStudent_IdAndStatus(studentId, status).size();
        } else {
            content = flightRepository.findByStudent_Id(studentId, pageable)
                    .stream().map(FlightMapper::toResponse).toList();
            total = flightRepository.findByStudent_Id(studentId).size();
        }
        return new PageImpl<>(content, pageable, total);
    }

    @Transactional(readOnly = true)
    @GetMapping("/schedule")
    public List<FlightResponse> getSchedule(@RequestParam(value = "from", required = false) LocalDate from,
                                            @RequestParam(value = "to", required = false) LocalDate to) {
        int studentId = studentScopeService.requireCurrentStudent().getId();
        LocalDate start = from != null ? from : LocalDate.now();
        LocalDate end = to != null ? to : start.plusDays(30);
        return flightRepository.findByStudent_IdAndFlightDateBetweenOrderByFlightDateAscFlightTimeAsc(studentId, start, end)
                .stream()
                .filter(f -> "scheduled".equals(FlightHoursCalculator.normalizeStatus(f)))
                .map(FlightMapper::toResponse)
                .toList();
    }

    @GetMapping("/hours")
    public FoHoursSummaryResponse getHours() {
        return foDashboardService.buildHoursSummary();
    }
}
