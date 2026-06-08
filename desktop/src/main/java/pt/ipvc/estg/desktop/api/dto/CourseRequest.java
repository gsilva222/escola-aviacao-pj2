package pt.ipvc.estg.desktop.api.dto;

public record CourseRequest(
        String name,
        String duration,
        Integer flightHours,
        Integer theoreticalHours,
        Double price,
        String description
) {
}
