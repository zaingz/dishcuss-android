package com.dishcuss.foodie.hub.Services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.dishcuss.foodie.Model.Comment;
import com.dishcuss.foodie.Model.FeaturedRestaurant;
import com.dishcuss.foodie.Model.FoodItems;
import com.dishcuss.foodie.Model.FoodsCategory;
import com.dishcuss.foodie.Model.KhabaHistoryModel;
import com.dishcuss.foodie.Model.LocalFeedCheckIn;
import com.dishcuss.foodie.Model.LocalFeedReview;
import com.dishcuss.foodie.Model.LocalFeeds;
import com.dishcuss.foodie.Model.Owner;
import com.dishcuss.foodie.Model.PhotoModel;
import com.dishcuss.foodie.Model.RecentSearchModel;
import com.dishcuss.foodie.Model.Reply;
import com.dishcuss.foodie.Model.Restaurant;
import com.dishcuss.foodie.Model.SpecificPostModel;
import com.dishcuss.foodie.Model.UserBeenThere;
import com.dishcuss.foodie.Model.UserFollowing;
import com.dishcuss.foodie.Model.UserOffersModel;
import com.dishcuss.foodie.Model.UserProfile;
import com.dishcuss.foodie.hub.Utils.Constants;

import io.realm.Realm;

/**
 * Created by Naeem Ibrahim on 10/24/2016.
 */

public class MyService extends Service {

    public MyService() {
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
//        Toast.makeText(getBaseContext(),"OnTaskRemoved",Toast.LENGTH_LONG).show();
        if(!Constants.GetUserLoginStatus(getBaseContext())){
             Constants.clearApplicationData(getBaseContext());
        }
        
//        RealmDeleteALL();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public int onStartCommand(Intent intent, int flags, int startId){
        return Service.START_STICKY;
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    void RealmDeleteALL(){

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();

        realm.delete(Comment.class);
        realm.delete(FeaturedRestaurant.class);
        realm.delete(FoodItems.class);
        realm.delete(FoodsCategory.class);
        realm.delete(KhabaHistoryModel.class);
        realm.delete(LocalFeedCheckIn.class);
        realm.delete(LocalFeedReview.class);
        realm.delete(LocalFeeds.class);
        realm.delete(Owner.class);
        realm.delete(PhotoModel.class);
        realm.delete(Reply.class);
        realm.delete(RecentSearchModel.class);
        realm.delete(Restaurant.class);
//        realm.delete(ReviewModel.class);
        realm.delete(SpecificPostModel.class);
        realm.delete(UserBeenThere.class);
        realm.delete(UserFollowing.class);
        realm.delete(UserOffersModel.class);
        realm.delete(UserProfile.class);

        realm.commitTransaction();
        realm.close();
    }

}
