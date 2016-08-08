package com.holygon.dishcuss.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.holygon.dishcuss.Adapters.NearbySearchAdapter;
import com.holygon.dishcuss.R;

import java.util.ArrayList;

/**
 * Created by Naeem Ibrahim on 7/23/2016.
 */
public class NearbyFragmentSearch  extends Fragment {

    AppCompatActivity activity;
    RecyclerView nearbySearchRecyclerView;
    private RecyclerView.LayoutManager nearbySearchLayoutManager;

    public NearbyFragmentSearch() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.nearby_search_list_fragment, container, false);
        activity = (AppCompatActivity) getActivity();

        nearbySearchRecyclerView = (RecyclerView) rootView.findViewById(R.id.nearby_search_recycler_view);


        nearbySearchLayoutManager = new LinearLayoutManager(activity);
        nearbySearchRecyclerView.setLayoutManager(nearbySearchLayoutManager);
        nearbySearchRecyclerView.setHasFixedSize(true);
        ArrayList<String> itemsData = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            itemsData.add("Local Feeds " + i + " / Item " + i);
        }


        nearbySearchRecyclerView.setNestedScrollingEnabled(false);

        NearbySearchAdapter adapter = new NearbySearchAdapter(itemsData);
        nearbySearchRecyclerView.setAdapter(adapter);


        return rootView;
    }
}
