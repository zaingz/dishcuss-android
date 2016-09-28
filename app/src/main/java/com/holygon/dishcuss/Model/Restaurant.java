package com.holygon.dishcuss.Model;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Naeem Ibrahim on 8/1/2016.
 */
public class Restaurant extends RealmObject {

    int id;
    String name;
    String opening_time;
    String closing_time;
    String location;
    String type;
    double restaurantLat;
    double restaurantLong;
    double ratting;
    double distanceAway;
    int pricePerHead;
//    "like": [],
//    "follow": [],
    int cover_image_id;
    int ownerID;
    String cover_image_url;
    String cover_image_thumbnail;


    // Check IN
    int check_Ins_ID;
    String check_Ins_Address;
    double check_In_lat;
    double check_In_long;
    String check_In_time;
    int check_Ins_user_ID;
    int check_Ins_restaurant_ID;


    int review_count;
    int bookmark_count;
    int been_here_count;

    String numbers;


    // Reviews
    RealmList<ReviewModel> reviewModels;



    int menuID;
    String menuName;
    String menuSummary;
    RealmList<FoodItems> foodItemsArrayList;




    public Restaurant() {
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getDistanceAway() {
        return distanceAway;
    }

    public void setDistanceAway(double distanceAway) {
        this.distanceAway = distanceAway;
    }

    public int getPricePerHead() {
        return pricePerHead;
    }

    public void setPricePerHead(int pricePerHead) {
        this.pricePerHead = pricePerHead;
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

    public String getNumbers() {
        return numbers;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }

    public int getReview_count() {
        return review_count;
    }

    public void setReview_count(int review_count) {
        this.review_count = review_count;
    }

    public int getBookmark_count() {
        return bookmark_count;
    }

    public void setBookmark_count(int bookmark_count) {
        this.bookmark_count = bookmark_count;
    }

    public int getBeen_here_count() {
        return been_here_count;
    }

    public void setBeen_here_count(int been_here_count) {
        this.been_here_count = been_here_count;
    }

    public RealmList<FoodItems> getFoodItemsArrayList() {
        return foodItemsArrayList;
    }

    public void setFoodItemsArrayList(RealmList<FoodItems> foodItemsArrayList) {
        this.foodItemsArrayList = foodItemsArrayList;
    }

    public RealmList<ReviewModel> getReviewModels() {
        return reviewModels;
    }

    public void setReviewModels(RealmList<ReviewModel> reviewModels) {
        this.reviewModels = reviewModels;
    }

    public int getMenuID() {
        return menuID;
    }

    public void setMenuID(int menuID) {
        this.menuID = menuID;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getMenuSummary() {
        return menuSummary;
    }

    public void setMenuSummary(String menuSummary) {
        this.menuSummary = menuSummary;
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

    public String getOpening_time() {
        return opening_time;
    }

    public void setOpening_time(String opening_time) {
        this.opening_time = opening_time;
    }

    public String getClosing_time() {
        return closing_time;
    }

    public void setClosing_time(String closing_time) {
        this.closing_time = closing_time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getRatting() {
        return ratting;
    }

    public void setRatting(double ratting) {
        this.ratting = ratting;
    }

    public int getCover_image_id() {
        return cover_image_id;
    }

    public void setCover_image_id(int cover_image_id) {
        this.cover_image_id = cover_image_id;
    }

    public int getOwnerID() {
        return ownerID;
    }

    public void setOwnerID(int ownerID) {
        this.ownerID = ownerID;
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

    public int getCheck_Ins_ID() {
        return check_Ins_ID;
    }

    public void setCheck_Ins_ID(int check_Ins_ID) {
        this.check_Ins_ID = check_Ins_ID;
    }

    public String getCheck_Ins_Address() {
        return check_Ins_Address;
    }

    public void setCheck_Ins_Address(String check_Ins_Address) {
        this.check_Ins_Address = check_Ins_Address;
    }

    public double getCheck_In_lat() {
        return check_In_lat;
    }

    public void setCheck_In_lat(double check_In_lat) {
        this.check_In_lat = check_In_lat;
    }

    public double getCheck_In_long() {
        return check_In_long;
    }

    public void setCheck_In_long(double check_In_long) {
        this.check_In_long = check_In_long;
    }

    public String getCheck_In_time() {
        return check_In_time;
    }

    public void setCheck_In_time(String check_In_time) {
        this.check_In_time = check_In_time;
    }

    public int getCheck_Ins_user_ID() {
        return check_Ins_user_ID;
    }

    public void setCheck_Ins_user_ID(int check_Ins_user_ID) {
        this.check_Ins_user_ID = check_Ins_user_ID;
    }

    public int getCheck_Ins_restaurant_ID() {
        return check_Ins_restaurant_ID;
    }

    public void setCheck_Ins_restaurant_ID(int check_Ins_restaurant_ID) {
        this.check_Ins_restaurant_ID = check_Ins_restaurant_ID;
    }
}
