package org.example.carrent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VehicleRepositoryTest {

    private IVehicleRepository repo;

    @BeforeEach
    void setUp() {
        repo = new VehicleRepositoryImpl();
    }

    @Test
    void getVehiclesShouldReturnDeepCopy() {
        List<Vehicle> vehicles1 = repo.getVehicles();
        List<Vehicle> vehicles2  = repo.getVehicles();
        assertNotSame(vehicles1, vehicles2);
        if (!vehicles1.isEmpty() && !vehicles2.isEmpty()) {
            assertNotSame(vehicles1.get(0), vehicles2.get(0));
        }
    }

    @Test
    void addingToReturnedListShouldNotChangeRepository() {
        List<Vehicle> vehicles = repo.getVehicles();
        int repoSizeBefore = repo.getVehicles().size();
        vehicles.add(new Car("100", "Test", "Test", 2026, 1, false));
        int repoSizeAfter = repo.getVehicles().size();
        assertEquals(repoSizeBefore, repoSizeAfter);
    }

    @Test
    void changingReturnedVehicleShouldNotChangeRepository() {
        List<Vehicle> vehicles = repo.getVehicles();
        if (vehicles.isEmpty()) {
            repo.add(new Car("1", "Test", "Test", 2022, 100, false));
            vehicles = repo.getVehicles();
        }
        Vehicle copy = vehicles.get(0);
        boolean rented = repo.getVehicle(copy.getId()).isRented();
        copy.setRented(!copy.isRented());
        boolean repoRentedAfterChange = repo.getVehicle(copy.getId()).isRented();
        assertEquals(rented, repoRentedAfterChange);
    }

    @Test
    void addVehicleShouldIncreaseRepositorySize() {
        int initialSize = repo.getVehicles().size();
        repo.add(new Car("200", "New", "Car", 2023, 200, false));
        assertEquals(initialSize + 1, repo.getVehicles().size());
    }

    @Test
    void removeVehicleShouldDecreaseRepositorySize() {
        Car carToAdd = new Car("300", "To", "Remove", 2023, 300, false);
        repo.add(carToAdd);
        int sizeBeforeRemove = repo.getVehicles().size();
        repo.remove("300");
        assertEquals(sizeBeforeRemove - 1, repo.getVehicles().size());
    }


    @Test
    void getVehicleShouldReturnCorrectVehicle() {
        Car car = new Car("400", "Test", "Get", 2023, 400, false);
        repo.add(car);
        Vehicle retrieved = repo.getVehicle("400");
        assertNotNull(retrieved);
        assertEquals("400", retrieved.getId());
    }

    @Test
    void getVehicleShouldReturnNullForNonexistentId() {
        assertNull(repo.getVehicle("nonexistent"));
    }
}