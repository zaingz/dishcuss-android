package com.holygon.dishcuss.Fragments;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.holygon.dishcuss.Adapters.ExploreAdapter;
import com.holygon.dishcuss.Adapters.NearbySearchAdapter;
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
 * Created by Naeem Ibrahim on 7/23/2016.
 */
public class NearbyFragmentSearch extends Fragment implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<LocationSettingsResult> {

    AppCompatActivity activity;
    RecyclerView nearbySearchRecyclerView;
    private RecyclerView.LayoutManager nearbySearchLayoutManager;
    Realm realm;
    int reviewsCount=0,bookmarksCount=0,beenHereCount=0;
    ArrayList<Restaurant> restaurantRealmList=new ArrayList<>();

    protected LocationSettingsRequest mLocationSettingsRequest;


    //Current Location
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    final private int REQUEST_PERMISSIONS = 123;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    //Current Location
    boolean getData=false;

    public NearbyFragmentSearch(){
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    protected void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.nearby_search_list_fragment, container, false);
        activity = (AppCompatActivity) getActivity();

        nearbySearchRecyclerView = (RecyclerView) rootView.findViewById(R.id.nearby_search_recycler_view);


        //Location

        createLocationRequest();
        buildLocationSettingsRequest();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        //Location
        checkLocationSettings();


        nearbySearchLayoutManager = new LinearLayoutManager(activity);
        nearbySearchRecyclerView.setLayoutManager(nearbySearchLayoutManager);
        ArrayList<String> itemsData = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            itemsData.add("Local Feeds " + i + " / Item " + i);
        }
        nearbySearchRecyclerView.setNestedScrollingEnabled(false);
        getData=false;

//        NearbySearchAdapter adapter = new NearbySearchAdapter(itemsData);
//        nearbySearchRecyclerView.setAdapter(adapter);






        return rootView;
    }


    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, getActivity(), 0).show();
            return false;
        }
    }


    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );

        result.setResultCallback(this);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        //  Log.d("Locations", mCurrentLocation.getLatitude()+"");
        // Log.d("Locations", mCurrentLocation.getLongitude()+"");

        if(!getData){
            RestaurantData();
            getData=true;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }



    void RestaurantData() {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Get_nearby_restaurants+"lat="+mCurrentLocation.getLatitude()+"&long="+mCurrentLocation.getLongitude())
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
                                realmRestaurant.setRatting(restaurantObj.getInt("rating"));

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
                                    realmRestaurant.setMenuID(restaurantMenu.getInt("id"));
                                    realmRestaurant.setMenuName("name");
                                    realmRestaurant.setMenuSummary("summary");

                                    //Arrays food_items
                                    JSONArray jsonDataMenuFoodItemsArray = restaurantMenu.getJSONArray("food_items");

                                    for (int c = 0; c < jsonDataMenuFoodItemsArray.length(); c++) {

                                        JSONObject menuFoodItem = jsonDataMenuFoodItemsArray.getJSONObject(c);


                                        realm.commitTransaction();
                                        realm.beginTransaction();

//                                        FoodItems foodItems = new FoodItems();
                                        FoodItems foodItems=realm.createObject(FoodItems.class);
                                        foodItems.setFoodID(menuFoodItem.getInt("id"));
                                        foodItems.setName(menuFoodItem.getString("name"));
                                        foodItems.setPrice(menuFoodItem.getInt("price"));

                                        JSONArray menuFoodItemCategoryArray=menuFoodItem.getJSONArray("category");

                                        for (int fc = 0; fc < menuFoodItemCategoryArray.length(); fc++) {

                                            JSONObject foodCategory = menuFoodItemCategoryArray.getJSONObject(fc);
                                            FoodsCategory foodsCategory=new FoodsCategory();

                                            foodsCategory.setId(foodCategory.getInt("id"));
                                            foodsCategory.setCategoryName(foodCategory.getString("name"));

                                            Log.e("ID",""+foodsCategory.getId());
                                            Log.e("Name",""+foodsCategory.getCategoryName());
//                                            // Persist unmanaged objects
                                            final FoodsCategory managedFoodsCategory = realm.copyToRealm(foodsCategory);
                                            foodItems.getFoodsCategories().add(managedFoodsCategory);

                                        }

                                        JSONArray menuFoodItemPhotosArray=menuFoodItem.getJSONArray("photos");

                                        for (int p = 0; p < menuFoodItemPhotosArray.length(); p++) {

                                            JSONObject photo = menuFoodItemPhotosArray.getJSONObject(p);

                                            PhotoModel photoModel=new PhotoModel();
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
                                }
                                realm.commitTransaction();
                                restaurantRealmList.add(realmRestaurant);
                            }


                            NearbySearchAdapter adapter = new NearbySearchAdapter(restaurantRealmList,getActivity());
                            nearbySearchRecyclerView.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        realm.close();
                    }
                });
            }
        });
    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        final Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:

                // NO need to show the dialog;

                break;

            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                //  Location settings are not satisfied. Show the user a dialog

                try {
                    // Show the dialog by calling startResolutionForResult(), and check the result
                    // in onActivityResult().

                    status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);

                } catch (IntentSender.SendIntentException e) {

                    //unable to execute request
                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are inadequate, and cannot be fixed here. Dialog not created
                break;
        }
    }
}
