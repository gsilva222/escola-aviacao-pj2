package pt.ipvc.estg.services;

import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.entities.Instructor;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.exception.ConflictException;
import pt.ipvc.estg.exception.EntityNotFoundException;
import pt.ipvc.estg.repositories.CourseRepository;
import pt.ipvc.estg.repositories.InstructorRepository;
import pt.ipvc.estg.repositories.StudentRepository;
import pt.ipvc.estg.validation.BusinessRules;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class StudentService {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final InstructorRepository instructorRepository;

    public StudentService(StudentRepository studentRepository,
                          CourseRepository courseRepository,
                          InstructorRepository instructorRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.instructorRepository = instructorRepository;
    }

    public StudentService() {
        this(
                pt.ipvc.estg.bootstrap.MockServices.getInstance().studentRepository(),
                pt.ipvc.estg.bootstrap.MockServices.getInstance().courseRepository(),
                pt.ipvc.estg.bootstrap.MockServices.getInstance().instructorRepository()
        );
    }

    public Optional<Student> getEstudante(Integer id) {
        validateId(id);
        return studentRepository.findById(id);
    }

    public Student requireEstudante(Integer id) {
        return getEstudante(id).orElseThrow(() -> new EntityNotFoundException("Estudante nao encontrado"));
    }

    public Optional<Student> getEstudantePorEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email deve ser valido");
        }
        return studentRepository.findByEmail(email);
    }

    public List<Student> getAllEstudantes() {
        return studentRepository.findAll();
    }

    public PageResult<Student> listEstudantes(PageQuery query, Integer courseId, String status) {
        if (courseId != null) {
            return studentRepository.findByCourse(courseId, query);
        }
        if (status != null && !status.trim().isEmpty()) {
            return studentRepository.findByStatus(status, query);
        }
        return studentRepository.findAll(query);
    }

    public List<Student> getEstudantesPorCurso(Integer courseId) {
        validateId(courseId);
        return studentRepository.findByCourse(courseId);
    }

    public List<Student> getEstudantesPorStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status deve ser valido");
        }
        return studentRepository.findByStatus(status);
    }

    public Student criarEstudante(String name, String email, Course course) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome e obrigatorio");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email e obrigatorio");
        }
        if (course == null) {
            throw new IllegalArgumentException("Curso e obrigatorio");
        }
        if (studentRepository.findByEmail(email).isPresent()) {
            throw new ConflictException("Ja existe um estudante com esse email");
        }
        Student student = new Student(name, email, course);
        return studentRepository.save(student);
    }

    public Student createEstudante(Student student) {
        validateStudentForSave(student, null);
        if (student.getCourse() != null && student.getCourse().getId() != null) {
            Course course = courseRepository.findById(student.getCourse().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Curso nao encontrado"));
            student.setCourse(course);
        }
        resolveInstructor(student);
        if (student.getName() != null) {
            student.setAvatar(generateAvatar(student.getName()));
        }
        return studentRepository.save(student);
    }

    public Student updateEstudante(Integer id, Student updates) {
        validateId(id);
        Student student = requireEstudante(id);
        validateStudentForSave(updates, id);

        if (updates.getName() != null && !updates.getName().trim().isEmpty()) {
            student.setName(updates.getName());
            student.setAvatar(generateAvatar(updates.getName()));
        }
        if (updates.getEmail() != null && !updates.getEmail().trim().isEmpty()) {
            studentRepository.findByEmail(updates.getEmail())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new ConflictException("Ja existe outro estudante com esse email");
                    });
            student.setEmail(updates.getEmail());
        }
        if (updates.getPhone() != null) student.setPhone(updates.getPhone());
        if (updates.getNif() != null) student.setNif(updates.getNif());
        if (updates.getBirthdate() != null) student.setBirthdate(updates.getBirthdate());
        if (updates.getAddress() != null) student.setAddress(updates.getAddress());
        if (updates.getNationality() != null) student.setNationality(updates.getNationality());
        if (updates.getStatus() != null) student.setStatus(updates.getStatus());
        if (updates.getEnrollmentDate() != null) student.setEnrollmentDate(updates.getEnrollmentDate());
        if (updates.getProgress() != null) student.setProgress(updates.getProgress());
        if (updates.getFlightHours() != null) student.setFlightHours(updates.getFlightHours());
        if (updates.getTheoreticalHours() != null) student.setTheoreticalHours(updates.getTheoreticalHours());
        if (updates.getPaymentStatus() != null) student.setPaymentStatus(updates.getPaymentStatus());
        if (updates.getCourse() != null && updates.getCourse().getId() != null) {
            Course course = courseRepository.findById(updates.getCourse().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Curso nao encontrado"));
            student.setCourse(course);
        }
        if (updates.getInstructor() != null) {
            resolveInstructorUpdate(student, updates.getInstructor().getId());
        }
        return studentRepository.save(student);
    }

    public Student atualizarEstudante(Integer id, String name, String email,
                                      String phone, String nif, LocalDate birthdate) {
        Student updates = new Student();
        updates.setName(name);
        updates.setEmail(email);
        updates.setPhone(phone);
        updates.setNif(nif);
        updates.setBirthdate(birthdate);
        return updateEstudante(id, updates);
    }

    public Student atualizarEstudanteCompleto(Integer id, String name, String email,
                                              String phone, String nif, LocalDate birthdate,
                                              String address, String nationality, Course course,
                                              String status, LocalDate enrollmentDate,
                                              Integer progress, Double flightHours,
                                              Double theoreticalHours, String paymentStatus) {
        Student updates = new Student();
        updates.setName(name);
        updates.setEmail(email);
        updates.setPhone(phone);
        updates.setNif(nif);
        updates.setBirthdate(birthdate);
        updates.setAddress(address);
        updates.setNationality(nationality);
        updates.setCourse(course);
        updates.setStatus(status);
        updates.setEnrollmentDate(enrollmentDate);
        updates.setProgress(progress);
        updates.setFlightHours(flightHours);
        updates.setTheoreticalHours(theoreticalHours);
        updates.setPaymentStatus(paymentStatus);
        return updateEstudante(id, updates);
    }

    public void atualizarProgresso(Integer id, Integer progress) {
        BusinessRules.requirePercent("Progresso", progress);
        Student student = requireEstudante(id);
        student.setProgress(progress);
        studentRepository.save(student);
    }

    public void atualizarStatus(Integer id, String status) {
        Student student = requireEstudante(id);
        student.setStatus(BusinessRules.requireAllowed("Status", status, BusinessRules.STUDENT_STATUSES));
        studentRepository.save(student);
    }

    public void eliminarEstudante(Integer id) {
        validateId(id);
        if (!studentRepository.existsById(id)) {
            throw new EntityNotFoundException("Estudante nao encontrado");
        }
        studentRepository.deleteById(id);
    }

    public long contarEstudantes() {
        return studentRepository.count();
    }

    public Student updateProfile(Integer id, String phone, String address, String nationality) {
        Student student = requireEstudante(id);
        if (phone != null) student.setPhone(phone);
        if (address != null) student.setAddress(address);
        if (nationality != null) student.setNationality(nationality);
        return studentRepository.save(student);
    }

    private void validateStudentForSave(Student student, Integer existingId) {
        BusinessRules.validatePortugueseNif(student.getNif());
        BusinessRules.validateAdultBirthdate(student.getBirthdate());
        BusinessRules.requirePercent("Progresso", student.getProgress());
        BusinessRules.requirePositiveOrZero("Horas de voo", student.getFlightHours());
        BusinessRules.requirePositiveOrZero("Horas teoricas", student.getTheoreticalHours());
        if (student.getEmail() != null) {
            studentRepository.findByEmail(student.getEmail())
                    .filter(existing -> existingId == null || !existing.getId().equals(existingId))
                    .ifPresent(existing -> {
                        throw new ConflictException("Ja existe um estudante com esse email");
                    });
        }
        if (student.getStatus() != null) {
            student.setStatus(BusinessRules.requireAllowed("Status", student.getStatus(), BusinessRules.STUDENT_STATUSES));
        }
        if (student.getPaymentStatus() != null) {
            student.setPaymentStatus(BusinessRules.requireAllowed(
                    "PaymentStatus", student.getPaymentStatus(), BusinessRules.STUDENT_PAYMENT_STATUSES));
        }
    }

    private void resolveInstructor(Student student) {
        if (student.getInstructor() == null || student.getInstructor().getId() == null) {
            return;
        }
        resolveInstructorUpdate(student, student.getInstructor().getId());
    }

    private void resolveInstructorUpdate(Student student, Integer instructorId) {
        if (instructorId == null) {
            return;
        }
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new EntityNotFoundException("Instrutor nao encontrado"));
        student.setInstructor(instructor);
    }

    private static void validateId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser valido");
        }
    }

    static String generateAvatar(String name) {
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
