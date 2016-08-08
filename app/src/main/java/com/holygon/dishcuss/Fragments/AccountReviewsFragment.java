package com.holygon.dishcuss.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.holygon.dishcuss.Adapters.AccountReviewsAdapter;
import com.holygon.dishcuss.Model.FoodItems;
import com.holygon.dishcuss.Model.Restaurant;
import com.holygon.dishcuss.Model.ReviewModel;
import com.holygon.dishcuss.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Naeem Ibrahim on 7/23/2016.
 */
public class AccountReviewsFragment extends Fragment {

    AppCompatActivity activity;
    RecyclerView reviewRecyclerView;
    private RecyclerView.LayoutManager reviewLayoutManager;
    Restaurant restaurant=new Restaurant();
    Realm realm;
    int restaurantID;
    RealmList<ReviewModel> reviewModels;


    public AccountReviewsFragment(int restaurantID) {

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

        restaurant=GetRestaurantData(restaurantID);
        if(restaurant!=null){
            GetReviewsData();
        }

        reviewLayoutManager = new LinearLayoutManager(activity);
        reviewRecyclerView.setLayoutManager(reviewLayoutManager);
        reviewRecyclerView.setHasFixedSize(true);
        reviewRecyclerView.setNestedScrollingEnabled(false);

        AccountReviewsAdapter adapter = new AccountReviewsAdapter(reviewModels);
        reviewRecyclerView.setAdapter(adapter);


        return rootView;
    }


    Restaurant GetRestaurantData(int rID){
        realm = Realm.getDefaultInstance();
        RealmResults<Restaurant> restaurants = realm.where(Restaurant.class).equalTo("id", rID).findAll();
        if(restaurants.size()>0){
            realm.beginTransaction();
            realm.commitTransaction();
            return restaurants.get(0);
        }
        return null;
    }


    void GetReviewsData(){
        reviewModels =restaurant.getReviewModels();
    }
}
