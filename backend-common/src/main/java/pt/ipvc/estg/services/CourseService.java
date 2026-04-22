package pt.ipvc.estg.services;

import pt.ipvc.estg.dal.mock.CourseDAOMock;
import pt.ipvc.estg.entities.Course;
import java.util.List;
import java.util.Optional;

/**
 * Serviço de Curso - Lógica de negócio para Cursos
 */
public class CourseService {
    
    private final CourseDAOMock courseDAO;
    
    public CourseService() {
        this.courseDAO = new CourseDAOMock();
    }
    
    public Optional<Course> getCurso(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        return courseDAO.findById(id);
    }
    
    public Optional<Course> getCursoPorNome(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome deve ser válido");
        }
        return courseDAO.findByName(name);
    }
    
    public List<Course> getAllCursos() {
        return courseDAO.findAll();
    }
    
    public Course criarCurso(String name, String duration, Integer flightHours, 
                            Integer theoreticalHours, Double price) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do curso é obrigatório");
        }
        if (courseDAO.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Já existe um curso com esse nome");
        }
        
        Course course = new Course(name, duration, flightHours, theoreticalHours, price);
        return courseDAO.insert(course);
    }
    
    public Course atualizarCurso(Integer id, String name, String duration, 
                                Integer flightHours, Integer theoreticalHours, Double price) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        
        Optional<Course> opt = courseDAO.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Curso não encontrado");
        }
        
        Course course = opt.get();
        
        if (name != null && !name.trim().isEmpty()) {
            Optional<Course> existente = courseDAO.findByName(name);
            if (existente.isPresent() && !existente.get().getId().equals(id)) {
                throw new IllegalArgumentException("Já existe outro curso com esse nome");
            }
            course.setName(name);
        }
        
        if (duration != null) course.setDuration(duration);
        if (flightHours != null) course.setFlightHours(flightHours);
        if (theoreticalHours != null) course.setTheoreticalHours(theoreticalHours);
        if (price != null) course.setPrice(price);
        
        return courseDAO.update(course);
    }
    
    public void eliminarCurso(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        if (courseDAO.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Curso não encontrado");
        }
        courseDAO.delete(id);
    }
    
    public long contarCursos() {
        return courseDAO.count();
    }
}
