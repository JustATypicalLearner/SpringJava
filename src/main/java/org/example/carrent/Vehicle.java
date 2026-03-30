package org.example.carrent;

public abstract class Vehicle {
    private String id;
    private String brand;
    private String model;
    private int year;
    private double price;
    private boolean rented;

    public Vehicle(String id, String brand, String model, int year, double price, boolean rented) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
        this.rented = rented;
    }

    public Vehicle(Vehicle v){
        this.id = v.id;
        this.brand = v.brand;
        this.model = v.model;
        this.year = v.year;
        this.price = v.price;
        this.rented = v.rented;
    }
    public abstract Vehicle copy();

    public String getId() {
        return id;
    }

    public String toCsv(){
        return id+";"+brand+";"+model+";"+year+";"+price+";"+rented;
    }

    public boolean isRented() {
        return rented;
    }

    public void setRented(boolean rented) {
        this.rented = rented;
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Brand: " + brand + ", Model: " + model + ", Year: " + year + ", Price: " + price + ", Rented: " + rented;
    }
}
