package pt.ipvc.estg.web.mappers;

import pt.ipvc.estg.entities.Instructor;
import pt.ipvc.estg.web.dto.InstructorResponse;

public final class InstructorMapper {
    private InstructorMapper() {}

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
