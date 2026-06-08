package pt.ipvc.estg.desktop.controllers;

import pt.ipvc.estg.desktop.api.ApiException;
import pt.ipvc.estg.desktop.api.BoDataAccess;
import pt.ipvc.estg.desktop.api.bo.BoApiService;
import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.entities.Maintenance;
import pt.ipvc.estg.services.MaintenanceService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MaintenanceController {

    private final MaintenanceService maintenanceService;
    private final BoApiService boApi;

    public MaintenanceController() {
        this.maintenanceService = new MaintenanceService();
        this.boApi = new BoApiService();
    }

    public List<Maintenance> listarManutenções() {
        if (BoDataAccess.useApi()) {
            return boApi.listMaintenance();
        }
        return maintenanceService.getAllManutencoes();
    }

    public Optional<Maintenance> obterManutencao(Integer id) {
        try {
            if (BoDataAccess.useApi()) {
                return boApi.getMaintenance(id);
            }
            return maintenanceService.getManutencao(id);
        } catch (IllegalArgumentException | ApiException e) {
            System.err.println("Erro ao obter manutencao: " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<Maintenance> obterManutencoesPorAviao(Integer aircraftId) {
        if (BoDataAccess.useApi()) {
            return boApi.listMaintenanceByAircraft(aircraftId);
        }
        return maintenanceService.getManutencoesPorAviao(aircraftId);
    }

    public List<Maintenance> obterManutencoesPorStatus(String status) {
        if (BoDataAccess.useApi()) {
            return boApi.listMaintenanceByStatus(status);
        }
        return maintenanceService.getManutencoesPorStatus(status);
    }

    public List<Maintenance> obterManutencoesPorPrioridade(String priority) {
        if (BoDataAccess.useApi()) {
            return listarManutenções().stream()
                    .filter(m -> priority.equals(m.getPriority()))
                    .toList();
        }
        return maintenanceService.getManutencoesPorPrioridade(priority);
    }

    public void criarManutencao(Aircraft aircraft, String maintenanceType, String description) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.createMaintenance(aircraft, maintenanceType, description);
                return;
            }
            maintenanceService.criarManutencao(aircraft, maintenanceType, description);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao criar manutencao: " + e.getMessage());
        }
    }

    public void atualizarManutencao(Integer id, String technician, LocalDate estimatedEnd, String priority, Double cost, String status) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.updateMaintenance(id, technician, estimatedEnd, priority, cost, status);
                return;
            }
            maintenanceService.atualizarManutencao(id, technician, estimatedEnd, priority, cost, status);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao atualizar manutencao: " + e.getMessage());
        }
    }

    public void marcarComoConcluida(Integer id, LocalDate actualEndDate) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.completeMaintenance(id, actualEndDate);
                return;
            }
            maintenanceService.marcarComoConcluida(id, actualEndDate);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao marcar manutencao como concluida: " + e.getMessage());
        }
    }

    public void eliminarManutencao(Integer id) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.deleteMaintenance(id);
                return;
            }
            maintenanceService.eliminarManutencao(id);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao eliminar manutencao: " + e.getMessage());
        }
    }

    public long obterTotalManutenções() {
        return listarManutenções().size();
    }

    public List<Maintenance> obterManutencoesPendentes() {
        if (BoDataAccess.useApi()) {
            return listarManutenções().stream()
                    .filter(m -> !"completed".equals(m.getStatus()))
                    .toList();
        }
        return maintenanceService.getManutencoesPendentes();
    }

    public Double obterCustoTotal() {
        if (BoDataAccess.useApi()) {
            return listarManutenções().stream()
                    .map(m -> m.getCost() != null ? m.getCost() : 0.0)
                    .reduce(0.0, Double::sum);
        }
        return maintenanceService.calcularCustoTotal();
    }
}
