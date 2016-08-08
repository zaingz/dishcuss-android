package com.holygon.dishcuss.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.holygon.dishcuss.Adapters.AccountBeenThereAdapter;
import com.holygon.dishcuss.Adapters.AccountFollowerAdapter;
import com.holygon.dishcuss.R;

import java.util.ArrayList;

/**
 * Created by Naeem Ibrahim on 7/24/2016.
 */
public class AccountBeenThereFragment extends Fragment {

    AppCompatActivity activity;
    RecyclerView nearbySearchRecyclerView;
    private RecyclerView.LayoutManager nearbySearchLayoutManager;

    public AccountBeenThereFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.simple_recycler_view_for_all, container, false);
        activity = (AppCompatActivity) getActivity();

        nearbySearchRecyclerView = (RecyclerView) rootView.findViewById(R.id.simple_recycler_view_for_all);



        nearbySearchLayoutManager = new LinearLayoutManager(activity);
        nearbySearchRecyclerView.setLayoutManager(nearbySearchLayoutManager);
        nearbySearchRecyclerView.setHasFixedSize(true);
        ArrayList<String> itemsData = new ArrayList<>();

        for (int i = 0; i < 50; i++) {
            itemsData.add("Local Feeds " + i + " / Item " + i);
        }


        nearbySearchRecyclerView.setNestedScrollingEnabled(false);

        AccountBeenThereAdapter adapter = new AccountBeenThereAdapter(itemsData);
        nearbySearchRecyclerView.setAdapter(adapter);


        return rootView;
    }
}
