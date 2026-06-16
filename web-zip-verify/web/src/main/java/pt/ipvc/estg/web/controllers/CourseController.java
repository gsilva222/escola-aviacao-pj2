package pt.ipvc.estg.web.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.estg.services.CourseService;
import pt.ipvc.estg.util.PageQueryParser;
import pt.ipvc.estg.web.dto.CourseRequest;
import pt.ipvc.estg.web.dto.CourseResponse;
import pt.ipvc.estg.web.mappers.CourseMapper;
import pt.ipvc.estg.web.mappers.DomainDtoMapper;

@RestController
@RequestMapping("/bo/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public Page<CourseResponse> getCourses(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size,
                                           @RequestParam(required = false) String sort) {
        var result = courseService.listCursos(PageQueryParser.parse(page, size, sort));
        return DomainDtoMapper.toSpringPage(result, CourseMapper::toResponse, sort);
    }

    @GetMapping("/{id}")
    public CourseResponse getCourse(@PathVariable("id") Integer id) {
        return CourseMapper.toResponse(courseService.requireCurso(id));
    }

    @PostMapping
    public CourseResponse createCourse(@Valid @RequestBody CourseRequest request) {
        return CourseMapper.toResponse(courseService.saveCurso(CourseMapper.toEntity(request)));
    }

    @PutMapping("/{id}")
    public CourseResponse updateCourse(@PathVariable("id") Integer id, @RequestBody CourseRequest request) {
        return CourseMapper.toResponse(courseService.updateCurso(id, CourseMapper.toEntity(request)));
    }

    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable("id") Integer id) {
        courseService.eliminarCurso(id);
    }
}
