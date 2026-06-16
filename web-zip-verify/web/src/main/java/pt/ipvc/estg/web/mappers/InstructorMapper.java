package pt.ipvc.estg.web.mappers;

import pt.ipvc.estg.entities.Instructor;
import pt.ipvc.estg.validation.BusinessRules;
import pt.ipvc.estg.web.dto.InstructorRequest;
import pt.ipvc.estg.web.dto.InstructorResponse;

public final class InstructorMapper {
    private InstructorMapper() {}

    public static Instructor toEntity(InstructorRequest request) {
        Instructor instructor = new Instructor(request.name(), request.license(), request.specialization());
        instructor.setFlightHours(request.flightHours());
        if (request.status() != null) {
            instructor.setStatus(BusinessRules.requireAllowed("Status", request.status(), BusinessRules.INSTRUCTOR_STATUSES));
        }
        instructor.setEmail(request.email());
        instructor.setPhone(request.phone());
        return instructor;
    }

    public static InstructorResponse toResponse(Instructor instructor) {
        if (instructor == null) {
            return null;
        }

        return new InstructorResponse(
                instructor.getId(),
                instructor.getName(),
                instructor.getLicense(),
                instructor.getSpecialization(),
                instructor.getFlightHours(),
                instructor.getStatus(),
                instructor.getEmail(),
                instructor.getPhone()
        );
    }
}
