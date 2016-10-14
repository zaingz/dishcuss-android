package com.holygon.dishcuss.Activities;

import android.Manifest;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
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
import com.holygon.dishcuss.Adapters.CurrentSearchAdapter;
import com.holygon.dishcuss.Adapters.SearchRestaurantNearbySuggestionsAdapter;
import com.holygon.dishcuss.Fragments.NearbyFragmentGoogleMap;
import com.holygon.dishcuss.Model.Comment;
import com.holygon.dishcuss.Model.FoodItems;
import com.holygon.dishcuss.Model.FoodsCategory;
import com.holygon.dishcuss.Model.PhotoModel;
import com.holygon.dishcuss.Model.RecentSearchModel;
import com.holygon.dishcuss.Model.Restaurant;
import com.holygon.dishcuss.Model.ReviewModel;
import com.holygon.dishcuss.Model.SearchModel;
import com.holygon.dishcuss.Model.UserBeenThere;
import com.holygon.dishcuss.Model.UserFollowing;
import com.holygon.dishcuss.Model.UserProfile;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;
import com.holygon.dishcuss.Utils.URLs;

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
 * Created by Naeem Ibrahim on 9/27/2016.
 */
public class SearchUserAndRestaurantActivity extends AppCompatActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<LocationSettingsResult> {

    RealmResults<RecentSearchModel> recentSearchModelRealmResults;

    //Nearby Search
    RecyclerView nearbySearchRecyclerView;
    private RecyclerView.LayoutManager nearbySearchLayoutManager;
    ArrayList<Restaurant> nearbyRestaurants=new ArrayList<>();
    protected LocationSettingsRequest mLocationSettingsRequest;
    int reviewsCount=0,bookmarksCount=0,beenHereCount=0;
    ProgressBar nearbySearchProgressBar;
    boolean getData=false;


    //Current Location
    private static final long INTERVAL = 1000 * 10;
    private static final long FASTEST_INTERVAL = 1000 * 5;
    final private int REQUEST_PERMISSIONS = 123;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    public static Location mCurrentLocation;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    //Current Location

    //Current Search
    RecyclerView current_search_user_recycler_view;
    private RecyclerView.LayoutManager currentSearchUserLayoutManager;
    RecyclerView current_search_restaurant_recycler_view;
    private RecyclerView.LayoutManager currentSearchRestaurantLayoutManager;
    Realm realm;

    CardView recent_search_card_view_parent,nearby_search_card_view_parent,current_search_results_parent_card_view;
    LinearLayout recent_search_result_1,recent_search_result_2,recent_search_result_3,recent_search_result_4;


    ArrayList<Restaurant> restaurantRealmList=new ArrayList<>();
    ArrayList<UserProfile> userProfileArrayList=new ArrayList<>();
    ProgressBar progressBar;
    String categoryName="";



    AutoCompleteTextView searchEditText;
    ArrayList<String> places=new ArrayList<>();
    ArrayAdapter<String> placeAdapter;
    String searchList="";







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
        setContentView(R.layout.search_bar_activity);
        searchEditText=(AutoCompleteTextView)findViewById(R.id.search_bar_edit_text);
        realm=Realm.getDefaultInstance();
        recentSearchModelRealmResults =realm.where(RecentSearchModel.class).findAll();
        current_search_user_recycler_view = (RecyclerView) findViewById(R.id.current_search_user_recycler_view);
        current_search_restaurant_recycler_view = (RecyclerView) findViewById(R.id.current_search_restaurant_recycler_view);
        nearbySearchRecyclerView = (RecyclerView) findViewById(R.id.search_nearby_recycler_view);

        currentSearchUserLayoutManager = new LinearLayoutManager(this);
        currentSearchRestaurantLayoutManager = new LinearLayoutManager(this);


        current_search_restaurant_recycler_view.setLayoutManager(currentSearchRestaurantLayoutManager);
        current_search_restaurant_recycler_view.setNestedScrollingEnabled(false);

        current_search_user_recycler_view.setLayoutManager(currentSearchUserLayoutManager);
        current_search_user_recycler_view.setNestedScrollingEnabled(false);

        progressBar=(ProgressBar)findViewById(R.id.native_progress_bar);
        nearbySearchProgressBar=(ProgressBar)findViewById(R.id.search_nearby_progress_bar);

        recent_search_card_view_parent=(CardView)findViewById(R.id.recent_search_card_view_parent);
        nearby_search_card_view_parent=(CardView)findViewById(R.id.nearby_search_card_view_parent);
        current_search_results_parent_card_view=(CardView)findViewById(R.id.current_search_results_parent_card_view);


        //

        //Location

        createLocationRequest();
        buildLocationSettingsRequest();

