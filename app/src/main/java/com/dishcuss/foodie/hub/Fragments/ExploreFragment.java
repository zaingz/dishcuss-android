package com.dishcuss.foodie.hub.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.dishcuss.foodie.hub.Activities.BookmarkActivity;
import com.dishcuss.foodie.hub.Activities.ExploreSelectedCategoryActivity;
import com.dishcuss.foodie.hub.Activities.LoginActivity;
import com.dishcuss.foodie.hub.Activities.NotificationActivity;
import com.dishcuss.foodie.hub.Activities.PunditSelectionActivity;
import com.dishcuss.foodie.hub.Activities.SearchUserAndRestaurantActivity;
import com.dishcuss.foodie.hub.Adapters.ExploreAdapter;
import com.dishcuss.foodie.hub.Helper.BusProvider;
import com.dishcuss.foodie.hub.Helper.EndlessRecyclerOnScrollListener;
import com.dishcuss.foodie.hub.Models.FoodItems;
import com.dishcuss.foodie.hub.Models.FoodsCategory;
import com.dishcuss.foodie.hub.Models.Notifications;
import com.dishcuss.foodie.hub.Models.PhotoModel;
import com.dishcuss.foodie.hub.Models.Restaurant;
import com.dishcuss.foodie.hub.Models.User;
import com.dishcuss.foodie.hub.R;
import com.dishcuss.foodie.hub.Utils.BadgeView;
import com.dishcuss.foodie.hub.Utils.Constants;
import com.dishcuss.foodie.hub.Utils.URLs;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 7/22/2016.
 */
public class ExploreFragment extends Fragment{

    AppCompatActivity activity;
    RecyclerView exploreRecyclerView;
    private LinearLayoutManager exploreLayoutManager;
    Realm realm;
    int reviewsCount=0,bookmarksCount=0,beenHereCount=0;

    ArrayList<Restaurant> restaurantRealmListServerData =new ArrayList<>();
    ArrayList<Restaurant> restaurantRealmListVisibleData =new ArrayList<>();
    ProgressBar progressBar;


    Button category_button_1;
    Button category_button_2;
    Button category_button_3;
    Button category_button_4;
    Button category_button_5;
    Button category_button_6;

    ExploreAdapter adapter;

