package pt.ipvc.estg.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entidade Flight - Representa um Voo
 */
@Entity
@Table(name = "flight")
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_flight")
    private Integer id;

    @Column(name = "flight_date", nullable = false)
    private LocalDate flightDate;

    @Column(name = "flight_time")
    private LocalTime flightTime;

    @Column(name = "duration")
    private Double duration; // em horas

    @ManyToOne
    @JoinColumn(name = "id_student", nullable = false)
    private Student student;

    @ManyToOne
    @JoinColumn(name = "id_instructor", nullable = false)
    private Instructor instructor;

    @ManyToOne
    @JoinColumn(name = "id_aircraft", nullable = false)
    private Aircraft aircraft;

    @Column(name = "origin", length = 10)
    private String origin; // ICAO code, ex: LPPT

    @Column(name = "destination", length = 10)
    private String destination; // ICAO code

    @Column(name = "flight_type", length = 50)
    private String flightType; // Local, Navigation, IFR, etc

    @Column(name = "status", length = 20)
    private String status; // scheduled, completed, cancelled

    @Column(name = "objectives", length = 500)
    private String objectives;

    @Column(name = "notes", length = 500)
    private String notes;

    @Column(name = "grade", length = 5)
    private String grade; // A, B, B+, C, etc

    // Construtores
    public Flight() {}

    public Flight(LocalDate flightDate, Student student, Instructor instructor, Aircraft aircraft) {
        this.flightDate = flightDate;
        this.student = student;
        this.instructor = instructor;
        this.aircraft = aircraft;
        this.status = "scheduled";
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public LocalDate getFlightDate() { return flightDate; }
    public void setFlightDate(LocalDate flightDate) { this.flightDate = flightDate; }

    public LocalTime getFlightTime() { return flightTime; }
    public void setFlightTime(LocalTime flightTime) { this.flightTime = flightTime; }

    public Double getDuration() { return duration; }
    public void setDuration(Double duration) { this.duration = duration; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Instructor getInstructor() { return instructor; }
    public void setInstructor(Instructor instructor) { this.instructor = instructor; }

    public Aircraft getAircraft() { return aircraft; }
    public void setAircraft(Aircraft aircraft) { this.aircraft = aircraft; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getFlightType() { return flightType; }
    public void setFlightType(String flightType) { this.flightType = flightType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getObjectives() { return objectives; }
    public void setObjectives(String objectives) { this.objectives = objectives; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    @Override
    public String toString() {
        return "Flight{" + "id=" + id + ", flightDate=" + flightDate + ", student=" + student.getName() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Flight flight = (Flight) o;
        return id != null ? id.equals(flight.id) : flight.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
