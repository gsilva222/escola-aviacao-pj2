package pt.ipvc.estg.services;

import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Instructor;
import pt.ipvc.estg.exception.ConflictException;
import pt.ipvc.estg.exception.EntityNotFoundException;
import pt.ipvc.estg.repositories.InstructorRepository;
import pt.ipvc.estg.validation.BusinessRules;

import java.util.List;
import java.util.Optional;

public class InstructorService {

    private final InstructorRepository instructorRepository;

    public InstructorService(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    public InstructorService() {
        this(pt.ipvc.estg.bootstrap.MockServices.getInstance().instructorRepository());
    }

    public Optional<Instructor> getInstrutor(Integer id) {
        validateId(id);
        return instructorRepository.findById(id);
    }

    public Instructor requireInstrutor(Integer id) {
        return getInstrutor(id).orElseThrow(() -> new EntityNotFoundException("Instrutor nao encontrado"));
    }

    public Optional<Instructor> getIntrutorPorNome(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome deve ser valido");
        }
        return instructorRepository.findByName(name);
    }

    public List<Instructor> getAllIntrutores() {
        return instructorRepository.findAll();
    }

    public PageResult<Instructor> listIntrutores(PageQuery query, String status) {
        if (status != null && !status.trim().isEmpty()) {
            return instructorRepository.findByStatus(status, query);
        }
        return instructorRepository.findAll(query);
    }

    public List<Instructor> getIntrutoresPorStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status deve ser valido");
        }
        return instructorRepository.findByStatus(status);
    }

    public Instructor criarInstrutor(String name, String license, String specialization) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome e obrigatorio");
        }
        if (license == null || license.trim().isEmpty()) {
            throw new IllegalArgumentException("Licenca e obrigatoria");
        }
        if (instructorRepository.findByName(name).isPresent()) {
            throw new ConflictException("Ja existe um instrutor com esse nome");
        }
        Instructor instructor = new Instructor(name, license, specialization);
        return instructorRepository.save(instructor);
    }

    public Instructor saveInstrutor(Instructor instructor) {
        if (instructor.getName() == null || instructor.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome e obrigatorio");
        }
        BusinessRules.requirePositiveOrZero("Horas de voo", instructor.getFlightHours());
        if (instructor.getStatus() != null) {
            instructor.setStatus(BusinessRules.requireAllowed("Status", instructor.getStatus(), BusinessRules.INSTRUCTOR_STATUSES));
        }
        return instructorRepository.save(instructor);
    }

    public Instructor updateInstrutor(Integer id, Instructor updates) {
        Instructor instructor = requireInstrutor(id);
        BusinessRules.requirePositiveOrZero("Horas de voo", updates.getFlightHours());
        if (updates.getName() != null && !updates.getName().trim().isEmpty()) instructor.setName(updates.getName());
        if (updates.getLicense() != null && !updates.getLicense().trim().isEmpty()) instructor.setLicense(updates.getLicense());
        if (updates.getSpecialization() != null) instructor.setSpecialization(updates.getSpecialization());
        if (updates.getFlightHours() != null) instructor.setFlightHours(updates.getFlightHours());
        if (updates.getStatus() != null) {
            instructor.setStatus(BusinessRules.requireAllowed("Status", updates.getStatus(), BusinessRules.INSTRUCTOR_STATUSES));
        }
        if (updates.getEmail() != null) instructor.setEmail(updates.getEmail());
        if (updates.getPhone() != null) instructor.setPhone(updates.getPhone());
        return instructorRepository.save(instructor);
    }

    public Instructor atualizarInstrutor(Integer id, String name, String license,
                                         String specialization, String email, String phone) {
        Instructor instructor = requireInstrutor(id);
        if (name != null && !name.trim().isEmpty()) instructor.setName(name);
        if (license != null && !license.trim().isEmpty()) instructor.setLicense(license);
        if (specialization != null) instructor.setSpecialization(specialization);
        if (email != null) instructor.setEmail(email);
        if (phone != null) instructor.setPhone(phone);
        return instructorRepository.save(instructor);
    }

    public void atualizarStatus(Integer id, String status) {
        Instructor instructor = requireInstrutor(id);
        instructor.setStatus(BusinessRules.requireAllowed("Status", status, BusinessRules.INSTRUCTOR_STATUSES));
        instructorRepository.save(instructor);
    }

    public void eliminarInstrutor(Integer id) {
        validateId(id);
        if (!instructorRepository.existsById(id)) {
            throw new EntityNotFoundException("Instrutor nao encontrado");
        }
        instructorRepository.deleteById(id);
    }

    public long contarIntrutores() {
        return instructorRepository.count();
    }

    private static void validateId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser valido");
        }
    }
}
