package pt.ipvc.estg.domain;

public record PaymentSummary(
        double totalPending,
        double totalOverdue,
        double totalPaid,
        long pendingCount,
        long overdueCount,
        long paidCount
) {
}
