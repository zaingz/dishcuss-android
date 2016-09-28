package com.holygon.dishcuss.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.holygon.dishcuss.Fragments.RestaurantMenuFragment;
import com.holygon.dishcuss.Fragments.RestaurantPhotosFragment;
import com.holygon.dishcuss.Fragments.RestaurantReviewsFragment;
import com.holygon.dishcuss.Model.Comment;
import com.holygon.dishcuss.Model.FoodItems;
import com.holygon.dishcuss.Model.FoodsCategory;
import com.holygon.dishcuss.Model.PhotoModel;
import com.holygon.dishcuss.Model.Restaurant;
import com.holygon.dishcuss.Model.ReviewModel;
import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.Model.UserProfile;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;
import com.holygon.dishcuss.Utils.GenericRoutes;
import com.holygon.dishcuss.Utils.URLs;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 7/27/2016.
 */
public class RestaurantDetailActivity extends AppCompatActivity {

    private ViewPager viewPager;
    TabLayout tabLayout;
    int restaurantID;
    Toolbar restaurant_details_awesome_toolbar;
    LinearLayout restaurant_details_awesome_toolbar_parent;
    LinearLayout restaurant_call_now,bookmark_button_layout,follow_button_layout;
    TextView bookmark_button_text,follow_button_text;
    ImageView restaurant_share_button;
    Realm realm;
    boolean dataAlreadyExists = false;
    Restaurant restaurant=new Restaurant();
    RealmList<FoodItems> foodItems;

    RealmList<FoodItems> foodItemsRealmList;
    RealmList<PhotoModel> photoModels;
    RealmList<FoodsCategory> foodsCategories;



    FloatingActionMenu materialDesignFAM;
    FloatingActionButton callNow, follow, like;



    int reviewsCount=0,bookmarksCount=0,beenHereCount=0;  // followersCount(Likes)  commentsCount(Checkins)

    TextView cafeName, cafeAddress, cafeTiming, review_count,bookmark_count,been_here_count;
    TextView explore_restaurant_cost;
    TextView restaurantType;
    TextView restaurantCuisine;


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

        setContentView(R.layout.activity_restaurant_details);
        viewPager = (ViewPager) findViewById(R.id.restaurant_detail_viewpager);
        restaurant_details_awesome_toolbar = (Toolbar) findViewById(R.id.restaurant_details_awesome_toolbar);
        setSupportActionBar(restaurant_details_awesome_toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cafeName = (TextView) findViewById(R.id.restaurant_detail_restaurant_name);
        explore_restaurant_cost = (TextView) findViewById(R.id.explore_restaurant_cost);
        restaurantType = (TextView) findViewById(R.id.explore_type);
        restaurantCuisine = (TextView) findViewById(R.id.explore_restaurant_cousine);
        cafeAddress = (TextView) findViewById(R.id.restaurant_detail_restaurant_address);
        cafeTiming = (TextView) findViewById(R.id.restaurant_detail_restaurant_timing);


        review_count = (TextView) findViewById(R.id.reviews_Count);
        bookmark_count = (TextView) findViewById(R.id.bookmark_Count);
        been_here_count = (TextView) findViewById(R.id.been_here_Count);

        restaurant_share_button=(ImageView)findViewById(R.id.restaurant_share_button);

        restaurant_call_now = (LinearLayout) findViewById(R.id.restaurant_call_now_button);
        follow_button_layout = (LinearLayout) findViewById(R.id.follow_button_layout);
        bookmark_button_layout = (LinearLayout) findViewById(R.id.bookmark_button_layout);
        bookmark_button_text=(TextView)findViewById(R.id.bookmark_button_text);
        follow_button_text=(TextView)findViewById(R.id.follow_button_text);

        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        callNow = (FloatingActionButton) findViewById(R.id.material_design_floating_action_call_now);
        like = (FloatingActionButton) findViewById(R.id.material_design_floating_action_like);
        follow = (FloatingActionButton) findViewById(R.id.material_design_floating_action_follow);

        restaurant_details_awesome_toolbar_parent=(LinearLayout)findViewById(R.id.restaurant_details_awesome_toolbar_parent);




            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                restaurantID = bundle.getInt("RestaurantID");

//                restaurant = GetRestaurantData(restaurantID);

                if (restaurant != null) {
                  //  SetValues();
                } else {
                    Log.e("", "ELSE");
                }
                if (!dataAlreadyExists)
                    RestaurantData();
            }


