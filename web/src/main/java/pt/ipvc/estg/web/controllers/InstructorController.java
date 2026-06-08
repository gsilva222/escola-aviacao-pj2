package pt.ipvc.estg.web.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.estg.services.InstructorService;
import pt.ipvc.estg.util.PageQueryParser;
import pt.ipvc.estg.web.dto.InstructorRequest;
import pt.ipvc.estg.web.dto.InstructorResponse;
import pt.ipvc.estg.web.mappers.DomainDtoMapper;
import pt.ipvc.estg.web.mappers.InstructorMapper;

@RestController
@RequestMapping("/bo/instructors")
public class InstructorController {

    private final InstructorService instructorService;

    public InstructorController(InstructorService instructorService) {
        this.instructorService = instructorService;
    }

    @GetMapping
    public Page<InstructorResponse> getInstructors(@RequestParam(required = false) String status,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "20") int size,
                                                   @RequestParam(required = false) String sort) {
        var result = instructorService.listIntrutores(PageQueryParser.parse(page, size, sort), status);
        return DomainDtoMapper.toSpringPage(result, InstructorMapper::toResponse, sort);
    }

    @GetMapping("/{id}")
    public InstructorResponse getInstructor(@PathVariable("id") Integer id) {
        return InstructorMapper.toResponse(instructorService.requireInstrutor(id));
    }

    @PostMapping
    public InstructorResponse createInstructor(@Valid @RequestBody InstructorRequest request) {
        return InstructorMapper.toResponse(instructorService.saveInstrutor(InstructorMapper.toEntity(request)));
    }

    @PutMapping("/{id}")
    public InstructorResponse updateInstructor(@PathVariable("id") Integer id, @RequestBody InstructorRequest request) {
        return InstructorMapper.toResponse(instructorService.updateInstrutor(id, InstructorMapper.toEntity(request)));
    }

    @DeleteMapping("/{id}")
    public void deleteInstructor(@PathVariable("id") Integer id) {
        instructorService.eliminarInstrutor(id);
    }
}
