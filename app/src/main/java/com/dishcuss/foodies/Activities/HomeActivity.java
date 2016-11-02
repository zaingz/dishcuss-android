package com.dishcuss.foodies.Activities;

import android.Manifest;
import android.animation.Animator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.dishcuss.foodies.Fragments.ExploreFragment;
import com.dishcuss.foodies.Fragments.HomeFragment2;
import com.dishcuss.foodies.Fragments.NearbyFragment;
import com.dishcuss.foodies.Fragments.PersonalProfileFragment;
import com.dishcuss.foodies.Fragments.ProfileFragment;
import com.dishcuss.foodies.GCM.QuickstartPreferences;
import com.dishcuss.foodies.GCM.RegistrationIntentService;
import com.dishcuss.foodies.Model.Comment;
import com.dishcuss.foodies.Model.PhotoModel;
import com.dishcuss.foodies.Model.RestaurantForStatus;
import com.dishcuss.foodies.Model.ReviewModel;
import com.dishcuss.foodies.Model.User;
import com.dishcuss.foodies.Model.UserBeenThere;
import com.dishcuss.foodies.Model.UserFollowing;
import com.dishcuss.foodies.Model.UserProfile;
import com.dishcuss.foodies.Posts.PostSelectionActivity;
import com.dishcuss.foodies.R;
import com.dishcuss.foodies.Utils.Constants;
import com.dishcuss.foodies.Utils.RuntimePermissionsActivity;
import com.dishcuss.foodies.Utils.URLs;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 7/20/2016.
 */
public class HomeActivity extends RuntimePermissionsActivity implements
        GoogleApiClient.OnConnectionFailedListener  {


    //Emulate@gmail.com
    //1234567890
    private Fragment currentFragment=null;
    private AHBottomNavigationAdapter navigationAdapter;
    private ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();
    private int[] tabColors;
    final private int REQUEST_PERMISSIONS = 123;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    SharedPreferences sharedPreferences;
    boolean sentToken;
    private boolean isReceiverRegistered;

    private boolean useMenuResource = false;
    private Menu menu;
    Realm realm;
    User user;


    // UI
    private AHBottomNavigation bottomNavigation;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HomeActivity.super.requestAppPermissions(new
                        String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION}, R.string.runtime_permissions_txt
                , REQUEST_PERMISSIONS);

        setContentView(R.layout.activity_home);
        if(!Constants.skipLogin) {
            realm =Realm.getDefaultInstance();
            user = realm.where(User.class).findFirst();
        }
        initUI();
        if(Constants.skipLogin) {
           floatingActionButton.setVisibility(View.GONE);
        }
        //on receiving push from server this
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                sharedPreferences=PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
                sentToken = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (!sentToken) {
//                    Toast.makeText(getApplicationContext(), "GCM received", Toast.LENGTH_LONG).show();
                }
            }
        };

        // Registering BroadcastReceiver
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        sentToken = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
        registerReceiver();

        if (checkPlayServices() && !sentToken) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    private void registerReceiver() {
        if (!isReceiverRegistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
            isReceiverRegistered = true;
        }
    }
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i("Menu", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        isReceiverRegistered = false;
        super.onPause();
    }


    @Override
    public void onPermissionsGranted(int requestCode) {

    }



    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler = new Handler();

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) { mHandler.removeCallbacks(mRunnable); }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        mHandler.postDelayed(mRunnable, 2000);
    }

    /**
     * Init UI
     */
    private void initUI() {

        Class fragmentClass;
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);

        if (useMenuResource) {
            tabColors = getApplicationContext().getResources().getIntArray(R.array.tab_colors);
            navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu_5);
            navigationAdapter.setupWithBottomNavigation(bottomNavigation, tabColors);
        }
        else
        {
            AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.menu_1, R.drawable.icon_home, R.color.color_tab_1);
            AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.menu_2, R.drawable.icon_nearby, R.color.color_tab_2);
            AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.menu_3, R.drawable.icon_profile, R.color.color_tab_3);
            if(Constants.skipLogin){
                item3 = new AHBottomNavigationItem(R.string.menu_3_1, R.drawable.ic_logout_p, R.color.color_tab_3);
            }
            AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.menu_4, R.drawable.icon_explore, R.color.color_tab_4);
            AHBottomNavigationItem item5 = new AHBottomNavigationItem(R.string.menu_5, R.drawable.icon_account, R.color.color_tab_5);


            bottomNavigationItems.add(item1);
            bottomNavigationItems.add(item2);
            bottomNavigationItems.add(item3);
            bottomNavigationItems.add(item4);
            bottomNavigationItems.add(item5);

            bottomNavigation.addItems(bottomNavigationItems);
        }

        bottomNavigation.setForceTitlesDisplay(true);
        bottomNavigation.setAccentColor(Color.parseColor("#FFE4770A"));
        bottomNavigation.setInactiveColor(Color.WHITE);
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#231F20"));


