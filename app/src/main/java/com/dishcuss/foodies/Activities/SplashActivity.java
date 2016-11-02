package com.dishcuss.foodies.Activities;

import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.dishcuss.foodies.Model.Comment;
import com.dishcuss.foodies.Model.FeaturedRestaurant;
import com.dishcuss.foodies.Model.FoodItems;
import com.dishcuss.foodies.Model.KhabaHistoryModel;
import com.dishcuss.foodies.Model.LocalFeedCheckIn;
import com.dishcuss.foodies.Model.LocalFeedReview;
import com.dishcuss.foodies.Model.LocalFeeds;
import com.dishcuss.foodies.Model.Restaurant;
import com.dishcuss.foodies.Model.RestaurantForStatus;
import com.dishcuss.foodies.Model.ReviewModel;
import com.dishcuss.foodies.R;
import com.dishcuss.foodies.Services.MyService;
import com.dishcuss.foodies.Utils.Constants;
import com.dishcuss.foodies.Utils.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 9/1/2016.
 */
public class SplashActivity extends AppCompatActivity {


    ImageView sp_2,sp_3,sp_4,sp_5,sp_6;
    Realm realm;

    private static int SPLASH_TIME_OUT = 3000;

    public static boolean isFeatureRestaurantsLoaded=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        FreeMemory();

        try{
            Thread.sleep(500);
        }catch(InterruptedException e){

        }

