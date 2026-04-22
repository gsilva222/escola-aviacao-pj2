package pt.ipvc.estg.desktop.controllers;

import pt.ipvc.estg.entities.Instructor;
import pt.ipvc.estg.services.InstructorService;
import java.util.List;
import java.util.Optional;

/**
 * Controller para gerenciar Instrutores na interface Desktop
 */
public class InstructorController {
    
    private final InstructorService instructorService;
    
    public InstructorController() {
        this.instructorService = new InstructorService();
    }
    
    public List<Instructor> listarInstrutores() {
        return instructorService.getAllIntrutores();
    }
    
    public Optional<Instructor> obterInstrutor(Integer id) {
        try {
            return instructorService.getInstrutor(id);
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao obter instrutor: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    public List<Instructor> obterIntrutoresPorStatus(String status) {
        return instructorService.getIntrutoresPorStatus(status);
    }
    
    public void criarInstrutor(String name, String license, String specialization) {
        try {
            Instructor instructor = instructorService.criarInstrutor(name, license, specialization);
            System.out.println("Instrutor criado com sucesso: " + instructor);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao criar instrutor: " + e.getMessage());
        }
    }
    
    public void atualizarInstrutor(Integer id, String name, String license, String specialization, String email, String phone) {
        try {
            Instructor instructor = instructorService.atualizarInstrutor(id, name, license, specialization, email, phone);
            System.out.println("Instrutor atualizado com sucesso: " + instructor);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao atualizar instrutor: " + e.getMessage());
        }
    }
    
    public void atualizarStatus(Integer id, String status) {
        try {
            instructorService.atualizarStatus(id, status);
            System.out.println("Status atualizado com sucesso");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao atualizar status: " + e.getMessage());
        }
    }
    
    public void eliminarInstrutor(Integer id) {
        try {
            instructorService.eliminarInstrutor(id);
            System.out.println("Instrutor eliminado com sucesso");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao eliminar instrutor: " + e.getMessage());
        }
    }
    
    public long obterTotalInstrutores() {
        return instructorService.contarIntrutores();
    }
}
