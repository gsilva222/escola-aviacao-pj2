package pt.ipvc.estg.web.mappers;

import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.web.dto.CourseResponse;

public final class CourseMapper {
    private CourseMapper() {}

    public static CourseResponse toResponse(Course course) {
        if (course == null) {
            return null;
        }

        return new CourseResponse(
                course.getId(),
                course.getName(),
                course.getDuration(),
                course.getFlightHours(),
                course.getTheoreticalHours(),
                course.getPrice(),
                course.getEnrolled(),
                course.getCompleted(),
                course.getDescription()
        );
    }
}
