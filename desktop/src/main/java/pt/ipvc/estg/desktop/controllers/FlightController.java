package pt.ipvc.estg.desktop.controllers;

import pt.ipvc.estg.desktop.api.ApiException;
import pt.ipvc.estg.desktop.api.BoDataAccess;
import pt.ipvc.estg.desktop.api.FoDataAccess;
import pt.ipvc.estg.desktop.api.bo.BoApiService;
import pt.ipvc.estg.desktop.services.FoStudentService;
import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Instructor;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.services.FlightService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class FlightController {

    private final FlightService flightService;
    private final BoApiService boApi;
    private final FoStudentService foStudentService;

    public FlightController() {
        this.flightService = new FlightService();
        this.boApi = new BoApiService();
        this.foStudentService = new FoStudentService();
    }

    public List<Flight> listarVoos() {
        if (BoDataAccess.useApi()) {
            return boApi.listFlights();
        }
        return flightService.getAllVoos();
    }

    public Optional<Flight> obterVoo(Integer id) {
        try {
            if (BoDataAccess.useApi()) {
                return boApi.getFlight(id);
            }
            return flightService.getVoo(id);
        } catch (IllegalArgumentException | ApiException e) {
            System.err.println("Erro ao obter voo: " + e.getMessage());
            return Optional.empty();
        }
    }

    public List<Flight> obterVoosPorEstudante(Integer studentId) {
        if (FoDataAccess.useApi()) {
            return foStudentService.myFlights();
        }
        if (BoDataAccess.useApi()) {
            return boApi.listFlightsByStudent(studentId);
        }
        return flightService.getVoosPorEstudante(studentId);
    }

    public List<Flight> obterAgendaEstudante(Integer studentId) {
        if (FoDataAccess.useApi()) {
            return foStudentService.mySchedule();
        }
        return flightService.getVoosPorEstudante(studentId).stream()
                .filter(f -> "scheduled".equalsIgnoreCase(f.getStatus()))
                .toList();
    }

    public List<Flight> obterVoosPorInstrutor(Integer instructorId) {
        if (BoDataAccess.useApi()) {
            return boApi.listFlightsByInstructor(instructorId);
        }
        return flightService.getVoosPorInstrutor(instructorId);
    }

    public List<Flight> obterVoosPorStatus(String status) {
        if (BoDataAccess.useApi()) {
            return boApi.listFlightsByStatus(status);
        }
        return flightService.getVoosPorStatus(status);
    }

    public void criarVoo(LocalDate flightDate, Student student, Instructor instructor, Aircraft aircraft) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.createFlight(flightDate, student, instructor, aircraft);
                return;
            }
            flightService.criarVoo(flightDate, student, instructor, aircraft);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao criar voo: " + e.getMessage());
        }
    }

    public void atualizarVoo(Integer id, Double duration, String origin, String destination, String flightType, String objectives, String grade) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.updateFlight(id, duration, origin, destination, flightType, objectives, grade);
                return;
            }
            flightService.atualizarVoo(id, duration, origin, destination, flightType, objectives, grade);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao atualizar voo: " + e.getMessage());
        }
    }

    public void marcarComoConcluido(Integer id, Double duration, String grade) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.completeFlight(id, duration, grade);
                return;
            }
            flightService.marcarComoConcluido(id, duration, grade);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao marcar voo como concluido: " + e.getMessage());
        }
    }

    public void eliminarVoo(Integer id) {
        try {
            if (BoDataAccess.useApi()) {
                boApi.deleteFlight(id);
                return;
            }
            flightService.eliminarVoo(id);
        } catch (IllegalArgumentException | ApiException e) {
            throw new RuntimeException("Erro ao eliminar voo: " + e.getMessage());
        }
    }

    public long obterTotalVoos() {
        return listarVoos().size();
    }
}
