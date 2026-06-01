package com.umcsuser.carrent.services.impl; 

import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.impl.VehicleRepositoryJpaAdapter;
import com.umcsuser.carrent.services.VehicleServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VehicleHibernateService implements VehicleServiceInterface {

    private final VehicleRepositoryJpaAdapter vehicleRepo;

    public VehicleHibernateService(VehicleRepositoryJpaAdapter vehicleRepo) {
        this.vehicleRepo = vehicleRepo;
    }

    @Override
    public List<Vehicle> findAllVehicles() {
        return vehicleRepo.findAll();
    }

    @Override
    public Optional<Vehicle> findVehicleById(String id) {
        return vehicleRepo.findById(id);
    }

    @Override
    public Vehicle addVehicle(Vehicle vehicle) {
        return vehicleRepo.save(vehicle);
    }

    @Override
    public void deleteVehicle(String id) {
        vehicleRepo.deleteById(id);
    }
}