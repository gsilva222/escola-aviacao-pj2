package pt.ipvc.estg.services;

import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.entities.Maintenance;
import pt.ipvc.estg.exception.EntityNotFoundException;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.repositories.FlightRepository;
import pt.ipvc.estg.repositories.AircraftRepository;
import pt.ipvc.estg.repositories.MaintenanceRepository;
import pt.ipvc.estg.validation.BusinessRules;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final AircraftRepository aircraftRepository;
    private final FlightRepository flightRepository;

    public MaintenanceService(MaintenanceRepository maintenanceRepository,
                              AircraftRepository aircraftRepository,
                              FlightRepository flightRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.aircraftRepository = aircraftRepository;
        this.flightRepository = flightRepository;
    }

    public MaintenanceService() {
        this(pt.ipvc.estg.bootstrap.MockServices.getInstance().maintenanceRepository(),
                pt.ipvc.estg.bootstrap.MockServices.getInstance().aircraftRepository(),
                pt.ipvc.estg.bootstrap.MockServices.getInstance().flightRepository());
    }

    public Optional<Maintenance> getManutencao(Integer id) {
        validateId(id);
        return maintenanceRepository.findById(id);
    }

    public Maintenance requireManutencao(Integer id) {
        return getManutencao(id).orElseThrow(() -> new EntityNotFoundException("Manutencao nao encontrada"));
    }

    public List<Maintenance> getAllManutencoes() {
        return maintenanceRepository.findAll();
    }

    public PageResult<Maintenance> listManutencoes(PageQuery query, Integer aircraftId, String status, String priority) {
        if (aircraftId != null) {
            return maintenanceRepository.findByAircraft(aircraftId, query);
        }
        if (status != null && !status.trim().isEmpty()) {
            return maintenanceRepository.findByStatus(status, query);
        }
        if (priority != null && !priority.trim().isEmpty()) {
            return maintenanceRepository.findByPriority(priority, query);
        }
        return maintenanceRepository.findAll(query);
    }

    public List<Maintenance> getManutencoesPorAviao(Integer aircraftId) {
        validateId(aircraftId);
        return maintenanceRepository.findByAircraft(aircraftId);
    }

    public List<Maintenance> getManutencoesPorStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status deve ser valido");
        }
        return maintenanceRepository.findByStatus(status);
    }

    public List<Maintenance> getManutencoesPorPrioridade(String priority) {
        if (priority == null || priority.trim().isEmpty()) {
            throw new IllegalArgumentException("Prioridade deve ser valida");
        }
        return maintenanceRepository.findByPriority(priority);
    }

    public Maintenance criarManutencao(Aircraft aircraft, String maintenanceType, String description) {
        if (aircraft == null) throw new IllegalArgumentException("Aviao e obrigatorio");
        if (maintenanceType == null || maintenanceType.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo de manutencao e obrigatorio");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Descricao e obrigatoria");
        }
        Maintenance maintenance = new Maintenance(aircraft, maintenanceType, description);
        return maintenanceRepository.save(maintenance);
    }

    public Maintenance saveManutencao(Maintenance maintenance) {
        validateMaintenanceFields(maintenance);
        resolveAircraft(maintenance);
        if (maintenance.getStatus() != null) {
            maintenance.setStatus(BusinessRules.requireAllowed("Status", maintenance.getStatus(), BusinessRules.MAINTENANCE_STATUSES));
        }
        if (maintenance.getPriority() != null) {
            maintenance.setPriority(BusinessRules.requireAllowed("Prioridade", maintenance.getPriority(), BusinessRules.MAINTENANCE_PRIORITIES));
        }
        validateMaintenanceDoesNotCoverScheduledFlights(maintenance);
        return maintenanceRepository.save(maintenance);
    }

    public Maintenance updateManutencao(Integer id, Maintenance updates) {
        Maintenance maintenance = requireManutencao(id);
        validateMaintenanceFields(updates, maintenance);
        if (updates.getMaintenanceType() != null && !updates.getMaintenanceType().trim().isEmpty()) {
            maintenance.setMaintenanceType(updates.getMaintenanceType());
        }
        if (updates.getDescription() != null && !updates.getDescription().trim().isEmpty()) {
            maintenance.setDescription(updates.getDescription());
        }
        if (updates.getTechnician() != null) maintenance.setTechnician(updates.getTechnician());
        if (updates.getStartDate() != null) maintenance.setStartDate(updates.getStartDate());
        if (updates.getEstimatedEndDate() != null) maintenance.setEstimatedEndDate(updates.getEstimatedEndDate());
        if (updates.getActualEndDate() != null) maintenance.setActualEndDate(updates.getActualEndDate());
        if (updates.getStatus() != null) {
            maintenance.setStatus(BusinessRules.requireAllowed("Status", updates.getStatus(), BusinessRules.MAINTENANCE_STATUSES));
        }
        if (updates.getPriority() != null) {
            maintenance.setPriority(BusinessRules.requireAllowed("Prioridade", updates.getPriority(), BusinessRules.MAINTENANCE_PRIORITIES));
        }
        if (updates.getCost() != null) maintenance.setCost(updates.getCost());
        if (updates.getNotes() != null) maintenance.setNotes(updates.getNotes());
        if (updates.getAircraft() != null && updates.getAircraft().getId() != null) {
            resolveAircraftUpdate(maintenance, updates.getAircraft().getId());
        }
        validateMaintenanceDoesNotCoverScheduledFlights(maintenance);
        return maintenanceRepository.save(maintenance);
    }

    public Maintenance atualizarManutencao(Integer id, String technician, LocalDate estimatedEnd,
                                           String priority, Double cost, String status) {
        Maintenance maintenance = requireManutencao(id);
        if (technician != null && !technician.trim().isEmpty()) maintenance.setTechnician(technician);
        if (estimatedEnd != null) maintenance.setEstimatedEndDate(estimatedEnd);
        if (priority != null) maintenance.setPriority(priority);
        if (cost != null && cost >= 0) maintenance.setCost(cost);
        if (status != null) maintenance.setStatus(status);
        validateMaintenanceDoesNotCoverScheduledFlights(maintenance);
        return maintenanceRepository.save(maintenance);
    }

    public void marcarComoConcluida(Integer id, LocalDate actualEndDate) {
        Maintenance maintenance = requireManutencao(id);
        maintenance.setStatus("completed");
        maintenance.setActualEndDate(actualEndDate != null ? actualEndDate : LocalDate.now());
        maintenanceRepository.save(maintenance);
    }

    public void eliminarManutencao(Integer id) {
        validateId(id);
        if (!maintenanceRepository.existsById(id)) {
            throw new EntityNotFoundException("Manutencao nao encontrada");
        }
        maintenanceRepository.deleteById(id);
    }

    public long contarManutencoes() {
        return maintenanceRepository.count();
    }

    public List<Maintenance> getManutencoesPendentes() {
        return getManutencoesPorStatus("scheduled");
    }

    public Double calcularCustoTotal() {
        return getAllManutencoes().stream()
                .mapToDouble(m -> m.getCost() != null ? m.getCost() : 0.0)
                .sum();
    }

    private void validateMaintenanceFields(Maintenance maintenance) {
        validateMaintenanceFields(maintenance, maintenance);
    }

    private void validateMaintenanceFields(Maintenance source, Maintenance current) {
        BusinessRules.requirePositiveOrZero("Custo", source.getCost());
        LocalDate start = source.getStartDate() != null ? source.getStartDate() : current.getStartDate();
        LocalDate estimatedEnd = source.getEstimatedEndDate() != null ? source.getEstimatedEndDate() : current.getEstimatedEndDate();
        LocalDate actualEnd = source.getActualEndDate() != null ? source.getActualEndDate() : current.getActualEndDate();
        BusinessRules.validateDateOrder("Data estimada de fim", start, estimatedEnd);
        BusinessRules.validateDateOrder("Data real de fim", start, actualEnd);
    }

    private void resolveAircraft(Maintenance maintenance) {
        if (maintenance.getAircraft() != null && maintenance.getAircraft().getId() != null) {
            resolveAircraftUpdate(maintenance, maintenance.getAircraft().getId());
        }
    }

    private void resolveAircraftUpdate(Maintenance maintenance, Integer aircraftId) {
        Aircraft aircraft = aircraftRepository.findById(aircraftId)
                .orElseThrow(() -> new EntityNotFoundException("Aeronave nao encontrada"));
        maintenance.setAircraft(aircraft);
    }

    private static void validateId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser valido");
        }
    }

    private void validateMaintenanceDoesNotCoverScheduledFlights(Maintenance maintenance) {
        if (maintenance == null) return;
        if (maintenance.getAircraft() == null || maintenance.getAircraft().getId() == null) return;
        if (maintenance.getStatus() == null) return;
        if ("completed".equalsIgnoreCase(maintenance.getStatus())) {
            return;
        }
        if (maintenance.getEstimatedEndDate() == null) {
            // Para validar conflitos precisamos pelo menos da data estimada de fim.
            throw new IllegalArgumentException("Manutencao em curso precisa de estimatedEndDate");
        }

        LocalDate start = maintenance.getStartDate() != null ? maintenance.getStartDate() : LocalDate.MIN;
        LocalDate end = maintenance.getEstimatedEndDate();

        Integer aircraftId = maintenance.getAircraft().getId();

        // Regra: não permitir que a manutenção (por datas) “cubra” voos já agendados dessa aeronave.
        for (Flight f : flightRepository.findAll()) {
            if (f == null || f.getId() == null) continue;
            if (!"scheduled".equalsIgnoreCase(f.getStatus())) continue;
            if (f.getAircraft() == null || f.getAircraft().getId() == null) continue;
            if (!f.getAircraft().getId().equals(aircraftId)) continue;
            if (f.getFlightDate() == null) continue;

            boolean covers = !f.getFlightDate().isBefore(start) && !f.getFlightDate().isAfter(end);
            if (covers) {
                throw new IllegalArgumentException("Manutencao em vigor ate " + end + " conflita com um voo agendado");
            }
        }
    }
}
