package pt.ipvc.estg.desktop.api.dto;

public record PaymentSummaryResponse(
        double totalPending,
        double totalOverdue,
        double totalPaid,
        long pendingCount,
        long overdueCount,
        long paidCount
) {
}
