package com.dishcuss.foodie.Model;

import io.realm.RealmObject;

/**
 * Created by Naeem Ibrahim on 8/4/2016.
 */
public class FoodsCategory extends RealmObject {

    int id;
    String categoryName;

    public FoodsCategory() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

}