//        if(Constants.skipLogin) {
//            bottomNavigationItems.get(2).setDrawable(R.drawable.ic_logout_p);
//        }

        currentFragment = new HomeFragment2();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, currentFragment);
//        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

        bottomNavigation.setBehaviorTranslationEnabled(false);
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                if (position == 0) {
                    bottomNavigation.setNotification("", 0);

                    floatingActionButton.setVisibility(View.VISIBLE);
                    floatingActionButton.setAlpha(0f);
                    floatingActionButton.setScaleX(0f);
                    floatingActionButton.setScaleY(0f);
                    floatingActionButton.animate()
                            .alpha(1)
                            .scaleX(1)
                            .scaleY(1)
                            .setDuration(300)
                            .setInterpolator(new OvershootInterpolator())
                            .setListener(new Animator.AnimatorListener() {
                                @Override
                                public void onAnimationStart(Animator animation) {

                                }

                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    floatingActionButton.animate()
                                            .setInterpolator(new LinearOutSlowInInterpolator())
                                            .start();
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {

                                }

                                @Override
                                public void onAnimationRepeat(Animator animation) {

                                }
                            })
                            .start();

                } else {
                    if (floatingActionButton.getVisibility() == View.VISIBLE) {
                        floatingActionButton.animate()
                                .alpha(0)
                                .scaleX(0)
                                .scaleY(0)
                                .setDuration(300)
                                .setInterpolator(new LinearOutSlowInInterpolator())
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        floatingActionButton.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {
                                        floatingActionButton.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {
                                    }
                                })
                                .start();
                    }
                }

                if(Constants.skipLogin) {
                   floatingActionButton.setVisibility(View.GONE);
                }

                switch (position) {
                    case 0:
                        currentFragment = new HomeFragment2();
                        break;
                    case 1:
                        currentFragment = new NearbyFragment();
                        break;
                    case 2:
                        if(!Constants.skipLogin) {
                            currentFragment = new PersonalProfileFragment();
                        }
                        else
                        {
                            Intent intent=new Intent(HomeActivity.this, LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            Constants.skipLogin=false;
                            startActivity(intent);
                            startActivity(intent);
                            finish();
                        }
                        break;
                    case 3:
                        currentFragment = new ExploreFragment();
                        break;
                    case 4:
                        currentFragment = new ProfileFragment();
                        break;
                    default:
                        //currentFragment=new NearbyFragment();
                }

                if (currentFragment != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, currentFragment);
//                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }

                return true;
            }


        });

        bottomNavigation.setOnNavigationPositionListener(new AHBottomNavigation.OnNavigationPositionListener() {
            @Override
            public void onPositionChange(int y) {
                Log.d("DemoActivity", "BottomNavigation Position: " + y);
            }
        });

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                bottomNavigation.setNotification("16", 1);
//                Snackbar.make(bottomNavigation, "Snackbar with bottom navigation",
//                        Snackbar.LENGTH_SHORT).show();
//            }
//        }, 3000);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              //  StatusRestaurantsData();
                Realm realms = Realm.getDefaultInstance();
                User user = realms.where(User.class).findFirst();
                UserProfile userProfile=GetUserData(user.getId());
                if(userProfile==null) {
                    UserData(user.getId());
                }

                Intent intent = new Intent(HomeActivity.this, PostSelectionActivity.class);
                startActivity(intent);
            }
        });

