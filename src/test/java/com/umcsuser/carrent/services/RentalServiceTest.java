package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.RentalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RentalServiceTest {

    private RentalRepository rentalRepositoryMock;
    private VehicleService vehicleServiceMock;
    private RentalService rentalService;

    @BeforeEach
    void setUp() {
        rentalRepositoryMock = mock(RentalRepository.class);
        vehicleServiceMock = mock(VehicleService.class);
        rentalService = new RentalService(rentalRepositoryMock, vehicleServiceMock);
    }

    @Test
    void shouldRentAvailableVehicleSuccessfully() {
        Vehicle car = Vehicle.builder().id("1").brand("Ford").build();
        when(vehicleServiceMock.findVehicleById("1")).thenReturn(Optional.of(car));
        when(rentalRepositoryMock.isRented("1")).thenReturn(false);
        when(rentalRepositoryMock.getRentalsByUser("szef")).thenReturn(Collections.emptyList());

        boolean success = rentalService.rentVehicle("szef", "1");

        assertTrue(success, "Serwis powinien pozwolić na wypożyczenie wolnego auta");
        verify(rentalRepositoryMock, times(1)).addRental(any(Rental.class));

        System.out.println("TEST ZDANY: Użytkownik pomyślnie wypożyczył wolny i istniejący pojazd!");
    }

    @Test
    void shouldNotRentAlreadyRentedVehicle() {
        Vehicle car = Vehicle.builder().id("1").brand("Ford").build();
        when(vehicleServiceMock.findVehicleById("1")).thenReturn(Optional.of(car));
        when(rentalRepositoryMock.isRented("1")).thenReturn(true);
        when(rentalRepositoryMock.getRentalsByUser("szef")).thenReturn(Collections.emptyList());

        boolean success = rentalService.rentVehicle("szef", "1");

        assertFalse(success, "Serwis powinien zablokować wypożyczenie zajętego auta");
        verify(rentalRepositoryMock, never()).addRental(any(Rental.class));

        System.out.println("TEST ZDANY: Zablokowano próbę wypożyczenia pojazdu, który jest aktualnie zajęty!");
    }

    @Test
    void shouldNotRentNonExistentVehicle() {
        when(vehicleServiceMock.findVehicleById("10")).thenReturn(Optional.empty());

        boolean success = rentalService.rentVehicle("szef", "10");

        assertFalse(success, "Wypożyczenie nieistniejącego auta musi zwrócić false");
        verify(rentalRepositoryMock, never()).addRental(any(Rental.class));

        System.out.println("TEST ZDANY: System odrzucił ID pojazdu, którego nie ma!");
    }

    @Test
    void shouldGetRentedVehicleData() {
        Rental activeRental = new Rental("szef", "1");
        when(rentalRepositoryMock.getRentalsByUser("szef")).thenReturn(List.of(activeRental));

        List<Rental> userRentals = rentalService.getRentalsByUser("szef");

        assertFalse(userRentals.isEmpty());
        assertEquals("1", userRentals.get(0).getVehicleId());

        System.out.println("TEST ZDANY: System poprawnie odzyskał dane o wypożyczonym pojeździe użytkownika!");
    }

    @Test
    void shouldReturnVehicleSuccessfully() {
        Rental activeRental = new Rental("szef", "1");
        when(rentalRepositoryMock.getRentalsByUser("szef")).thenReturn(List.of(activeRental));
        when(rentalRepositoryMock.removeRentalByVehicleId("1")).thenReturn(true);

        boolean success = rentalService.returnVehicle("szef");

        assertTrue(success, "Zwrot pojazdu powinien się powieść");
        verify(rentalRepositoryMock, times(1)).removeRentalByVehicleId("1");

        System.out.println("TEST ZDANY: Pojazd został pomyślnie zwrócony i wypisany z rejestru użytkownika!");
    }
}