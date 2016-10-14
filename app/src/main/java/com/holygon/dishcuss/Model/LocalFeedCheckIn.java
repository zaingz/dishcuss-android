package com.holygon.dishcuss.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Naeem Ibrahim on 8/8/2016.
 */
public class LocalFeedCheckIn extends RealmObject implements Parcelable{


    int checkInID;

    String updated_at;
    String checkInTitle;
    String checkInStatus;
    double checkInLat;
    double checkInLong;
    Boolean isBookmarked;
    Boolean isLiked;

    int checkInWriterID;
    String checkInWriterName;
    String checkInWriterLocation;
    String checkInWriterAvatar;

    int checkInOnID;
    String checkInOnName;
    String checkInOnImage;
    String checkInOnLocation;

    String checkInImage;

    public String getCheckInOnImage() {
        return checkInOnImage;
    }

    public void setCheckInOnImage(String checkInOnImage) {
        this.checkInOnImage = checkInOnImage;
    }

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

    public Boolean getLiked() {
        return isLiked;
    }

    public void setLiked(Boolean liked) {
        isLiked = liked;
    }

    public Boolean getBookmarked() {
        return isBookmarked;
    }

    public void setBookmarked(Boolean bookmarked) {
        isBookmarked = bookmarked;
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


    public LocalFeedCheckIn(Parcel in) {
        this.checkInID=in.readInt();
        this.checkInWriterName = in.readString();
        this.checkInWriterAvatar = in.readString();
        this.updated_at = in.readString();
        this.checkInStatus = in.readString();
        this.checkInImage = in.readString();
        this.reviewLikesCount = in.readInt();
        this.reviewCommentCount = in.readInt();
        this.reviewSharesCount = in.readInt();
        this.commentRealmList=new RealmList<Comment>();
        in.readTypedList(this.commentRealmList,Comment.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(checkInID);
        dest.writeString(checkInWriterName);
        dest.writeString(checkInWriterAvatar);
        dest.writeString(updated_at);
        dest.writeString(checkInStatus);
        dest.writeString(checkInImage);
        dest.writeInt(reviewLikesCount);
        dest.writeInt(reviewCommentCount);
        dest.writeInt(reviewSharesCount);
        dest.writeTypedList(commentRealmList);
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public LocalFeedCheckIn createFromParcel(Parcel in) {
            return new LocalFeedCheckIn(in);
        }

        public LocalFeedCheckIn[] newArray(int size) {
            return new LocalFeedCheckIn[size];
        }
    };
}
