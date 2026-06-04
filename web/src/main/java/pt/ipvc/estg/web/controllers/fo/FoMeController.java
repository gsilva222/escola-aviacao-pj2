package pt.ipvc.estg.web.controllers.fo;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.estg.entities.Student;
import pt.ipvc.estg.web.dto.FoProfileUpdateRequest;
import pt.ipvc.estg.web.dto.StudentResponse;
import pt.ipvc.estg.web.mappers.StudentMapper;
import pt.ipvc.estg.web.repositories.StudentRepository;
import pt.ipvc.estg.web.services.StudentScopeService;

@RestController
@RequestMapping("/fo/me")
public class FoMeController {

    private final StudentScopeService studentScopeService;
    private final StudentRepository studentRepository;

    public FoMeController(StudentScopeService studentScopeService, StudentRepository studentRepository) {
        this.studentScopeService = studentScopeService;
        this.studentRepository = studentRepository;
    }

    @GetMapping
    public StudentResponse getProfile() {
        return StudentMapper.toResponse(studentScopeService.requireCurrentStudent());
    }

    @PutMapping
    public StudentResponse updateProfile(@Valid @RequestBody FoProfileUpdateRequest request) {
        Student student = studentScopeService.requireCurrentStudent();
        if (request.phone() != null) {
            student.setPhone(request.phone());
        }
        if (request.address() != null) {
            student.setAddress(request.address());
        }
        if (request.nationality() != null) {
            student.setNationality(request.nationality());
        }
        return StudentMapper.toResponse(studentRepository.save(student));
    }
}
