package com.holygon.dishcuss.Model;

/**
 * Created by Naeem Ibrahim on 10/28/2016.
 */

public class SearchRestaurant {


    int id;
    String name;
    double restaurantLat;
    double restaurantLong;


    public SearchRestaurant() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRestaurantLat() {
        return restaurantLat;
    }

    public void setRestaurantLat(double restaurantLat) {
        this.restaurantLat = restaurantLat;
    }

    public double getRestaurantLong() {
        return restaurantLong;
    }

    public void setRestaurantLong(double restaurantLong) {
        this.restaurantLong = restaurantLong;
    }
}
