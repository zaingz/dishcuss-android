package com.dishcuss.foodie.hub.Models;

import io.realm.RealmObject;

/**
 * Created by Naeem Ibrahim on 8/25/2016.
 */
public class UserOffersModel extends RealmObject {

    int id;
    int points;
    String description;
    String img;
    int restaurantID;
    String restaurantName;
    String restaurantOpeningTime;
    String restaurantClosingTime;
    String restaurantLocation;

    public UserOffersModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(int restaurantID) {
        this.restaurantID = restaurantID;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantOpeningTime() {
        return restaurantOpeningTime;
    }

    public void setRestaurantOpeningTime(String restaurantOpeningTime) {
        this.restaurantOpeningTime = restaurantOpeningTime;
    }

    public String getRestaurantClosingTime() {
        return restaurantClosingTime;
    }

    public void setRestaurantClosingTime(String restaurantClosingTime) {
        this.restaurantClosingTime = restaurantClosingTime;
    }

    public String getRestaurantLocation() {
        return restaurantLocation;
    }

    public void setRestaurantLocation(String restaurantLocation) {
        this.restaurantLocation = restaurantLocation;
    }
}
