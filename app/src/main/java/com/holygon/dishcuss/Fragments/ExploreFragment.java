package com.holygon.dishcuss.Fragments;

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
import android.widget.TextView;

import com.holygon.dishcuss.Activities.BookmarkActivity;
import com.holygon.dishcuss.Activities.ExploreSelectedCategoryActivity;
import com.holygon.dishcuss.Activities.NotificationActivity;
import com.holygon.dishcuss.Activities.PunditSelectionActivity;
import com.holygon.dishcuss.Activities.RestaurantDetailActivity;
import com.holygon.dishcuss.Activities.SearchMainActivity;
import com.holygon.dishcuss.Activities.SelectRestaurantActivity;
import com.holygon.dishcuss.Activities.SelectRestaurantSearchActivity;
import com.holygon.dishcuss.Adapters.ExploreAdapter;
import com.holygon.dishcuss.Adapters.SelectRestaurantAdapter;
import com.holygon.dishcuss.Model.FoodItems;
import com.holygon.dishcuss.Model.FoodsCategory;
import com.holygon.dishcuss.Model.Notifications;
import com.holygon.dishcuss.Model.PhotoModel;
import com.holygon.dishcuss.Model.Restaurant;
import com.holygon.dishcuss.Model.ReviewModel;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.BadgeView;
import com.holygon.dishcuss.Utils.Constants;
import com.holygon.dishcuss.Utils.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
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
    private RecyclerView.LayoutManager exploreLayoutManager;
    Realm realm;
    int reviewsCount=0,bookmarksCount=0,beenHereCount=0;

    ArrayList<Restaurant> restaurantRealmList=new ArrayList<>();
    ProgressBar progressBar;


    Button category_button_1;
    Button category_button_2;
    Button category_button_3;
    Button category_button_4;
    Button category_button_5;
    Button category_button_6;

    public ExploreFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.explore_fragment, container, false);
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
                Intent intent= new Intent(getActivity(), SearchMainActivity.class);
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
            }
        });

        ImageView target =(ImageView) rootView.findViewById(R.id.image_notification);
