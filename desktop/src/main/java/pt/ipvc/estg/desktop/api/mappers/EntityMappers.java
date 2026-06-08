package pt.ipvc.estg.desktop.api.mappers;

import pt.ipvc.estg.desktop.api.dto.*;
import pt.ipvc.estg.entities.*;

public final class EntityMappers {

    private EntityMappers() {
    }

    public static Student toStudent(StudentResponse r) {
        return StudentDtoMapper.fromResponse(r);
    }

    public static Course toCourse(CourseResponse r) {
        Course c = new Course();
        c.setId(r.id());
        c.setName(r.name());
        c.setDuration(r.duration());
        c.setFlightHours(r.flightHours());
        c.setTheoreticalHours(r.theoreticalHours());
        c.setPrice(r.price());
        c.setDescription(r.description());
        c.setEnrolled(r.enrolled());
        c.setCompleted(r.completed());
        return c;
    }

    public static Instructor toInstructor(InstructorResponse r) {
        Instructor i = new Instructor();
        i.setId(r.id());
        i.setName(r.name());
        i.setLicense(r.license());
        i.setSpecialization(r.specialization());
        i.setFlightHours(r.flightHours());
        i.setStatus(r.status());
        i.setEmail(r.email());
        i.setPhone(r.phone());
        return i;
    }

    public static Aircraft toAircraft(AircraftResponse r) {
        Aircraft a = new Aircraft();
        a.setId(r.id());
        a.setRegistration(r.registration());
        a.setModel(r.model());
        a.setType(r.type());
        a.setManufYear(r.manufYear());
        a.setStatus(r.status());
        a.setFlightHours(r.flightHours());
        a.setLastMaintenance(r.lastMaintenance());
        a.setNextMaintenance(r.nextMaintenance());
        a.setLocation(r.location());
        a.setFuelLevel(r.fuelLevel());
        a.setNotes(r.notes());
        return a;
    }

    public static Flight toFlight(FlightResponse r) {
        Flight f = new Flight();
        f.setId(r.id());
        f.setFlightDate(r.flightDate());
        f.setFlightTime(r.flightTime());
        f.setDuration(r.duration());
        f.setOrigin(r.origin());
        f.setDestination(r.destination());
        f.setFlightType(r.flightType());
        f.setStatus(r.status());
        f.setObjectives(r.objectives());
        f.setNotes(r.notes());
        f.setGrade(r.grade());

        if (r.studentId() != null) {
            Student s = new Student();
            s.setId(r.studentId());
            s.setName(r.studentName());
            f.setStudent(s);
        }
        if (r.instructorId() != null) {
            Instructor i = new Instructor();
            i.setId(r.instructorId());
            i.setName(r.instructorName());
            f.setInstructor(i);
        }
        if (r.aircraftId() != null) {
            Aircraft a = new Aircraft();
            a.setId(r.aircraftId());
            a.setRegistration(r.aircraftRegistration());
            f.setAircraft(a);
        }
        return f;
    }

    public static Maintenance toMaintenance(MaintenanceResponse r) {
        Maintenance m = new Maintenance();
        m.setId(r.id());
        m.setMaintenanceType(r.maintenanceType());
        m.setDescription(r.description());
        m.setTechnician(r.technician());
        m.setStartDate(r.startDate());
        m.setEstimatedEndDate(r.estimatedEndDate());
        m.setActualEndDate(r.actualEndDate());
        m.setStatus(r.status());
        m.setPriority(r.priority());
        m.setCost(r.cost());
        m.setNotes(r.notes());
        if (r.aircraftId() != null) {
            Aircraft a = new Aircraft();
            a.setId(r.aircraftId());
            a.setRegistration(r.aircraftRegistration());
            m.setAircraft(a);
        }
        return m;
    }

    public static Evaluation toEvaluation(EvaluationResponse r) {
        Evaluation e = new Evaluation();
        e.setId(r.id());
        e.setExamName(r.examName());
        e.setEvaluationDate(r.evaluationDate());
        e.setScore(r.score());
        e.setMaxScore(r.maxScore());
        e.setStatus(r.status());
        e.setEvaluationType(r.evaluationType());
        e.setNotes(r.notes());
        if (r.studentId() != null) {
            Student s = new Student();
            s.setId(r.studentId());
            s.setName(r.studentName());
            e.setStudent(s);
        }
        if (r.courseId() != null) {
            Course c = new Course();
            c.setId(r.courseId());
            c.setName(r.courseName());
            e.setCourse(c);
        }
        return e;
    }

    public static Payment toPayment(PaymentResponse r) {
        Payment p = new Payment();
        p.setId(r.id());
        p.setDescription(r.description());
        p.setAmount(r.amount());
        p.setDueDate(r.dueDate());
        p.setPaidDate(r.paidDate());
        p.setStatus(r.status());
        p.setPaymentMethod(r.paymentMethod());
        p.setNotes(r.notes());
        if (r.studentId() != null) {
            Student s = new Student();
            s.setId(r.studentId());
            s.setName(r.studentName());
            p.setStudent(s);
        }
        return p;
    }
}
