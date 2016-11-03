package com.dishcuss.foodie.Model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Naeem Ibrahim on 10/10/2016.
 */
public class SpecificPostModel extends RealmObject{

    int checkInID;
    String updated_at;
    String checkInStatus;
    int checkInWriterID;
    String checkInWriterName;
    String checkInWriterAvatar;
    String checkInImage;
    boolean isLiked;

    int checkInOnID;
    double checkInOnLat;
    double checkInOnLong;

    int reviewLikesCount;
    int reviewCommentCount;
    int reviewSharesCount;

    RealmList<Comment> commentRealmList;

    public SpecificPostModel() {
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public int getCheckInOnID() {
        return checkInOnID;
    }

    public void setCheckInOnID(int checkInOnID) {
        this.checkInOnID = checkInOnID;
    }

    public double getCheckInOnLong() {
        return checkInOnLong;
    }

    public void setCheckInOnLong(double checkInOnLong) {
        this.checkInOnLong = checkInOnLong;
    }

    public double getCheckInOnLat() {
        return checkInOnLat;
    }

    public void setCheckInOnLat(double checkInOnLat) {
        this.checkInOnLat = checkInOnLat;
    }

    public int getCheckInWriterID() {
        return checkInWriterID;
    }

    public void setCheckInWriterID(int checkInWriterID) {
        this.checkInWriterID = checkInWriterID;
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

    public String getCheckInStatus() {
        return checkInStatus;
    }

    public void setCheckInStatus(String checkInStatus) {
        this.checkInStatus = checkInStatus;
    }

    public String getCheckInWriterName() {
        return checkInWriterName;
    }

    public void setCheckInWriterName(String checkInWriterName) {
        this.checkInWriterName = checkInWriterName;
    }

    public String getCheckInWriterAvatar() {
        return checkInWriterAvatar;
    }

    public void setCheckInWriterAvatar(String checkInWriterAvatar) {
        this.checkInWriterAvatar = checkInWriterAvatar;
    }

    public String getCheckInImage() {
        return checkInImage;
    }

    public void setCheckInImage(String checkInImage) {
        this.checkInImage = checkInImage;
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

    public RealmList<Comment> getCommentRealmList() {
        return commentRealmList;
    }

    public void setCommentRealmList(RealmList<Comment> commentRealmList) {
        this.commentRealmList = commentRealmList;
    }
}
