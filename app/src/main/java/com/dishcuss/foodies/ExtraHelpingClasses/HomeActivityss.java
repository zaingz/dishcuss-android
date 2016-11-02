package com.dishcuss.foodies.ExtraHelpingClasses;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.dishcuss.foodies.Fragments.LoginIntroFragment;
import com.dishcuss.foodies.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Naeem Ibrahim on 7/19/2016.
 */

public class HomeActivityss extends AppCompatActivity{

    Toolbar mToolbar;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_fragment);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        viewPager = (ViewPager)findViewById(R.id.home_viewpager);
        setupViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(LoginIntroFragment.newInstance(R.layout.login_intro_fragment,R.drawable.intro1_logo,R.drawable.ic_bell));
        adapter.addFragment(LoginIntroFragment.newInstance(R.layout.login_intro_fragment,R.drawable.intro2_logo,R.drawable.ic_bell));
        adapter.addFragment(LoginIntroFragment.newInstance(R.layout.login_intro_fragment,R.drawable.intro3_logo,R.drawable.ic_bell));
        adapter.addFragment(LoginIntroFragment.newInstance(R.layout.login_intro_fragment,R.drawable.intro4_logo,R.drawable.ic_bell));
        adapter.addFragment(LoginIntroFragment.newInstance(R.layout.login_intro_fragment,R.drawable.intro5_logo,R.drawable.ic_bell));
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }
}
