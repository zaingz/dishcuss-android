package com.dishcuss.foodies.Model;

import io.realm.RealmObject;

/**
 * Created by Naeem Ibrahim on 8/18/2016.
 */
public class Notifications extends RealmObject {

    int id;
    int userID;
    String body;
    String username;
    String avatarPic;

    int redirectID;
    String redirectType;

    public Notifications() {
    }


    public int getRedirectID() {
        return redirectID;
    }

    public void setRedirectID(int redirectID) {
        this.redirectID = redirectID;
    }

    public String getRedirectType() {
        return redirectType;
    }

    public void setRedirectType(String redirectType) {
        this.redirectType = redirectType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarPic() {
        return avatarPic;
    }

    public void setAvatarPic(String avatarPic) {
        this.avatarPic = avatarPic;
    }
}
