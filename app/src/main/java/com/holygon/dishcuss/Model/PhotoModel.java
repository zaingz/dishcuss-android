package com.holygon.dishcuss.Model;

import io.realm.RealmObject;

/**
 * Created by Naeem Ibrahim on 8/4/2016.
 */
public class PhotoModel extends RealmObject {

    int id;
    String url;

    public PhotoModel() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
