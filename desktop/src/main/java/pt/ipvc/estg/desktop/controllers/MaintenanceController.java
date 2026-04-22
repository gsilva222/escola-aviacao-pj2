package pt.ipvc.estg.desktop.controllers;

import pt.ipvc.estg.entities.Maintenance;
import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.services.MaintenanceService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller para gerenciar Manutenções na interface Desktop
 */
public class MaintenanceController {
    
    private final MaintenanceService maintenanceService;
    
    public MaintenanceController() {
        this.maintenanceService = new MaintenanceService();
    }
    
    public List<Maintenance> listarManutenções() {
        return maintenanceService.getAllManutenções();
    }
    
    public Optional<Maintenance> obterManutencao(Integer id) {
        try {
            return maintenanceService.getManutencao(id);
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao obter manutenção: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    public List<Maintenance> obterManutencoesPorAviao(Integer aircraftId) {
        return maintenanceService.getManutencoesPorAviao(aircraftId);
    }
    
    public List<Maintenance> obterManutencoesPorStatus(String status) {
        return maintenanceService.getManutencoesPorStatus(status);
    }
    
    public List<Maintenance> obterManutencoesPorPrioridade(String priority) {
        return maintenanceService.getManutencoesPorPrioridade(priority);
    }
    
    public void criarManutencao(Aircraft aircraft, String maintenanceType, String description) {
        try {
            Maintenance maintenance = maintenanceService.criarManutencao(aircraft, maintenanceType, description);
            System.out.println("Manutenção criada com sucesso: " + maintenance);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao criar manutenção: " + e.getMessage());
        }
    }
    
    public void atualizarManutencao(Integer id, String technician, LocalDate estimatedEnd, String priority, Double cost, String status) {
        try {
            Maintenance maintenance = maintenanceService.atualizarManutencao(id, technician, estimatedEnd, priority, cost, status);
            System.out.println("Manutenção atualizada com sucesso: " + maintenance);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao atualizar manutenção: " + e.getMessage());
        }
    }
    
    public void marcarComoConcluida(Integer id, LocalDate actualEndDate) {
        try {
            maintenanceService.marcarComoConcluida(id, actualEndDate);
            System.out.println("Manutenção marcada como concluída com sucesso");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao marcar manutenção como concluída: " + e.getMessage());
        }
    }
    
    public void eliminarManutencao(Integer id) {
        try {
            maintenanceService.eliminarManutencao(id);
            System.out.println("Manutenção eliminada com sucesso");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao eliminar manutenção: " + e.getMessage());
        }
    }
    
    public long obterTotalManutenções() {
        return maintenanceService.contarManutenções();
    }
    
    public List<Maintenance> obterManutencoesPendentes() {
        return maintenanceService.getManutencoesPendentes();
    }
    
    public Double obterCustoTotal() {
        return maintenanceService.calcularCustoTotal();
    }
}