        mGoogleApiClient = new GoogleApiClient.Builder(SearchUserAndRestaurantActivity.this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();

        //Location
        checkLocationSettings();


        nearbySearchLayoutManager = new LinearLayoutManager(SearchUserAndRestaurantActivity.this);
        nearbySearchRecyclerView.setLayoutManager(nearbySearchLayoutManager);
        nearbySearchRecyclerView.setNestedScrollingEnabled(false);
        getData=false;
        //


        if(recentSearchModelRealmResults.size()>0)
        {
            recent_search_card_view_parent.setVisibility(View.VISIBLE);
            SetRecentSearchLinearLayouts();
        }
        else
        {
            recent_search_card_view_parent.setVisibility(View.GONE);
        }
        nearby_search_card_view_parent.setVisibility(View.VISIBLE);
        current_search_results_parent_card_view.setVisibility(View.GONE);





        searchEditText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    restaurantRealmList=new ArrayList<>();
                    userProfileArrayList=new ArrayList<>();
                    categoryName=searchEditText.getText().toString();
                    recent_search_card_view_parent.setVisibility(View.GONE);
                    nearby_search_card_view_parent.setVisibility(View.GONE);
                    current_search_results_parent_card_view.setVisibility(View.VISIBLE);
                    TextView Search_Results_for=(TextView)findViewById(R.id.Search_Results_for);
                    Search_Results_for.setText("Search Results for "+categoryName);
                    SearchData(categoryName);
                    return true;
                }
                return false;
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void afterTextChanged(Editable s) {

                Log.e("AutoComplete :: ",""+s.length());
                if(s.length()>0 && s.length()%2==0){
                    categoryName=searchEditText.getText().toString();
                    SearchSuggestionsData(categoryName);
                }

                if(s.length()==0){
                    if(recentSearchModelRealmResults.size()>0)
                    {
                        recent_search_card_view_parent.setVisibility(View.VISIBLE);
                        SetRecentSearchLinearLayouts();
                    }
//                    else
//                    {
//                        recent_search_card_view_parent.setVisibility(View.GONE);
//                    }
                    nearby_search_card_view_parent.setVisibility(View.VISIBLE);
                    current_search_results_parent_card_view.setVisibility(View.GONE);
                }
            }
        });

        searchEditText.setOnItemClickListener(mAutocompleteClickListenerLocationSelection);

    }

    private AdapterView.OnItemClickListener mAutocompleteClickListenerLocationSelection
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){

            restaurantRealmList=new ArrayList<>();
            userProfileArrayList=new ArrayList<>();
            categoryName=searchEditText.getText().toString();
            recent_search_card_view_parent.setVisibility(View.GONE);
            nearby_search_card_view_parent.setVisibility(View.GONE);
            current_search_results_parent_card_view.setVisibility(View.VISIBLE);
            TextView Search_Results_for=(TextView)findViewById(R.id.Search_Results_for);
            Search_Results_for.setText("Search Results for "+categoryName);
            SearchData(categoryName);
        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    void SearchSuggestionsData(String type) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Select_Search_All_User_Restaurants +type)
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
                            JSONArray jsonDataUsersArray = jsonObj.getJSONArray("user");

                            realm = Realm.getDefaultInstance();

                            if(jsonDataArray.length()>0 || jsonDataUsersArray.length()>0 ){
                                places=new ArrayList<String>();
                            }

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

                                places.add(restaurantObj.getString("name"));

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
//                                JSONArray jsonDataCheckInsArray = restaurantObj.getJSONArray("checkins");
//                                JSONArray jsonDataReviewsArray = restaurantObj.getJSONArray("reviews");
                                JSONArray jsonDataCallsArray = restaurantObj.getJSONArray("call_nows");

//                                realmRestaurant.setReview_count(jsonDataReviewsArray.length());
                                realmRestaurant.setBookmark_count(jsonDataLikesArray.length());
//                                realmRestaurant.setBeen_here_count(jsonDataCheckInsArray.length());

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

