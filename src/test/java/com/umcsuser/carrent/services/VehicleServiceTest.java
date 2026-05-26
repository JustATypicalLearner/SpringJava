package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VehicleServiceTest {

    private VehicleValidator vehicleValidatorMock;
    private VehicleRepository vehicleRepositoryMock;
    private RentalRepository rentalRepositoryMock;
    private VehicleService vehicleService;

    @BeforeEach
    void setUp() {
        vehicleValidatorMock = mock(VehicleValidator.class);
        vehicleRepositoryMock = mock(VehicleRepository.class);
        rentalRepositoryMock = mock(RentalRepository.class);

        vehicleService = new VehicleService(vehicleValidatorMock, vehicleRepositoryMock, rentalRepositoryMock);
    }

    @Test
    void shouldAddVehicleSuccessfully() {
        Vehicle vehicle = Vehicle.builder().id("1").brand("Toyota").model("Yaris").build();

        vehicleService.addVehicle(vehicle);

        verify(vehicleValidatorMock, times(1)).validate(vehicle);
        verify(vehicleRepositoryMock, times(1)).save(vehicle);

        System.out.println("TEST ZDANY: Nowy pojazd został pomyślnie zwalidowany i wysłany do zapisu!");
    }

    @Test
    void shouldReturnAllVehicles() {
        Vehicle v1 = Vehicle.builder().id("1").brand("Toyota").build();
        Vehicle v2 = Vehicle.builder().id("2").brand("BMW").build();
        when(vehicleRepositoryMock.findAll()).thenReturn(List.of(v1, v2));

        List<Vehicle> result = vehicleService.findAllVehicles();

        assertEquals(2, result.size(), "Powinno zwrócić dokładnie 2 pojazdy");
        assertEquals("Toyota", result.get(0).getBrand());

        System.out.println("TEST ZDANY: Pobieranie listy wszystkich pojazdów (2 sztuki) zwróciło poprawne dane!");
    }

    @Test
    void shouldDeleteVehicleWhenNotRented() {
        String vehicleId = "1";
        when(rentalRepositoryMock.isRented(vehicleId)).thenReturn(false);

        vehicleService.deleteVehicle(vehicleId);

        verify(vehicleRepositoryMock, times(1)).deleteById(vehicleId);

        System.out.println("TEST ZDANY: Usunięcie wolnego pojazdu powiodło się (repozytorium otrzymało komendę)!");
    }

    @Test
    void shouldNotDeleteVehicleWhenRented() {
        String vehicleId = "1";
        when(rentalRepositoryMock.isRented(vehicleId)).thenReturn(true);

        vehicleService.deleteVehicle(vehicleId);

        verify(vehicleRepositoryMock, never()).deleteById(anyString());

        System.out.println("TEST ZDANY: Reguła biznesowa zadziałała! Zablokowano próbę usunięcia wypożyczonego pojazdu.");
    }
}