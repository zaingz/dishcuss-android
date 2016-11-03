package com.dishcuss.foodie.Model;

import io.realm.RealmObject;

/**
 * Created by Naeem Ibrahim on 9/7/2016.
 */
public class RecentSearchModel extends RealmObject{

    int id;
    String name;
    String avatar;
    String type;


    public RecentSearchModel() {
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
