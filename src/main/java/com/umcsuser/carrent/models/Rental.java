package com.umcsuser.carrent.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "rental")
public class Rental implements Serializable {

    @Id
    @Column(nullable = false, unique = true)
    private String id;    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "rent_date", nullable = false)
    private String rentDateTime;

    @Column(name = "return_date")
    private String returnDateTime;    public Rental() {
    }    public Rental(String id, Vehicle vehicle, User user) {
        this.id = id;
        this.vehicle = vehicle;
        this.user = user;
        this.rentDateTime = LocalDateTime.now().toString();
    }    public Rental(String id, Vehicle vehicle, User user, String rentDateTime, String returnDateTime) {
        this.id = id;
        this.vehicle = vehicle;
        this.user = user;
        this.rentDateTime = rentDateTime;
        this.returnDateTime = returnDateTime;
    }    public String getId() { return id; }
    public Vehicle getVehicle() { return vehicle; }
    public User getUser() { return user; }
    public String getRentDateTime() { return rentDateTime; }
    public String getReturnDateTime() { return returnDateTime; }    public String getVehicleId() {
        return vehicle == null ? null : vehicle.getId();
    }

    public String getUserId() {
        return user == null ? null : user.getId();
    }    public void setId(String id) { this.id = id; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public void setUser(User user) { this.user = user; }
    public void setRentDateTime(String rentDateTime) { this.rentDateTime = rentDateTime; }
    public void setReturnDateTime(String returnDateTime) { this.returnDateTime = returnDateTime; }    public boolean isActive() {
        return returnDateTime == null || returnDateTime.isBlank();
    }

    public Rental copy() {
        return new Rental(this.id, this.vehicle, this.user, this.rentDateTime, this.returnDateTime);
    }    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rental rental = (Rental) o;
        return Objects.equals(id, rental.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Rental{" +
                "id='" + id + '\'' +
                ", vehicleId=" + (vehicle != null ? vehicle.getId() : "null") +
                ", userId=" + (user != null ? user.getId() : "null") +
                ", rentDateTime='" + rentDateTime + '\'' +
                ", returnDateTime='" + returnDateTime + '\'' +
                '}';
    }
}