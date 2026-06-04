package pt.ipvc.estg.web.services;

import pt.ipvc.estg.entities.Flight;

import java.util.List;
import java.util.Locale;

public final class FlightHoursCalculator {
    private FlightHoursCalculator() {}

    public static double completedHours(List<Flight> flights) {
        return flights.stream()
                .filter(FlightHoursCalculator::isCompleted)
                .mapToDouble(f -> f.getDuration() != null ? f.getDuration() : 0.0)
                .sum();
    }

    public static double localHours(List<Flight> flights) {
        return flights.stream()
                .filter(FlightHoursCalculator::isCompleted)
                .filter(f -> "local".equals(normalizeType(f)))
                .mapToDouble(f -> f.getDuration() != null ? f.getDuration() : 0.0)
                .sum();
    }

    public static double navigationHours(List<Flight> flights) {
        return flights.stream()
                .filter(FlightHoursCalculator::isCompleted)
                .filter(f -> {
                    String type = normalizeType(f);
                    return "navigation".equals(type) || "ifr".equals(type);
                })
                .mapToDouble(f -> f.getDuration() != null ? f.getDuration() : 0.0)
                .sum();
    }

    public static boolean isCompleted(Flight flight) {
        return "completed".equals(normalizeStatus(flight));
    }

    public static String normalizeStatus(Flight flight) {
        String status = safe(flight.getStatus(), "scheduled").toLowerCase(Locale.ROOT);
        if (status.contains("complete")) {
            return "completed";
        }
        if (status.contains("cancel")) {
            return "cancelled";
        }
        if (status.contains("sched")) {
            return "scheduled";
        }
        return status;
    }

    public static String normalizeType(Flight flight) {
        String type = safe(flight.getFlightType(), "local").toLowerCase(Locale.ROOT);
        if (type.contains("nav")) {
            return "navigation";
        }
        if (type.contains("ifr") || type.contains("instrument")) {
            return "ifr";
        }
        if (type.contains("local") || type.contains("circuit")) {
            return "local";
        }
        return type;
    }

    private static String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
