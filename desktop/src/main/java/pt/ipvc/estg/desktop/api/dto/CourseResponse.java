package pt.ipvc.estg.desktop.api.dto;

public record CourseResponse(
        Integer id,
        String name,
        String duration,
        Integer flightHours,
        Integer theoreticalHours,
        Double price,
        Integer enrolled,
        Integer completed,
        String description
) {
}
