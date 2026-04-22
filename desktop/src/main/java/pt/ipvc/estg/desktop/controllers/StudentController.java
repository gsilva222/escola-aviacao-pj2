package pt.ipvc.estg.desktop.controllers;

import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.services.StudentService;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controller para gerenciar Estudantes na interface Desktop
 */
public class StudentController {
    
    private final StudentService studentService;
    
    public StudentController() {
        this.studentService = new StudentService();
    }
    
    public List<Student> listarEstudantes() {
        return studentService.getAllEstudantes();
    }
    
    public Optional<Student> obterEstudante(Integer id) {
        try {
            return studentService.getEstudante(id);
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao obter estudante: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    public List<Student> obterEstudantesPorCurso(Integer courseId) {
        return studentService.getEstudantesPorCurso(courseId);
    }
    
    public List<Student> obterEstudantesPorStatus(String status) {
        return studentService.getEstudantesPorStatus(status);
    }
    
    public void criarEstudante(String name, String email, Course course) {
        try {
            Student student = studentService.criarEstudante(name, email, course);
            System.out.println("Estudante criado com sucesso: " + student);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao criar estudante: " + e.getMessage());
        }
    }
    
    public void atualizarEstudante(Integer id, String name, String email, String phone, String nif, LocalDate birthdate) {
        try {
            Student student = studentService.atualizarEstudante(id, name, email, phone, nif, birthdate);
            System.out.println("Estudante atualizado com sucesso: " + student);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao atualizar estudante: " + e.getMessage());
        }
    }
    
    public void atualizarProgresso(Integer id, Integer progress) {
        try {
            studentService.atualizarProgresso(id, progress);
            System.out.println("Progresso atualizado com sucesso");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao atualizar progresso: " + e.getMessage());
        }
    }
    
    public void atualizarStatus(Integer id, String status) {
        try {
            studentService.atualizarStatus(id, status);
            System.out.println("Status atualizado com sucesso");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao atualizar status: " + e.getMessage());
        }
    }
    
    public void eliminarEstudante(Integer id) {
        try {
            studentService.eliminarEstudante(id);
            System.out.println("Estudante eliminado com sucesso");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao eliminar estudante: " + e.getMessage());
        }
    }
    
    public long obterTotalEstudantes() {
        return studentService.contarEstudantes();
    }
}
