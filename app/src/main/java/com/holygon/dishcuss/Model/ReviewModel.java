package com.holygon.dishcuss.Model;

import io.realm.RealmObject;

/**
 * Created by Naeem Ibrahim on 8/4/2016.
 */
public class ReviewModel extends RealmObject {



    int review_ID;
    String review_title;
    String review_summary;
    int reviewable_id;
    String reviewable_type;
    int review_reviewer_ID;
    String review_reviewer_Name;
    String review_reviewer_time;
    String review_reviewer_Avatar;
    int review_Likes_count;
    int review_comments_count;
    int review_shares_count;

    public ReviewModel() {
    }

    public int getReview_ID() {
        return review_ID;
    }

    public void setReview_ID(int review_ID) {
        this.review_ID = review_ID;
    }

    public String getReview_title() {
        return review_title;
    }

    public void setReview_title(String review_title) {
        this.review_title = review_title;
    }

    public String getReview_summary() {
        return review_summary;
    }

    public void setReview_summary(String review_summary) {
        this.review_summary = review_summary;
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

    public int getReview_reviewer_ID() {
        return review_reviewer_ID;
    }

    public void setReview_reviewer_ID(int review_reviewer_ID) {
        this.review_reviewer_ID = review_reviewer_ID;
    }

    public String getReview_reviewer_Name() {
        return review_reviewer_Name;
    }

    public void setReview_reviewer_Name(String review_reviewer_Name) {
        this.review_reviewer_Name = review_reviewer_Name;
    }

    public String getReview_reviewer_time() {
        return review_reviewer_time;
    }

    public void setReview_reviewer_time(String review_reviewer_time) {
        this.review_reviewer_time = review_reviewer_time;
    }

    public String getReview_reviewer_Avatar() {
        return review_reviewer_Avatar;
    }

    public void setReview_reviewer_Avatar(String review_reviewer_Avatar) {
        this.review_reviewer_Avatar = review_reviewer_Avatar;
    }

    public int getReview_Likes_count() {
        return review_Likes_count;
    }

    public void setReview_Likes_count(int review_Likes_count) {
        this.review_Likes_count = review_Likes_count;
    }

    public int getReview_comments_count() {
        return review_comments_count;
    }

    public void setReview_comments_count(int review_comments_count) {
        this.review_comments_count = review_comments_count;
    }

    public int getReview_shares_count() {
        return review_shares_count;
    }

    public void setReview_shares_count(int review_shares_count) {
        this.review_shares_count = review_shares_count;
    }
}
