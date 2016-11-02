package com.dishcuss.foodies.Model;

import io.realm.RealmObject;

/**
 * Created by Naeem Ibrahim on 8/26/2016.
 */
public class KhabaHistoryModel extends RealmObject {

    int id;
    int price;
    String credit_time;

    int restaurantID;
    String restaurantName;
    String restaurantOpeningTime;
    String restaurantClosingTime;
    String restaurantLocation;
    String restaurantImage;

    public KhabaHistoryModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getCredit_time() {
        return credit_time;
    }

    public void setCredit_time(String credit_time) {
        this.credit_time = credit_time;
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

    public String getRestaurantImage() {
        return restaurantImage;
    }

    public void setRestaurantImage(String restaurantImage) {
        this.restaurantImage = restaurantImage;
    }
}
