package pt.ipvc.estg.services;

import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.exception.ConflictException;
import pt.ipvc.estg.exception.EntityNotFoundException;
import pt.ipvc.estg.repositories.AircraftRepository;
import pt.ipvc.estg.validation.BusinessRules;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AircraftService {

    private final AircraftRepository aircraftRepository;

    public AircraftService(AircraftRepository aircraftRepository) {
        this.aircraftRepository = aircraftRepository;
    }

    public AircraftService() {
        this(pt.ipvc.estg.bootstrap.MockServices.getInstance().aircraftRepository());
    }

    public Optional<Aircraft> getAviao(Integer id) {
        validateId(id);
        return aircraftRepository.findById(id);
    }

    public Aircraft requireAviao(Integer id) {
        return getAviao(id).orElseThrow(() -> new EntityNotFoundException("Aviao nao encontrado"));
    }

    public Optional<Aircraft> getAviaoPorMatricula(String registration) {
        if (registration == null || registration.trim().isEmpty()) {
            throw new IllegalArgumentException("Matricula deve ser valida");
        }
        return aircraftRepository.findByRegistration(registration);
    }

    public List<Aircraft> getAllAvioes() {
        return aircraftRepository.findAll();
    }

    public PageResult<Aircraft> listAvioes(PageQuery query, String status) {
        if (status != null && !status.trim().isEmpty()) {
            return aircraftRepository.findByStatus(status, query);
        }
        return aircraftRepository.findAll(query);
    }

    public List<Aircraft> getAvioesPorStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status deve ser valido");
        }
        return aircraftRepository.findByStatus(status);
    }

    public Aircraft criarAviao(String registration, String model, String type) {
        if (registration == null || registration.trim().isEmpty()) {
            throw new IllegalArgumentException("Matricula e obrigatoria");
        }
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Modelo e obrigatorio");
        }
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo e obrigatorio");
        }
        if (aircraftRepository.findByRegistration(registration).isPresent()) {
            throw new ConflictException("Ja existe um aviao com essa matricula");
        }
        Aircraft aircraft = new Aircraft(registration, model, type);
        return aircraftRepository.save(aircraft);
    }

    public Aircraft saveAviao(Aircraft aircraft) {
        validateAircraftFields(aircraft);
        if (aircraft.getRegistration() != null) {
            aircraftRepository.findByRegistration(aircraft.getRegistration())
                    .filter(existing -> aircraft.getId() == null || !existing.getId().equals(aircraft.getId()))
                    .ifPresent(existing -> {
                        throw new ConflictException("Ja existe um aviao com essa matricula");
                    });
        }
        if (aircraft.getStatus() != null) {
            aircraft.setStatus(BusinessRules.requireAllowed("Status", aircraft.getStatus(), BusinessRules.AIRCRAFT_STATUSES));
        }
        return aircraftRepository.save(aircraft);
    }

    public Aircraft updateAviao(Integer id, Aircraft updates) {
        Aircraft aircraft = requireAviao(id);
        validateAircraftFields(updates);
        if (updates.getRegistration() != null && !updates.getRegistration().trim().isEmpty()) {
            aircraftRepository.findByRegistration(updates.getRegistration())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new ConflictException("Ja existe outra aeronave com esta matricula");
                    });
            aircraft.setRegistration(updates.getRegistration());
        }
        if (updates.getModel() != null && !updates.getModel().trim().isEmpty()) aircraft.setModel(updates.getModel());
        if (updates.getType() != null && !updates.getType().trim().isEmpty()) aircraft.setType(updates.getType());
        if (updates.getManufYear() != null) aircraft.setManufYear(updates.getManufYear());
        if (updates.getStatus() != null) {
            aircraft.setStatus(BusinessRules.requireAllowed("Status", updates.getStatus(), BusinessRules.AIRCRAFT_STATUSES));
        }
        if (updates.getFlightHours() != null) aircraft.setFlightHours(updates.getFlightHours());
        if (updates.getLastMaintenance() != null) aircraft.setLastMaintenance(updates.getLastMaintenance());
        if (updates.getNextMaintenance() != null) aircraft.setNextMaintenance(updates.getNextMaintenance());
        if (updates.getLocation() != null) aircraft.setLocation(updates.getLocation());
        if (updates.getFuelLevel() != null) aircraft.setFuelLevel(updates.getFuelLevel());
        if (updates.getNotes() != null) aircraft.setNotes(updates.getNotes());
        return aircraftRepository.save(aircraft);
    }

    private void validateAircraftFields(Aircraft aircraft) {
        BusinessRules.requirePositiveOrZero("Horas de voo", aircraft.getFlightHours());
        BusinessRules.requirePercent("Nivel de combustivel", aircraft.getFuelLevel());
    }

    public Aircraft atualizarAviao(Integer id, String model, Integer year,
                                   String location, Integer fuelLevel) {
        Aircraft aircraft = requireAviao(id);
        if (model != null && !model.trim().isEmpty()) aircraft.setModel(model);
        if (year != null && year > 0) aircraft.setManufYear(year);
        if (location != null) aircraft.setLocation(location);
        if (fuelLevel != null && fuelLevel >= 0 && fuelLevel <= 100) aircraft.setFuelLevel(fuelLevel);
        return aircraftRepository.save(aircraft);
    }

    public void atualizarStatus(Integer id, String status) {
        Aircraft aircraft = requireAviao(id);
        aircraft.setStatus(BusinessRules.requireAllowed("Status", status, BusinessRules.AIRCRAFT_STATUSES));
        aircraftRepository.save(aircraft);
    }

    public void atualizarManutencao(Integer id, LocalDate nextMaintenance) {
        Aircraft aircraft = requireAviao(id);
        aircraft.setLastMaintenance(LocalDate.now());
        if (nextMaintenance != null) aircraft.setNextMaintenance(nextMaintenance);
        aircraftRepository.save(aircraft);
    }

    public void eliminarAviao(Integer id) {
        validateId(id);
        if (!aircraftRepository.existsById(id)) {
            throw new EntityNotFoundException("Aviao nao encontrado");
        }
        aircraftRepository.deleteById(id);
    }

    public long contarAvioes() {
        return aircraftRepository.count();
    }

    public long contarAvioesOperacionais() {
        return getAllAvioes().stream().filter(a -> "operational".equals(a.getStatus())).count();
    }

    private static void validateId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser valido");
        }
    }
}
