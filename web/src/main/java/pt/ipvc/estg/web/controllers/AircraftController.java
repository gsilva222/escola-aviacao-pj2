package pt.ipvc.estg.web.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.estg.services.AircraftService;
import pt.ipvc.estg.util.PageQueryParser;
import pt.ipvc.estg.web.dto.AircraftRequest;
import pt.ipvc.estg.web.dto.AircraftResponse;
import pt.ipvc.estg.web.mappers.AircraftMapper;
import pt.ipvc.estg.web.mappers.DomainDtoMapper;

@RestController
@RequestMapping("/bo/aircraft")
public class AircraftController {

    private final AircraftService aircraftService;

    public AircraftController(AircraftService aircraftService) {
        this.aircraftService = aircraftService;
    }

    @GetMapping
    public Page<AircraftResponse> getAircraft(@RequestParam(required = false) String status,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "20") int size,
                                              @RequestParam(required = false) String sort) {
        var result = aircraftService.listAvioes(PageQueryParser.parse(page, size, sort), status);
        return DomainDtoMapper.toSpringPage(result, AircraftMapper::toResponse, sort);
    }

    @GetMapping("/{id}")
    public AircraftResponse getAircraftById(@PathVariable("id") Integer id) {
        return AircraftMapper.toResponse(aircraftService.requireAviao(id));
    }

    @PostMapping
    public AircraftResponse createAircraft(@Valid @RequestBody AircraftRequest request) {
        return AircraftMapper.toResponse(aircraftService.saveAviao(AircraftMapper.toEntity(request)));
    }

    @PutMapping("/{id}")
    public AircraftResponse updateAircraft(@PathVariable("id") Integer id, @RequestBody AircraftRequest request) {
        return AircraftMapper.toResponse(aircraftService.updateAviao(id, AircraftMapper.toEntity(request)));
    }

    @DeleteMapping("/{id}")
    public void deleteAircraft(@PathVariable("id") Integer id) {
        aircraftService.eliminarAviao(id);
    }
}
