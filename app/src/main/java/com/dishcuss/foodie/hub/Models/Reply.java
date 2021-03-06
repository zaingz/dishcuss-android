package com.dishcuss.foodie.hub.Models;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;

/**
 * Created by Naeem Ibrahim on 10/13/2016.
 */
public class Reply  extends RealmObject implements Parcelable {

    int commentID;
    String commentUpdated_at;
    String commentTitle;
    String commentSummary;

    int commentatorID;
    String commentatorName;
    String commentatorImage;

    int commentLikesCount;

    public Reply() {
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

    public Reply(Parcel in) {
        this.commentID = in.readInt();
        this.commentUpdated_at = in.readString();
        this.commentTitle = in.readString();
        this.commentSummary = in.readString();
        this.commentatorID = in.readInt();
        this.commentatorName = in.readString();
        this.commentatorImage = in.readString();
        this.commentLikesCount = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(commentID);
        dest.writeString(commentUpdated_at);
        dest.writeString(commentTitle);
        dest.writeString(commentSummary);
        dest.writeInt(commentatorID);
        dest.writeString(commentatorName);
        dest.writeString(commentatorImage);
        dest.writeInt(commentLikesCount);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Reply createFromParcel(Parcel in) {
            return new Reply(in);
        }

        public Reply[] newArray(int size) {
            return new Reply[size];
        }
    };
}
