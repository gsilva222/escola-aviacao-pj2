package pt.ipvc.estg.desktop.api.mappers;

import pt.ipvc.estg.desktop.api.dto.StudentResponse;
import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.entities.Instructor;
import pt.ipvc.estg.entities.Student;

public final class StudentDtoMapper {

    private StudentDtoMapper() {
    }

    public static Student fromResponse(StudentResponse response) {
        Student student = new Student();
        student.setId(response.id());
        student.setName(response.name());
        student.setEmail(response.email());
        student.setPhone(response.phone());
        student.setNif(response.nif());
        student.setBirthdate(response.birthdate());
        student.setAddress(response.address());
        student.setNationality(response.nationality());
        student.setStatus(response.status());
        student.setEnrollmentDate(response.enrollmentDate());
        student.setProgress(response.progress());
        student.setFlightHours(response.flightHours());
        student.setTheoreticalHours(response.theoreticalHours());
        student.setPaymentStatus(response.paymentStatus());
        student.setAvatar(response.avatar());

        if (response.courseId() != null) {
            Course course = new Course();
            course.setId(response.courseId());
            course.setName(response.courseName());
            student.setCourse(course);
        }

        if (response.instructorId() != null || response.instructorName() != null) {
            Instructor instructor = new Instructor();
            instructor.setId(response.instructorId());
            instructor.setName(response.instructorName());
            student.setInstructor(instructor);
        }

        return student;
    }
}
