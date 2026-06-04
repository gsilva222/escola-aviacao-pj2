package pt.ipvc.estg.web.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.web.dto.AircraftRequest;
import pt.ipvc.estg.web.dto.AircraftResponse;
import pt.ipvc.estg.web.mappers.AircraftMapper;
import pt.ipvc.estg.web.repositories.AircraftRepository;
import pt.ipvc.estg.web.validation.BusinessRules;

import java.util.List;

@RestController
@RequestMapping("/bo/aircraft")
public class AircraftController {

    private final AircraftRepository aircraftRepository;

    public AircraftController(AircraftRepository aircraftRepository) {
        this.aircraftRepository = aircraftRepository;
    }

    @GetMapping
    public Page<AircraftResponse> getAircraft(@RequestParam(required = false) String status,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size,
                                              @RequestParam(required = false) String sort) {
        PageRequest pageable = PageRequest.of(page, size, buildSort(sort));
        if (status != null && !status.trim().isEmpty()) {
            List<AircraftResponse> content = aircraftRepository.findByStatus(status, pageable)
                    .stream().map(AircraftMapper::toResponse).toList();
            long total = aircraftRepository.findByStatus(status).size();
            return new PageImpl<>(content, pageable, total);
        }
        return aircraftRepository.findAll(pageable).map(AircraftMapper::toResponse);
    }

    @GetMapping("/{id}")
    public AircraftResponse getAircraftById(@PathVariable("id") Integer id) {
        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Aeronave nao encontrada"));
        return AircraftMapper.toResponse(aircraft);
    }

    @PostMapping
    public AircraftResponse createAircraft(@Valid @RequestBody AircraftRequest request) {
        BusinessRules.requirePositiveOrZero("Horas de voo", request.flightHours());
        BusinessRules.requirePercent("Nivel de combustivel", request.fuelLevel());
        aircraftRepository.findByRegistrationIgnoreCase(request.registration())
                .ifPresent(existing -> {
                    throw new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Ja existe aeronave com esta matricula");
                });

        Aircraft aircraft = new Aircraft(request.registration(), request.model(), request.type());
        if (request.manufYear() != null) aircraft.setManufYear(request.manufYear());
        if (request.status() != null) aircraft.setStatus(BusinessRules.requireAllowed("Status", request.status(), BusinessRules.AIRCRAFT_STATUSES));
        if (request.flightHours() != null) aircraft.setFlightHours(request.flightHours());
        if (request.lastMaintenance() != null) aircraft.setLastMaintenance(request.lastMaintenance());
        if (request.nextMaintenance() != null) aircraft.setNextMaintenance(request.nextMaintenance());
        if (request.location() != null) aircraft.setLocation(request.location());
        if (request.fuelLevel() != null) aircraft.setFuelLevel(request.fuelLevel());
        if (request.notes() != null) aircraft.setNotes(request.notes());

        Aircraft created = aircraftRepository.save(aircraft);
        return AircraftMapper.toResponse(created);
    }

    @PutMapping("/{id}")
    public AircraftResponse updateAircraft(@PathVariable("id") Integer id, @RequestBody AircraftRequest request) {
        BusinessRules.requirePositiveOrZero("Horas de voo", request.flightHours());
        BusinessRules.requirePercent("Nivel de combustivel", request.fuelLevel());
        Aircraft aircraft = aircraftRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Aeronave nao encontrada"));

        if (request.registration() != null && !request.registration().trim().isEmpty()) {
            aircraftRepository.findByRegistrationIgnoreCase(request.registration())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Ja existe outra aeronave com esta matricula");
                    });
            aircraft.setRegistration(request.registration());
        }
        if (request.model() != null && !request.model().trim().isEmpty()) aircraft.setModel(request.model());
        if (request.type() != null && !request.type().trim().isEmpty()) aircraft.setType(request.type());
        if (request.manufYear() != null) aircraft.setManufYear(request.manufYear());
        if (request.status() != null) aircraft.setStatus(BusinessRules.requireAllowed("Status", request.status(), BusinessRules.AIRCRAFT_STATUSES));
        if (request.flightHours() != null) aircraft.setFlightHours(request.flightHours());
        if (request.lastMaintenance() != null) aircraft.setLastMaintenance(request.lastMaintenance());
        if (request.nextMaintenance() != null) aircraft.setNextMaintenance(request.nextMaintenance());
        if (request.location() != null) aircraft.setLocation(request.location());
        if (request.fuelLevel() != null) aircraft.setFuelLevel(request.fuelLevel());
        if (request.notes() != null) aircraft.setNotes(request.notes());

        Aircraft updated = aircraftRepository.save(aircraft);
        return AircraftMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteAircraft(@PathVariable("id") Integer id) {
        if (!aircraftRepository.existsById(id)) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Aeronave nao encontrada");
        }
        aircraftRepository.deleteById(id);
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
