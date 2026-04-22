package pt.ipvc.estg.desktop.controllers;

import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.services.CourseService;
import java.util.List;
import java.util.Optional;

/**
 * Controller para gerenciar Cursos na interface Desktop
 */
public class CourseController {
    
    private final CourseService courseService;
    
    public CourseController() {
        this.courseService = new CourseService();
    }
    
    public List<Course> listarCursos() {
        return courseService.getAllCursos();
    }
    
    public Optional<Course> obterCurso(Integer id) {
        try {
            return courseService.getCurso(id);
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao obter curso: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    public void criarCurso(String name, String duration, Integer flightHours, Integer theoreticalHours, Double price) {
        try {
            Course course = courseService.criarCurso(name, duration, flightHours, theoreticalHours, price);
            System.out.println("Curso criado com sucesso: " + course);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao criar curso: " + e.getMessage());
        }
    }
    
    public void atualizarCurso(Integer id, String name, String duration, Integer flightHours, Integer theoreticalHours, Double price) {
        try {
            Course course = courseService.atualizarCurso(id, name, duration, flightHours, theoreticalHours, price);
            System.out.println("Curso atualizado com sucesso: " + course);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao atualizar curso: " + e.getMessage());
        }
    }
    
    public void eliminarCurso(Integer id) {
        try {
            courseService.eliminarCurso(id);
            System.out.println("Curso eliminado com sucesso");
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao eliminar curso: " + e.getMessage());
        }
    }
    
    public long obterTotalCursos() {
        return courseService.contarCursos();
    }
}
