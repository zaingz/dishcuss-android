package com.dishcuss.foodie.hub.Models;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Naeem Ibrahim on 8/8/2016.
 */
public class LocalFeedReview extends RealmObject implements Parcelable {


    int reviewID;

    String updated_at;
    String title;
    String summary;
    int rating;
    int reviewable_id;
    String reviewable_type;
    Boolean isBookmarked;
    Boolean isLiked;

    int reviewOnID;
    String reviewOnName;
    String reviewOnLocation;

    String reviewImage;

    int reviewerID;
    String reviewerName;
    String reviewerLocation;
    String reviewerAvatar;

    int reviewLikesCount;
    int reviewCommentCount;
    int reviewSharesCount;

    RealmList<Comment> commentRealmList;



    public LocalFeedReview(){

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

    public int getReviewID() {
        return reviewID;
    }

    public void setReviewID(int reviewID) {
        this.reviewID = reviewID;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getReviewable_id() {
        return reviewable_id;
    }

    public void setReviewable_id(int reviewable_id) {
        this.reviewable_id = reviewable_id;
    }

    public String getReviewable_type() {
        return reviewable_type;
    }

    public void setReviewable_type(String reviewable_type) {
        this.reviewable_type = reviewable_type;
    }

    public int getReviewOnID() {
        return reviewOnID;
    }

    public void setReviewOnID(int reviewOnID) {
        this.reviewOnID = reviewOnID;
    }

    public String getReviewOnName() {
        return reviewOnName;
    }

    public void setReviewOnName(String reviewOnName) {
        this.reviewOnName = reviewOnName;
    }

    public String getReviewOnLocation() {
        return reviewOnLocation;
    }

    public void setReviewOnLocation(String reviewOnLocation) {
        this.reviewOnLocation = reviewOnLocation;
    }

    public String getReviewImage() {
        return reviewImage;
    }

    public void setReviewImage(String reviewImage) {
        this.reviewImage = reviewImage;
    }

    public int getReviewerID() {
        return reviewerID;
    }

    public void setReviewerID(int reviewerID) {
        this.reviewerID = reviewerID;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getReviewerLocation() {
        return reviewerLocation;
    }

    public void setReviewerLocation(String reviewerLocation) {
        this.reviewerLocation = reviewerLocation;
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

    public String getReviewerAvatar() {
        return reviewerAvatar;
    }

    public void setReviewerAvatar(String reviewerAvatar) {
        this.reviewerAvatar = reviewerAvatar;
    }

    public LocalFeedReview(Parcel in){
        this.reviewID = in.readInt();

        this.reviewerName = in.readString();
        this.reviewerAvatar = in.readString();
        this.updated_at = in.readString();
        this.summary = in.readString();
        this.reviewImage = in.readString();

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
        dest.writeInt(reviewID);

        dest.writeString(reviewerName);
        dest.writeString(reviewerAvatar);
        dest.writeString(updated_at);
        dest.writeString(summary);
        dest.writeString(reviewImage);


        dest.writeInt(reviewLikesCount);
        dest.writeInt(reviewCommentCount);
        dest.writeInt(reviewSharesCount);

        dest.writeTypedList(commentRealmList);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public LocalFeedReview createFromParcel(Parcel in) {
            return new LocalFeedReview(in);
        }

        public LocalFeedReview[] newArray(int size) {
            return new LocalFeedReview[size];
        }
    };
}
