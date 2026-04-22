package pt.ipvc.estg.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Course - Representa um Curso de Aviação
 * (PPL, CPL, IR, ATPL, etc.)
 */
@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_course")
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "duration", length = 50)
    private String duration; // ex: "12 meses", "18 meses"

    @Column(name = "flight_hours")
    private Integer flightHours;

    @Column(name = "theoretical_hours")
    private Integer theoreticalHours;

    @Column(name = "price")
    private Double price;

    @Column(name = "enrolled")
    private Integer enrolled = 0;

    @Column(name = "completed")
    private Integer completed = 0;

    @Column(name = "description", length = 500)
    private String description;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Student> students = new ArrayList<>();

    // Construtores
    public Course() {}

    public Course(String name, String duration, Integer flightHours, Integer theoreticalHours, Double price) {
        this.name = name;
        this.duration = duration;
        this.flightHours = flightHours;
        this.theoreticalHours = theoreticalHours;
        this.price = price;
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public Integer getFlightHours() { return flightHours; }
    public void setFlightHours(Integer flightHours) { this.flightHours = flightHours; }

    public Integer getTheoreticalHours() { return theoreticalHours; }
    public void setTheoreticalHours(Integer theoreticalHours) { this.theoreticalHours = theoreticalHours; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Integer getEnrolled() { return enrolled; }
    public void setEnrolled(Integer enrolled) { this.enrolled = enrolled; }

    public Integer getCompleted() { return completed; }
    public void setCompleted(Integer completed) { this.completed = completed; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }

    @Override
    public String toString() {
        return "Course{" + "id=" + id + ", name='" + name + '\'' + ", flightHours=" + flightHours + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id != null ? id.equals(course.id) : course.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