    public ExploreFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.explore_fragment, container, false);
        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        exploreRecyclerView = (RecyclerView) rootView.findViewById(R.id.explore_recycler_view);


        progressBar=(ProgressBar)rootView.findViewById(R.id.native_progress_bar);
        category_button_1 = (Button) rootView.findViewById(R.id.category_button_1);
        category_button_2 = (Button) rootView.findViewById(R.id.category_button_2);
        category_button_3 = (Button) rootView.findViewById(R.id.category_button_3);
        category_button_4 = (Button) rootView.findViewById(R.id.category_button_4);
        category_button_5 = (Button) rootView.findViewById(R.id.category_button_5);
        category_button_6 = (Button) rootView.findViewById(R.id.category_button_6);

        realm = Realm.getDefaultInstance();

        exploreLayoutManager = new LinearLayoutManager(activity);
        exploreRecyclerView.setLayoutManager(exploreLayoutManager);
        exploreRecyclerView.setNestedScrollingEnabled(false);
        RestaurantData();



        exploreRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(exploreLayoutManager) {
            @Override
            public void onLoadMore(int current_page,int current_item) {

              //  progressBar.setVisibility(View.VISIBLE);

//                Handler handler = new Handler();
//                handler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        progressBar.setVisibility(View.GONE);
//                    }
//                }, 2*1000);
//
//                // do something...
//
//                restaurantRealmListVisibleData=new ArrayList<>();
//                int newLoad=current_item+10;
//
//                if(restaurantRealmListServerData.size()>=newLoad)
//                {
//                    for (int j = current_item; j <newLoad; j++) {
//                        restaurantRealmListVisibleData.add(restaurantRealmListServerData.get(j));
//                    }
//                }
//                else
//                {
//                    for (int j = current_item; j<restaurantRealmListServerData.size() ; j++) {
//                        restaurantRealmListVisibleData.add(restaurantRealmListServerData.get(j));
//                    }
//                }
//
//                adapter.UpdateList(restaurantRealmListVisibleData);
            }
        });


        category_button_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), ExploreSelectedCategoryActivity.class);
                intent.putExtra("CategoryName",category_button_1.getText().toString());
                startActivity(intent);
            }
        });

        category_button_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), ExploreSelectedCategoryActivity.class);
                intent.putExtra("CategoryName",category_button_2.getText().toString());
                startActivity(intent);
            }
        });
        category_button_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), ExploreSelectedCategoryActivity.class);
                intent.putExtra("CategoryName",category_button_3.getText().toString());
                startActivity(intent);
            }
        });
        category_button_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), ExploreSelectedCategoryActivity.class);
                intent.putExtra("CategoryName",category_button_4.getText().toString());
                startActivity(intent);
            }
        });
        category_button_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), ExploreSelectedCategoryActivity.class);
                intent.putExtra("CategoryName",category_button_5.getText().toString());
                startActivity(intent);
            }
        });
        category_button_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), ExploreSelectedCategoryActivity.class);
                intent.putExtra("CategoryName",category_button_6.getText().toString());
                startActivity(intent);
            }
        });



        ImageView home_fragment_image_search=(ImageView)rootView.findViewById(R.id.home_fragment_image_search);
        home_fragment_image_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(), SearchUserAndRestaurantActivity.class);
                startActivity(intent);
            }
        });

        ImageView image_bookmark_icon=(ImageView)rootView.findViewById(R.id.image_bookmark_icon);
        image_bookmark_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Constants.skipLogin) {
                    Intent intent = new Intent(getActivity(), BookmarkActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent=new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Constants.skipLogin=false;
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        ImageView target =(ImageView) rootView.findViewById(R.id.image_notification);
//        ImageView ic_bookMark =(ImageView) rootView.findViewById(R.id.image_bookmark_icon);
        HomeFragment2.badge = new BadgeView(getActivity(), target);
        if(!Constants.skipLogin) {
            if (NotificationActivity.newNotifications > 0) {
                HomeFragment2.badge.show(true);
                HomeFragment2.badge.setText("" + NotificationActivity.newNotifications);
            } else {
                HomeFragment2.badge.hide(true);
            }
        }

        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Constants.skipLogin) {
                        Intent intent = new Intent(getActivity(), NotificationActivity.class);
                        startActivity(intent);
                        if(HomeFragment2.badge.isShown()) {
                            HomeFragment2.badge.hide(true);
                        }
                }
                else
                {
                    Intent intent=new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Constants.skipLogin=false;
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            }
        });


        LinearLayout pundit_linear_layout=(LinearLayout) rootView.findViewById(R.id.pundit_linear_layout);

        pundit_linear_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PunditSelectionActivity.class);
                startActivity(intent);
            }
        });


        return rootView;
    }

    void RestaurantData() {
        progressBar.setVisibility(View.VISIBLE);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Get_All_Restaurants_data)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr = response.body().string();

                try
                {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                /** check if activity still exist */
                if (getActivity() == null) {
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject jsonObj = new JSONObject(objStr);
                             JSONArray jsonDataArray = jsonObj.getJSONArray("restaurants");



                            for (int i = 0; i < jsonDataArray.length(); i++)
                          //  if(jsonObj.has("restaurant"))
                            {
                                JSONObject restaurantObj = jsonDataArray.getJSONObject(i);

                                realm.beginTransaction();
                                Restaurant realmRestaurant = realm.createObject(Restaurant.class);

                                realmRestaurant.setId(restaurantObj.getInt("id"));
                                realmRestaurant.setName(restaurantObj.getString("name"));
                                realmRestaurant.setType(restaurantObj.getString("typee"));
                                realmRestaurant.setLocation(restaurantObj.getString("location"));
                                realmRestaurant.setOpening_time(restaurantObj.getString("opening"));
                                realmRestaurant.setClosing_time(restaurantObj.getString("closing"));
                                realmRestaurant.setRatting(restaurantObj.getDouble("rating"));
                                realmRestaurant.setPricePerHead(restaurantObj.getInt("price_per_head"));

                                if(!restaurantObj.isNull("latitude")) {
                                    realmRestaurant.setRestaurantLat(restaurantObj.getDouble("latitude"));
                                }else {
                                    realmRestaurant.setRestaurantLat(0.0);
                                }
                                if(!restaurantObj.isNull("longitude")) {
                                    realmRestaurant.setRestaurantLong(restaurantObj.getDouble("longitude"));
                                }else {
                                    realmRestaurant.setRestaurantLong(0.0);
                                }

                                //Arrays
                                JSONArray jsonDataLikesArray = restaurantObj.getJSONArray("like");
                                JSONArray jsonDataCallsArray = restaurantObj.getJSONArray("call_nows");

                                bookmarksCount=jsonDataLikesArray.length();

                                realmRestaurant.setBookmark_count(bookmarksCount);

                                if(!restaurantObj.isNull("cover_image")) {
                                    JSONObject restaurantCoverImage = restaurantObj.getJSONObject("cover_image");
                                    realmRestaurant.setCover_image_id(restaurantCoverImage.getInt("id"));
                                    JSONObject CoverImage = restaurantCoverImage.getJSONObject("image");
                                    JSONObject CoverImageURL = CoverImage.getJSONObject("image");
                                    realmRestaurant.setCover_image_url(CoverImageURL.getString("url"));
                                    JSONObject CoverImageThumbnailURL = CoverImageURL.getJSONObject("thumbnail");
                                    realmRestaurant.setCover_image_thumbnail(CoverImageThumbnailURL.getString("url"));
                                }

                                for (int c = 0; c < jsonDataCallsArray.length();c++) {

                                    JSONObject callObj = jsonDataCallsArray.getJSONObject(c);
                                    realmRestaurant.setNumbers(callObj.getString("number"));
                                }

                                if(!restaurantObj.isNull("menu")) {
                                    JSONObject restaurantMenu = restaurantObj.getJSONObject("menu");
                                    if(restaurantMenu.has("id")) {
                                        realmRestaurant.setMenuID(restaurantMenu.getInt("id"));
                                        realmRestaurant.setMenuName("name");
                                        realmRestaurant.setMenuSummary("summary");

                                        //Sessions Array
                                        JSONArray jsonDataMenuSessionsArray = restaurantMenu.getJSONArray("sections");

                                        for (int s = 0; s < jsonDataMenuSessionsArray.length(); s++) {

                                            JSONObject sessionObj = jsonDataMenuSessionsArray.getJSONObject(s);

                                            String sessionTitle = sessionObj.getString("title");

                                            //Arrays food_items
                                            JSONArray jsonDataMenuFoodItemsArray = sessionObj.getJSONArray("food_items");

                                            for (int f = 0; f < jsonDataMenuFoodItemsArray.length(); f++) {

                                                JSONObject menuFoodItem = jsonDataMenuFoodItemsArray.getJSONObject(f);


                                                realm.commitTransaction();
                                                realm.beginTransaction();

//                                        FoodItems foodItems = new FoodItems();
                                                FoodItems foodItems = realm.createObject(FoodItems.class);
                                                foodItems.setFoodID(menuFoodItem.getInt("id"));
                                                foodItems.setName(menuFoodItem.getString("name"));
                                                foodItems.setPrice(menuFoodItem.getInt("price"));
                                                foodItems.setSections_Title(sessionTitle);

                                                JSONArray menuFoodItemCategoryArray = menuFoodItem.getJSONArray("category");

                                                for (int fc = 0; fc < menuFoodItemCategoryArray.length(); fc++) {

                                                    JSONObject foodCategory = menuFoodItemCategoryArray.getJSONObject(fc);
                                                    FoodsCategory foodsCategory = new FoodsCategory();

                                                    foodsCategory.setId(foodCategory.getInt("id"));
                                                    foodsCategory.setCategoryName(foodCategory.getString("name"));

                                                    Log.e("ID", "" + foodsCategory.getId());
                                                    Log.e("Name", "" + foodsCategory.getCategoryName());
//                                            // Persist unmanaged objects
                                                    final FoodsCategory managedFoodsCategory = realm.copyToRealm(foodsCategory);
                                                    foodItems.getFoodsCategories().add(managedFoodsCategory);

                                                }

                                                JSONArray menuFoodItemPhotosArray = menuFoodItem.getJSONArray("photos");

                                                for (int p = 0; p < menuFoodItemPhotosArray.length(); p++) {

                                                    JSONObject photo = menuFoodItemPhotosArray.getJSONObject(p);

                                                    PhotoModel photoModel = new PhotoModel();
                                                    photoModel.setId(photo.getInt("id"));
                                                    photoModel.setUrl(photo.getString("image_url"));

                                                    // Persist unmanaged objects
                                                    final PhotoModel managedPhotoModel = realm.copyToRealm(photoModel);
                                                    foodItems.getPhotoModels().add(managedPhotoModel);
                                                }

                                                realm.commitTransaction();
                                                realm.beginTransaction();
//                                        foodItems.setPhotoModels(photoModels);
                                                // Persist unmanaged objects
//                                        final FoodItems managedFoodItems = realm.copyToRealm(foodItems);
                                                realmRestaurant.getFoodItemsArrayList().add(foodItems);
                                            }
                                            //Food Items Array

                                        }// Session Array

                                    }
                                }
                                realm.commitTransaction();
                                restaurantRealmListServerData.add(realmRestaurant);
                            }

//                            Log.e("ExpSize",""+restaurantRealmListServerData.size());
//
//                            if(restaurantRealmListServerData.size()>=10) {
//                                for (int i = 0; i < 10; i++) {
//                                    restaurantRealmListVisibleData.add(restaurantRealmListServerData.get(i));
//                                }
//                            }else {
//                                for (int i = 0; i < restaurantRealmListServerData.size(); i++) {
//                                    restaurantRealmListVisibleData.add(restaurantRealmListServerData.get(i));
//                                }
//                            }
                           progressBar.setVisibility(View.GONE);
                           adapter = new ExploreAdapter(restaurantRealmListServerData,getActivity());
                           exploreRecyclerView.setAdapter(adapter);
                           realm.close();

                        } catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onStop() {
        BusProvider.getInstance().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onMessageEvent(String event){
        Notifications();
    }

    void Notifications(){

        // Get a Realm instance for this thread
         realm = Realm.getDefaultInstance();
        // Persist your data in a transaction

        User user = realm.where(User.class).findFirst();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Get_Notification)
                .addHeader("Authorization", "Token token="+user.getToken())
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr=response.body().string();


                if(getActivity()==null){
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject jsonObj = new JSONObject(objStr);
                            JSONArray jsonDataArray = jsonObj.getJSONArray("users");
                            ArrayList<Notifications> notificationsArrayList=new ArrayList<>();

                            for (int i = 0; i < jsonDataArray.length(); i++) {

                                JSONObject c = jsonDataArray.getJSONObject(i);

                                boolean isDataExist=false;
                                realm.beginTransaction();
                                RealmResults<Notifications> localFeedsRealmResults =realm.where(Notifications.class).equalTo("id",c.getInt("id")).findAll();
                                if (localFeedsRealmResults.size() > 0) {
                                    notificationsArrayList.add(localFeedsRealmResults.get(0));
                                    isDataExist=true;
                                }
                                realm.commitTransaction();

                                if(!isDataExist) {
                                    realm.beginTransaction();
                                    Notifications notification = realm.createObject(Notifications.class);

                                    notification.setId(c.getInt("id"));
                                    notification.setBody(c.getString("body"));

                                    if (!c.isNull("notifier")) {
                                        JSONObject notifier = c.getJSONObject("notifier");
                                        notification.setUserID(notifier.getInt("id"));
                                        notification.setUsername(notifier.getString("username"));
                                        notification.setAvatarPic(notifier.getString("avatar"));
                                    }
                                    if (!c.isNull("redirect_to")) {
                                        JSONObject redirect = c.getJSONObject("redirect_to");
                                        notification.setRedirectID(redirect.getInt("id"));
                                        notification.setRedirectType(redirect.getString("typee"));
                                    }

                                    notificationsArrayList.add(notification);
                                    realm.commitTransaction();
                                }
                            }

                            if (notificationsArrayList.size() > 0) {
                                NotificationActivity.newNotifications = notificationsArrayList.size();
                                HomeFragment2.badge.show(true);
                                HomeFragment2.badge.setText("" + notificationsArrayList.size());
                            } else {
                                HomeFragment2.badge.hide(true);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {

                        }
                    }
                });
            }
        });
        realm.close();
    }
}
