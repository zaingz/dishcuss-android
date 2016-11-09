package com.dishcuss.foodie.hub.Models;

import java.util.ArrayList;

/**
 * Created by Naeem Ibrahim on 9/7/2016.
 */
public class SearchModel {

    ArrayList<Restaurant> restaurantArrayList;
    ArrayList<UserProfile> userProfileArrayList;

    public SearchModel() {
    }

    public SearchModel(ArrayList<Restaurant> restaurantArrayList, ArrayList<UserProfile> userProfileArrayList) {
        this.restaurantArrayList = restaurantArrayList;
        this.userProfileArrayList = userProfileArrayList;
    }

    public ArrayList<Restaurant> getRestaurantArrayList() {
        return restaurantArrayList;
    }

    public void setRestaurantArrayList(ArrayList<Restaurant> restaurantArrayList) {
        this.restaurantArrayList = restaurantArrayList;
    }

    public ArrayList<UserProfile> getUserProfileArrayList() {
        return userProfileArrayList;
    }

    public void setUserProfileArrayList(ArrayList<UserProfile> userProfileArrayList) {
        this.userProfileArrayList = userProfileArrayList;
    }

}
