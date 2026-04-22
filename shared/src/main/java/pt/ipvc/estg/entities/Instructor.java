package pt.ipvc.estg.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Instructor - Representa um Instrutor/Piloto Instrutor
 */
@Entity
@Table(name = "instructor")
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_instructor")
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "license", nullable = false, length = 50)
    private String license; // ex: "CPL/IR/FI"

    @Column(name = "specialization", length = 200)
    private String specialization; // ex: "PPL, IR", "CPL, ATPL"

    @Column(name = "flight_hours")
    private Integer flightHours = 0;

    @Column(name = "status", length = 20)
    private String status; // active, inactive

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "instructor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Flight> flights = new ArrayList<>();

    // Construtores
    public Instructor() {}

    public Instructor(String name, String license, String specialization) {
        this.name = name;
        this.license = license;
        this.specialization = specialization;
        this.status = "active";
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLicense() { return license; }
    public void setLicense(String license) { this.license = license; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public Integer getFlightHours() { return flightHours; }
    public void setFlightHours(Integer flightHours) { this.flightHours = flightHours; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }

    public List<Flight> getFlights() { return flights; }
    public void setFlights(List<Flight> flights) { this.flights = flights; }

    @Override
    public String toString() {
        return "Instructor{" + "id=" + id + ", name='" + name + '\'' + ", license='" + license + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Instructor instructor = (Instructor) o;
        return id != null ? id.equals(instructor.id) : instructor.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
