package pt.ipvc.estg.web.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.estg.util.PageQueryParser;
import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.services.StudentService;
import pt.ipvc.estg.web.dto.StudentRequest;
import pt.ipvc.estg.web.dto.StudentResponse;
import pt.ipvc.estg.web.mappers.DomainDtoMapper;
import pt.ipvc.estg.web.mappers.StudentMapper;

@RestController
@RequestMapping("/bo/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public Page<StudentResponse> getStudents(@RequestParam(value = "courseId", required = false) Integer courseId,
                                             @RequestParam(value = "status", required = false) String status,
                                             @RequestParam(value = "page", defaultValue = "0") int page,
                                             @RequestParam(value = "size", defaultValue = "20") int size,
                                             @RequestParam(value = "sort", required = false) String sort) {
        PageQuery query = PageQueryParser.parse(page, size, sort);
        var result = studentService.listEstudantes(query, courseId, status);
        return DomainDtoMapper.toSpringPage(result, StudentMapper::toResponse, sort);
    }

    @GetMapping("/{id}")
    public StudentResponse getStudent(@PathVariable("id") Integer id) {
        return StudentMapper.toResponse(studentService.requireEstudante(id));
    }

    @PostMapping
    public StudentResponse createStudent(@Valid @RequestBody StudentRequest request) {
        Student created = studentService.createEstudante(StudentMapper.toEntity(request));
        return StudentMapper.toResponse(created);
    }

    @PutMapping("/{id}")
    public StudentResponse updateStudent(@PathVariable("id") Integer id, @RequestBody StudentRequest request) {
        Student updated = studentService.updateEstudante(id, StudentMapper.toEntity(request));
        return StudentMapper.toResponse(updated);
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable("id") Integer id) {
        studentService.eliminarEstudante(id);
    }
}
