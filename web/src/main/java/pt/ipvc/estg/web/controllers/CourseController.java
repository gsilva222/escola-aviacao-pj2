package pt.ipvc.estg.web.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.web.dto.CourseRequest;
import pt.ipvc.estg.web.dto.CourseResponse;
import pt.ipvc.estg.web.mappers.CourseMapper;
import pt.ipvc.estg.web.repositories.CourseRepository;
import pt.ipvc.estg.web.validation.BusinessRules;

import java.util.List;

@RestController
@RequestMapping("/bo/courses")
public class CourseController {

    private final CourseRepository courseRepository;

    public CourseController(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public Page<CourseResponse> getCourses(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size,
                                           @RequestParam(required = false) String sort) {
        PageRequest pageable = PageRequest.of(page, size, buildSort(sort));
        return courseRepository.findAll(pageable).map(CourseMapper::toResponse);
    }

    @GetMapping("/{id}")
    public CourseResponse getCourse(@PathVariable("id") Integer id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Curso nao encontrado"));
        return CourseMapper.toResponse(course);
    }

    @PostMapping
    public CourseResponse createCourse(@Valid @RequestBody CourseRequest request) {
        BusinessRules.requirePositive("Horas de voo", request.flightHours());
        BusinessRules.requirePositive("Horas teoricas", request.theoreticalHours());
        BusinessRules.requirePositiveOrZero("Preco", request.price());
        courseRepository.findByNameIgnoreCase(request.name()).ifPresent(existing -> {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Ja existe um curso com esse nome");
        });

        Course course = new Course(
                request.name(),
                request.duration(),
                request.flightHours(),
                request.theoreticalHours(),
                request.price()
        );
        if (request.description() != null) {
            course.setDescription(request.description());
        }

        Course created = courseRepository.save(course);
        return CourseMapper.toResponse(created);
    }

    @PutMapping("/{id}")
    public CourseResponse updateCourse(@PathVariable("id") Integer id, @RequestBody CourseRequest request) {
        BusinessRules.requirePositive("Horas de voo", request.flightHours());
        BusinessRules.requirePositive("Horas teoricas", request.theoreticalHours());
        BusinessRules.requirePositiveOrZero("Preco", request.price());
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Curso nao encontrado"));

        if (request.name() != null && !request.name().trim().isEmpty()) {
            courseRepository.findByNameIgnoreCase(request.name())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Ja existe outro curso com esse nome");
                    });
            course.setName(request.name());
        }
        if (request.duration() != null) course.setDuration(request.duration());
        if (request.flightHours() != null) course.setFlightHours(request.flightHours());
        if (request.theoreticalHours() != null) course.setTheoreticalHours(request.theoreticalHours());
        if (request.price() != null) course.setPrice(request.price());
        if (request.description() != null) course.setDescription(request.description());

        Course updated = courseRepository.save(course);
        return CourseMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable("id") Integer id) {
        if (!courseRepository.existsById(id)) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Curso nao encontrado");
        }
        courseRepository.deleteById(id);
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
