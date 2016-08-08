package com.holygon.dishcuss.Activities;

import android.Manifest;
import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.holygon.dishcuss.Fragments.AccountFragment;
import com.holygon.dishcuss.Fragments.ExploreFragment;
import com.holygon.dishcuss.Fragments.HomeFragment;
import com.holygon.dishcuss.Fragments.NearbyFragment;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.RuntimePermissionsActivity;

import java.util.ArrayList;

/**
 * Created by Naeem Ibrahim on 7/20/2016.
 */
public class HomeActivity extends RuntimePermissionsActivity {

    private Fragment currentFragment=null;
    private AHBottomNavigationAdapter navigationAdapter;
    private ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();
    private int[] tabColors;
    final private int REQUEST_PERMISSIONS = 123;

    // UI
    private AHBottomNavigation bottomNavigation;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HomeActivity.super.requestAppPermissions(new
                        String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION}, R.string.runtime_permissions_txt
                , REQUEST_PERMISSIONS);
        setContentView(R.layout.activity_home);
        initUI();

    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    /**
     * Init UI
     */
    private void initUI() {

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floating_action_button);

        tabColors = getApplicationContext().getResources().getIntArray(R.array.tab_colors);
        navigationAdapter = new AHBottomNavigationAdapter(this, R.menu.bottom_navigation_menu_5);
        navigationAdapter.setupWithBottomNavigation(bottomNavigation, tabColors);

        bottomNavigation.setAccentColor(Color.parseColor("#FFE4770A"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));
        bottomNavigation.setColored(true);
        bottomNavigation.setBackgroundColor (Color.BLACK);


        currentFragment = new HomeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, currentFragment);
        fragmentTransaction.commit();

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                if (position == 1) {
                    bottomNavigation.setNotification("", 1);

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

                switch(position) {
                    case 0:
                        currentFragment=new HomeFragment();
                        break;
                    case 1:
                      //  currentFragment=new NearbyFragment();
                        break;
                    case 2:
                        //currentFragment=new AccountFragment();
                        break;
                    case 3:
                        currentFragment=new ExploreFragment();
                        break;
                    case 4:
                        //currentFragment=new AccountFragment();
                        break;
                    default:
                        //currentFragment=new NearbyFragment();

                }

                if (currentFragment  != null) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, currentFragment);
                    fragmentTransaction.commit();
                }
                return true;
            }




        });

        bottomNavigation.setOnNavigationPositionListener(new AHBottomNavigation.OnNavigationPositionListener() {
            @Override public void onPositionChange(int y) {
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

                Intent intent=new Intent(HomeActivity.this,RestaurantDetailActivity.class);
                startActivity(intent);

            }
        });

//        Intent intent=new Intent(HomeActivity.this,StatusActivity.class);
//        startActivity(intent);
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
}
