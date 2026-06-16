package pt.ipvc.estg.web.mappers;

import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.web.dto.CourseRequest;
import pt.ipvc.estg.web.dto.CourseResponse;

public final class CourseMapper {
    private CourseMapper() {}

    public static Course toEntity(CourseRequest request) {
        Course course = new Course(
                request.name(),
                request.duration(),
                request.flightHours(),
                request.theoreticalHours(),
                request.price()
        );
        course.setDescription(request.description());
        return course;
    }

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