        restaurant_call_now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +restaurant.getNumbers()));
                if ( ContextCompat.checkSelfPermission( RestaurantDetailActivity.this, Manifest.permission.CALL_PHONE ) != PackageManager.PERMISSION_GRANTED ) {
                    return;
                }
                startActivity(intent);
            }
        });

        restaurant_share_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InviteFriends();
            }
        });

        if(Constants.isNetworkAvailable(RestaurantDetailActivity.this)) {
            IsRestaurantFollowedData(restaurantID);
        }

        bookmark_button_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Constants.skipLogin) {
                    if (bookmark_button_text.getText().toString().equals(" Bookmark")) {
                        bookmark_button_text.setText("Bookmarked");
                        if(Constants.isNetworkAvailable(RestaurantDetailActivity.this)) {
                            GenericRoutes.Like(restaurantID, "restaurant", RestaurantDetailActivity.this);
                        }
                    } else {
                        bookmark_button_text.setText(" Bookmark");
                        if(Constants.isNetworkAvailable(RestaurantDetailActivity.this)) {
                            GenericRoutes.UnLike(restaurantID, "restaurant");
                        }
                    }
                }
            }
        });

        follow_button_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Constants.skipLogin) {
                    
                    if (follow_button_text.getText().toString().equals("  Follow")) {
                        follow_button_text.setText("Unfollow");
                        GenericRoutes.FollowRestaurant(restaurantID);

                    } else {
                        follow_button_text.setText("  Follow");
                        GenericRoutes.UnFollowRestaurant(restaurantID);
                    }

                }
            }
        });


//        callNow.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                //TODO something when floating action menu first item clicked
//                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +restaurant.getNumbers()));
//                if ( ContextCompat.checkSelfPermission( RestaurantDetailActivity.this, Manifest.permission.CALL_PHONE ) != PackageManager.PERMISSION_GRANTED ) {
//                    return;
//                }
//                startActivity(intent);
//            }
//        });
//
//        like.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                //TODO something when floating action menu second item clicked
//                GenericRoutes.Like(restaurantID,"restaurant");
//            }
//        });
//
//        follow.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                //TODO something when floating action menu third item clicked
//                GenericRoutes.FollowRestaurant(restaurantID);
//            }
//        });

    }

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


    void GetFooDItems(){

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Restaurant> restaurants = realm.where(Restaurant.class).equalTo("id", restaurantID).findAll();
        realm.beginTransaction();
        foodItems=restaurants.get(restaurants.size()-1).getFoodItemsArrayList();
        realm.commitTransaction();
        realm.close();

    }

    void SetValues() {
        cafeName.setText(restaurant.getName());
        cafeAddress.setText(restaurant.getLocation());
        cafeTiming.setText(restaurant.getOpening_time()+" to "+restaurant.getClosing_time());
        explore_restaurant_cost.setText("  Rs "+restaurant.getPricePerHead()+"/head");
        restaurantType.setText(""+restaurant.getType());
        restaurantCuisine.setText("");
        GetFooDItems();

        for (int i=0;i<foodItems.size();i++) {
            RealmList<FoodsCategory> foodsCategoryRealmList = foodItems.get(i).getFoodsCategories();
            if (foodsCategoryRealmList.size() > 0) {
                    if (restaurantCuisine.getText().toString().equals("")) {
                        restaurantCuisine.setText(foodsCategoryRealmList.get(0).getCategoryName());
                    }else {
                        restaurantCuisine.setText(","+foodsCategoryRealmList.get(0).getCategoryName());
                    }
            }
        }

        if(restaurant.getReview_count()>0){
            reviewsCount=restaurant.getReview_count();
        }
        if(restaurant.getBookmark_count()>0){
            bookmarksCount=restaurant.getBookmark_count();
        }
        if(restaurant.getBeen_here_count()>0){
            beenHereCount=restaurant.getBeen_here_count();
        }
        review_count.setText(""+reviewsCount);
        bookmark_count.setText(""+bookmarksCount);
        been_here_count.setText(""+beenHereCount);

        String imageUri = restaurant.getCover_image_url();
        Picasso.with(RestaurantDetailActivity.this).load(imageUri).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                restaurant_details_awesome_toolbar_parent.setBackground(new BitmapDrawable(bitmap));
            }

            @Override
            public void onBitmapFailed(final Drawable errorDrawable) {
//                Log.d("TAG", "FAILED");
            }

            @Override
            public void onPrepareLoad(final Drawable placeHolderDrawable) {
//                Log.d("TAG", "Prepare Load");
            }
        });
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.clearAll();
        viewPager.setAdapter(null);
        adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new RestaurantReviewsFragment(restaurantID), "Reviews");
        adapter.addFragment(new RestaurantPhotosFragment(restaurantID), "Photos");
        adapter.addFragment(new RestaurantMenuFragment(restaurantID), "Menu");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();
        FragmentManager fragmentManager;

        public Adapter(FragmentManager fm) {
            super(fm);
            fragmentManager = fm;
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }

        public void clearAll() //Clear all page
        {
            for (int i = 0; i < mFragments.size(); i++)
                fragmentManager.beginTransaction().remove(mFragments.get(i)).commit();
            mFragments.clear();
        }
    }


    void RestaurantData() {
        showSpinner("Loading Data...");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Get_Restaurant_data+restaurantID)
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
                           // JSONArray jsonDataArray = jsonObj.getJSONArray("restaurant");

                            realm = Realm.getDefaultInstance();

                            realm.beginTransaction();
                            RealmResults<Restaurant> restaurantRealmResults = realm.where(Restaurant.class).equalTo("id", restaurantID).findAll();
                            restaurantRealmResults.deleteAllFromRealm();
                            realm.commitTransaction();

