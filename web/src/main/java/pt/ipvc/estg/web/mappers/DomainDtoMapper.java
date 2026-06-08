package pt.ipvc.estg.web.mappers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import pt.ipvc.estg.domain.FoDashboardSummary;
import pt.ipvc.estg.domain.FoHoursSummary;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.domain.PaymentSummary;
import pt.ipvc.estg.domain.ReportsSummary;
import pt.ipvc.estg.web.dto.FoDashboardResponse;
import pt.ipvc.estg.web.dto.FoHoursSummaryResponse;
import pt.ipvc.estg.web.dto.PaymentSummaryResponse;
import pt.ipvc.estg.web.dto.ReportsSummaryResponse;

import java.util.function.Function;

public final class DomainDtoMapper {
    private DomainDtoMapper() {}

    public static PaymentSummaryResponse toResponse(PaymentSummary summary) {
        return new PaymentSummaryResponse(
                summary.totalPending(),
                summary.totalOverdue(),
                summary.totalPaid(),
                summary.pendingCount(),
                summary.overdueCount(),
                summary.paidCount()
        );
    }

    public static ReportsSummaryResponse toResponse(ReportsSummary summary) {
        return new ReportsSummaryResponse(
                summary.totalStudents(),
                summary.activeStudents(),
                summary.suspendedStudents(),
                summary.completedStudents(),
                summary.totalCourses(),
                summary.totalFlights(),
                summary.scheduledFlights(),
                summary.completedFlights(),
                summary.totalAircraft(),
                summary.operationalAircraft(),
                summary.maintenanceAircraft(),
                summary.groundedAircraft(),
                summary.pendingPayments(),
                summary.overduePayments(),
                summary.totalPendingAmount(),
                summary.activeMaintenances(),
                summary.studentsByCourse()
        );
    }

    public static FoDashboardResponse toResponse(FoDashboardSummary summary) {
        return new FoDashboardResponse(
                summary.studentId(),
                summary.studentName(),
                summary.courseName(),
                summary.progress(),
                summary.flightHours(),
                summary.theoreticalHours(),
                summary.requiredHours(),
                summary.completedHours(),
                summary.totalFlights(),
                summary.upcomingFlights(),
                summary.pendingPaymentCount(),
                summary.totalPending(),
                summary.recentFlights().stream().map(FlightMapper::toResponse).toList(),
                summary.recentEvaluations().stream().map(EvaluationMapper::toResponse).toList()
        );
    }

    public static FoHoursSummaryResponse toResponse(FoHoursSummary summary) {
        return new FoHoursSummaryResponse(
                summary.completedHours(),
                summary.localHours(),
                summary.navigationHours(),
                summary.totalFlights(),
                summary.averagePerFlight(),
                summary.requiredHours(),
                summary.remainingHours(),
                summary.progressPercent()
        );
    }

    public static <T, R> Page<R> toSpringPage(PageResult<T> result, Function<T, R> mapper, String sort) {
        Sort springSort = buildSort(sort);
        PageRequest pageable = PageRequest.of(result.page(), result.size(), springSort);
        return new PageImpl<>(result.items().stream().map(mapper).toList(), pageable, result.totalElements());
    }

    private static Sort buildSort(String sort) {
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
