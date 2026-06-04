package pt.ipvc.estg.web.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.entities.Maintenance;
import pt.ipvc.estg.web.dto.MaintenanceRequest;
import pt.ipvc.estg.web.dto.MaintenanceResponse;
import pt.ipvc.estg.web.mappers.MaintenanceMapper;
import pt.ipvc.estg.web.repositories.AircraftRepository;
import pt.ipvc.estg.web.repositories.MaintenanceRepository;
import pt.ipvc.estg.web.validation.BusinessRules;

import java.util.List;

@RestController
@RequestMapping("/bo/maintenance")
public class MaintenanceController {

    private final MaintenanceRepository maintenanceRepository;
    private final AircraftRepository aircraftRepository;

    public MaintenanceController(MaintenanceRepository maintenanceRepository, AircraftRepository aircraftRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.aircraftRepository = aircraftRepository;
    }

    @GetMapping
    public Page<MaintenanceResponse> getMaintenance(@RequestParam(value = "aircraftId", required = false) Integer aircraftId,
                                                    @RequestParam(value = "status", required = false) String status,
                                                    @RequestParam(value = "priority", required = false) String priority,
                                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "20") int size,
                                                    @RequestParam(value = "sort", required = false) String sort) {
        PageRequest pageable = PageRequest.of(page, size, buildSort(sort));
        if (aircraftId != null) {
            List<MaintenanceResponse> content = maintenanceRepository.findByAircraft_Id(aircraftId, pageable)
                    .stream().map(MaintenanceMapper::toResponse).toList();
            long total = maintenanceRepository.findByAircraft_Id(aircraftId).size();
            return new PageImpl<>(content, pageable, total);
        }
        if (status != null && !status.trim().isEmpty()) {
            List<MaintenanceResponse> content = maintenanceRepository.findByStatus(status, pageable)
                    .stream().map(MaintenanceMapper::toResponse).toList();
            long total = maintenanceRepository.findByStatus(status).size();
            return new PageImpl<>(content, pageable, total);
        }
        if (priority != null && !priority.trim().isEmpty()) {
            List<MaintenanceResponse> content = maintenanceRepository.findByPriority(priority, pageable)
                    .stream().map(MaintenanceMapper::toResponse).toList();
            long total = maintenanceRepository.findByPriority(priority).size();
            return new PageImpl<>(content, pageable, total);
        }
        return maintenanceRepository.findAll(pageable).map(MaintenanceMapper::toResponse);
    }

    @GetMapping("/{id}")
    public MaintenanceResponse getMaintenanceById(@PathVariable("id") Integer id) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Manutencao nao encontrada"));
        return MaintenanceMapper.toResponse(maintenance);
    }

    @PostMapping
    public MaintenanceResponse createMaintenance(@Valid @RequestBody MaintenanceRequest request) {
        BusinessRules.requirePositiveOrZero("Custo", request.cost());
        BusinessRules.validateDateOrder("Data estimada de fim", request.startDate(), request.estimatedEndDate());
        BusinessRules.validateDateOrder("Data real de fim", request.startDate(), request.actualEndDate());
        Aircraft aircraft = aircraftRepository.findById(request.aircraftId())
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Aeronave nao encontrada"));

        Maintenance maintenance = new Maintenance(aircraft, request.maintenanceType(), request.description());
        if (request.technician() != null) maintenance.setTechnician(request.technician());
        if (request.startDate() != null) maintenance.setStartDate(request.startDate());
        if (request.estimatedEndDate() != null) maintenance.setEstimatedEndDate(request.estimatedEndDate());
        if (request.actualEndDate() != null) maintenance.setActualEndDate(request.actualEndDate());
        if (request.status() != null) maintenance.setStatus(BusinessRules.requireAllowed("Status", request.status(), BusinessRules.MAINTENANCE_STATUSES));
        if (request.priority() != null) maintenance.setPriority(BusinessRules.requireAllowed("Prioridade", request.priority(), BusinessRules.MAINTENANCE_PRIORITIES));
        if (request.cost() != null) maintenance.setCost(request.cost());
        if (request.notes() != null) maintenance.setNotes(request.notes());

        Maintenance created = maintenanceRepository.save(maintenance);
        return MaintenanceMapper.toResponse(created);
    }

    @PutMapping("/{id}")
    public MaintenanceResponse updateMaintenance(@PathVariable("id") Integer id, @RequestBody MaintenanceRequest request) {
        Maintenance maintenance = maintenanceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Manutencao nao encontrada"));
        BusinessRules.requirePositiveOrZero("Custo", request.cost());
        java.time.LocalDate startDate = request.startDate() != null ? request.startDate() : maintenance.getStartDate();
        BusinessRules.validateDateOrder("Data estimada de fim", startDate,
                request.estimatedEndDate() != null ? request.estimatedEndDate() : maintenance.getEstimatedEndDate());
        BusinessRules.validateDateOrder("Data real de fim", startDate,
                request.actualEndDate() != null ? request.actualEndDate() : maintenance.getActualEndDate());

        if (request.maintenanceType() != null && !request.maintenanceType().trim().isEmpty()) {
            maintenance.setMaintenanceType(request.maintenanceType());
        }
        if (request.description() != null && !request.description().trim().isEmpty()) {
            maintenance.setDescription(request.description());
        }
        if (request.technician() != null) maintenance.setTechnician(request.technician());
        if (request.startDate() != null) maintenance.setStartDate(request.startDate());
        if (request.estimatedEndDate() != null) maintenance.setEstimatedEndDate(request.estimatedEndDate());
        if (request.actualEndDate() != null) maintenance.setActualEndDate(request.actualEndDate());
        if (request.status() != null) maintenance.setStatus(BusinessRules.requireAllowed("Status", request.status(), BusinessRules.MAINTENANCE_STATUSES));
        if (request.priority() != null) maintenance.setPriority(BusinessRules.requireAllowed("Prioridade", request.priority(), BusinessRules.MAINTENANCE_PRIORITIES));
        if (request.cost() != null) maintenance.setCost(request.cost());
        if (request.notes() != null) maintenance.setNotes(request.notes());

        if (request.aircraftId() != null) {
            Aircraft aircraft = aircraftRepository.findById(request.aircraftId())
                    .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Aeronave nao encontrada"));
            maintenance.setAircraft(aircraft);
        }

        Maintenance updated = maintenanceRepository.save(maintenance);
        return MaintenanceMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteMaintenance(@PathVariable("id") Integer id) {
        if (!maintenanceRepository.existsById(id)) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Manutencao nao encontrada");
        }
        maintenanceRepository.deleteById(id);
    }

    private Sort buildSort(String sort) {
        if (sort == null || sort.trim().isEmpty()) {
            return Sort.unsorted();
        }
        String[] parts = sort.split(",", 2);
        String property = parts[0].trim();
        if (property.isEmpty()) {
            return Sort.unsorted();
        }
        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length == 2 && "desc".equalsIgnoreCase(parts[1].trim())) {
            direction = Sort.Direction.DESC;
        }
        return Sort.by(direction, property);
    }
}
