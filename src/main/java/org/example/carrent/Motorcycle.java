package org.example.carrent;

public class Motorcycle extends Vehicle {
    String drivingLicence;

    public Motorcycle(String id, String brand, String model, int year, double price, boolean rented, String drivingLicence) {
        super(id, brand, model, year, price, rented);
        this.drivingLicence = drivingLicence;
    }

    public Motorcycle(Vehicle v, String drivingLicence) {
        super(v);
        this.drivingLicence = drivingLicence;
    }

    public Motorcycle(Motorcycle m){
        super(m);
        this.drivingLicence = m.drivingLicence;
    }

    public String toCsv(){
        return "MOTORCYCLE;"+super.toCsv() + ";" + drivingLicence;
    }

    @Override
    public Motorcycle copy(){
        return new Motorcycle(this);
    }

    @Override
    public String toString() {
        return "Motorcycle - " + super.toString() + ", Driving Licence: " + drivingLicence;
    }
}
