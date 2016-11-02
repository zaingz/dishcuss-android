package com.dishcuss.foodies.Model;

import io.realm.RealmObject;

/**
 * Created by Naeem Ibrahim on 8/1/2016.
 */

public class FeaturedRestaurant extends RealmObject {

    int id;

    String name;
    String location;
    int cover_image_id;
    int ownerID;
    String cover_image_url;
    String cover_image_thumbnail;

    public FeaturedRestaurant(){

    }


    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
