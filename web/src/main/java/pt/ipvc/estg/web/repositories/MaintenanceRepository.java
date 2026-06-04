package pt.ipvc.estg.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import pt.ipvc.estg.entities.Maintenance;

import java.util.List;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Integer> {
    List<Maintenance> findByAircraft_Id(Integer aircraftId);
    List<Maintenance> findByAircraft_Id(Integer aircraftId, Pageable pageable);
    List<Maintenance> findByStatus(String status);
    List<Maintenance> findByStatus(String status, Pageable pageable);
    List<Maintenance> findByPriority(String priority);
    List<Maintenance> findByPriority(String priority, Pageable pageable);
}