//                                for (int r = 0; r < jsonDataReviewsArray.length();r++) {
//
//                                    JSONObject reviewObj = jsonDataReviewsArray.getJSONObject(r);
//
//                                    ReviewModel reviewModel=new ReviewModel();
//
//                                    reviewModel.setReview_ID(reviewObj.getInt("id"));
//                                    reviewModel.setReviewable_id(reviewObj.getInt("reviewable_id"));
//                                    reviewModel.setReview_title(reviewObj.getString("title"));
//                                    reviewModel.setReview_summary(reviewObj.getString("summary"));
//                                    reviewModel.setReviewable_type(reviewObj.getString("reviewable_type"));
//
//                                    JSONObject reviewObjReviewer= reviewObj.getJSONObject("reviewer");
//
//                                    reviewModel.setReview_reviewer_ID(reviewObjReviewer.getInt("id"));
//                                    reviewModel.setReview_reviewer_Name(reviewObjReviewer.getString("name"));
//                                    reviewModel.setReview_reviewer_Avatar(reviewObjReviewer.getString("avatar"));
//                                    reviewModel.setReview_reviewer_time(reviewObjReviewer.getString("location"));
//
//                                    JSONArray reviewLikesArray = reviewObj.getJSONArray("likes");
//                                    JSONArray reviewCommentsArray = reviewObj.getJSONArray("comments");
//                                    JSONArray reviewShareArray = reviewObj.getJSONArray("reports");
//
//                                    reviewModel.setReview_Likes_count(reviewLikesArray.length());
//                                    reviewModel.setReview_comments_count(reviewCommentsArray.length());
//                                    reviewModel.setReview_shares_count(reviewShareArray.length());
//
//                                    final ReviewModel managedReviewModel= realm.copyToRealm(reviewModel);
//
//                                    realmRestaurant.getReviewModels().add(managedReviewModel);
//
//                                }

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
                               // restaurantRealmList.add(realmRestaurant);
                            }

                            for (int u = 0; u < jsonDataUsersArray.length(); u++)
                            {

                                JSONObject userObj = jsonDataUsersArray.getJSONObject(u);
                                realm.beginTransaction();
                                UserProfile userProfileRealm = realm.createObject(UserProfile.class);

                                userProfileRealm.setId(userObj.getInt("id"));
                                userProfileRealm.setName(userObj.getString("name"));
                                userProfileRealm.setUsername(userObj.getString("username"));
                                userProfileRealm.setEmail(userObj.getString("email"));
                                userProfileRealm.setAvatar(userObj.getString("avatar"));
                                userProfileRealm.setLocation(userObj.getString("location"));
                                userProfileRealm.setGender(userObj.getString("gender"));
                                userProfileRealm.setRole(userObj.getString("role"));

                                places.add(userObj.getString("name"));
                                //Arrays
//                                JSONArray jsonDataFollowingArray = userObj.getJSONArray("following");
//                                JSONArray jsonDataFollowersArray = userObj.getJSONArray("followers");
//                                JSONArray jsonDataPostsArray = userObj.getJSONArray("posts");
//                                JSONArray jsonDataReviewsArray = userObj.getJSONArray("reviews");

//                                userProfileRealm.setReviewsCount(jsonDataReviewsArray.length());
//                                userProfileRealm.setFollowersCount(jsonDataFollowersArray.length());


//                                for(int p=0;p<jsonDataPostsArray.length();p++){
//
//                                    JSONObject postObj=jsonDataPostsArray.getJSONObject(p);
//
//                                    JSONObject checkinObj = postObj.getJSONObject("checkin");
//
//                                    if(checkinObj.has("restaurant")) {
//
//                                        JSONObject restaurantObj = checkinObj.getJSONObject("restaurant");
//
//                                        UserBeenThere userBeenThere = new UserBeenThere();
//                                        userBeenThere.setId(restaurantObj.getInt("id"));
//                                        userBeenThere.setRestaurantName(restaurantObj.getString("name"));
//                                        userBeenThere.setRestaurantLocation(restaurantObj.getString("location"));
//                                        userBeenThere.setCover_image_url(checkinObj.getString("restaurant_image"));
//                                        userBeenThere.setBeenThereTime(checkinObj.getString("time"));
//                                        final UserBeenThere beenThere = realm.copyToRealm(userBeenThere);
//                                        userProfileRealm.getUserBeenThereRealmList().add(beenThere);
//
//                                    }
//
//                                    JSONArray jsonDataPhotosArray = postObj.getJSONArray("photos");
//                                    for (int ph = 0; ph < jsonDataPhotosArray.length(); ph++) {
//                                        JSONObject photo = jsonDataPhotosArray.getJSONObject(ph);
//                                        PhotoModel photoModel = new PhotoModel();
//                                        photoModel.setId(photo.getInt("id"));
//                                        photoModel.setUrl(photo.getString("image_url"));
//                                        final PhotoModel managedPhotoModel = realm.copyToRealm(photoModel);
//                                        userProfileRealm.getPhotoModelRealmList().add(managedPhotoModel);
//                                    }
//
//                                    JSONArray jsonDataCommentsArray = postObj.getJSONArray("comments");
//                                    userProfileRealm.setCommentsCount(jsonDataCommentsArray.length());
//                                    for (int c = 0; c < jsonDataCommentsArray.length(); c++) {
//                                        JSONObject commentObj = jsonDataCommentsArray.getJSONObject(c);
//                                        Comment comment= new Comment();
//                                        comment.setCommentID(commentObj.getInt("id"));
//                                        comment.setCommentTitle(commentObj.getString("title"));
//                                        comment.setCommentUpdated_at(commentObj.getString("created_at"));
//                                        comment.setCommentSummary(commentObj.getString("comment"));
//                                        JSONObject commentatorObj = commentObj.getJSONObject("commentor");
//                                        comment.setCommentatorID(commentatorObj.getInt("id"));
//                                        comment.setCommentatorName(commentatorObj.getString("name"));
//                                        comment.setCommentatorImage(commentatorObj.getString("avatar"));
//                                        JSONArray commentLikeArray=commentObj.getJSONArray("likes");
//                                        comment.setCommentLikesCount(commentLikeArray.length());
//                                        final Comment managedComment = realm.copyToRealm(comment);
//                                        userProfileRealm.getCommentRealmList().add(managedComment);
//                                    }
//                                }

//                                for (int r = 0; r < jsonDataReviewsArray.length();r++) {
//
//                                    JSONObject reviewObj = jsonDataReviewsArray.getJSONObject(r);
//                                    realm.commitTransaction();
//                                    realm.beginTransaction();
//                                    ReviewModel reviewModel=realm.createObject(ReviewModel.class);
//
//                                    reviewModel.setReview_ID(reviewObj.getInt("id"));
//                                    reviewModel.setReviewable_id(reviewObj.getInt("reviewable_id"));
//                                    reviewModel.setReview_title(reviewObj.getString("title"));
//                                    reviewModel.setUpdated_at(reviewObj.getString("updated_at"));
//                                    reviewModel.setReview_summary(reviewObj.getString("summary"));
//                                    reviewModel.setReviewable_type(reviewObj.getString("reviewable_type"));
//
//                                    JSONObject reviewObjReviewer= reviewObj.getJSONObject("reviewer");
//
//                                    reviewModel.setReview_reviewer_ID(reviewObjReviewer.getInt("id"));
//                                    reviewModel.setReview_reviewer_Name(reviewObjReviewer.getString("name"));
//                                    reviewModel.setReview_reviewer_Avatar(reviewObjReviewer.getString("avatar"));
//                                    reviewModel.setReview_reviewer_time(reviewObjReviewer.getString("location"));
//
//                                    JSONArray reviewLikesArray = reviewObj.getJSONArray("likes");
//                                    JSONArray reviewCommentsArray = reviewObj.getJSONArray("comments");
//                                    JSONArray reviewShareArray = reviewObj.getJSONArray("reports");
//
//                                    reviewModel.setReview_Likes_count(reviewLikesArray.length());
//                                    reviewModel.setReview_comments_count(reviewCommentsArray.length());
//                                    reviewModel.setReview_shares_count(reviewShareArray.length());
//
//
//
//                                    realm.commitTransaction();
//                                    realm.beginTransaction();
//
//                                    for (int c = 0; c < reviewCommentsArray.length(); c++) {
//
//                                        JSONObject commentObj = reviewCommentsArray.getJSONObject(c);
//                                        Comment comment=realm.createObject(Comment.class);
//                                        comment.setCommentID(commentObj.getInt("id"));
//                                        comment.setCommentTitle(commentObj.getString("title"));
//                                        comment.setCommentUpdated_at(commentObj.getString("created_at"));
//                                        comment.setCommentSummary(commentObj.getString("comment"));
//                                        JSONObject commentatorObj = commentObj.getJSONObject("commentor");
//                                        comment.setCommentatorID(commentatorObj.getInt("id"));
//                                        comment.setCommentatorName(commentatorObj.getString("name"));
//                                        comment.setCommentatorImage(commentatorObj.getString("avatar"));
//                                        JSONArray commentLikeArray=commentObj.getJSONArray("likes");
//                                        comment.setCommentLikesCount(commentLikeArray.length());
//                                        final Comment managedComment = realm.copyToRealm(comment);
//                                        reviewModel.getCommentRealmList().add(managedComment);
//                                    }
//                                    realm.commitTransaction();
//                                    realm.beginTransaction();
//
//                                    final ReviewModel managedReviewModel= realm.copyToRealm(reviewModel);
//                                    userProfileRealm.getReviewModelRealmList().add(managedReviewModel);
//                                }
//
//                                for(int fs=0;fs<jsonDataFollowingArray.length();fs++){
//                                    JSONObject jsonFollowingObject = jsonDataFollowingArray.getJSONObject(fs);
//                                    UserFollowing userFollowing=new UserFollowing();
//
//                                    userFollowing.setId(jsonFollowingObject.getInt("id"));
//
//                                    userFollowing.setLikesCount(jsonFollowingObject.getInt("likees_count"));
//                                    userFollowing.setFollowerCount(jsonFollowingObject.getInt("followers_count"));
//                                    userFollowing.setFollowingCount(jsonFollowingObject.getInt("followees_count"));
//
//                                    userFollowing.setName(jsonFollowingObject.getString("name"));
//                                    userFollowing.setUsername(jsonFollowingObject.getString("username"));
//                                    userFollowing.setAvatar(jsonFollowingObject.getString("avatar"));
//                                    userFollowing.setLocation(jsonFollowingObject.getString("location"));
//                                    userFollowing.setEmail(jsonFollowingObject.getString("email"));
//                                    userFollowing.setGender(jsonFollowingObject.getString("gender"));
//                                    userFollowing.setRole(jsonFollowingObject.getString("name"));
//                                    userFollowing.setReferral_code(jsonFollowingObject.getString("referal_code"));
//
//                                    final UserFollowing managedUserFollowing = realm.copyToRealm(userFollowing);
//                                    userProfileRealm.getUserFollowingRealmList().add(managedUserFollowing);
//                                }
//
//                                for(int fr=0;fr<jsonDataFollowersArray.length();fr++){
//                                    JSONObject jsonFollowingObject = jsonDataFollowersArray.getJSONObject(fr);
//
//                                    UserFollowing userFollowing=new UserFollowing();
//
//                                    userFollowing.setId(jsonFollowingObject.getInt("id"));
//                                    userFollowing.setLikesCount(jsonFollowingObject.getInt("likees_count"));
//                                    userFollowing.setFollowerCount(jsonFollowingObject.getInt("followers_count"));
//                                    userFollowing.setFollowingCount(jsonFollowingObject.getInt("followees_count"));
//
//                                    userFollowing.setName(jsonFollowingObject.getString("name"));
//                                    userFollowing.setUsername(jsonFollowingObject.getString("username"));
//                                    userFollowing.setAvatar(jsonFollowingObject.getString("avatar"));
//                                    userFollowing.setLocation(jsonFollowingObject.getString("location"));
//                                    userFollowing.setEmail(jsonFollowingObject.getString("email"));
//                                    userFollowing.setGender(jsonFollowingObject.getString("gender"));
//                                    userFollowing.setRole(jsonFollowingObject.getString("name"));
//                                    userFollowing.setReferral_code(jsonFollowingObject.getString("referal_code"));
//
//                                    final UserFollowing managedUserFollowing = realm.copyToRealm(userFollowing);
//                                    userProfileRealm.getUserFollowersRealmList().add(managedUserFollowing);
//                                }

//                                userProfile=userProfileRealm;
//                                userProfileArrayList.add(userProfileRealm);
                                realm.commitTransaction();


                            }

                            placeAdapter = new ArrayAdapter<String>(SearchUserAndRestaurantActivity.this, android.R.layout.simple_list_item_1, places);
                            searchEditText.setAdapter(placeAdapter);
                            realm.close();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }

    void SearchData(String type) {
        restaurantRealmList=new ArrayList<>();
        userProfileArrayList=new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Select_Search_All_User_Restaurants +type)
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
                            JSONArray jsonDataUsersArray = jsonObj.getJSONArray("user");

                            realm = Realm.getDefaultInstance();

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
//                                JSONArray jsonDataCheckInsArray = restaurantObj.getJSONArray("checkins");
//                                JSONArray jsonDataReviewsArray = restaurantObj.getJSONArray("reviews");
                                JSONArray jsonDataCallsArray = restaurantObj.getJSONArray("call_nows");

