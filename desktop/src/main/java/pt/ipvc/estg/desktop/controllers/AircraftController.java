package pt.ipvc.estg.desktop.controllers;

import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.services.AircraftService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller para gerenciar Aviões na interface Desktop
 */
public class AircraftController {
    
    private final AircraftService aircraftService;
    
    public AircraftController() {
        this.aircraftService = new AircraftService();
    }
    
    public List<Aircraft> listarAvioes() {
        return aircraftService.getAllAvioes();
    }
    
    public Optional<Aircraft> obterAviao(Integer id) {
        try {
            return aircraftService.getAviao(id);
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao obter avião: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    public List<Aircraft> obterAvioesPorStatus(String status) {
        return aircraftService.getAvioesPorStatus(status);
    }
    
    public void criarAviao(String registration, String model, String type) {
        try {
            Aircraft aircraft = aircraftService.criarAviao(registration, model, type);
            System.out.println("Avião criado com sucesso: " + aircraft);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao criar avião: " + e.getMessage());
        }
    }
    
    public void atualizarAviao(Integer id, String model, Integer year, String location, Integer fuelLevel) {
        try {
            Aircraft aircraft = aircraftService.atualizarAviao(id, model, year, location, fuelLevel);
            System.out.println("Avião atualizado com sucesso: " + aircraft);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao atualizar avião: " + e.getMessage());
        }
    }
    
    public void atualizarStatus(Integer id, String status) {
        try {
            aircraftService.atualizarStatus(id, status);
            System.out.println("Status atualizado com sucesso");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao atualizar status: " + e.getMessage());
        }
    }
    
    public void atualizarManutenção(Integer id, LocalDate nextMaintenance) {
        try {
            aircraftService.atualizarManutenção(id, nextMaintenance);
            System.out.println("Manutenção atualizada com sucesso");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao atualizar manutenção: " + e.getMessage());
        }
    }
    
    public void eliminarAviao(Integer id) {
        try {
            aircraftService.eliminarAviao(id);
            System.out.println("Avião eliminado com sucesso");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao eliminar avião: " + e.getMessage());
        }
    }
    
    public long obterTotalAvioes() {
        return aircraftService.contarAvioes();
    }
    
    public long obterTotalAviõesOperacionais() {
        return aircraftService.contarAviõesOperacionais();
    }
}
