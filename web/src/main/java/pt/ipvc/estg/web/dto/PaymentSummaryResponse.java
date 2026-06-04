package pt.ipvc.estg.web.dto;

public record PaymentSummaryResponse(
        double totalPending,
        double totalOverdue,
        double totalPaid,
        long pendingCount,
        long overdueCount,
        long paidCount
) {
}
