package pt.ipvc.estg.services;

import pt.ipvc.estg.dal.mock.AircraftDAOMock;
import pt.ipvc.estg.dal.mock.MockDataSeeder;
import pt.ipvc.estg.entities.Aircraft;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Serviço de Aircraft - Lógica de negócio para Aviões
 */
public class AircraftService {
    
    private final AircraftDAOMock aircraftDAO;
    
    public AircraftService() {
        MockDataSeeder.seedAllData();
        this.aircraftDAO = new AircraftDAOMock();
    }
    
    public Optional<Aircraft> getAviao(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        return aircraftDAO.findById(id);
    }
    
    public Optional<Aircraft> getAviãoPorMatricula(String registration) {
        if (registration == null || registration.trim().isEmpty()) {
            throw new IllegalArgumentException("Matrícula deve ser válida");
        }
        return aircraftDAO.findByRegistration(registration);
    }
    
    public List<Aircraft> getAllAvioes() {
        return aircraftDAO.findAll();
    }
    
    public List<Aircraft> getAvioesPorStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status deve ser válido");
        }
        return aircraftDAO.findByStatus(status);
    }
    
    public Aircraft criarAviao(String registration, String model, String type) {
        if (registration == null || registration.trim().isEmpty()) {
            throw new IllegalArgumentException("Matrícula é obrigatória");
        }
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Modelo é obrigatório");
        }
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo é obrigatório");
        }
        
        if (aircraftDAO.findByRegistration(registration).isPresent()) {
            throw new IllegalArgumentException("Já existe um avião com essa matrícula");
        }
        
        Aircraft aircraft = new Aircraft(registration, model, type);
        return aircraftDAO.insert(aircraft);
    }
    
    public Aircraft atualizarAviao(Integer id, String model, Integer year, 
                                  String location, Integer fuelLevel) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        
        Optional<Aircraft> opt = aircraftDAO.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Avião não encontrado");
        }
        
        Aircraft aircraft = opt.get();
        
        if (model != null && !model.trim().isEmpty()) {
            aircraft.setModel(model);
        }
        if (year != null && year > 0) {
            aircraft.setManufYear(year);
        }
        if (location != null) {
            aircraft.setLocation(location);
        }
        if (fuelLevel != null && fuelLevel >= 0 && fuelLevel <= 100) {
            aircraft.setFuelLevel(fuelLevel);
        }
        
        return aircraftDAO.update(aircraft);
    }
    
    public void atualizarStatus(Integer id, String status) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        
        Optional<Aircraft> opt = aircraftDAO.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Avião não encontrado");
        }
        
        Aircraft aircraft = opt.get();
        aircraft.setStatus(status);
        aircraftDAO.update(aircraft);
    }
    
    public void atualizarManutenção(Integer id, LocalDate nextMaintenance) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        
        Optional<Aircraft> opt = aircraftDAO.findById(id);
        if (opt.isEmpty()) {
            throw new IllegalArgumentException("Avião não encontrado");
        }
        
        Aircraft aircraft = opt.get();
        aircraft.setLastMaintenance(LocalDate.now());
        if (nextMaintenance != null) {
            aircraft.setNextMaintenance(nextMaintenance);
        }
        aircraftDAO.update(aircraft);
    }
    
    public void eliminarAviao(Integer id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID deve ser válido");
        }
        if (aircraftDAO.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Avião não encontrado");
        }
        aircraftDAO.delete(id);
    }
    
    public long contarAvioes() {
        return aircraftDAO.count();
    }
    
    public long contarAviõesOperacionais() {
        return getAllAvioes().stream()
                .filter(a -> "operational".equals(a.getStatus()))
                .count();
    }
}
