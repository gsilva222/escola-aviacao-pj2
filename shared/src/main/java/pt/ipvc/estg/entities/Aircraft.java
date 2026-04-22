package pt.ipvc.estg.entities;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Aircraft - Representa um Avião
 */
@Entity
@Table(name = "aircraft")
public class Aircraft {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aircraft")
    private Integer id;

    @Column(name = "registration", unique = true, nullable = false, length = 20)
    private String registration; // ex: "CS-AER"

    @Column(name = "model", nullable = false, length = 100)
    private String model; // ex: "Cessna 172 Skyhawk"

    @Column(name = "type", nullable = false, length = 50)
    private String type; // ex: "Single Engine", "Multi Engine"

    @Column(name = "manufacturing_year")
    private Integer manufYear;

    @Column(name = "status", length = 20)
    private String status; // operational, maintenance, grounded

    @Column(name = "flight_hours")
    private Double flightHours = 0.0;

    @Column(name = "last_maintenance")
    private java.time.LocalDate lastMaintenance;

    @Column(name = "next_maintenance")
    private java.time.LocalDate nextMaintenance;

    @Column(name = "location", length = 100)
    private String location; // Hangar A, B, etc

    @Column(name = "fuel_level")
    private Integer fuelLevel = 0; // Percentagem

    @Column(name = "notes", length = 500)
    private String notes;

    @OneToMany(mappedBy = "aircraft", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Flight> flights = new ArrayList<>();

    @OneToMany(mappedBy = "aircraft", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Maintenance> maintenances = new ArrayList<>();

    // Construtores
    public Aircraft() {}

    public Aircraft(String registration, String model, String type) {
        this.registration = registration;
        this.model = model;
        this.type = type;
        this.status = "operational";
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getRegistration() { return registration; }
    public void setRegistration(String registration) { this.registration = registration; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getManufYear() { return manufYear; }
    public void setManufYear(Integer manufYear) { this.manufYear = manufYear; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getFlightHours() { return flightHours; }
    public void setFlightHours(Double flightHours) { this.flightHours = flightHours; }

    public java.time.LocalDate getLastMaintenance() { return lastMaintenance; }
    public void setLastMaintenance(java.time.LocalDate lastMaintenance) { this.lastMaintenance = lastMaintenance; }

    public java.time.LocalDate getNextMaintenance() { return nextMaintenance; }
    public void setNextMaintenance(java.time.LocalDate nextMaintenance) { this.nextMaintenance = nextMaintenance; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Integer getFuelLevel() { return fuelLevel; }
    public void setFuelLevel(Integer fuelLevel) { this.fuelLevel = fuelLevel; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public List<Flight> getFlights() { return flights; }
    public void setFlights(List<Flight> flights) { this.flights = flights; }

    public List<Maintenance> getMaintenances() { return maintenances; }
    public void setMaintenances(List<Maintenance> maintenances) { this.maintenances = maintenances; }

    @Override
    public String toString() {
        return "Aircraft{" + "id=" + id + ", registration='" + registration + '\'' + ", model='" + model + '\'' + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Aircraft aircraft = (Aircraft) o;
        return id != null ? id.equals(aircraft.id) : aircraft.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
