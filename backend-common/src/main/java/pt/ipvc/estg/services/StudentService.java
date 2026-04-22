package pt.ipvc.estg.services;

import pt.ipvc.estg.dal.mock.StudentDAOMock;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.entities.Course;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Serviço de Student - Lógica de negócio para Estudantes
 */
public class StudentService {
    
    private final StudentDAOMock studentDAO;
    
    public StudentService() {
        this.studentDAO = new StudentDAOMock();
    }
    
    public Optional<Student> getEstudante(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        return studentDAO.findById(id);
    }
    
    public Optional<Student> getEstudantePorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email deve ser válido");
        }
        return studentDAO.findByEmail(email);
    }
    
    public List<Student> getAllEstudantes() {
        return studentDAO.findAll();
    }
    
    public List<Student> getEstudantesPorCurso(Integer courseId) {
        if (courseId == null || courseId <= 0) {
            throw new IllegalArgumentException("Course ID deve ser válido");
        }
        return studentDAO.findByCourse(courseId);
    }
    
    public List<Student> getEstudantesPorStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status deve ser válido");
        }
        return studentDAO.findByStatus(status);
    }
    
    public Student criarEstudante(String name, String email, Course course) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        if (course == null) {
            throw new IllegalArgumentException("Curso é obrigatório");
        }
        
        if (studentDAO.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Já existe um estudante com esse email");
        }
        
        Student student = new Student(name, email, course);
        return studentDAO.insert(student);
    }
    
    public Student atualizarEstudante(Integer id, String name, String email, 
                                     String phone, String nif, LocalDate birthdate) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        
        Optional<Student> opt = studentDAO.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Estudante não encontrado");
        }
        
        Student student = opt.get();
        
        if (name != null && !name.trim().isEmpty()) {
            student.setName(name);
            student.setAvatar(generateAvatar(name));
        }
        
        if (email != null && !email.trim().isEmpty()) {
            Optional<Student> existente = studentDAO.findByEmail(email);
            if (existente.isPresent() && !existente.get().getId().equals(id)) {
                throw new IllegalArgumentException("Já existe outro estudante com esse email");
            }
            student.setEmail(email);
        }
        
        if (phone != null) student.setPhone(phone);
        if (nif != null) student.setNif(nif);
        if (birthdate != null) student.setBirthdate(birthdate);
        
        return studentDAO.update(student);
    }
    
    public void atualizarProgresso(Integer id, Integer progress) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        if (progress == null || progress < 0 || progress > 100) {
            throw new IllegalArgumentException("Progresso deve estar entre 0-100");
        }
        
        Optional<Student> opt = studentDAO.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Estudante não encontrado");
        }
        
        Student student = opt.get();
        student.setProgress(progress);
        studentDAO.update(student);
    }
    
    public void atualizarStatus(Integer id, String status) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        
        Optional<Student> opt = studentDAO.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Estudante não encontrado");
        }
        
        Student student = opt.get();
        student.setStatus(status);
        studentDAO.update(student);
    }
    
    public void eliminarEstudante(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        if (studentDAO.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Estudante não encontrado");
        }
        studentDAO.delete(id);
    }
    
    public long contarEstudantes() {
        return studentDAO.count();
    }
    
    private String generateAvatar(String name) {
        String[] parts = name.split(" ");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                sb.append(part.charAt(0));
                if (sb.length() == 2) break;
            }
        }
        return sb.toString().toUpperCase();
    }
}
