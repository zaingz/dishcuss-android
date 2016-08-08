package com.holygon.dishcuss.Fragments;

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

import com.holygon.dishcuss.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Naeem Ibrahim on 7/21/2016.
 */
public class AccountFragment extends Fragment {

    private ViewPager viewPager;
    AppCompatActivity activity;
    TabLayout tabLayout;


    public AccountFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.account_fragment, container, false);
        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
//        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        return rootView;
    }



    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(activity.getSupportFragmentManager());
        adapter.clearAll();
        viewPager.setAdapter(null);
        adapter = new Adapter(getActivity().getSupportFragmentManager());
//        adapter.addFragment(new AccountReviewsFragment(1), "Reviews");
        adapter.addFragment(new AccountPhotosFragment(), "Reviews");
        adapter.addFragment(new AccountPhotosFragment(), "Photos");
        adapter.addFragment(new AccountFollowersFragment(), "Followers");
        adapter.addFragment(new AccountFollowersFragment(), "Following");
        adapter.addFragment(new AccountBeenThereFragment(), "Been There");
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
