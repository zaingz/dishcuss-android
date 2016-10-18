package com.holygon.dishcuss.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.holygon.dishcuss.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

/**
 * Created by Naeem Ibrahim on 10/18/2016.
 */
public class UserImagesActivity  extends FragmentActivity {
    public static int NUM_ITEMS;
    public static ImageView imageView;
    public static ProgressBar progressBar;
    ImageFragmentPagerAdapter imageFragmentPagerAdapter;
    ViewPager viewPager;
    public static ArrayList<String> imageViewArrayList=new ArrayList<>();

    public static final String[] IMAGE_NAME = {"eagle", "horse", "bonobo", "wolf", "owl", "bear",};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_pager);

        Intent i = getIntent();
        imageViewArrayList = i.getStringArrayListExtra("images");
        NUM_ITEMS=imageViewArrayList.size();

        imageFragmentPagerAdapter = new ImageFragmentPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(imageFragmentPagerAdapter);
    }

    public static class ImageFragmentPagerAdapter extends FragmentPagerAdapter {
        public ImageFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public Fragment getItem(int position) {
            SwipeFragment fragment = new SwipeFragment();
            return SwipeFragment.newInstance(position);
        }
    }

    public static class SwipeFragment extends Fragment {
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View swipeView = inflater.inflate(R.layout.swipe_fragment, container, false);
            imageView= (ImageView) swipeView.findViewById(R.id.imageView);
            progressBar= (ProgressBar) swipeView.findViewById(R.id.image_spinner);
            Bundle bundle = getArguments();
            int position = bundle.getInt("position");
            String imageFileNames = imageViewArrayList.get(position);

            if(imageFileNames!=null && !imageFileNames.equals("")){
                imageView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                Picasso.with(getActivity()).load(imageFileNames)
                        .placeholder(imageView.getDrawable())
                        .into(new Target(){

                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                imageView.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.GONE);
                                imageView.setBackground(new BitmapDrawable(getResources(), bitmap));
                            }

                            @Override
                            public void onBitmapFailed(final Drawable errorDrawable) {
                            }

                            @Override
                            public void onPrepareLoad(final Drawable placeHolderDrawable) {
                            }
                        });
            }
            return swipeView;
        }

        static SwipeFragment newInstance(int position) {
            SwipeFragment swipeFragment = new SwipeFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("position", position);
            swipeFragment.setArguments(bundle);
            return swipeFragment;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
