package pt.ipvc.estg.repositories;

import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Maintenance;

import java.util.List;
import java.util.Optional;

public interface MaintenanceRepository {
    Optional<Maintenance> findById(Integer id);
    List<Maintenance> findAll();
    PageResult<Maintenance> findAll(PageQuery query);
    List<Maintenance> findByAircraft(Integer aircraftId);
    PageResult<Maintenance> findByAircraft(Integer aircraftId, PageQuery query);
    List<Maintenance> findByStatus(String status);
    PageResult<Maintenance> findByStatus(String status, PageQuery query);
    List<Maintenance> findByPriority(String priority);
    PageResult<Maintenance> findByPriority(String priority, PageQuery query);
    Maintenance save(Maintenance maintenance);
    void deleteById(Integer id);
    boolean existsById(Integer id);
    long count();
}
