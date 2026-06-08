package pt.ipvc.estg.desktop.controllers;

import pt.ipvc.estg.desktop.api.ApiException;
import pt.ipvc.estg.desktop.api.BoDataAccess;
import pt.ipvc.estg.desktop.api.bo.BoApiService;
import pt.ipvc.estg.entities.Instructor;
import pt.ipvc.estg.services.InstructorService;

import java.util.List;
import java.util.Optional;

public class InstructorController {

    private final InstructorService instructorService;
    private final BoApiService boApi;

    public InstructorController() {
        this.instructorService = new InstructorService();
        this.boApi = new BoApiService();
    }

    public List<Instructor> listarInstrutores() {
        if (BoDataAccess.useApi()) {
            return boApi.listInstructors();
        }
        return instructorService.getAllIntrutores();
    }

    public Optional<Instructor> obterInstrutor(Integer id) {
        try {
            if (BoDataAccess.useApi()) {
                return boApi.getInstructor(id);
            }
            return instructorService.getInstrutor(id);
        } catch (IllegalArgumentException | ApiException e) {
            System.err.println("Erro ao obter instrutor: " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<Instructor> obterIntrutoresPorStatus(String status) {
        if (BoDataAccess.useApi()) {
            return boApi.listInstructorsByStatus(status);
        }
        return instructorService.getIntrutoresPorStatus(status);
    }

    public void criarInstrutor(String name, String license, String specialization) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.createInstructor(name, license, specialization);
                return;
            }
            instructorService.criarInstrutor(name, license, specialization);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao criar instrutor: " + e.getMessage());
        }
    }

    public void atualizarInstrutor(Integer id, String name, String license, String specialization, String email, String phone) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.updateInstructor(id, name, license, specialization, email, phone);
                return;
            }
            instructorService.atualizarInstrutor(id, name, license, specialization, email, phone);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao atualizar instrutor: " + e.getMessage());
        }
    }

    public void atualizarStatus(Integer id, String status) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.updateInstructorStatus(id, status);
                return;
            }
            instructorService.atualizarStatus(id, status);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao atualizar status: " + e.getMessage());
        }
    }

    public void eliminarInstrutor(Integer id) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.deleteInstructor(id);
                return;
            }
            instructorService.eliminarInstrutor(id);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao eliminar instrutor: " + e.getMessage());
        }
    }

    public long obterTotalInstrutores() {
        return listarInstrutores().size();
    }
}