        sp_2 = (ImageView) findViewById(R.id.sp_2);
        sp_3 = (ImageView) findViewById(R.id.sp_3);
        sp_4 = (ImageView) findViewById(R.id.sp_4);
        sp_5 = (ImageView) findViewById(R.id.sp_5);
        sp_6 = (ImageView) findViewById(R.id.sp_6);


        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_right_to_left);
        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_left_to_right);
        Animation animation3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_bottom_to_up);

        Animation animation4 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_up_anim);
        Animation animation5 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_down_anim);



        sp_2.startAnimation(animation5);
        sp_3.startAnimation(animation4);
        sp_4.startAnimation(animation2);
        sp_5.startAnimation(animation1);
        sp_6.startAnimation(animation3);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(Constants.GetUserLoginStatus(SplashActivity.this)){
                    if(Constants.isNetworkAvailable(SplashActivity.this)) {
                        DeleteOnStart();
                    }
                    Constants.skipLogin=false;
                    Intent intent=new Intent(SplashActivity.this,HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    DeleteAll();
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);

        FeaturedRestaurantData();
        StatusRestaurantsData();

        Intent intent = new Intent(SplashActivity.this, MyService.class);
        startService(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void animateDiagonalPan(View v) {
        AnimatorSet animSetXY = new AnimatorSet();

//        ObjectAnimator y = ObjectAnimator.ofFloat(v,
//                "translationY",v.getY(), targetY);
//
//        ObjectAnimator x = ObjectAnimator.ofFloat(v,
//                "translationX", v.getX(), targetX);
//
//        animSetXY.playTogether(x, y);
//        animSetXY.setInterpolator(new LinearInterpolator(1f));
//        animSetXY.setDuration(300);
//        animSetXY.start();
    }

    void DeleteAll(){

        Realm realm= Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.deleteAll();
//        RealmResults<User> users = realm.where(User.class).findAll();
//        users.deleteAllFromRealm();
//        RealmResults<Comment> comments = realm.where(Comment.class).findAll();
//        comments.deleteAllFromRealm();
//
//        RealmResults<KhabaHistoryModel> khabaHistoryModelRealmResults = realm.where(KhabaHistoryModel.class).findAll();
//        khabaHistoryModelRealmResults.deleteAllFromRealm();
//
//        RealmResults<LocalFeedCheckIn> localFeedCheckInRealmResults = realm.where(LocalFeedCheckIn.class).findAll();
//        localFeedCheckInRealmResults.deleteAllFromRealm();
//
//        RealmResults<LocalFeedReview> localFeedReviewRealmResults = realm.where(LocalFeedReview.class).findAll();
//        localFeedReviewRealmResults.deleteAllFromRealm();
//
//        RealmResults<LocalFeeds> localFeedsRealmResults = realm.where(LocalFeeds.class).findAll();
//        localFeedsRealmResults.deleteAllFromRealm();
//
//        RealmResults<Restaurant> restaurantRealmResults = realm.where(Restaurant.class).findAll();
//        restaurantRealmResults.deleteAllFromRealm();
//        RealmResults<UserProfile> userProfileRealmResults = realm.where(UserProfile.class).findAll();
//        userProfileRealmResults.deleteAllFromRealm();

        realm.commitTransaction();
    }

    void DeleteOnStart(){

        Realm realm= Realm.getDefaultInstance();
        realm.beginTransaction();


        RealmResults<Comment> comments = realm.where(Comment.class).findAll();
        comments.deleteAllFromRealm();

        RealmResults<KhabaHistoryModel> khabaHistoryModelRealmResults = realm.where(KhabaHistoryModel.class).findAll();
        khabaHistoryModelRealmResults.deleteAllFromRealm();

        RealmResults<LocalFeedCheckIn> localFeedCheckInRealmResults = realm.where(LocalFeedCheckIn.class).findAll();
        localFeedCheckInRealmResults.deleteAllFromRealm();

        RealmResults<LocalFeedReview> localFeedReviewRealmResults = realm.where(LocalFeedReview.class).findAll();
        localFeedReviewRealmResults.deleteAllFromRealm();

        RealmResults<LocalFeeds> localFeedsRealmResults = realm.where(LocalFeeds.class).findAll();
        localFeedsRealmResults.deleteAllFromRealm();

        RealmResults<Restaurant> restaurantRealmResults = realm.where(Restaurant.class).findAll();
        restaurantRealmResults.deleteAllFromRealm();

        RealmResults<ReviewModel> reviewModels = realm.where(ReviewModel.class).findAll();
        reviewModels.deleteAllFromRealm();

        RealmResults<FoodItems> foodItemsRealmResults = realm.where(FoodItems.class).findAll();
        foodItemsRealmResults.deleteAllFromRealm();

//        RealmResults<UserProfile> userProfileRealmResults = realm.where(UserProfile.class).findAll();
//        userProfileRealmResults.deleteAllFromRealm();

        realm.commitTransaction();
    }


    void FeaturedRestaurantData(){

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Featured_Restaurant_URL)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr=response.body().string();

                /** check if activity still exist */

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject jsonObj = new JSONObject(objStr);

//                            Log.e("Featured",objStr.toString());

                            JSONArray jsonDataArray=jsonObj.getJSONArray("restaurants");

                            realm =Realm.getDefaultInstance();
                            realm.beginTransaction();
                            RealmResults<FeaturedRestaurant> result = realm.where(FeaturedRestaurant.class).findAll();
                            result.deleteAllFromRealm();
                            realm.commitTransaction();

                            for (int i = 0; i < jsonDataArray.length(); i++) {
                                JSONObject featureRestaurantObj = jsonDataArray.getJSONObject(i);

//                                dataAlreadyExists=false;

//                                if(!dataAlreadyExists)
                                {

                                    realm.beginTransaction();
                                    FeaturedRestaurant featuredRestaurant=realm.createObject(FeaturedRestaurant.class);
                                    featuredRestaurant.setId(featureRestaurantObj.getInt("id"));
                                    featuredRestaurant.setName(featureRestaurantObj.getString("name"));
                                    featuredRestaurant.setLocation(featureRestaurantObj.getString("location"));

                                    JSONObject featureRestaurantCoverImage = featureRestaurantObj.getJSONObject("cover_image");

                                    featuredRestaurant.setCover_image_id(featureRestaurantCoverImage.getInt("id"));

                                    JSONObject CoverImage = featureRestaurantCoverImage.getJSONObject("image");
                                    JSONObject CoverImageURL = CoverImage.getJSONObject("image");

                                    featuredRestaurant.setCover_image_url(CoverImageURL.getString("url"));

                                    JSONObject CoverImageThumbnailURL = CoverImageURL.getJSONObject("thumbnail");

                                    featuredRestaurant.setCover_image_thumbnail(CoverImageThumbnailURL.getString("url"));

                                    JSONObject featureRestaurantOwner = featureRestaurantObj.getJSONObject("owner");

                                    featuredRestaurant.setOwnerID(featureRestaurantOwner.getInt("id"));
                                    realm.commitTransaction();
                                }
                            }

                            isFeatureRestaurantsLoaded=true;
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    void StatusRestaurantsData(){

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Get_Restaurants)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr=response.body().string();

                /** check if activity still exist */

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject jsonObj = new JSONObject(objStr);

                            JSONArray jsonDataArray=jsonObj.getJSONArray("restaurants");


                            realm =Realm.getDefaultInstance();
                            realm.beginTransaction();
                            RealmResults<RestaurantForStatus> result = realm.where(RestaurantForStatus.class).findAll();
                            result.deleteAllFromRealm();
                            realm.commitTransaction();

                            for (int i = 0; i < jsonDataArray.length(); i++) {
                                JSONObject c = jsonDataArray.getJSONObject(i);

                                realm =Realm.getDefaultInstance();
                                realm.beginTransaction();
                                RestaurantForStatus restaurantForStatus=realm.createObject(RestaurantForStatus.class);
                                restaurantForStatus.setId(c.getInt("id"));
                                restaurantForStatus.setName(c.getString("name"));
                                if(!c.isNull("lat")) {
                                    restaurantForStatus.setRestaurantLat(c.getDouble("lat"));
                                } else {
                                    restaurantForStatus.setRestaurantLat(0.0);
                                }
                                if(!c.isNull("long")) {
                                    restaurantForStatus.setRestaurantLong(c.getDouble("long"));
                                } else {
                                    restaurantForStatus.setRestaurantLong(0.0);
                                }

                                realm.commitTransaction();
//                                SplashActivity.restaurantForStatusArrayList.add(restaurantForStatus);

                            }


                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    public void FreeMemory(){
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();

    }
}
