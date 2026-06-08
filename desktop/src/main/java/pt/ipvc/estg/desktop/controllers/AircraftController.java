package pt.ipvc.estg.desktop.controllers;

import pt.ipvc.estg.desktop.api.ApiException;
import pt.ipvc.estg.desktop.api.BoDataAccess;
import pt.ipvc.estg.desktop.api.bo.BoApiService;
import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.services.AircraftService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AircraftController {

    private final AircraftService aircraftService;
    private final BoApiService boApi;

    public AircraftController() {
        this.aircraftService = new AircraftService();
        this.boApi = new BoApiService();
    }

    public List<Aircraft> listarAvioes() {
        if (BoDataAccess.useApi()) {
            return boApi.listAircraft();
        }
        return aircraftService.getAllAvioes();
    }

    public Optional<Aircraft> obterAviao(Integer id) {
        try {
            if (BoDataAccess.useApi()) {
                return boApi.getAircraft(id);
            }
            return aircraftService.getAviao(id);
        } catch (IllegalArgumentException | ApiException e) {
            System.err.println("Erro ao obter aviao: " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<Aircraft> obterAvioesPorStatus(String status) {
        if (BoDataAccess.useApi()) {
            return boApi.listAircraftByStatus(status);
        }
        return aircraftService.getAvioesPorStatus(status);
    }

    public void criarAviao(String registration, String model, String type) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.createAircraft(registration, model, type);
                return;
            }
            aircraftService.criarAviao(registration, model, type);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao criar aviao: " + e.getMessage());
        }
    }

    public void atualizarAviao(Integer id, String model, Integer year, String location, Integer fuelLevel) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.updateAircraft(id, model, year, location, fuelLevel);
                return;
            }
            aircraftService.atualizarAviao(id, model, year, location, fuelLevel);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao atualizar aviao: " + e.getMessage());
        }
    }

    public void atualizarStatus(Integer id, String status) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.updateAircraftStatus(id, status);
                return;
            }
            aircraftService.atualizarStatus(id, status);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao atualizar status: " + e.getMessage());
        }
    }

    public void atualizarManutenção(Integer id, LocalDate nextMaintenance) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.updateAircraftMaintenance(id, nextMaintenance);
                return;
            }
            aircraftService.atualizarManutencao(id, nextMaintenance);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao atualizar manutencao: " + e.getMessage());
        }
    }

    public void eliminarAviao(Integer id) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.deleteAircraft(id);
                return;
            }
            aircraftService.eliminarAviao(id);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao eliminar aviao: " + e.getMessage());
        }
    }

    public long obterTotalAvioes() {
        return listarAvioes().size();
    }

    public long obterTotalAviõesOperacionais() {
        if (BoDataAccess.useApi()) {
            return boApi.listAircraftByStatus("operational").size();
        }
        return aircraftService.contarAvioesOperacionais();
    }
}