//        Intent intent=new Intent(HomeActivity.this,StatusActivity.class);

//        startActivity(intent);


        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.e("Image","Loading");
//                bottomNavigationItems.get(2).setDrawable(new BitmapDrawable(getResources(), bitmap));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        if(!Constants.skipLogin) {
            RealmResults<UserProfile> userProfiles = realm.where(UserProfile.class).equalTo("id", user.getId()).findAll();
            if (userProfiles.size() > 0) {

                if(!userProfiles.get(0).getAvatar().equals("")) {
                    Picasso.with(this).load(userProfiles.get(0).getAvatar()).into(target);
                }
//            ImageView imgView = new ImageView(this);
//            Constants.PicassoImageSrc(userProfiles.get(0).getAvatar(), imgView, this);
//            Drawable myDrawable = imgView.getDrawable();
//            //  myDrawable = downloadImage(userProfiles.get(0).getAvatar());
//            bottomNavigationItems.get(2).setDrawable(myDrawable);
            }
        }
    }
    /**
     * Return if the bottom navigation is colored
     */
    public boolean isBottomNavigationColored() {
        return bottomNavigation.isColored();
    }

    /**
     * Show or hide the bottom navigation with animation
     */
    public void showOrHideBottomNavigation(boolean show) {
        if (show) {
            bottomNavigation.restoreBottomNavigation(true);
        } else {
            bottomNavigation.hideBottomNavigation(true);
        }
    }

    /**
     * Return the number of items in the bottom navigation
     */
    public int getBottomNavigationNbItems() {
        return bottomNavigation.getItemsCount();
    }

    public Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    public Drawable downloadImage(String loc) {

        try {
            URLConnection connection = new URL(loc).openConnection();
            InputStream stream = connection.getInputStream();
            BufferedInputStream in = new BufferedInputStream(stream);
            ByteArrayOutputStream out = new ByteArrayOutputStream(50000);
            int read;
            byte[] b = new byte[50000];
            while ((read = in.read(b)) != -1) {
                out.write(b, 0, read);
            }
            out.flush();
            out.close();
            byte[] raw = out.toByteArray();
            Drawable d = new BitmapDrawable(new ByteArrayInputStream(raw));

            return d;
        } catch (Exception e) {

            return null;
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    void UserData(int userID) {




        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Get_User_data+userID)
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

                            if(jsonObj.has("user"))
                            {
                                JSONObject userObj = jsonObj.getJSONObject("user");

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
                                JSONArray jsonDataFollowingArray = userObj.getJSONArray("following");
                                JSONArray jsonDataFollowersArray = userObj.getJSONArray("followers");
                                JSONArray jsonDataPostsArray = userObj.getJSONArray("posts");
                                JSONArray jsonDataReviewsArray = userObj.getJSONArray("reviews");


                                for(int p=0;p<jsonDataPostsArray.length();p++){
                                    JSONObject postObj=jsonDataPostsArray.getJSONObject(p);
                                    JSONObject checkinObj = postObj.getJSONObject("checkin");

                                    if(checkinObj.has("restaurant")) {
                                        JSONObject restaurantObj = checkinObj.getJSONObject("restaurant");

                                        UserBeenThere userBeenThere = new UserBeenThere();
                                        userBeenThere.setId(restaurantObj.getInt("id"));
                                        userBeenThere.setRestaurantName(restaurantObj.getString("name"));
                                        userBeenThere.setRestaurantLocation(restaurantObj.getString("location"));
                                        userBeenThere.setCover_image_url(checkinObj.getString("restaurant_image"));
                                        userBeenThere.setBeenThereTime(checkinObj.getString("time"));
                                        final UserBeenThere beenThere = realm.copyToRealm(userBeenThere);
                                        userProfileRealm.getUserBeenThereRealmList().add(beenThere);
                                    }


                                    JSONArray jsonDataPhotosArray = postObj.getJSONArray("photos");
                                    for (int ph = 0; ph < jsonDataPhotosArray.length(); ph++) {
                                        JSONObject photo = jsonDataPhotosArray.getJSONObject(ph);
                                        PhotoModel photoModel = new PhotoModel();
                                        photoModel.setId(photo.getInt("id"));
                                        photoModel.setUrl(photo.getString("image_url"));
                                        final PhotoModel managedPhotoModel = realm.copyToRealm(photoModel);
                                        userProfileRealm.getPhotoModelRealmList().add(managedPhotoModel);
                                    }

                                    JSONArray jsonDataCommentsArray = postObj.getJSONArray("comments");

                                    for (int c = 0; c < jsonDataCommentsArray.length(); c++) {
                                        JSONObject commentObj = jsonDataCommentsArray.getJSONObject(c);
                                        Comment comment= new Comment();
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
                                        userProfileRealm.getCommentRealmList().add(managedComment);
                                    }
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
                                    userProfileRealm.getReviewModelRealmList().add(managedReviewModel);

                                }



                                for(int fs=0;fs<jsonDataFollowingArray.length();fs++){
                                    JSONObject jsonFollowingObject = jsonDataFollowingArray.getJSONObject(fs);
                                    UserFollowing userFollowing=new UserFollowing();

                                    userFollowing.setId(jsonFollowingObject.getInt("id"));
                                    userFollowing.setLikesCount(jsonFollowingObject.getInt("likees_count"));
                                    userFollowing.setFollowerCount(jsonFollowingObject.getInt("followers_count"));
                                    userFollowing.setFollowingCount(jsonFollowingObject.getInt("followees_count"));

                                    userFollowing.setName(jsonFollowingObject.getString("name"));
                                    userFollowing.setUsername(jsonFollowingObject.getString("username"));
                                    userFollowing.setAvatar(jsonFollowingObject.getString("avatar"));
                                    userFollowing.setLocation(jsonFollowingObject.getString("location"));
                                    userFollowing.setEmail(jsonFollowingObject.getString("email"));
                                    userFollowing.setGender(jsonFollowingObject.getString("gender"));
                                    userFollowing.setRole(jsonFollowingObject.getString("name"));
                                    userFollowing.setReferral_code(jsonFollowingObject.getString("referal_code"));

                                    final UserFollowing managedUserFollowing = realm.copyToRealm(userFollowing);
                                    userProfileRealm.getUserFollowingRealmList().add(managedUserFollowing);
                                }

                                for(int fr=0;fr<jsonDataFollowersArray.length();fr++){
                                    JSONObject jsonFollowingObject = jsonDataFollowersArray.getJSONObject(fr);

                                    UserFollowing userFollowing=new UserFollowing();

                                    userFollowing.setId(jsonFollowingObject.getInt("id"));
                                    userFollowing.setLikesCount(jsonFollowingObject.getInt("likees_count"));
                                    userFollowing.setFollowerCount(jsonFollowingObject.getInt("followers_count"));
                                    userFollowing.setFollowingCount(jsonFollowingObject.getInt("followees_count"));

                                    userFollowing.setName(jsonFollowingObject.getString("name"));
                                    userFollowing.setUsername(jsonFollowingObject.getString("username"));
                                    userFollowing.setAvatar(jsonFollowingObject.getString("avatar"));
                                    userFollowing.setLocation(jsonFollowingObject.getString("location"));
                                    userFollowing.setEmail(jsonFollowingObject.getString("email"));
                                    userFollowing.setGender(jsonFollowingObject.getString("gender"));
                                    userFollowing.setRole(jsonFollowingObject.getString("name"));
                                    userFollowing.setReferral_code(jsonFollowingObject.getString("referal_code"));

                                    final UserFollowing managedUserFollowing = realm.copyToRealm(userFollowing);
                                    userProfileRealm.getUserFollowersRealmList().add(managedUserFollowing);
                                }



                                realm.commitTransaction();

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

    UserProfile GetUserData(int uid){
        realm = Realm.getDefaultInstance();
        RealmResults<UserProfile> userProfiles = realm.where(UserProfile.class).equalTo("id", uid).findAll();
        Log.e("Count",""+userProfiles.size());
        if(userProfiles.size()>0){
            realm.beginTransaction();
            realm.commitTransaction();
            return userProfiles.get(userProfiles.size()-1);
        }
        return null;
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
}
