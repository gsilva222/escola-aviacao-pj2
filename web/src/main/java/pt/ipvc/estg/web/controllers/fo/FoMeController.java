package pt.ipvc.estg.web.controllers.fo;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.estg.services.StudentService;
import pt.ipvc.estg.web.dto.FoProfileUpdateRequest;
import pt.ipvc.estg.web.dto.StudentResponse;
import pt.ipvc.estg.web.mappers.StudentMapper;
import pt.ipvc.estg.web.services.StudentScopeService;

@RestController
@RequestMapping("/fo/me")
public class FoMeController {

    private final StudentScopeService studentScopeService;
    private final StudentService studentService;

    public FoMeController(StudentScopeService studentScopeService, StudentService studentService) {
        this.studentScopeService = studentScopeService;
        this.studentService = studentService;
    }

    @GetMapping
    public StudentResponse getProfile() {
        return StudentMapper.toResponse(studentScopeService.requireCurrentStudent());
    }

    @PutMapping
    public StudentResponse updateProfile(@Valid @RequestBody FoProfileUpdateRequest request) {
        var student = studentScopeService.requireCurrentStudent();
        var updated = studentService.updateProfile(
                student.getId(), request.phone(), request.address(), request.nationality());
        return StudentMapper.toResponse(updated);
    }
}
