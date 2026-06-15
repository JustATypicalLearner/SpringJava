package com.umcsuser.carrent.web;

import com.umcsuser.carrent.dto.RentalRequest;
import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.services.RentalServiceInterface;
import com.umcsuser.carrent.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalServiceInterface rentalService;
    private final UserService userService;

    public RentalController(RentalServiceInterface rentalService, UserService userService) {
        this.rentalService = rentalService;
        this.userService = userService;
    }

    @PostMapping("/rent")
    public ResponseEntity<Rental> rent(
            @RequestBody RentalRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String login = userDetails.getUsername();
        User user = userService.findByLogin(login);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rentalService.rentVehicle(user.getId(), request.vehicleId()));
    }

    @PostMapping("/return")
    public ResponseEntity<Rental> returnVehicle(
            @AuthenticationPrincipal UserDetails userDetails) {
        String login = userDetails.getUsername();
        User user = userService.findByLogin(login);
        return ResponseEntity.ok(rentalService.returnVehicle(user.getId()));
    }
}
