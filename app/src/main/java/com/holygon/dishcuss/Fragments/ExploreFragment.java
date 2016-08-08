package com.holygon.dishcuss.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.holygon.dishcuss.Adapters.ExploreAdapter;
import com.holygon.dishcuss.R;

import java.util.ArrayList;

/**
 * Created by Naeem Ibrahim on 7/22/2016.
 */
public class ExploreFragment extends Fragment{

    AppCompatActivity activity;
    RecyclerView exploreRecyclerView;
    private RecyclerView.LayoutManager exploreLayoutManager;

    public ExploreFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.explore_fragment, container, false);
        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        exploreRecyclerView = (RecyclerView) rootView.findViewById(R.id.explore_recycler_view);


        exploreLayoutManager = new LinearLayoutManager(activity);
        exploreRecyclerView.setLayoutManager(exploreLayoutManager);
        exploreRecyclerView.setHasFixedSize(true);
        ArrayList<String> itemsData = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            itemsData.add("Local Feeds " + i + " / Item " + i);
        }


        exploreRecyclerView.setNestedScrollingEnabled(false);

        ExploreAdapter adapter = new ExploreAdapter(itemsData);
        exploreRecyclerView.setAdapter(adapter);


        return rootView;
    }
}
