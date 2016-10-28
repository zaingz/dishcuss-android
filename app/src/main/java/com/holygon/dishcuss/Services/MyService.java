package com.holygon.dishcuss.Services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.holygon.dishcuss.Activities.LoginActivity;
import com.holygon.dishcuss.Helper.BusProvider;
import com.holygon.dishcuss.Model.Comment;
import com.holygon.dishcuss.Model.FeaturedRestaurant;
import com.holygon.dishcuss.Model.FoodItems;
import com.holygon.dishcuss.Model.FoodsCategory;
import com.holygon.dishcuss.Model.KhabaHistoryModel;
import com.holygon.dishcuss.Model.LocalFeedCheckIn;
import com.holygon.dishcuss.Model.LocalFeedReview;
import com.holygon.dishcuss.Model.LocalFeeds;
import com.holygon.dishcuss.Model.Owner;
import com.holygon.dishcuss.Model.PhotoModel;
import com.holygon.dishcuss.Model.RecentSearchModel;
import com.holygon.dishcuss.Model.Reply;
import com.holygon.dishcuss.Model.Restaurant;
import com.holygon.dishcuss.Model.ReviewModel;
import com.holygon.dishcuss.Model.SearchModel;
import com.holygon.dishcuss.Model.SpecificPostModel;
import com.holygon.dishcuss.Model.UserBeenThere;
import com.holygon.dishcuss.Model.UserFollowing;
import com.holygon.dishcuss.Model.UserOffersModel;
import com.holygon.dishcuss.Model.UserProfile;
import com.holygon.dishcuss.Utils.Constants;

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
