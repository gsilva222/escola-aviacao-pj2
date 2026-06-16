package pt.ipvc.estg.web.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import pt.ipvc.estg.util.PageQueryParser;
import pt.ipvc.estg.domain.PageQuery;
import pt.ipvc.estg.entities.Flight;
import pt.ipvc.estg.services.FlightService;
import pt.ipvc.estg.validation.BusinessRules;
import pt.ipvc.estg.web.dto.FlightRequest;
import pt.ipvc.estg.web.dto.FlightResponse;
import pt.ipvc.estg.web.mappers.DomainDtoMapper;
import pt.ipvc.estg.web.mappers.FlightMapper;

@RestController
@RequestMapping("/bo/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    public Page<FlightResponse> getFlights(@RequestParam(value = "studentId", required = false) Integer studentId,
                                           @RequestParam(value = "status", required = false) String status,
                                           @RequestParam(value = "page", defaultValue = "0") int page,
                                           @RequestParam(value = "size", defaultValue = "20") int size,
                                           @RequestParam(value = "sort", required = false) String sort) {
        PageQuery query = PageQueryParser.parse(page, size, sort);
        var result = flightService.listVoos(query, studentId, status);
        return DomainDtoMapper.toSpringPage(result, FlightMapper::toResponse, sort);
    }

    @GetMapping("/{id}")
    public FlightResponse getFlight(@PathVariable("id") Integer id) {
        return FlightMapper.toResponse(flightService.requireVoo(id));
    }

    @PostMapping
    public FlightResponse createFlight(@Valid @RequestBody FlightRequest request) {
        BusinessRules.requirePositive("Duracao", request.duration());
        Flight created = flightService.saveVoo(FlightMapper.toEntity(request));
        return FlightMapper.toResponse(created);
    }

    @PutMapping("/{id}")
    public FlightResponse updateFlight(@PathVariable("id") Integer id, @RequestBody FlightRequest request) {
        BusinessRules.requirePositive("Duracao", request.duration());
        Flight flight = flightService.requireVoo(id);
        Flight updates = FlightMapper.toEntity(request);
        if (updates.getFlightDate() != null) flight.setFlightDate(updates.getFlightDate());
        if (updates.getFlightTime() != null) flight.setFlightTime(updates.getFlightTime());
        if (updates.getDuration() != null) flight.setDuration(updates.getDuration());
        if (updates.getOrigin() != null) flight.setOrigin(updates.getOrigin());
        if (updates.getDestination() != null) flight.setDestination(updates.getDestination());
        if (updates.getFlightType() != null) flight.setFlightType(updates.getFlightType());
        if (updates.getStatus() != null) flight.setStatus(updates.getStatus());
        if (updates.getObjectives() != null) flight.setObjectives(updates.getObjectives());
        if (updates.getNotes() != null) flight.setNotes(updates.getNotes());
        if (updates.getGrade() != null) flight.setGrade(updates.getGrade());
        if (updates.getStudent() != null) flight.setStudent(updates.getStudent());
        if (updates.getInstructor() != null) flight.setInstructor(updates.getInstructor());
        if (updates.getAircraft() != null) flight.setAircraft(updates.getAircraft());
        return FlightMapper.toResponse(flightService.saveVoo(flight));
    }

    @DeleteMapping("/{id}")
    public void deleteFlight(@PathVariable("id") Integer id) {
        flightService.eliminarVoo(id);
    }
}
