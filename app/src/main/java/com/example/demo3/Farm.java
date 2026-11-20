package com.example.demo3;

import java.io.Serializable;

public class Farm implements Serializable {
    private String id;
    private String name;
    private String address;
    private double lat;
    private double lng;
    private double rating;
    private String fruits;
    private String price;
    private String phone;
    private String hours;

    public Farm(String id, String name, String address, double lat, double lng,
                double rating, String fruits, String price, String phone, String hours) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.rating = rating;
        this.fruits = fruits;
        this.price = price;
        this.phone = phone;
        this.hours = hours;
    }

    // Getter 方法
    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }
    public double getRating() { return rating; }
    public String getFruits() { return fruits; }
    public String getPrice() { return price; }
    public String getPhone() { return phone; }
    public String getHours() { return hours; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setFruits(String fruits) { this.fruits = fruits; }
    public void setPrice(String price) { this.price = price; }
    public void setHours(String hours) { this.hours = hours; }
    public void setRating(double rating) { this.rating = rating; }

}