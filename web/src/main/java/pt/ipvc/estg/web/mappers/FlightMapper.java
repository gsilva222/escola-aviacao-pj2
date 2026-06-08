package pt.ipvc.estg.web.mappers;

import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Instructor;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.validation.BusinessRules;
import pt.ipvc.estg.web.dto.FlightRequest;
import pt.ipvc.estg.web.dto.FlightResponse;

public final class FlightMapper {
    private FlightMapper() {}

    public static Flight toEntity(FlightRequest request) {
        Flight flight = new Flight();
        flight.setFlightDate(request.flightDate());
        flight.setFlightTime(request.flightTime());
        flight.setDuration(request.duration());
        if (request.studentId() != null) {
            Student student = new Student();
            student.setId(request.studentId());
            flight.setStudent(student);
        }
        if (request.instructorId() != null) {
            Instructor instructor = new Instructor();
            instructor.setId(request.instructorId());
            flight.setInstructor(instructor);
        }
        if (request.aircraftId() != null) {
            Aircraft aircraft = new Aircraft();
            aircraft.setId(request.aircraftId());
            flight.setAircraft(aircraft);
        }
        flight.setOrigin(request.origin());
        flight.setDestination(request.destination());
        flight.setFlightType(request.flightType());
        if (request.status() != null) {
            flight.setStatus(BusinessRules.requireAllowed("Status", request.status(), BusinessRules.FLIGHT_STATUSES));
        }
        flight.setObjectives(request.objectives());
        flight.setNotes(request.notes());
        flight.setGrade(request.grade());
        return flight;
    }

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
