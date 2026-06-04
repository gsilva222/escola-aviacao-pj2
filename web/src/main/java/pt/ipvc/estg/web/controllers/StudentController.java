package pt.ipvc.estg.web.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.web.dto.StudentRequest;
import pt.ipvc.estg.web.dto.StudentResponse;
import pt.ipvc.estg.web.mappers.StudentMapper;
import pt.ipvc.estg.web.repositories.CourseRepository;
import pt.ipvc.estg.web.repositories.StudentRepository;
import pt.ipvc.estg.web.validation.BusinessRules;

import java.util.List;

@RestController
@RequestMapping("/bo/students")
public class StudentController {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public StudentController(StudentRepository studentRepository, CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public Page<StudentResponse> getStudents(@RequestParam(required = false) Integer courseId,
                                             @RequestParam(required = false) String status,
                                             @RequestParam(defaultValue = "0") int page,
                                             @RequestParam(defaultValue = "20") int size,
                                             @RequestParam(required = false) String sort) {
        PageRequest pageable = PageRequest.of(page, size, buildSort(sort));
        if (courseId != null) {
            List<StudentResponse> content = studentRepository.findByCourse_Id(courseId, pageable)
                    .stream().map(StudentMapper::toResponse).toList();
            long total = studentRepository.findByCourse_Id(courseId).size();
            return new PageImpl<>(content, pageable, total);
        }
        if (status != null && !status.trim().isEmpty()) {
            List<StudentResponse> content = studentRepository.findByStatus(status, pageable)
                    .stream().map(StudentMapper::toResponse).toList();
            long total = studentRepository.findByStatus(status).size();
            return new PageImpl<>(content, pageable, total);
        }
        return studentRepository.findAll(pageable).map(StudentMapper::toResponse);
    }

    @GetMapping("/{id}")
    public StudentResponse getStudent(@PathVariable("id") Integer id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Estudante nao encontrado"));
        return StudentMapper.toResponse(student);
    }

    @PostMapping
    public StudentResponse createStudent(@Valid @RequestBody StudentRequest request) {
        BusinessRules.validatePortugueseNif(request.nif());
        BusinessRules.validateAdultBirthdate(request.birthdate());
        BusinessRules.requirePercent("Progresso", request.progress());
        BusinessRules.requirePositiveOrZero("Horas de voo", request.flightHours());
        BusinessRules.requirePositiveOrZero("Horas teoricas", request.theoreticalHours());
        if (studentRepository.findByEmailIgnoreCase(request.email()).isPresent()) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Ja existe um estudante com esse email");
        }

        Course course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Curso nao encontrado"));

        Student student = new Student(request.name(), request.email(), course);
        if (request.phone() != null) student.setPhone(request.phone());
        if (request.nif() != null) student.setNif(request.nif());
        if (request.birthdate() != null) student.setBirthdate(request.birthdate());
        if (request.address() != null) student.setAddress(request.address());
        if (request.nationality() != null) student.setNationality(request.nationality());
        if (request.status() != null) student.setStatus(BusinessRules.requireAllowed("Status", request.status(), BusinessRules.STUDENT_STATUSES));
        if (request.enrollmentDate() != null) student.setEnrollmentDate(request.enrollmentDate());
        if (request.progress() != null) {
            validateProgress(request.progress());
            student.setProgress(request.progress());
        }
        if (request.flightHours() != null) student.setFlightHours(request.flightHours());
        if (request.theoreticalHours() != null) student.setTheoreticalHours(request.theoreticalHours());
        if (request.paymentStatus() != null) student.setPaymentStatus(BusinessRules.requireAllowed("PaymentStatus", request.paymentStatus(), BusinessRules.STUDENT_PAYMENT_STATUSES));

        Student created = studentRepository.save(student);
        return StudentMapper.toResponse(created);
    }

    @PutMapping("/{id}")
    public StudentResponse updateStudent(@PathVariable("id") Integer id, @RequestBody StudentRequest request) {
        BusinessRules.validatePortugueseNif(request.nif());
        BusinessRules.validateAdultBirthdate(request.birthdate());
        BusinessRules.requirePercent("Progresso", request.progress());
        BusinessRules.requirePositiveOrZero("Horas de voo", request.flightHours());
        BusinessRules.requirePositiveOrZero("Horas teoricas", request.theoreticalHours());
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Estudante nao encontrado"));

        if (request.name() != null && !request.name().trim().isEmpty()) {
            student.setName(request.name());
            student.setAvatar(generateAvatar(request.name()));
        }

        if (request.email() != null && !request.email().trim().isEmpty()) {
            studentRepository.findByEmailIgnoreCase(request.email())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Ja existe outro estudante com esse email");
                    });
            student.setEmail(request.email());
        }

        if (request.phone() != null) student.setPhone(request.phone());
        if (request.nif() != null) student.setNif(request.nif());
        if (request.birthdate() != null) student.setBirthdate(request.birthdate());
        if (request.address() != null) student.setAddress(request.address());
        if (request.nationality() != null) student.setNationality(request.nationality());
        if (request.status() != null) student.setStatus(BusinessRules.requireAllowed("Status", request.status(), BusinessRules.STUDENT_STATUSES));
        if (request.enrollmentDate() != null) student.setEnrollmentDate(request.enrollmentDate());
        if (request.progress() != null) {
            validateProgress(request.progress());
            student.setProgress(request.progress());
        }
        if (request.flightHours() != null) student.setFlightHours(request.flightHours());
        if (request.theoreticalHours() != null) student.setTheoreticalHours(request.theoreticalHours());
        if (request.paymentStatus() != null) student.setPaymentStatus(BusinessRules.requireAllowed("PaymentStatus", request.paymentStatus(), BusinessRules.STUDENT_PAYMENT_STATUSES));

        if (request.courseId() != null) {
            Course course = courseRepository.findById(request.courseId())
                    .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Curso nao encontrado"));
            student.setCourse(course);
        }

        Student updated = studentRepository.save(student);
        return StudentMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable("id") Integer id) {
        if (!studentRepository.existsById(id)) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Estudante nao encontrado");
        }
        studentRepository.deleteById(id);
    }

    private void validateProgress(Integer progress) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("Progresso deve estar entre 0-100");
        }
    }

    private Sort buildSort(String sort) {
        if (sort == null || sort.trim().isEmpty()) {
            return Sort.unsorted();
        }
        String[] parts = sort.split(",", 2);
        String property = parts[0].trim();
        if (property.isEmpty()) {
            return Sort.unsorted();
        }
        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length == 2 && "desc".equalsIgnoreCase(parts[1].trim())) {
            direction = Sort.Direction.DESC;
        }
        return Sort.by(direction, property);
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
