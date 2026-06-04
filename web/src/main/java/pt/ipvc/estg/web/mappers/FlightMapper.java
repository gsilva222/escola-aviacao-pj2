package pt.ipvc.estg.web.mappers;

import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Instructor;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.web.dto.FlightResponse;

public final class FlightMapper {
    private FlightMapper() {}

    public static FlightResponse toResponse(Flight flight) {
        if (flight == null) {
            return null;
        }

        Student student = flight.getStudent();
        Instructor instructor = flight.getInstructor();
        Aircraft aircraft = flight.getAircraft();

        return new FlightResponse(
                flight.getId(),
                flight.getFlightDate(),
                flight.getFlightTime(),
                flight.getDuration(),
                student != null ? student.getId() : null,
                student != null ? student.getName() : null,
                instructor != null ? instructor.getId() : null,
                instructor != null ? instructor.getName() : null,
                aircraft != null ? aircraft.getId() : null,
                aircraft != null ? aircraft.getRegistration() : null,
                flight.getOrigin(),
                flight.getDestination(),
                flight.getFlightType(),
                flight.getStatus(),
                flight.getObjectives(),
                flight.getNotes(),
                flight.getGrade()
        );
    }
}
