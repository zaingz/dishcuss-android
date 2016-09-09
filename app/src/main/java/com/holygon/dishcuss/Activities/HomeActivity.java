package com.holygon.dishcuss.Activities;

import android.Manifest;
import android.animation.Animator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.holygon.dishcuss.Fragments.HomeFragment2;
import com.holygon.dishcuss.Fragments.PersonalProfileFragment;
import com.holygon.dishcuss.Fragments.ProfileFragment;
import com.holygon.dishcuss.Fragments.ExploreFragment;
import com.holygon.dishcuss.Fragments.NearbyFragment;
import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.Model.UserProfile;
import com.holygon.dishcuss.Posts.PostSelectionActivity;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;
import com.holygon.dishcuss.Utils.RuntimePermissionsActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Naeem Ibrahim on 7/20/2016.
 */
public class HomeActivity extends RuntimePermissionsActivity implements
        GoogleApiClient.OnConnectionFailedListener  {

    private Fragment currentFragment=null;
    private AHBottomNavigationAdapter navigationAdapter;
    private ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();
    private int[] tabColors;
    final private int REQUEST_PERMISSIONS = 123;

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
        realm =Realm.getDefaultInstance();
        user= realm.where(User.class).findFirst();
        initUI();
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
            AHBottomNavigationItem item4 = new AHBottomNavigationItem(R.string.menu_4, R.drawable.icon_explore, R.color.color_tab_4);
            AHBottomNavigationItem item5 = new AHBottomNavigationItem(R.string.menu_5, R.drawable.icon_account, R.color.color_tab_5);


            bottomNavigationItems.add(item1);
            bottomNavigationItems.add(item2);
            bottomNavigationItems.add(item3);
            bottomNavigationItems.add(item4);
            bottomNavigationItems.add(item5);

            bottomNavigation.addItems(bottomNavigationItems);
        }

        bottomNavigation.setAccentColor(Color.parseColor("#FFE4770A"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));
        bottomNavigation.setColored(true);
        bottomNavigation.setBackgroundColor(Color.BLACK);


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

                switch (position) {
                    case 0:
                        currentFragment = new HomeFragment2();
                        break;
                    case 1:
                        currentFragment = new NearbyFragment();
                        break;
                    case 2:
                        currentFragment = new PersonalProfileFragment();
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
                bottomNavigationItems.get(2).setDrawable(new BitmapDrawable(getResources(), bitmap));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };

        RealmResults<UserProfile> userProfiles = realm.where(UserProfile.class).equalTo("id", user.getId()).findAll();
        if (userProfiles.size() > 0) {

            Picasso.with(this).load(userProfiles.get(0).getAvatar()).into(target);
//            ImageView imgView = new ImageView(this);
//            Constants.PicassoImageSrc(userProfiles.get(0).getAvatar(), imgView, this);
//            Drawable myDrawable = imgView.getDrawable();
//            //  myDrawable = downloadImage(userProfiles.get(0).getAvatar());
//            bottomNavigationItems.get(2).setDrawable(myDrawable);
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
}
