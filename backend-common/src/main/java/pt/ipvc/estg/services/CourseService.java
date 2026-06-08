package pt.ipvc.estg.services;

import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.domain.PageResult;
import pt.ipvc.estg.entities.Course;
import pt.ipvc.estg.exception.ConflictException;
import pt.ipvc.estg.exception.EntityNotFoundException;
import pt.ipvc.estg.repositories.CourseRepository;
import pt.ipvc.estg.validation.BusinessRules;

import java.util.List;
import java.util.Optional;

public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public CourseService() {
        this(pt.ipvc.estg.bootstrap.MockServices.getInstance().courseRepository());
    }

    public Optional<Course> getCurso(Integer id) {
        validateId(id);
        return courseRepository.findById(id);
    }

    public Course requireCurso(Integer id) {
        return getCurso(id).orElseThrow(() -> new EntityNotFoundException("Curso nao encontrado"));
    }

    public Optional<Course> getCursoPorNome(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome deve ser valido");
        }
        return courseRepository.findByName(name);
    }

    public List<Course> getAllCursos() {
        return courseRepository.findAll();
    }

    public PageResult<Course> listCursos(PageQuery query) {
        return courseRepository.findAll(query);
    }

    public Course criarCurso(String name, String duration, Integer flightHours,
                             Integer theoreticalHours, Double price) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do curso e obrigatorio");
        }
        if (courseRepository.findByName(name).isPresent()) {
            throw new ConflictException("Ja existe um curso com esse nome");
        }
        Course course = new Course(name, duration, flightHours, theoreticalHours, price);
        return courseRepository.save(course);
    }

    public Course saveCurso(Course course) {
        validateCourseFields(course);
        if (course.getName() == null || course.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do curso e obrigatorio");
        }
        if (course.getId() == null) {
            courseRepository.findByName(course.getName()).ifPresent(existing -> {
                throw new ConflictException("Ja existe um curso com esse nome");
            });
        } else {
            courseRepository.findByName(course.getName())
                    .filter(existing -> !existing.getId().equals(course.getId()))
                    .ifPresent(existing -> {
                        throw new ConflictException("Ja existe outro curso com esse nome");
                    });
        }
        return courseRepository.save(course);
    }

    public Course updateCurso(Integer id, Course updates) {
        Course course = requireCurso(id);
        validateCourseFields(updates);
        if (updates.getName() != null && !updates.getName().trim().isEmpty()) {
            courseRepository.findByName(updates.getName())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new ConflictException("Ja existe outro curso com esse nome");
                    });
            course.setName(updates.getName());
        }
        if (updates.getDuration() != null) course.setDuration(updates.getDuration());
        if (updates.getFlightHours() != null) course.setFlightHours(updates.getFlightHours());
        if (updates.getTheoreticalHours() != null) course.setTheoreticalHours(updates.getTheoreticalHours());
        if (updates.getPrice() != null) course.setPrice(updates.getPrice());
        if (updates.getDescription() != null) course.setDescription(updates.getDescription());
        return courseRepository.save(course);
    }

    private void validateCourseFields(Course course) {
        BusinessRules.requirePositive("Horas de voo", course.getFlightHours());
        BusinessRules.requirePositive("Horas teoricas", course.getTheoreticalHours());
        BusinessRules.requirePositiveOrZero("Preco", course.getPrice());
    }

    public Course atualizarCurso(Integer id, String name, String duration,
                                 Integer flightHours, Integer theoreticalHours, Double price) {
        Course course = requireCurso(id);
        if (name != null && !name.trim().isEmpty()) {
            courseRepository.findByName(name)
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new ConflictException("Ja existe outro curso com esse nome");
                    });
            course.setName(name);
        }
        if (duration != null) course.setDuration(duration);
        if (flightHours != null) course.setFlightHours(flightHours);
        if (theoreticalHours != null) course.setTheoreticalHours(theoreticalHours);
        if (price != null) course.setPrice(price);
        return courseRepository.save(course);
    }

    public void eliminarCurso(Integer id) {
        validateId(id);
        if (!courseRepository.existsById(id)) {
            throw new EntityNotFoundException("Curso nao encontrado");
        }
        courseRepository.deleteById(id);
    }

    public long contarCursos() {
        return courseRepository.count();
    }

    private static void validateId(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser valido");
        }
    }
}
