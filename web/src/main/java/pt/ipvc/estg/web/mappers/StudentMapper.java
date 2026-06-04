package pt.ipvc.estg.web.mappers;

import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.web.dto.StudentResponse;

public final class StudentMapper {
    private StudentMapper() {}

    public static StudentResponse toResponse(Student student) {
        if (student == null) {
            return null;
        }

        Integer courseId = null;
        String courseName = null;
        if (student.getCourse() != null) {
            courseId = student.getCourse().getId();
            courseName = student.getCourse().getName();
        }

        String instructorName = null;
        if (student.getInstructor() != null) {
            instructorName = student.getInstructor().getName();
        }

        return new StudentResponse(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getPhone(),
                student.getNif(),
                student.getBirthdate(),
                student.getAddress(),
                student.getNationality(),
                courseId,
                courseName,
                student.getStatus(),
                student.getEnrollmentDate(),
                student.getProgress(),
                student.getFlightHours(),
                student.getTheoreticalHours(),
                student.getPaymentStatus(),
                student.getAvatar(),
                instructorName
        );
    }
}
