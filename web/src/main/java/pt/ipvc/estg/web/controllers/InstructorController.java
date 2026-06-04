package pt.ipvc.estg.web.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pt.ipvc.estg.entities.Instructor;
import pt.ipvc.estg.web.dto.InstructorRequest;
import pt.ipvc.estg.web.dto.InstructorResponse;
import pt.ipvc.estg.web.mappers.InstructorMapper;
import pt.ipvc.estg.web.repositories.InstructorRepository;
import pt.ipvc.estg.web.validation.BusinessRules;

import java.util.List;

@RestController
@RequestMapping("/bo/instructors")
public class InstructorController {

    private final InstructorRepository instructorRepository;

    public InstructorController(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
    }

    @GetMapping
    public Page<InstructorResponse> getInstructors(@RequestParam(required = false) String status,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "20") int size,
                                                   @RequestParam(required = false) String sort) {
        PageRequest pageable = PageRequest.of(page, size, buildSort(sort));
        if (status != null && !status.trim().isEmpty()) {
            List<InstructorResponse> content = instructorRepository.findByStatus(status, pageable)
                    .stream().map(InstructorMapper::toResponse).toList();
            long total = instructorRepository.findByStatus(status).size();
            return new PageImpl<>(content, pageable, total);
        }
        return instructorRepository.findAll(pageable).map(InstructorMapper::toResponse);
    }

    @GetMapping("/{id}")
    public InstructorResponse getInstructor(@PathVariable("id") Integer id) {
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Instrutor nao encontrado"));
        return InstructorMapper.toResponse(instructor);
    }

    @PostMapping
    public InstructorResponse createInstructor(@Valid @RequestBody InstructorRequest request) {
        BusinessRules.requirePositiveOrZero("Horas de voo", request.flightHours());
        Instructor instructor = new Instructor(request.name(), request.license(), request.specialization());
        if (request.flightHours() != null) instructor.setFlightHours(request.flightHours());
        if (request.status() != null) instructor.setStatus(BusinessRules.requireAllowed("Status", request.status(), BusinessRules.INSTRUCTOR_STATUSES));
        if (request.email() != null) instructor.setEmail(request.email());
        if (request.phone() != null) instructor.setPhone(request.phone());

        Instructor created = instructorRepository.save(instructor);
        return InstructorMapper.toResponse(created);
    }

    @PutMapping("/{id}")
    public InstructorResponse updateInstructor(@PathVariable("id") Integer id, @RequestBody InstructorRequest request) {
        BusinessRules.requirePositiveOrZero("Horas de voo", request.flightHours());
        Instructor instructor = instructorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Instrutor nao encontrado"));

        if (request.name() != null && !request.name().trim().isEmpty()) instructor.setName(request.name());
        if (request.license() != null && !request.license().trim().isEmpty()) instructor.setLicense(request.license());
        if (request.specialization() != null) instructor.setSpecialization(request.specialization());
        if (request.flightHours() != null) instructor.setFlightHours(request.flightHours());
        if (request.status() != null) instructor.setStatus(BusinessRules.requireAllowed("Status", request.status(), BusinessRules.INSTRUCTOR_STATUSES));
        if (request.email() != null) instructor.setEmail(request.email());
        if (request.phone() != null) instructor.setPhone(request.phone());

        Instructor updated = instructorRepository.save(instructor);
        return InstructorMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteInstructor(@PathVariable("id") Integer id) {
        if (!instructorRepository.existsById(id)) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Instrutor nao encontrado");
        }
        instructorRepository.deleteById(id);
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
}
