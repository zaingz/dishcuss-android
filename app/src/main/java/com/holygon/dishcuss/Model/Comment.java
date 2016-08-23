package com.holygon.dishcuss.Model;


import io.realm.RealmObject;

/**
 * Created by Naeem Ibrahim on 8/16/2016.
 */
public class Comment extends RealmObject {

    int commentID;
    String commentUpdated_at;
    String commentTitle;
    String commentSummary;

    int commentatorID;
    String commentatorName;
    String commentatorImage;

    int commentLikesCount;

    public Comment() {
    }


    public int getCommentID() {
        return commentID;
    }

    public void setCommentID(int commentID) {
        this.commentID = commentID;
    }

    public String getCommentUpdated_at() {
        return commentUpdated_at;
    }

    public void setCommentUpdated_at(String commentUpdated_at) {
        this.commentUpdated_at = commentUpdated_at;
    }

    public String getCommentTitle() {
        return commentTitle;
    }

    public void setCommentTitle(String commentTitle) {
        this.commentTitle = commentTitle;
    }

    public String getCommentSummary() {
        return commentSummary;
    }

    public void setCommentSummary(String commentSummary) {
        this.commentSummary = commentSummary;
    }

    public int getCommentatorID() {
        return commentatorID;
    }

    public void setCommentatorID(int commentatorID) {
        this.commentatorID = commentatorID;
    }

    public String getCommentatorName() {
        return commentatorName;
    }

    public void setCommentatorName(String commentatorName) {
        this.commentatorName = commentatorName;
    }

    public String getCommentatorImage() {
        return commentatorImage;
    }

    public void setCommentatorImage(String commentatorImage) {
        this.commentatorImage = commentatorImage;
    }

    public int getCommentLikesCount() {
        return commentLikesCount;
    }

    public void setCommentLikesCount(int commentLikesCount) {
        this.commentLikesCount = commentLikesCount;
    }
}
