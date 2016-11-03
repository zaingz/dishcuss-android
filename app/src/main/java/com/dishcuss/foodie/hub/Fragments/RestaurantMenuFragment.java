package com.dishcuss.foodie.hub.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dishcuss.foodie.hub.Adapters.RestaurantMenuAdapter;
import com.dishcuss.foodie.Model.FoodItems;
import com.dishcuss.foodie.Model.Restaurant;
import com.dishcuss.foodie.hub.R;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Naeem Ibrahim on 9/16/2016.
 */
public class RestaurantMenuFragment extends Fragment{

    AppCompatActivity activity;
    RecyclerView reviewRecyclerView;
    private RecyclerView.LayoutManager reviewLayoutManager;
    Realm realm;
    int restaurantID;
    RealmList<FoodItems> foodItems;


    public RestaurantMenuFragment() {
    }

    public RestaurantMenuFragment(int restaurantID) {
        this.restaurantID=restaurantID;
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

        reviewRecyclerView = (RecyclerView) rootView.findViewById(R.id.simple_recycler_view_for_all);
        GetFooDItems();
        reviewLayoutManager = new LinearLayoutManager(activity);
        reviewRecyclerView.setLayoutManager(reviewLayoutManager);
        reviewRecyclerView.setHasFixedSize(true);
        reviewRecyclerView.setNestedScrollingEnabled(false);


        RestaurantMenuAdapter adapter = new RestaurantMenuAdapter(foodItems,getActivity());
        reviewRecyclerView.setAdapter(adapter);


        return rootView;
    }


    void GetFooDItems(){

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Restaurant> restaurants = realm.where(Restaurant.class).equalTo("id", restaurantID).findAll();
        realm.beginTransaction();
        foodItems =restaurants.get(restaurants.size()-1).getFoodItemsArrayList();
        realm.commitTransaction();
        realm.close();

    }



}
