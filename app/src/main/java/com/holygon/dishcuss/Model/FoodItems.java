package com.holygon.dishcuss.Model;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Naeem Ibrahim on 8/3/2016.
 */
public class FoodItems extends RealmObject {


    int foodID;
    String name;
    int Price;

    RealmList<FoodsCategory> foodsCategories;
    RealmList<PhotoModel> photoModels;


    public FoodItems(){

    }

    public RealmList<FoodsCategory> getFoodsCategories() {
        return foodsCategories;
    }

    public void setFoodsCategories(RealmList<FoodsCategory> foodsCategories) {
        this.foodsCategories = foodsCategories;
    }

    public RealmList<PhotoModel> getPhotoModels() {
        return photoModels;
    }

    public void setPhotoModels(RealmList<PhotoModel> photoModels) {
        this.photoModels = photoModels;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFoodID() {
        return foodID;
    }

    public void setFoodID(int foodID) {
        this.foodID = foodID;
    }
}
