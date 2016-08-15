package com.holygon.dishcuss.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.holygon.dishcuss.Adapters.ExploreAdapter;
import com.holygon.dishcuss.Adapters.SelectRestaurantAdapter;
import com.holygon.dishcuss.Model.FoodItems;
import com.holygon.dishcuss.Model.FoodsCategory;
import com.holygon.dishcuss.Model.PhotoModel;
import com.holygon.dishcuss.Model.Restaurant;
import com.holygon.dishcuss.Model.ReviewModel;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 8/11/2016.
 */
public class SelectRestaurantActivity extends AppCompatActivity {

    RecyclerView selectRestaurantRecyclerView;
    private RecyclerView.LayoutManager selectRestaurantLayoutManager;
    Realm realm;

    ArrayList<Restaurant> restaurantRealmList=new ArrayList<>();

    String categoryName="";



    //*******************PROGRESS******************************
    private ProgressDialog mSpinner;

    private void showSpinner(String title) {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle(title);
        mSpinner.show();
    }

    private void DismissSpinner(){
        if(mSpinner!=null){
            mSpinner.dismiss();
        }
    }

//*******************PROGRESS******************************

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_a_restaurant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView headerName=(TextView)findViewById(R.id.app_toolbar_name);
        headerName.setText("Select A Restaurant");


        selectRestaurantRecyclerView = (RecyclerView) findViewById(R.id.select_restaurant_recycler_view);
        selectRestaurantLayoutManager = new LinearLayoutManager(this);
        selectRestaurantRecyclerView.setLayoutManager(selectRestaurantLayoutManager);
        selectRestaurantRecyclerView.setNestedScrollingEnabled(false);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            categoryName = bundle.getString("CategoryName");

        }

        RestaurantData("Desi");
    }

    void RestaurantData(String type) {
        showSpinner("Loading...");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Select_restaurants+type)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr = response.body().string();
               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject jsonObj = new JSONObject(objStr);
                            JSONArray jsonDataArray = jsonObj.getJSONArray("restaurant");

                            realm = Realm.getDefaultInstance();

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

                                realmRestaurant.setReview_count(jsonDataReviewsArray.length());
                                realmRestaurant.setBookmark_count(jsonDataLikesArray.length());
                                realmRestaurant.setBeen_here_count(jsonDataCheckInsArray.length());

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
                                realm.commitTransaction();
                                restaurantRealmList.add(realmRestaurant);
                            }

                            SelectRestaurantAdapter adapter = new SelectRestaurantAdapter(restaurantRealmList,SelectRestaurantActivity.this);
                            selectRestaurantRecyclerView.setAdapter(adapter);
                            DismissSpinner();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        realm.close();
                    }
                });
            }
        });
    }
}
