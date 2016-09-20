package com.holygon.dishcuss.Fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.holygon.dishcuss.Activities.RestaurantDetailActivity;
import com.holygon.dishcuss.Model.FeaturedRestaurant;
import com.holygon.dishcuss.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

/**
 * Created by Naeem Ibrahim on 7/21/2016.
 */
public class HomeViewPagerFragment extends Fragment {

    FeaturedRestaurant featuredRestaurant=new FeaturedRestaurant();

    LinearLayout parentLayout;
    TextView cafeName,cafeAddress,cafeTrending;

    public HomeViewPagerFragment(FeaturedRestaurant featuredRestaurant){
        this.featuredRestaurant=featuredRestaurant;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_viewpager_fragment, container, false);

        parentLayout=(LinearLayout) rootView.findViewById(R.id.view_pager_parent_layout);

        cafeName=(TextView) rootView.findViewById(R.id.view_page_restaurant_name);
        cafeAddress=(TextView) rootView.findViewById(R.id.view_page_restaurant_address);
        cafeTrending=(TextView) rootView.findViewById(R.id.view_page_restaurant_trending);


        String imageUri = featuredRestaurant.getCover_image_url();
        Picasso.with(getActivity()).load(imageUri)
                .resize(400,250)
                .onlyScaleDown()
                .centerCrop()
                .into(new Target(){
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                parentLayout.setBackground(new BitmapDrawable(bitmap));
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

        cafeName.setText(featuredRestaurant.getName());
        cafeAddress.setText(featuredRestaurant.getLocation());


        parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), RestaurantDetailActivity.class);
                intent.putExtra("RestaurantID", featuredRestaurant.getId());
                startActivity(intent);
            }
        });

        return rootView;
    }
}
