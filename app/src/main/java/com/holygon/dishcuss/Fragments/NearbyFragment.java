package com.holygon.dishcuss.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.holygon.dishcuss.Activities.BookmarkActivity;
import com.holygon.dishcuss.Activities.NotificationActivity;
import com.holygon.dishcuss.Activities.PunditSelectionActivity;
import com.holygon.dishcuss.Activities.SelectRestaurantSearchActivity;
import com.holygon.dishcuss.Model.Notifications;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.BadgeView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Naeem Ibrahim on 7/21/2016.
 */
public class NearbyFragment extends Fragment {

    private ViewPager viewPager;
    AppCompatActivity activity;
    TabLayout tabLayout;
    public NearbyFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.nearby_fragment, container, false);
        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);



        ImageView home_fragment_image_search=(ImageView)rootView.findViewById(R.id.home_fragment_image_search);

        home_fragment_image_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(), SelectRestaurantSearchActivity.class);
                startActivity(intent);
            }
        });

        ImageView image_bookmark_icon=(ImageView)rootView.findViewById(R.id.image_bookmark_icon);
        image_bookmark_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(), BookmarkActivity.class);
                startActivity(intent);
            }
        });

        ImageView target =(ImageView) rootView.findViewById(R.id.image_notification);
//        ImageView ic_bookMark =(ImageView) rootView.findViewById(R.id.image_bookmark_icon);
        HomeFragment2.badge = new BadgeView(getActivity(), target);
        if(NotificationActivity.notificationsArrayList.size()>0) {
            HomeFragment2.badge.show(true);
            HomeFragment2.badge.setText("" + NotificationActivity.notificationsArrayList.size());
        }else
        {
            HomeFragment2.badge.hide(true);
        }

        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NotificationActivity.notificationsArrayList.size()>0) {
                    Intent intent = new Intent(getActivity(), NotificationActivity.class);
                    HomeFragment2.badge.hide(true);
                    startActivity(intent);
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

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(activity.getSupportFragmentManager());
        adapter.clearAll();
        viewPager.setAdapter(null);
        adapter = new Adapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new NearbyFragmentSearch(), "List");
        adapter.addFragment(new NearbyFragmentGoogleMap(), "Map");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();
        FragmentManager fragmentManager;

        public Adapter(FragmentManager fm) {
            super(fm);
            fragmentManager=fm;
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
            for(int i=0; i<mFragments.size(); i++)
                fragmentManager.beginTransaction().remove(mFragments.get(i)).commit();
            mFragments.clear();
        }
    }
}
