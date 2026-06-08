package pt.ipvc.estg.web.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.estg.services.MaintenanceService;
import pt.ipvc.estg.util.PageQueryParser;
import pt.ipvc.estg.web.dto.MaintenanceRequest;
import pt.ipvc.estg.web.dto.MaintenanceResponse;
import pt.ipvc.estg.web.mappers.DomainDtoMapper;
import pt.ipvc.estg.web.mappers.MaintenanceMapper;

@RestController
@RequestMapping("/bo/maintenance")
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    public MaintenanceController(MaintenanceService maintenanceService) {
        this.maintenanceService = maintenanceService;
    }

    @GetMapping
    public Page<MaintenanceResponse> getMaintenance(@RequestParam(value = "aircraftId", required = false) Integer aircraftId,
                                                    @RequestParam(value = "status", required = false) String status,
                                                    @RequestParam(value = "priority", required = false) String priority,
                                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                                    @RequestParam(value = "size", defaultValue = "20") int size,
                                                    @RequestParam(value = "sort", required = false) String sort) {
        var result = maintenanceService.listManutencoes(PageQueryParser.parse(page, size, sort), aircraftId, status, priority);
        return DomainDtoMapper.toSpringPage(result, MaintenanceMapper::toResponse, sort);
    }

    @GetMapping("/{id}")
    public MaintenanceResponse getMaintenanceById(@PathVariable("id") Integer id) {
        return MaintenanceMapper.toResponse(maintenanceService.requireManutencao(id));
    }

    @PostMapping
    public MaintenanceResponse createMaintenance(@Valid @RequestBody MaintenanceRequest request) {
        return MaintenanceMapper.toResponse(maintenanceService.saveManutencao(MaintenanceMapper.toEntity(request)));
    }

    @PutMapping("/{id}")
    public MaintenanceResponse updateMaintenance(@PathVariable("id") Integer id, @RequestBody MaintenanceRequest request) {
        return MaintenanceMapper.toResponse(maintenanceService.updateManutencao(id, MaintenanceMapper.toEntity(request)));
    }

    @DeleteMapping("/{id}")
    public void deleteMaintenance(@PathVariable("id") Integer id) {
        maintenanceService.eliminarManutencao(id);
    }
}
