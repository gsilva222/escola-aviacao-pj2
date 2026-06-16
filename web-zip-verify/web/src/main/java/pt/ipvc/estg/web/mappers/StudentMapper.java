package pt.ipvc.estg.web.mappers;

import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.entities.Instructor;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.validation.BusinessRules;
import pt.ipvc.estg.web.dto.StudentRequest;
import pt.ipvc.estg.web.dto.StudentResponse;

public final class StudentMapper {
    private StudentMapper() {}

    public static Student toEntity(StudentRequest request) {
        Student student = new Student();
        student.setName(request.name());
        student.setEmail(request.email());
        student.setPhone(request.phone());
        student.setNif(request.nif());
        student.setBirthdate(request.birthdate());
        student.setAddress(request.address());
        student.setNationality(request.nationality());
        if (request.courseId() != null) {
            student.setCourse(new Course());
            student.getCourse().setId(request.courseId());
        }
        if (request.instructorId() != null) {
            student.setInstructor(new Instructor());
            student.getInstructor().setId(request.instructorId());
        }
        if (request.status() != null) {
            student.setStatus(BusinessRules.requireAllowed("Status", request.status(), BusinessRules.STUDENT_STATUSES));
        }
        student.setEnrollmentDate(request.enrollmentDate());
        student.setProgress(request.progress());
        student.setFlightHours(request.flightHours());
        student.setTheoreticalHours(request.theoreticalHours());
        if (request.paymentStatus() != null) {
            student.setPaymentStatus(BusinessRules.requireAllowed(
                    "PaymentStatus", request.paymentStatus(), BusinessRules.STUDENT_PAYMENT_STATUSES));
        }
        return student;
    }

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

        Integer instructorId = null;
        String instructorName = null;
        if (student.getInstructor() != null) {
            instructorId = student.getInstructor().getId();
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
                instructorId,
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
