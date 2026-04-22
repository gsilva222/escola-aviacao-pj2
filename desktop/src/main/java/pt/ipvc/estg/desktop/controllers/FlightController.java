package pt.ipvc.estg.desktop.controllers;

import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.Instructor;
import pt.ipvc.estg.entities.Aircraft;
import pt.ipvc.estg.services.FlightService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller para gerenciar Voos na interface Desktop
 */
public class FlightController {
    
    private final FlightService flightService;
    
    public FlightController() {
        this.flightService = new FlightService();
    }
    
    public List<Flight> listarVoos() {
        return flightService.getAllVoos();
    }
    
    public Optional<Flight> obterVoo(Integer id) {
        try {
            return flightService.getVoo(id);
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao obter voo: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    public List<Flight> obterVoosPorEstudante(Integer studentId) {
        return flightService.getVoosPorEstudante(studentId);
    }
    
    public List<Flight> obterVoosPorInstrutor(Integer instructorId) {
        return flightService.getVoosPorInstrutor(instructorId);
    }
    
    public List<Flight> obterVoosPorStatus(String status) {
        return flightService.getVoosPorStatus(status);
    }
    
    public void criarVoo(LocalDate flightDate, Student student, Instructor instructor, Aircraft aircraft) {
        try {
            Flight flight = flightService.criarVoo(flightDate, student, instructor, aircraft);
            System.out.println("Voo criado com sucesso: " + flight);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao criar voo: " + e.getMessage());
        }
    }
    
    public void atualizarVoo(Integer id, Double duration, String origin, String destination, String flightType, String objectives, String grade) {
        try {
            Flight flight = flightService.atualizarVoo(id, duration, origin, destination, flightType, objectives, grade);
            System.out.println("Voo atualizado com sucesso: " + flight);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao atualizar voo: " + e.getMessage());
        }
    }
    
    public void marcarComoConcluido(Integer id, Double duration, String grade) {
        try {
            flightService.marcarComoConcluido(id, duration, grade);
            System.out.println("Voo marcado como concluído com sucesso");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao marcar voo como concluído: " + e.getMessage());
        }
    }
    
    public void eliminarVoo(Integer id) {
        try {
            flightService.eliminarVoo(id);
            System.out.println("Voo eliminado com sucesso");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao eliminar voo: " + e.getMessage());
        }
    }
    
    public long obterTotalVoos() {
        return flightService.contarVoos();
    }
}
