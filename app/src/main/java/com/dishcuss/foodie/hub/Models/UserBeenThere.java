package com.dishcuss.foodie.hub.Models;

import io.realm.RealmObject;

/**
 * Created by Naeem Ibrahim on 8/17/2016.
 */
public class UserBeenThere extends RealmObject {

    int id;
    String restaurantName;
    String restaurantLocation;
    String beenThereTime;
    int cover_image_id;
    String cover_image_url;
    String cover_image_thumbnail;

    public UserBeenThere() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getRestaurantLocation() {
        return restaurantLocation;
    }

    public void setRestaurantLocation(String restaurantLocation) {
        this.restaurantLocation = restaurantLocation;
    }

    public String getBeenThereTime() {
        return beenThereTime;
    }

    public void setBeenThereTime(String beenThereTime) {
        this.beenThereTime = beenThereTime;
    }

    public int getCover_image_id() {
        return cover_image_id;
    }

    public void setCover_image_id(int cover_image_id) {
        this.cover_image_id = cover_image_id;
    }

    public String getCover_image_url() {
        return cover_image_url;
    }

    public void setCover_image_url(String cover_image_url) {
        this.cover_image_url = cover_image_url;
    }

    public String getCover_image_thumbnail() {
        return cover_image_thumbnail;
    }

    public void setCover_image_thumbnail(String cover_image_thumbnail) {
        this.cover_image_thumbnail = cover_image_thumbnail;
    }
}
