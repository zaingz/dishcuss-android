package com.holygon.dishcuss.Model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Naeem Ibrahim on 8/17/2016.
 */
public class UserProfile extends RealmObject {

    int id;
    String name;
    String username;
    String email;
    String gender;
    String location;
    String avatar;
    String role;

    int reviewsCount,followersCount,commentsCount;


    RealmList<ReviewModel> reviewModelRealmList;

    RealmList<PhotoModel> photoModelRealmList;

    RealmList<UserFollowing> userFollowersRealmList;

    RealmList<UserFollowing> userFollowingRealmList;

    RealmList<UserBeenThere> userBeenThereRealmList;

    RealmList<Comment> commentRealmList;

    public UserProfile() {
    }


    public int getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(int reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public RealmList<Comment> getCommentRealmList() {
        return commentRealmList;
    }

    public void setCommentRealmList(RealmList<Comment> commentRealmList) {
        this.commentRealmList = commentRealmList;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public RealmList<ReviewModel> getReviewModelRealmList() {
        return reviewModelRealmList;
    }

    public void setReviewModelRealmList(RealmList<ReviewModel> reviewModelRealmList) {
        this.reviewModelRealmList = reviewModelRealmList;
    }

    public RealmList<PhotoModel> getPhotoModelRealmList() {
        return photoModelRealmList;
    }

    public void setPhotoModelRealmList(RealmList<PhotoModel> photoModelRealmList) {
        this.photoModelRealmList = photoModelRealmList;
    }

    public RealmList<UserFollowing> getUserFollowersRealmList() {
        return userFollowersRealmList;
    }

    public void setUserFollowersRealmList(RealmList<UserFollowing> userFollowersRealmList) {
        this.userFollowersRealmList = userFollowersRealmList;
    }

    public RealmList<UserFollowing> getUserFollowingRealmList() {
        return userFollowingRealmList;
    }

    public void setUserFollowingRealmList(RealmList<UserFollowing> userFollowingRealmList) {
        this.userFollowingRealmList = userFollowingRealmList;
    }

    public RealmList<UserBeenThere> getUserBeenThereRealmList() {
        return userBeenThereRealmList;
    }

    public void setUserBeenThereRealmList(RealmList<UserBeenThere> userBeenThereRealmList) {
        this.userBeenThereRealmList = userBeenThereRealmList;
    }
}
