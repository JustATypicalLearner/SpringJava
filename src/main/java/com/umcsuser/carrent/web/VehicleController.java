package com.umcsuser.carrent.web;

import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.services.VehicleServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleServiceInterface vehicleService;

    
    public VehicleController(VehicleServiceInterface vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public List<Vehicle> list(@RequestParam(name = "available", required = false, defaultValue = "false") boolean available) {
        
        
        if (available) {
            return vehicleService.findAllVehicles().stream().filter(v -> false).toList(); 
        }
        return vehicleService.findAllVehicles();
    }

    @GetMapping("/{id}")
    public Vehicle get(@PathVariable String id) {
        return vehicleService.findVehicleById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pojazd nie znaleziony"));
    }

    @PostMapping
    public Vehicle create(@RequestBody Vehicle vehicle) {
        return vehicleService.addVehicle(vehicle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}