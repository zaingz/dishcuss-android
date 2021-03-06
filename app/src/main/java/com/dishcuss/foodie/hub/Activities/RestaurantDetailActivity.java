package com.dishcuss.foodie.hub.Activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.dishcuss.foodie.hub.Fragments.RestaurantMenuFragment;
import com.dishcuss.foodie.hub.Fragments.RestaurantPhotosFragment;
import com.dishcuss.foodie.hub.Fragments.RestaurantReviewsFragment;
import com.dishcuss.foodie.hub.Models.Comment;
import com.dishcuss.foodie.hub.Models.FoodItems;
import com.dishcuss.foodie.hub.Models.FoodsCategory;
import com.dishcuss.foodie.hub.Models.PhotoModel;
import com.dishcuss.foodie.hub.Models.Restaurant;
import com.dishcuss.foodie.hub.Models.ReviewModel;
import com.dishcuss.foodie.hub.Models.User;
import com.dishcuss.foodie.hub.Posts.PostSelectionActivity;
import com.dishcuss.foodie.hub.R;
import com.dishcuss.foodie.hub.Utils.Constants;
import com.dishcuss.foodie.hub.Utils.GenericRoutes;
import com.dishcuss.foodie.hub.Utils.URLs;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
    int restaurantID=0;
    Target target;
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
    private android.support.design.widget.FloatingActionButton floatingActionButton;
    FloatingActionButton callNow, follow, like;

    int reviewsCount=0,bookmarksCount=0,beenHereCount=0;  // followersCount(Likes)  followingCount(Checkins)

    TextView cafeName, cafeAddress, cafeTiming, review_count,bookmark_count,been_here_count,cafeOpenClosed;
    TextView explore_restaurant_cost;
    TextView restaurantType;
    TextView restaurantCuisine;
    TextView main_restaurant_rating;
    boolean isDataLoaded=false;
    String date;

    private int[] imageResId = {
            R.drawable.ic_bell,
            R.drawable.ic_item,
            R.drawable.ic_search,
            R.drawable.ic_item,
            R.drawable.ic_search
    };


    //*******************PROGRESS******************************
    private ProgressDialog mSpinner;

    private void showSpinner(String title) {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle(title);
        mSpinner.show();
        mSpinner.setCancelable(false);
        mSpinner.setCanceledOnTouchOutside(false);
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
        restaurant_details_awesome_toolbar = (Toolbar) findViewById(R.id.toolbar_lower);
      //  setSupportActionBar(restaurant_details_awesome_toolbar);
    //    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cafeName = (TextView) findViewById(R.id.restaurant_detail_restaurant_name);
        explore_restaurant_cost = (TextView) findViewById(R.id.explore_restaurant_cost);
        restaurantType = (TextView) findViewById(R.id.explore_type);
        restaurantCuisine = (TextView) findViewById(R.id.explore_restaurant_cousine);
        cafeAddress = (TextView) findViewById(R.id.restaurant_detail_restaurant_address);
        cafeTiming = (TextView) findViewById(R.id.restaurant_detail_restaurant_timing);
        cafeOpenClosed = (TextView) findViewById(R.id.cafeOpenClosed_tv);

        main_restaurant_rating = (Button) findViewById(R.id.main_restaurant_rating);

        review_count = (TextView) findViewById(R.id.reviews_Count);
        bookmark_count = (TextView) findViewById(R.id.bookmark_Count);
        been_here_count = (TextView) findViewById(R.id.been_here_Count);

        restaurant_share_button=(ImageView)findViewById(R.id.restaurant_share_button);

        restaurant_call_now = (LinearLayout) findViewById(R.id.restaurant_call_now_button);
        follow_button_layout = (LinearLayout) findViewById(R.id.follow_button_layout);
        bookmark_button_layout = (LinearLayout) findViewById(R.id.bookmark_button_layout);
        bookmark_button_text=(TextView)findViewById(R.id.bookmark_button_text);
        follow_button_text=(TextView)findViewById(R.id.follow_button_text);
        floatingActionButton = (android.support.design.widget.FloatingActionButton) findViewById(R.id.floating_action_button_restaurantDetail);
        floatingActionButton.setVisibility(View.GONE);
        materialDesignFAM = (FloatingActionMenu) findViewById(R.id.material_design_android_floating_action_menu);
        callNow = (FloatingActionButton) findViewById(R.id.material_design_floating_action_call_now);
        like = (FloatingActionButton) findViewById(R.id.material_design_floating_action_like);
        follow = (FloatingActionButton) findViewById(R.id.material_design_floating_action_follow);

        restaurant_details_awesome_toolbar_parent=(LinearLayout)findViewById(R.id.restaurant_details_awesome_toolbar_parent);



        Intent intentShare = getIntent();
        String action = intentShare.getAction();
        Uri data = intentShare.getData();
        if(data!=null) {
            String url = data.toString();
            String[] separated = url.split("/");

//            Log.e("DATA0", "" + separated[0]);
//            Log.e("DATA1", "" + separated[1]);
//            Log.e("DATA2", "" + separated[2]);
//            Log.e("DATA3", "" + separated[3]);
//            Log.e("DATA4", "" + separated[4]);

            restaurantID= Integer.parseInt(separated[4]);
        }



        Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                if(bundle.containsKey("RestaurantID")) {
                    restaurantID = bundle.getInt("RestaurantID");
                    Log.e("restaurantID", "" + restaurantID);
                }
                restaurant = GetRestaurantData(restaurantID);

                    if (restaurant != null) {
                          SetValues();
                          isDataLoaded=true;
                    } else {
                        Log.e("", "ELSE");
                    }

                    dataAlreadyExists=false;
                    if (!dataAlreadyExists) {

                        if(!isDataLoaded){
                            showSpinner("Loading...");
                            isDataLoaded=true;
                        }
                        RestaurantData();
                    }
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

        if(Constants.isNetworkAvailable(RestaurantDetailActivity.this) && !Constants.skipLogin) {
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

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestaurantDetailActivity.this, PostSelectionActivity.class);
                intent.putExtra("RestaurantID",restaurant.getId());
                intent.putExtra("RestaurantName",restaurant.getName());
                intent.putExtra("RestaurantLat",restaurant.getRestaurantLat());
                intent.putExtra("RestaurantLong",restaurant.getRestaurantLong());
                startActivity(intent);
            }
        });
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

        boolean isDes=true;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            if (!isDestroyed()) {
                isDes=false;
            }
        }
        else
        {
            isDes=false;
        }

        if(!isDes) {
            DismissSpinner();
            cafeName.setText(restaurant.getName());
            cafeAddress.setText(restaurant.getLocation());
            cafeTiming.setText(restaurant.getOpening_time() + " to " + restaurant.getClosing_time());



            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("hh:mm a");
            String formattedDate = df.format(c.getTime());

            DateFormat currentDate = new SimpleDateFormat("EEE, d MMM yyyy");
            date= currentDate.format(Calendar.getInstance().getTime());

            if(isTimeWithinInterval(restaurant.getOpening_time(),restaurant.getClosing_time(),formattedDate)){
                cafeOpenClosed.setText("Open Now");
            }else {
                cafeOpenClosed.setText("Close Now");
            }

//            if(CheckTime(restaurant.getOpening_time(),restaurant.getClosing_time())){
//                cafeOpenClosed.setText("Open Now");
//            }else {
//                cafeOpenClosed.setText("Close Now");
//            }

            explore_restaurant_cost.setText("  Rs " + restaurant.getPricePerHead() + "/head");
            restaurantType.setText("" + restaurant.getType());
            main_restaurant_rating.setText("" + restaurant.getRatting());
            GetFooDItems();

            for (int i = 0; i < foodItems.size(); i++) {
                RealmList<FoodsCategory> foodsCategoryRealmList = foodItems.get(i).getFoodsCategories();
                if (foodsCategoryRealmList.size() > 0) {
                    if (restaurantCuisine.getText().toString().equals("")) {
                        restaurantCuisine.setText(foodsCategoryRealmList.get(0).getCategoryName());
                    } else {
                        restaurantCuisine.setText("," + foodsCategoryRealmList.get(0).getCategoryName());
                    }
                }
            }

            if (restaurant.getReview_count() > 0) {
                reviewsCount = restaurant.getReview_count();
            }
            if (restaurant.getBookmark_count() > 0) {
                bookmarksCount = restaurant.getBookmark_count();
            }
            if (restaurant.getBeen_here_count() > 0) {
                beenHereCount = restaurant.getBeen_here_count();
            }

            review_count.setText("" + reviewsCount);
            bookmark_count.setText("" + bookmarksCount);
            been_here_count.setText("" + beenHereCount);

            String imageUri = restaurant.getCover_image_url();

            target=new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    restaurant_details_awesome_toolbar_parent.setBackground(new BitmapDrawable(bitmap));
                }

                @Override
                public void onBitmapFailed(final Drawable errorDrawable) {
                }

                @Override
                public void onPrepareLoad(final Drawable placeHolderDrawable) {
                }
            };

            Picasso.with(RestaurantDetailActivity.this).load(imageUri).into(target);

            if (viewPager != null) {
                setupViewPager(viewPager);
            }

            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);

