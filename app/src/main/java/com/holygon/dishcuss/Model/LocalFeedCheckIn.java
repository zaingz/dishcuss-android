package com.holygon.dishcuss.Model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Naeem Ibrahim on 8/8/2016.
 */
public class LocalFeedCheckIn extends RealmObject{


    int checkInID;

    String updated_at;
    String checkInTitle;
    String checkInStatus;
    double checkInLat;
    double checkInLong;


    int checkInWriterID;
    String checkInWriterName;
    String checkInWriterLocation;
    String checkInWriterAvatar;

    int checkInOnID;
    String checkInOnName;
    String checkInOnLocation;

    String checkInImage;

    RealmList<PhotoModel> photoModels;

    int reviewLikesCount;
    int reviewCommentCount;
    int reviewSharesCount;

    RealmList<Comment> commentRealmList;

    public LocalFeedCheckIn() {

    }

    public RealmList<Comment> getCommentRealmList() {
        return commentRealmList;
    }

    public void setCommentRealmList(RealmList<Comment> commentRealmList) {
        this.commentRealmList = commentRealmList;
    }

    public int getCheckInID() {
        return checkInID;
    }

    public void setCheckInID(int checkInID) {
        this.checkInID = checkInID;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getCheckInTitle() {
        return checkInTitle;
    }

    public void setCheckInTitle(String checkInTitle) {
        this.checkInTitle = checkInTitle;
    }

    public String getCheckInStatus() {
        return checkInStatus;
    }

    public void setCheckInStatus(String checkInStatus) {
        this.checkInStatus = checkInStatus;
    }

    public double getCheckInLat() {
        return checkInLat;
    }

    public void setCheckInLat(double checkInLat) {
        this.checkInLat = checkInLat;
    }

    public double getCheckInLong() {
        return checkInLong;
    }

    public void setCheckInLong(double checkInLong) {
        this.checkInLong = checkInLong;
    }

    public int getCheckInWriterID() {
        return checkInWriterID;
    }

    public void setCheckInWriterID(int checkInWriterID) {
        this.checkInWriterID = checkInWriterID;
    }

    public String getCheckInWriterName() {
        return checkInWriterName;
    }

    public void setCheckInWriterName(String checkInWriterName) {
        this.checkInWriterName = checkInWriterName;
    }

    public String getCheckInWriterLocation() {
        return checkInWriterLocation;
    }

    public void setCheckInWriterLocation(String checkInWriterLocation) {
        this.checkInWriterLocation = checkInWriterLocation;
    }

    public String getCheckInWriterAvatar() {
        return checkInWriterAvatar;
    }

    public void setCheckInWriterAvatar(String checkInWriterAvatar) {
        this.checkInWriterAvatar = checkInWriterAvatar;
    }

    public int getCheckInOnID() {
        return checkInOnID;
    }

    public void setCheckInOnID(int checkInOnID) {
        this.checkInOnID = checkInOnID;
    }

    public String getCheckInOnName() {
        return checkInOnName;
    }

    public void setCheckInOnName(String checkInOnName) {
        this.checkInOnName = checkInOnName;
    }

    public String getCheckInOnLocation() {
        return checkInOnLocation;
    }

    public void setCheckInOnLocation(String checkInOnLocation) {
        this.checkInOnLocation = checkInOnLocation;
    }

    public String getCheckInImage() {
        return checkInImage;
    }

    public void setCheckInImage(String checkInImage) {
        this.checkInImage = checkInImage;
    }

    public RealmList<PhotoModel> getPhotoModels() {
        return photoModels;
    }

    public void setPhotoModels(RealmList<PhotoModel> photoModels) {
        this.photoModels = photoModels;
    }

    public int getReviewLikesCount() {
        return reviewLikesCount;
    }

    public void setReviewLikesCount(int reviewLikesCount) {
        this.reviewLikesCount = reviewLikesCount;
    }

    public int getReviewCommentCount() {
        return reviewCommentCount;
    }

    public void setReviewCommentCount(int reviewCommentCount) {
        this.reviewCommentCount = reviewCommentCount;
    }

    public int getReviewSharesCount() {
        return reviewSharesCount;
    }

    public void setReviewSharesCount(int reviewSharesCount) {
        this.reviewSharesCount = reviewSharesCount;
    }
}
