package pt.ipvc.estg.services;

import pt.ipvc.estg.dal.mock.InstructorDAOMock;
import pt.ipvc.estg.dal.mock.MockDataSeeder;
import pt.ipvc.estg.entities.Instructor;
import java.util.List;
import java.util.Optional;

/**
 * Serviço de Instructor - Lógica de negócio para Instrutores
 */
public class InstructorService {
    
    private final InstructorDAOMock instructorDAO;
    
    public InstructorService() {
        MockDataSeeder.seedAllData();
        this.instructorDAO = new InstructorDAOMock();
    }
    
    public Optional<Instructor> getInstrutor(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        return instructorDAO.findById(id);
    }
    
    public Optional<Instructor> getIntrutorPorNome(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome deve ser válido");
        }
        return instructorDAO.findByName(name);
    }
    
    public List<Instructor> getAllIntrutores() {
        return instructorDAO.findAll();
    }
    
    public List<Instructor> getIntrutoresPorStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status deve ser válido");
        }
        return instructorDAO.findByStatus(status);
    }
    
    public Instructor criarInstrutor(String name, String license, String specialization) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (license == null || license.trim().isEmpty()) {
            throw new IllegalArgumentException("Licença é obrigatória");
        }
        
        if (instructorDAO.findByName(name).isPresent()) {
            throw new IllegalArgumentException("Já existe um instrutor com esse nome");
        }
        
        Instructor instructor = new Instructor(name, license, specialization);
        return instructorDAO.insert(instructor);
    }
    
    public Instructor atualizarInstrutor(Integer id, String name, String license, 
                                        String specialization, String email, String phone) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        
        Optional<Instructor> opt = instructorDAO.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Instrutor não encontrado");
        }
        
        Instructor instructor = opt.get();
        
        if (name != null && !name.trim().isEmpty()) {
            instructor.setName(name);
        }
        if (license != null && !license.trim().isEmpty()) {
            instructor.setLicense(license);
        }
        if (specialization != null) instructor.setSpecialization(specialization);
        if (email != null) instructor.setEmail(email);
        if (phone != null) instructor.setPhone(phone);
        
        return instructorDAO.update(instructor);
    }
    
    public void atualizarStatus(Integer id, String status) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        
        Optional<Instructor> opt = instructorDAO.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Instrutor não encontrado");
        }
        
        Instructor instructor = opt.get();
        instructor.setStatus(status);
        instructorDAO.update(instructor);
    }
    
    public void eliminarInstrutor(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        if (instructorDAO.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Instrutor não encontrado");
        }
        instructorDAO.delete(id);
    }
    
    public long contarIntrutores() {
        return instructorDAO.count();
    }
}
