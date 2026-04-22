package pt.ipvc.estg.entities;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Entidade Maintenance - Representa uma Manutenção de Avião
 */
@Entity
@Table(name = "maintenance")
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_maintenance")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_aircraft", nullable = false)
    private Aircraft aircraft;

    @Column(name = "maintenance_type", nullable = false, length = 50)
    private String maintenanceType; // Major, Minor, Scheduled, Unscheduled, Avionics, etc

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "technician", length = 100)
    private String technician;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "estimated_end_date")
    private LocalDate estimatedEndDate;

    @Column(name = "actual_end_date")
    private LocalDate actualEndDate;

    @Column(name = "status", length = 20)
    private String status; // scheduled, in_progress, waiting_parts, completed

    @Column(name = "priority", length = 20)
    private String priority; // low, medium, high

    @Column(name = "cost")
    private Double cost = 0.0;

    @Column(name = "notes", length = 500)
    private String notes;

    // Construtores
    public Maintenance() {}

    public Maintenance(Aircraft aircraft, String maintenanceType, String description) {
        this.aircraft = aircraft;
        this.maintenanceType = maintenanceType;
        this.description = description;
        this.status = "scheduled";
        this.startDate = LocalDate.now();
    }

    // Getters e Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Aircraft getAircraft() { return aircraft; }
    public void setAircraft(Aircraft aircraft) { this.aircraft = aircraft; }

    public String getMaintenanceType() { return maintenanceType; }
    public void setMaintenanceType(String maintenanceType) { this.maintenanceType = maintenanceType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getTechnician() { return technician; }
    public void setTechnician(String technician) { this.technician = technician; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEstimatedEndDate() { return estimatedEndDate; }
    public void setEstimatedEndDate(LocalDate estimatedEndDate) { this.estimatedEndDate = estimatedEndDate; }

    public LocalDate getActualEndDate() { return actualEndDate; }
    public void setActualEndDate(LocalDate actualEndDate) { this.actualEndDate = actualEndDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Double getCost() { return cost; }
    public void setCost(Double cost) { this.cost = cost; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    @Override
    public String toString() {
        return "Maintenance{" + "id=" + id + ", maintenanceType='" + maintenanceType + '\'' + ", aircraft=" + aircraft.getRegistration() + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Maintenance maintenance = (Maintenance) o;
        return id != null ? id.equals(maintenance.id) : maintenance.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
