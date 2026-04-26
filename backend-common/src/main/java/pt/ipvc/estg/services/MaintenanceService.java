package pt.ipvc.estg.services;

import pt.ipvc.estg.dal.mock.MaintenanceDAOMock;
import pt.ipvc.estg.dal.mock.MockDataSeeder;
import pt.ipvc.estg.entities.Maintenance;
import pt.ipvc.estg.entities.Aircraft;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Serviço de Maintenance - Lógica de negócio para Manutenções
 */
public class MaintenanceService {
    
    private final MaintenanceDAOMock maintenanceDAO;
    
    public MaintenanceService() {
        MockDataSeeder.seedAllData();
        this.maintenanceDAO = new MaintenanceDAOMock();
    }
    
    public Optional<Maintenance> getManutencao(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        return maintenanceDAO.findById(id);
    }
    
    public List<Maintenance> getAllManutenções() {
        return maintenanceDAO.findAll();
    }
    
    public List<Maintenance> getManutencoesPorAviao(Integer aircraftId) {
        if (aircraftId == null || aircraftId <= 0) {
            throw new IllegalArgumentException("Aircraft ID deve ser válido");
        }
        return maintenanceDAO.findByAircraft(aircraftId);
    }
    
    public List<Maintenance> getManutencoesPorStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status deve ser válido");
        }
        return maintenanceDAO.findByStatus(status);
    }
    
    public List<Maintenance> getManutencoesPorPrioridade(String priority) {
        if (priority == null || priority.trim().isEmpty()) {
            throw new IllegalArgumentException("Prioridade deve ser válida");
        }
        return maintenanceDAO.findByPriority(priority);
    }
    
    public Maintenance criarManutencao(Aircraft aircraft, String maintenanceType, String description) {
        if (aircraft == null) {
            throw new IllegalArgumentException("Avião é obrigatório");
        }
        if (maintenanceType == null || maintenanceType.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo de manutenção é obrigatório");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
        
        Maintenance maintenance = new Maintenance(aircraft, maintenanceType, description);
        return maintenanceDAO.insert(maintenance);
    }
    
    public Maintenance atualizarManutencao(Integer id, String technician, LocalDate estimatedEnd, 
                                          String priority, Double cost, String status) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        
        Optional<Maintenance> opt = maintenanceDAO.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Manutenção não encontrada");
        }
        
        Maintenance maintenance = opt.get();
        
        if (technician != null && !technician.trim().isEmpty()) {
            maintenance.setTechnician(technician);
        }
        if (estimatedEnd != null) {
            maintenance.setEstimatedEndDate(estimatedEnd);
        }
        if (priority != null) {
            maintenance.setPriority(priority);
        }
        if (cost != null && cost >= 0) {
            maintenance.setCost(cost);
        }
        if (status != null) {
            maintenance.setStatus(status);
        }
        
        return maintenanceDAO.update(maintenance);
    }
    
    public void marcarComoConcluida(Integer id, LocalDate actualEndDate) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        
        Optional<Maintenance> opt = maintenanceDAO.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Manutenção não encontrada");
        }
        
        Maintenance maintenance = opt.get();
        maintenance.setStatus("completed");
        maintenance.setActualEndDate(actualEndDate != null ? actualEndDate : LocalDate.now());
        
        maintenanceDAO.update(maintenance);
    }
    
    public void eliminarManutencao(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        if (maintenanceDAO.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Manutenção não encontrada");
        }
        maintenanceDAO.delete(id);
    }
    
    public long contarManutenções() {
        return maintenanceDAO.count();
    }
    
    public List<Maintenance> getManutencoesPendentes() {
        return getManutencoesPorStatus("scheduled");
    }
    
    public Double calcularCustoTotal() {
        return getAllManutenções().stream()
                .mapToDouble(m -> m.getCost() != null ? m.getCost() : 0.0)
                .sum();
    }
}
