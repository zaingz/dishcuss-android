package com.dishcuss.foodies.Services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.dishcuss.foodies.Model.Comment;
import com.dishcuss.foodies.Model.FeaturedRestaurant;
import com.dishcuss.foodies.Model.FoodItems;
import com.dishcuss.foodies.Model.FoodsCategory;
import com.dishcuss.foodies.Model.KhabaHistoryModel;
import com.dishcuss.foodies.Model.LocalFeedCheckIn;
import com.dishcuss.foodies.Model.LocalFeedReview;
import com.dishcuss.foodies.Model.LocalFeeds;
import com.dishcuss.foodies.Model.Owner;
import com.dishcuss.foodies.Model.PhotoModel;
import com.dishcuss.foodies.Model.RecentSearchModel;
import com.dishcuss.foodies.Model.Reply;
import com.dishcuss.foodies.Model.Restaurant;
import com.dishcuss.foodies.Model.SpecificPostModel;
import com.dishcuss.foodies.Model.UserBeenThere;
import com.dishcuss.foodies.Model.UserFollowing;
import com.dishcuss.foodies.Model.UserOffersModel;
import com.dishcuss.foodies.Model.UserProfile;
import com.dishcuss.foodies.Utils.Constants;

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
