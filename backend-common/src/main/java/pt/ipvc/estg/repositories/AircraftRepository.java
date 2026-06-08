package pt.ipvc.estg.repositories;

import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Aircraft;

import java.util.List;
import java.util.Optional;

public interface AircraftRepository {
    Optional<Aircraft> findById(Integer id);
    Optional<Aircraft> findByRegistration(String registration);
    List<Aircraft> findAll();
    PageResult<Aircraft> findAll(PageQuery query);
    List<Aircraft> findByStatus(String status);
    PageResult<Aircraft> findByStatus(String status, PageQuery query);
    Aircraft save(Aircraft aircraft);
    void deleteById(Integer id);
    boolean existsById(Integer id);
    long count();
}