//        for (int i = 0; i < tabLayout.getTabCount(); i++) {
//            tabLayout.getTabAt(i).setIcon(imageResId[i]);
//        }
        }
        if(!Constants.skipLogin) {
            floatingActionButton.setVisibility(View.VISIBLE);
        }
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
   //     showSpinner("Loading Data...");
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

//                            realm.beginTransaction();
//                            RealmResults<Restaurant> restaurantRealmResults = realm.where(Restaurant.class).equalTo("id", restaurantID).findAll();
//                            restaurantRealmResults.deleteAllFromRealm();
//                            realm.commitTransaction();

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

                                        JSONObject reviewOnObj=reviewObj.getJSONObject("review_on");
                                        if(reviewOnObj.has("id")) {
                                            reviewModel.setReview_On_ID(reviewOnObj.getInt("id"));
                                        }

                                        JSONArray reviewLikesArray = reviewObj.getJSONArray("likes");
                                        JSONArray reviewCommentsArray = reviewObj.getJSONArray("comments");
                                        JSONArray reviewShareArray = reviewObj.getJSONArray("reports");

                                        reviewModel.setReview_Likes_count(reviewLikesArray.length());
                                        reviewModel.setReview_comments_count(reviewCommentsArray.length());
                                        reviewModel.setReview_shares_count(reviewObj.getInt("shares"));



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
//        dishcuss.pk/r/123
        intent.putExtra(Intent.EXTRA_TEXT, "Lets Enjoy dishcuss " + "http://dishcuss.pk/r/"+restaurantID);
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

    boolean CompareTime(int openHour, int closeHour){
        int hour,min;
        String AM_PM;

        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        min = c.get(Calendar.MINUTE);
        int ds = c.get(Calendar.AM_PM);
        if(ds==0)
            AM_PM="am";
        else
            AM_PM="pm";

        Toast.makeText(RestaurantDetailActivity.this, ""+hour+":"+min+AM_PM, Toast.LENGTH_SHORT).show();
        if((hour>=openHour&&AM_PM.matches("am")) || (hour<=closeHour&&AM_PM.matches("pm")))
        {
            Toast.makeText(RestaurantDetailActivity.this, "Time is between the range", Toast.LENGTH_SHORT).show();
            return true;
        }
        else {
            Toast.makeText(RestaurantDetailActivity.this, "Time is not between the range", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


    private Calendar fromTime;
    private Calendar toTime;
    private Calendar currentTime;

    public boolean CheckTime(String timeFrom,String timeUntil) {
        try {

            SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
            SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");

            Date From = parseFormat.parse(timeFrom);
            Date Until = parseFormat.parse(timeUntil);

            String newFromTime=displayFormat.format(From).toString();
            String newUntilTime=displayFormat.format(Until).toString();
//            String[] times = time.split("-");
            String[] from = newFromTime.split(":");
            String[] until = newUntilTime.split(":");

            fromTime = Calendar.getInstance();
            fromTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(from[0]));
            fromTime.set(Calendar.MINUTE, Integer.valueOf(from[1]));

            toTime = Calendar.getInstance();
            toTime.set(Calendar.HOUR_OF_DAY, Integer.valueOf(until[0]));
            toTime.set(Calendar.MINUTE, Integer.valueOf(until[1]));

            currentTime = Calendar.getInstance();
            currentTime.set(Calendar.HOUR_OF_DAY, Calendar.HOUR_OF_DAY);
            currentTime.set(Calendar.MINUTE, Calendar.MINUTE);

            Log.e("OTime",""+fromTime.toString());
            Log.e("CTime",""+toTime.toString());
            Log.e("CUime",""+currentTime.toString());

            if(currentTime.after(fromTime) && currentTime.before(toTime)){

                return true;
            }
        }
        catch (Exception e)
        {
            return false;
        }
        return false;
    }


    public boolean isTimeWithinInterval(String lwrLimit, String uprLimit, String time){

        try {
            Calendar now = Calendar.getInstance();
            int year = now.get(Calendar.YEAR);
            int month = now.get(Calendar.MONTH); // Note: zero based!
            int day = now.get(Calendar.DAY_OF_MONTH);




            Date time_1 = new SimpleDateFormat("hh:mm a").parse(lwrLimit);
            Calendar calendar_1 = Calendar.getInstance();
            calendar_1.setTime(time_1);
            calendar_1.set(year,month,day);
//            int hourOpen = calendar_1.get(Calendar.HOUR_OF_DAY);
//            int minuteOpen = calendar_1.get(Calendar.MINUTE);
//            int secondOpen = calendar_1.get(Calendar.SECOND);
//

            // Time 2 in string - Upper limit
            Date time_2 = new SimpleDateFormat("hh:mm a").parse(uprLimit);
            Calendar calendar_2 = Calendar.getInstance();
            calendar_2.setTime(time_2);
            int hourClose = calendar_2.get(Calendar.HOUR_OF_DAY);
            int minuteClose = calendar_2.get(Calendar.MINUTE);
            int secondClose = calendar_2.get(Calendar.SECOND);
            if(hourClose>=0 && hourClose<6){
                int d=day+1;
                calendar_2.set(year,month,d);
            }else {
                calendar_2.set(year,month,day);
            }


            // Time 3 in String - to be checked
            Date d = new SimpleDateFormat("hh:mm a").parse(time);
            Calendar calendar_3 = Calendar.getInstance();
            calendar_3.setTime(d);
            calendar_3.set(year,month,day);
            int hourCurrent = calendar_1.get(Calendar.HOUR_OF_DAY);
            int minuteCurrent = calendar_1.get(Calendar.MINUTE);
            int secondCurrent = calendar_1.get(Calendar.SECOND);



            Log.e("TlwrLimit",""+calendar_1.getTime());
            Log.e("TUperLimit",""+calendar_2.getTime());
            Log.e("Tcurrent",""+calendar_3.getTime());

            Date x = calendar_3.getTime();
            if (x.after(calendar_1.getTime()) && x.before(calendar_2.getTime())) {
                //checkes whether the current time is between two times
                return true;
            }

        }catch (Exception e)
            {
                return false;
            }

        return false;
    }
}
