package com.holygon.dishcuss.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.holygon.dishcuss.Activities.GetFreeFoodActivity;
import com.holygon.dishcuss.R;

/**
 * Created by Naeem Ibrahim on 7/21/2016.
 */
public class ProfileFragment extends Fragment {


    AppCompatActivity activity;
    LinearLayout get_more_food;


    public ProfileFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_fragment, container, false);
        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        TextView header=(TextView) rootView.findViewById(R.id.app_toolbar_name);
        header.setText("My Account");


        get_more_food=(LinearLayout)rootView.findViewById(R.id.get_more_food);


        get_more_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), GetFreeFoodActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
