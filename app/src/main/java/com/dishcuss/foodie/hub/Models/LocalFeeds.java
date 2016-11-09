package com.dishcuss.foodie.hub.Models;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by Naeem Ibrahim on 8/4/2016.
 */
public class LocalFeeds extends RealmObject{

    RealmList<LocalFeedReview> localFeedReviewRealmList;
    RealmList<LocalFeedCheckIn> localFeedCheckInRealmList;


    public LocalFeeds(){
    }


    public RealmList<LocalFeedReview> getLocalFeedReviewRealmList() {
        return localFeedReviewRealmList;
    }

    public void setLocalFeedReviewRealmList(RealmList<LocalFeedReview> localFeedReviewRealmList) {
        this.localFeedReviewRealmList = localFeedReviewRealmList;
    }

    public RealmList<LocalFeedCheckIn> getLocalFeedCheckInRealmList() {
        return localFeedCheckInRealmList;
    }

    public void setLocalFeedCheckInRealmList(RealmList<LocalFeedCheckIn> localFeedCheckInRealmList) {
        this.localFeedCheckInRealmList = localFeedCheckInRealmList;
    }
}