//        ImageView ic_bookMark =(ImageView) rootView.findViewById(R.id.image_bookmark_icon);
        HomeFragment2.badge = new BadgeView(getActivity(), target);
        if(!Constants.skipLogin) {
            if (NotificationActivity.notificationsArrayList.size() > 0) {
                HomeFragment2.badge.show(true);
                HomeFragment2.badge.setText("" + NotificationActivity.notificationsArrayList.size());
            } else {
                HomeFragment2.badge.hide(true);
            }
        }

        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Constants.skipLogin) {
                    if (NotificationActivity.notificationsArrayList.size() > 0) {
                        Intent intent = new Intent(getActivity(), NotificationActivity.class);
                        HomeFragment2.badge.hide(true);
                        startActivity(intent);
                    }
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
                                realmRestaurant.setLocation(restaurantObj.getString("location"));
                                realmRestaurant.setOpening_time(restaurantObj.getString("opening"));
                                realmRestaurant.setClosing_time(restaurantObj.getString("closing"));
                                realmRestaurant.setRatting(restaurantObj.getDouble("rating"));

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
                                JSONArray jsonDataCheckInsArray = restaurantObj.getJSONArray("checkins");
                                JSONArray jsonDataReviewsArray = restaurantObj.getJSONArray("reviews");
                                JSONArray jsonDataCallsArray = restaurantObj.getJSONArray("call_nows");

                                reviewsCount=jsonDataReviewsArray.length();
                                bookmarksCount=jsonDataLikesArray.length();
                                beenHereCount=jsonDataCheckInsArray.length();

                                realmRestaurant.setReview_count(reviewsCount);
                                realmRestaurant.setBookmark_count(bookmarksCount);
                                realmRestaurant.setBeen_here_count(beenHereCount);

                                if(!restaurantObj.isNull("cover_image")) {
                                    JSONObject restaurantCoverImage = restaurantObj.getJSONObject("cover_image");
                                    realmRestaurant.setCover_image_id(restaurantCoverImage.getInt("id"));
                                    JSONObject CoverImage = restaurantCoverImage.getJSONObject("image");
                                    JSONObject CoverImageURL = CoverImage.getJSONObject("image");
                                    realmRestaurant.setCover_image_url(CoverImageURL.getString("url"));
                                    JSONObject CoverImageThumbnailURL = CoverImageURL.getJSONObject("thumbnail");
                                    realmRestaurant.setCover_image_thumbnail(CoverImageThumbnailURL.getString("url"));
                                }
//                                    for (int c = 0; c < jsonDataCheckInsArray.length(); c++) {
//                                        JSONObject checkInsObj = jsonDataCheckInsArray.getJSONObject(c);
//                                        realmRestaurant.setCheck_Ins_ID(checkInsObj.getInt("id"));
//                                        realmRestaurant.setCheck_Ins_Address(checkInsObj.getString("address"));
//                                        realmRestaurant.setCheck_In_time(checkInsObj.getString("time"));
//                                        realmRestaurant.setCheck_In_lat(checkInsObj.getDouble("lat"));
//                                        realmRestaurant.setCheck_In_long(checkInsObj.getDouble("long"));
//
//                                        JSONObject checkInsObjUser= checkInsObj.getJSONObject("user");
//                                        JSONObject checkInsObjRestaurant= checkInsObj.getJSONObject("restaurant");
//
//                                        realmRestaurant.setCheck_Ins_user_ID(checkInsObjUser.getInt("id"));
//                                        realmRestaurant.setCheck_Ins_restaurant_ID(checkInsObjRestaurant.getInt("id"));
//                                    }

                                for (int c = 0; c < jsonDataCallsArray.length();c++) {

                                    JSONObject callObj = jsonDataCallsArray.getJSONObject(c);
                                    realmRestaurant.setNumbers(callObj.getString("number"));
                                }

                                for (int r = 0; r < jsonDataReviewsArray.length();r++) {

                                    JSONObject reviewObj = jsonDataReviewsArray.getJSONObject(r);

                                    ReviewModel reviewModel=new ReviewModel();

                                    reviewModel.setReview_ID(reviewObj.getInt("id"));
                                    reviewModel.setReviewable_id(reviewObj.getInt("reviewable_id"));
                                    reviewModel.setReview_title(reviewObj.getString("title"));
                                    reviewModel.setReview_summary(reviewObj.getString("summary"));
                                    reviewModel.setReviewable_type(reviewObj.getString("reviewable_type"));

                                    JSONObject reviewObjReviewer= reviewObj.getJSONObject("reviewer");

                                    reviewModel.setReview_reviewer_ID(reviewObjReviewer.getInt("id"));
                                    reviewModel.setReview_reviewer_Name(reviewObjReviewer.getString("name"));
                                    reviewModel.setReview_reviewer_Avatar(reviewObjReviewer.getString("avatar"));
                                    reviewModel.setReview_reviewer_time(reviewObjReviewer.getString("location"));

                                    JSONArray reviewLikesArray = reviewObj.getJSONArray("likes");
                                    JSONArray reviewCommentsArray = reviewObj.getJSONArray("comments");
                                    JSONArray reviewShareArray = reviewObj.getJSONArray("reports");

                                    reviewModel.setReview_Likes_count(reviewLikesArray.length());
                                    reviewModel.setReview_comments_count(reviewCommentsArray.length());
                                    reviewModel.setReview_shares_count(reviewShareArray.length());

                                    final ReviewModel managedReviewModel= realm.copyToRealm(reviewModel);

                                    realmRestaurant.getReviewModels().add(managedReviewModel);

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
                                restaurantRealmList.add(realmRestaurant);
                            }


                            ExploreAdapter adapter = new ExploreAdapter(restaurantRealmList,getActivity());
                            exploreRecyclerView.setAdapter(adapter);
                            progressBar.setVisibility(View.GONE);
                            realm.close();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }
}
