package com.holygon.dishcuss.Model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Naeem Ibrahim on 8/8/2016.
 */
public class LocalFeedReview extends RealmObject {


    int reviewID;

    String updated_at;
    String title;
    String summary;
    int rating;
    int reviewable_id;
    String reviewable_type;

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

    public LocalFeedReview(){

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
}
