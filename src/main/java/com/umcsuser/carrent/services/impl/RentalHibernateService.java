package com.umcsuser.carrent.services.impl; 

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.impl.*;
import com.umcsuser.carrent.services.RentalServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RentalHibernateService implements RentalServiceInterface {

    
    
    
    
    

    
    private final RentalRepositoryJpaAdapter rentalRepo;
    private final VehicleRepositoryJpaAdapter vehicleRepo;
    private final UserRepositoryJpaAdapter userRepo;

    public RentalHibernateService(RentalRepositoryJpaAdapter rentalRepo, VehicleRepositoryJpaAdapter vehicleRepo, UserRepositoryJpaAdapter userRepo) {
        this.rentalRepo = rentalRepo;
        this.vehicleRepo = vehicleRepo;
        this.userRepo = userRepo;
    }

    @Override
    public Rental rentVehicle(String userId, String vehicleId) {
        boolean userHasActiveRental = rentalRepo.findAll().stream()
                .anyMatch(r -> userId.equals(r.getUserId()) && r.isActive());

        if (userHasActiveRental) {
            throw new IllegalStateException("Masz już aktywne wypożyczenie.");
        }

        Vehicle vehicle = vehicleRepo.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono pojazdu o podanym id."));

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono użytkownika o podanym id."));

        boolean vehicleIsRented = rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicle.getId()).isPresent();

        if (vehicleIsRented) {
            throw new IllegalStateException("Ten pojazd jest już wypożyczony.");
        }

        Rental rental = new Rental(UUID.randomUUID().toString(), vehicle, user);
        return rentalRepo.save(rental);
    }

    @Override
    public Rental returnVehicle(String userId) {
        Rental rental = rentalRepo.findAll().stream()
                .filter(r -> userId.equals(r.getUserId()))
                .filter(Rental::isActive)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nie masz aktualnie wypożyczonego pojazdu."));

        rental.setReturnDateTime(LocalDateTime.now().toString());
        return rentalRepo.save(rental);
    }

    @Override
    public Optional<Rental> findActiveRentalByUserId(String userId) {
        return rentalRepo.findAll().stream()
                .filter(r -> userId.equals(r.getUserId()))
                .filter(Rental::isActive)
                .findFirst();
    }

    @Override
    public List<Rental> findAllRentals() {
        return rentalRepo.findAll();
    }

    @Override
    public List<Rental> findUserRentals(String userId) {
        return rentalRepo.findAll().stream()
                .filter(r -> userId.equals(r.getUserId()))
                .toList();
    }

    @Override
    public boolean userHasActiveRental(String userId) {
        return findActiveRentalByUserId(userId).isPresent();
    }

    @Override
    public boolean vehicleHasActiveRental(String vehicleId) {
        return rentalRepo.findByVehicleIdAndReturnDateIsNull(vehicleId).isPresent();
    }
}