//                                realmRestaurant.setReview_count(jsonDataReviewsArray.length());
                                realmRestaurant.setBookmark_count(jsonDataLikesArray.length());
//                                realmRestaurant.setBeen_here_count(jsonDataCheckInsArray.length());

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

//                                for (int r = 0; r < jsonDataReviewsArray.length();r++) {
//
//                                    JSONObject reviewObj = jsonDataReviewsArray.getJSONObject(r);
//
//                                    ReviewModel reviewModel=new ReviewModel();
//
//                                    reviewModel.setReview_ID(reviewObj.getInt("id"));
//                                    reviewModel.setReviewable_id(reviewObj.getInt("reviewable_id"));
//                                    reviewModel.setReview_title(reviewObj.getString("title"));
//                                    reviewModel.setReview_summary(reviewObj.getString("summary"));
//                                    reviewModel.setReviewable_type(reviewObj.getString("reviewable_type"));
//
//                                    JSONObject reviewObjReviewer= reviewObj.getJSONObject("reviewer");
//
//                                    reviewModel.setReview_reviewer_ID(reviewObjReviewer.getInt("id"));
//                                    reviewModel.setReview_reviewer_Name(reviewObjReviewer.getString("name"));
//                                    reviewModel.setReview_reviewer_Avatar(reviewObjReviewer.getString("avatar"));
//                                    reviewModel.setReview_reviewer_time(reviewObjReviewer.getString("location"));
//
//                                    JSONArray reviewLikesArray = reviewObj.getJSONArray("likes");
//                                    JSONArray reviewCommentsArray = reviewObj.getJSONArray("comments");
//                                    JSONArray reviewShareArray = reviewObj.getJSONArray("reports");
//
//                                    reviewModel.setReview_Likes_count(reviewLikesArray.length());
//                                    reviewModel.setReview_comments_count(reviewCommentsArray.length());
//                                    reviewModel.setReview_shares_count(reviewShareArray.length());
//
//                                    final ReviewModel managedReviewModel= realm.copyToRealm(reviewModel);
//
//                                    realmRestaurant.getReviewModels().add(managedReviewModel);
//
//                                }

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

                            for (int u = 0; u < jsonDataUsersArray.length(); u++)
                            {

                                JSONObject userObj = jsonDataUsersArray.getJSONObject(u);
                                realm.beginTransaction();
                                UserProfile userProfileRealm = realm.createObject(UserProfile.class);

                                userProfileRealm.setId(userObj.getInt("id"));
                                userProfileRealm.setName(userObj.getString("name"));
                                userProfileRealm.setUsername(userObj.getString("username"));
                                userProfileRealm.setEmail(userObj.getString("email"));
                                userProfileRealm.setAvatar(userObj.getString("avatar"));
                                userProfileRealm.setLocation(userObj.getString("location"));
                                userProfileRealm.setGender(userObj.getString("gender"));
                                userProfileRealm.setRole(userObj.getString("role"));

                                //Arrays
//                                JSONArray jsonDataFollowingArray = userObj.getJSONArray("following");
//                                JSONArray jsonDataFollowersArray = userObj.getJSONArray("followers");
//                                JSONArray jsonDataPostsArray = userObj.getJSONArray("posts");
//                                JSONArray jsonDataReviewsArray = userObj.getJSONArray("reviews");

//                                userProfileRealm.setReviewsCount(jsonDataReviewsArray.length());
//                                userProfileRealm.setFollowersCount(jsonDataFollowersArray.length());


//                                for(int p=0;p<jsonDataPostsArray.length();p++){
//
//                                    JSONObject postObj=jsonDataPostsArray.getJSONObject(p);
//
//                                    JSONObject checkinObj = postObj.getJSONObject("checkin");
//
//                                    if(checkinObj.has("restaurant") && !checkinObj.isNull("restaurant")) {
//
//                                        JSONObject restaurantObj = checkinObj.getJSONObject("restaurant");
//
//                                        UserBeenThere userBeenThere = new UserBeenThere();
//                                        userBeenThere.setId(restaurantObj.getInt("id"));
//                                        userBeenThere.setRestaurantName(restaurantObj.getString("name"));
//                                        userBeenThere.setRestaurantLocation(restaurantObj.getString("location"));
//                                        userBeenThere.setCover_image_url(checkinObj.getString("restaurant_image"));
//                                        userBeenThere.setBeenThereTime(checkinObj.getString("time"));
//                                        final UserBeenThere beenThere = realm.copyToRealm(userBeenThere);
//                                        userProfileRealm.getUserBeenThereRealmList().add(beenThere);
//
//                                    }
//
//                                    JSONArray jsonDataPhotosArray = postObj.getJSONArray("photos");
//                                    for (int ph = 0; ph < jsonDataPhotosArray.length(); ph++) {
//                                        JSONObject photo = jsonDataPhotosArray.getJSONObject(ph);
//                                        PhotoModel photoModel = new PhotoModel();
//                                        photoModel.setId(photo.getInt("id"));
//                                        photoModel.setUrl(photo.getString("image_url"));
//                                        final PhotoModel managedPhotoModel = realm.copyToRealm(photoModel);
//                                        userProfileRealm.getPhotoModelRealmList().add(managedPhotoModel);
//                                    }
//
//                                    JSONArray jsonDataCommentsArray = postObj.getJSONArray("comments");
//                                    userProfileRealm.setCommentsCount(jsonDataCommentsArray.length());
//                                    for (int c = 0; c < jsonDataCommentsArray.length(); c++) {
//                                        JSONObject commentObj = jsonDataCommentsArray.getJSONObject(c);
//                                        Comment comment= new Comment();
//                                        comment.setCommentID(commentObj.getInt("id"));
//                                        comment.setCommentTitle(commentObj.getString("title"));
//                                        comment.setCommentUpdated_at(commentObj.getString("created_at"));
//                                        comment.setCommentSummary(commentObj.getString("comment"));
//                                        JSONObject commentatorObj = commentObj.getJSONObject("commentor");
//                                        comment.setCommentatorID(commentatorObj.getInt("id"));
//                                        comment.setCommentatorName(commentatorObj.getString("name"));
//                                        comment.setCommentatorImage(commentatorObj.getString("avatar"));
//                                        JSONArray commentLikeArray=commentObj.getJSONArray("likes");
//                                        comment.setCommentLikesCount(commentLikeArray.length());
//                                        final Comment managedComment = realm.copyToRealm(comment);
//                                        userProfileRealm.getCommentRealmList().add(managedComment);
//                                    }
//                                }

//                                for (int r = 0; r < jsonDataReviewsArray.length();r++) {
//
//                                    JSONObject reviewObj = jsonDataReviewsArray.getJSONObject(r);
//                                    realm.commitTransaction();
//                                    realm.beginTransaction();
//                                    ReviewModel reviewModel=realm.createObject(ReviewModel.class);
//
//                                    reviewModel.setReview_ID(reviewObj.getInt("id"));
//                                    reviewModel.setReviewable_id(reviewObj.getInt("reviewable_id"));
//                                    reviewModel.setReview_title(reviewObj.getString("title"));
//                                    reviewModel.setUpdated_at(reviewObj.getString("updated_at"));
//                                    reviewModel.setReview_summary(reviewObj.getString("summary"));
//                                    reviewModel.setReviewable_type(reviewObj.getString("reviewable_type"));
//
//                                    JSONObject reviewObjReviewer= reviewObj.getJSONObject("reviewer");
//
//                                    reviewModel.setReview_reviewer_ID(reviewObjReviewer.getInt("id"));
//                                    reviewModel.setReview_reviewer_Name(reviewObjReviewer.getString("name"));
//                                    reviewModel.setReview_reviewer_Avatar(reviewObjReviewer.getString("avatar"));
//                                    reviewModel.setReview_reviewer_time(reviewObjReviewer.getString("location"));
//
//                                    JSONArray reviewLikesArray = reviewObj.getJSONArray("likes");
//                                    JSONArray reviewCommentsArray = reviewObj.getJSONArray("comments");
//                                    JSONArray reviewShareArray = reviewObj.getJSONArray("reports");
//
//                                    reviewModel.setReview_Likes_count(reviewLikesArray.length());
//                                    reviewModel.setReview_comments_count(reviewCommentsArray.length());
//                                    reviewModel.setReview_shares_count(reviewShareArray.length());
//
//
//
//                                    realm.commitTransaction();
//                                    realm.beginTransaction();
//
//                                    for (int c = 0; c < reviewCommentsArray.length(); c++) {
//
//                                        JSONObject commentObj = reviewCommentsArray.getJSONObject(c);
//                                        Comment comment=realm.createObject(Comment.class);
//                                        comment.setCommentID(commentObj.getInt("id"));
//                                        comment.setCommentTitle(commentObj.getString("title"));
//                                        comment.setCommentUpdated_at(commentObj.getString("created_at"));
//                                        comment.setCommentSummary(commentObj.getString("comment"));
//                                        JSONObject commentatorObj = commentObj.getJSONObject("commentor");
//                                        comment.setCommentatorID(commentatorObj.getInt("id"));
//                                        comment.setCommentatorName(commentatorObj.getString("name"));
//                                        comment.setCommentatorImage(commentatorObj.getString("avatar"));
//                                        JSONArray commentLikeArray=commentObj.getJSONArray("likes");
//                                        comment.setCommentLikesCount(commentLikeArray.length());
//                                        final Comment managedComment = realm.copyToRealm(comment);
//                                        reviewModel.getCommentRealmList().add(managedComment);
//                                    }
//                                    realm.commitTransaction();
//                                    realm.beginTransaction();
//
//                                    final ReviewModel managedReviewModel= realm.copyToRealm(reviewModel);
//                                    userProfileRealm.getReviewModelRealmList().add(managedReviewModel);
//                                }

//                                for(int fs=0;fs<jsonDataFollowingArray.length();fs++){
//                                    JSONObject jsonFollowingObject = jsonDataFollowingArray.getJSONObject(fs);
//                                    UserFollowing userFollowing=new UserFollowing();
//
//                                    userFollowing.setId(jsonFollowingObject.getInt("id"));
//
//                                    userFollowing.setLikesCount(jsonFollowingObject.getInt("likees_count"));
//                                    userFollowing.setFollowerCount(jsonFollowingObject.getInt("followers_count"));
//                                    userFollowing.setFollowingCount(jsonFollowingObject.getInt("followees_count"));
//
//                                    userFollowing.setName(jsonFollowingObject.getString("name"));
//                                    userFollowing.setUsername(jsonFollowingObject.getString("username"));
//                                    userFollowing.setAvatar(jsonFollowingObject.getString("avatar"));
//                                    userFollowing.setLocation(jsonFollowingObject.getString("location"));
//                                    userFollowing.setEmail(jsonFollowingObject.getString("email"));
//                                    userFollowing.setGender(jsonFollowingObject.getString("gender"));
//                                    userFollowing.setRole(jsonFollowingObject.getString("name"));
//                                    userFollowing.setReferral_code(jsonFollowingObject.getString("referal_code"));
//
//                                    final UserFollowing managedUserFollowing = realm.copyToRealm(userFollowing);
//                                    userProfileRealm.getUserFollowingRealmList().add(managedUserFollowing);
//                                }

//                                for(int fr=0;fr<jsonDataFollowersArray.length();fr++){
//                                    JSONObject jsonFollowingObject = jsonDataFollowersArray.getJSONObject(fr);
//
//                                    UserFollowing userFollowing=new UserFollowing();
//
//                                    userFollowing.setId(jsonFollowingObject.getInt("id"));
//                                    userFollowing.setLikesCount(jsonFollowingObject.getInt("likees_count"));
//                                    userFollowing.setFollowerCount(jsonFollowingObject.getInt("followers_count"));
//                                    userFollowing.setFollowingCount(jsonFollowingObject.getInt("followees_count"));
//
//                                    userFollowing.setName(jsonFollowingObject.getString("name"));
//                                    userFollowing.setUsername(jsonFollowingObject.getString("username"));
//                                    userFollowing.setAvatar(jsonFollowingObject.getString("avatar"));
//                                    userFollowing.setLocation(jsonFollowingObject.getString("location"));
//                                    userFollowing.setEmail(jsonFollowingObject.getString("email"));
//                                    userFollowing.setGender(jsonFollowingObject.getString("gender"));
//                                    userFollowing.setRole(jsonFollowingObject.getString("name"));
//                                    userFollowing.setReferral_code(jsonFollowingObject.getString("referal_code"));
//
//                                    final UserFollowing managedUserFollowing = realm.copyToRealm(userFollowing);
//                                    userProfileRealm.getUserFollowersRealmList().add(managedUserFollowing);
//                                }

//                                userProfile=userProfileRealm;
                                userProfileArrayList.add(userProfileRealm);
                                realm.commitTransaction();


                            }

                            SearchModel searchModel=new SearchModel(restaurantRealmList,userProfileArrayList);

                            CurrentSearchAdapter adapter1 = new CurrentSearchAdapter(searchModel,SearchUserAndRestaurantActivity.this);
                            current_search_user_recycler_view.setAdapter(adapter1);

                            realm.close();
                            progressBar.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }

    void NearbyRestaurantData() {
        nearbySearchProgressBar.setVisibility(View.VISIBLE);
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

                runOnUiThread(new Runnable() {
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
                                realmRestaurant.setType(restaurantObj.getString("typee"));
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

                                Location location=new Location("");
                                location.setLatitude(realmRestaurant.getRestaurantLat());
                                location.setLongitude(realmRestaurant.getRestaurantLong());

                                NearbyFragmentGoogleMap.locations.add(location);
                                NearbyFragmentGoogleMap.restaurantName.add(restaurantObj.getString("name"));
                                NearbyFragmentGoogleMap.restaurantID.add(restaurantObj.getInt("id"));

                                //Arrays
                                JSONArray jsonDataLikesArray = restaurantObj.getJSONArray("like");
//                                JSONArray jsonDataCheckInsArray = restaurantObj.getJSONArray("checkins");
//                                JSONArray jsonDataReviewsArray = restaurantObj.getJSONArray("reviews");
                                JSONArray jsonDataCallsArray = restaurantObj.getJSONArray("call_nows");

//                                reviewsCount=jsonDataReviewsArray.length();
                                bookmarksCount=jsonDataLikesArray.length();
//                                beenHereCount=jsonDataCheckInsArray.length();

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

//                                for (int r = 0; r < jsonDataReviewsArray.length();r++) {
//
//                                    JSONObject reviewObj = jsonDataReviewsArray.getJSONObject(r);
//
//                                    ReviewModel reviewModel=new ReviewModel();
//
//                                    reviewModel.setReview_ID(reviewObj.getInt("id"));
//                                    reviewModel.setReviewable_id(reviewObj.getInt("reviewable_id"));
//                                    reviewModel.setReview_title(reviewObj.getString("title"));
//                                    reviewModel.setReview_summary(reviewObj.getString("summary"));
//                                    reviewModel.setReviewable_type(reviewObj.getString("reviewable_type"));
//
//                                    JSONObject reviewObjReviewer= reviewObj.getJSONObject("reviewer");
//
//                                    reviewModel.setReview_reviewer_ID(reviewObjReviewer.getInt("id"));
//                                    reviewModel.setReview_reviewer_Name(reviewObjReviewer.getString("name"));
//                                    reviewModel.setReview_reviewer_Avatar(reviewObjReviewer.getString("avatar"));
//                                    reviewModel.setReview_reviewer_time(reviewObjReviewer.getString("location"));
//
//                                    JSONArray reviewLikesArray = reviewObj.getJSONArray("likes");
//                                    JSONArray reviewCommentsArray = reviewObj.getJSONArray("comments");
//                                    JSONArray reviewShareArray = reviewObj.getJSONArray("reports");
//
//                                    reviewModel.setReview_Likes_count(reviewLikesArray.length());
//                                    reviewModel.setReview_comments_count(reviewCommentsArray.length());
//                                    reviewModel.setReview_shares_count(reviewShareArray.length());
//
//                                    final ReviewModel managedReviewModel= realm.copyToRealm(reviewModel);
//
//                                    realmRestaurant.getReviewModels().add(managedReviewModel);
//
//                                }

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

                                            }//Food Items Array

                                        }// Session Array
                                    }
                                }
                                realm.commitTransaction();
                                restaurantRealmList.add(realmRestaurant);
                            }


                            SearchRestaurantNearbySuggestionsAdapter adapter = new SearchRestaurantNearbySuggestionsAdapter(restaurantRealmList,SearchUserAndRestaurantActivity.this);
                            nearbySearchRecyclerView.setAdapter(adapter);
                            realm.close();
                            nearbySearchProgressBar.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
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
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(SearchUserAndRestaurantActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SearchUserAndRestaurantActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
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
        Log.d("Locations", mCurrentLocation.getLatitude()+"");
        Log.d("Locations", mCurrentLocation.getLongitude()+"");

        if(!getData){
            NearbyRestaurantData();
            getData=true;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

                    status.startResolutionForResult(SearchUserAndRestaurantActivity.this, REQUEST_CHECK_SETTINGS);

                } catch (IntentSender.SendIntentException e) {

                    //unable to execute request
                }
                break;

            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are inadequate, and cannot be fixed here. Dialog not created
                break;
        }
    }

    void SetRecentSearchLinearLayouts(){

        de.hdodenhof.circleimageview.CircleImageView recent_search_image_1=(de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.recent_search_image_1);
        de.hdodenhof.circleimageview.CircleImageView recent_search_image_2=(de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.recent_search_image_2);
        de.hdodenhof.circleimageview.CircleImageView recent_search_image_3=(de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.recent_search_image_3);
        de.hdodenhof.circleimageview.CircleImageView recent_search_image_4=(de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.recent_search_image_4);

        TextView recent_search_user_name_1=(TextView)findViewById(R.id.recent_search_user_name_1);
        TextView recent_search_user_name_2=(TextView)findViewById(R.id.recent_search_user_name_2);
        TextView recent_search_user_name_3=(TextView)findViewById(R.id.recent_search_user_name_3);
        TextView recent_search_user_name_4=(TextView)findViewById(R.id.recent_search_user_name_4);

        TextView recent_search_info_1=(TextView)findViewById(R.id.recent_search_info_1);
        TextView recent_search_info_2=(TextView)findViewById(R.id.recent_search_info_2);
        TextView recent_search_info_3=(TextView)findViewById(R.id.recent_search_info_3);
        TextView recent_search_info_4=(TextView)findViewById(R.id.recent_search_info_4);

        recent_search_result_1=(LinearLayout)findViewById(R.id.recent_search_result_1);
        recent_search_result_2=(LinearLayout)findViewById(R.id.recent_search_result_2);
        recent_search_result_3=(LinearLayout)findViewById(R.id.recent_search_result_3);
        recent_search_result_4=(LinearLayout)findViewById(R.id.recent_search_result_4);

        if(recentSearchModelRealmResults.size()>=4)
        {

            Constants.PicassoImageSrc(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-1).getAvatar(), recent_search_image_1, SearchUserAndRestaurantActivity.this);
            recent_search_user_name_1.setText(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-1).getName());
            recent_search_info_1.setText(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-1).getType());

            Constants.PicassoImageSrc(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-2).getAvatar(), recent_search_image_2, SearchUserAndRestaurantActivity.this);
            recent_search_user_name_2.setText(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-2).getName());
            recent_search_info_2.setText(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-2).getType());


            Constants.PicassoImageSrc(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-3).getAvatar(), recent_search_image_3, SearchUserAndRestaurantActivity.this);
            recent_search_user_name_3.setText(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-3).getName());
            recent_search_info_3.setText(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-3).getType());


            Constants.PicassoImageSrc(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-4).getAvatar(), recent_search_image_4, SearchUserAndRestaurantActivity.this);
            recent_search_user_name_4.setText(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-4).getName());
            recent_search_info_4.setText(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-4).getType());

            recent_search_result_1.setVisibility(View.VISIBLE);
            recent_search_result_2.setVisibility(View.VISIBLE);
            recent_search_result_3.setVisibility(View.VISIBLE);
            recent_search_result_4.setVisibility(View.VISIBLE);
        }else if(recentSearchModelRealmResults.size()==3)
        {
            Constants.PicassoImageSrc(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-1).getAvatar(), recent_search_image_1, SearchUserAndRestaurantActivity.this);
            recent_search_user_name_1.setText(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-1).getName());
            recent_search_info_1.setText(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-1).getType());

            Constants.PicassoImageSrc(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-2).getAvatar(), recent_search_image_2, SearchUserAndRestaurantActivity.this);
            recent_search_user_name_2.setText(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-2).getName());
            recent_search_info_2.setText(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-2).getType());


            Constants.PicassoImageSrc(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-3).getAvatar(), recent_search_image_3, SearchUserAndRestaurantActivity.this);
            recent_search_user_name_3.setText(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-3).getName());
            recent_search_info_3.setText(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-3).getType());

            recent_search_result_1.setVisibility(View.VISIBLE);
            recent_search_result_2.setVisibility(View.VISIBLE);
            recent_search_result_3.setVisibility(View.VISIBLE);
            recent_search_result_4.setVisibility(View.GONE);

        }else if(recentSearchModelRealmResults.size()==2)
        {
            Constants.PicassoImageSrc(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-1).getAvatar(), recent_search_image_1, SearchUserAndRestaurantActivity.this);
            recent_search_user_name_1.setText(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-1).getName());
            recent_search_info_1.setText(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-1).getType());

            Constants.PicassoImageSrc(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-2).getAvatar(), recent_search_image_2, SearchUserAndRestaurantActivity.this);
            recent_search_user_name_2.setText(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-2).getName());
            recent_search_info_2.setText(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-2).getType());
            recent_search_result_1.setVisibility(View.VISIBLE);
            recent_search_result_2.setVisibility(View.VISIBLE);
            recent_search_result_3.setVisibility(View.GONE);
            recent_search_result_4.setVisibility(View.GONE);

        }else if(recentSearchModelRealmResults.size()==1)
        {
            Constants.PicassoImageSrc(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-1).getAvatar(), recent_search_image_1, SearchUserAndRestaurantActivity.this);
            recent_search_user_name_1.setText(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-1).getName());
            recent_search_info_1.setText(recentSearchModelRealmResults.get(recentSearchModelRealmResults.size()-1).getType());
            recent_search_result_1.setVisibility(View.VISIBLE);
            recent_search_result_2.setVisibility(View.GONE);
            recent_search_result_3.setVisibility(View.GONE);
            recent_search_result_4.setVisibility(View.GONE);
        }
    }
}