//                            for (int i = 0; i < jsonDataArray.length(); i++)
                            if(jsonObj.has("restaurant"))
                            {
                                JSONObject restaurantObj = jsonObj.getJSONObject("restaurant");

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

                                    if(restaurantCoverImage.has("id")) {
                                        realmRestaurant.setCover_image_id(restaurantCoverImage.getInt("id"));
                                        JSONObject CoverImage = restaurantCoverImage.getJSONObject("image");
                                        JSONObject CoverImageURL = CoverImage.getJSONObject("image");
                                        realmRestaurant.setCover_image_url(CoverImageURL.getString("url"));
                                        JSONObject CoverImageThumbnailURL = CoverImageURL.getJSONObject("thumbnail");
                                        realmRestaurant.setCover_image_thumbnail(CoverImageThumbnailURL.getString("url"));
                                    }
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
                                        realm.commitTransaction();
                                        realm.beginTransaction();
                                        ReviewModel reviewModel=realm.createObject(ReviewModel.class);

                                        reviewModel.setReview_ID(reviewObj.getInt("id"));
                                        reviewModel.setReviewable_id(reviewObj.getInt("reviewable_id"));
                                        reviewModel.setReview_title(reviewObj.getString("title"));
                                        reviewModel.setUpdated_at(reviewObj.getString("updated_at"));
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



                                        realm.commitTransaction();
                                        realm.beginTransaction();

                                        for (int c = 0; c < reviewCommentsArray.length(); c++) {

                                            JSONObject commentObj = reviewCommentsArray.getJSONObject(c);

                                            Comment comment=realm.createObject(Comment.class);

                                            comment.setCommentID(commentObj.getInt("id"));
                                            comment.setCommentTitle(commentObj.getString("title"));
                                            comment.setCommentUpdated_at(commentObj.getString("created_at"));
                                            comment.setCommentSummary(commentObj.getString("comment"));


                                            JSONObject commentatorObj = commentObj.getJSONObject("commentor");
                                            comment.setCommentatorID(commentatorObj.getInt("id"));
                                            comment.setCommentatorName(commentatorObj.getString("name"));
                                            comment.setCommentatorImage(commentatorObj.getString("avatar"));

                                            JSONArray commentLikeArray=commentObj.getJSONArray("likes");
                                            comment.setCommentLikesCount(commentLikeArray.length());

                                            final Comment managedComment = realm.copyToRealm(comment);
                                            reviewModel.getCommentRealmList().add(managedComment);
                                        }

                                        realm.commitTransaction();
                                        realm.beginTransaction();

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

//                                                Log.e("ID", "" + foodsCategory.getId());
//                                                Log.e("Name", "" + foodsCategory.getCategoryName());
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

                                    restaurant=realmRestaurant;
                                    realm.commitTransaction();
                                    SetValues();
                                    DismissSpinner();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        realm.close();
                    }
                });
            }
        });
    }

    Restaurant GetRestaurantData(int rID){
        realm = Realm.getDefaultInstance();
        RealmResults<Restaurant> restaurants = realm.where(Restaurant.class).equalTo("id", rID).findAll();
        Log.e("Count",""+restaurants.size());
        if(restaurants.size()>0){
            dataAlreadyExists=true;
            return restaurants.get(restaurants.size()-1);
        }
        return null;
    }
    private void InviteFriends(){
        Intent intent=new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Lets Enjoy on Dishcuss");
        intent.putExtra(Intent.EXTRA_TEXT, "Lets Enjoy dishcuss Referral code is");
        startActivity(Intent.createChooser(intent, "Share Dishcuss With Friends"));
    }


    void IsRestaurantFollowedData(int rid){
        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        // Persist your data in a transaction

        User user = realm.where(User.class).findFirst();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.IsRestaurantFollowed+rid)
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
                Log.e("Follows",""+objStr);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    try {

                        JSONObject jsonObj = new JSONObject(objStr);

                        if(jsonObj.has("follows")) {
                            boolean f = jsonObj.getBoolean("follows");
                            boolean b = jsonObj.getBoolean("likes");

                            if (b) {
                                bookmark_button_text.setText("Bookmarked");
                            } else {
                                bookmark_button_text.setText(" Bookmark");
                            }

                            if (f) {
                                follow_button_text.setText("Unfollow");
                            } else {
                                follow_button_text.setText("  Follow");
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            }
        });
    }
}
