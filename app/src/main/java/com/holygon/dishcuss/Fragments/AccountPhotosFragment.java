package com.holygon.dishcuss.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.holygon.dishcuss.Adapters.AccountBeenThereAdapter;
import com.holygon.dishcuss.Adapters.AccountPhotosAdapter;
import com.holygon.dishcuss.Model.FoodItems;
import com.holygon.dishcuss.Model.FoodsCategory;
import com.holygon.dishcuss.Model.PhotoModel;
import com.holygon.dishcuss.Model.Restaurant;
import com.holygon.dishcuss.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Naeem Ibrahim on 7/25/2016.
 */
public class AccountPhotosFragment extends Fragment {

    AppCompatActivity activity;
    RecyclerView nearbySearchRecyclerView;
    private GridLayoutManager gridLayout;
    ArrayList<String> itemsData = new ArrayList<>();
    int restaurantID;

    public AccountPhotosFragment(int restaurantID) {
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
        gridLayout = new GridLayoutManager(getActivity(),3);
        nearbySearchRecyclerView = (RecyclerView) rootView.findViewById(R.id.simple_recycler_view_for_all);


        nearbySearchRecyclerView.setLayoutManager(gridLayout);
        nearbySearchRecyclerView.setHasFixedSize(true);

        SetImageURL();

        nearbySearchRecyclerView.setNestedScrollingEnabled(false);
        AccountPhotosAdapter adapter = new AccountPhotosAdapter(itemsData,getActivity());
        nearbySearchRecyclerView.setAdapter(adapter);


        return rootView;
    }


    void SetImageURL(){

        Realm realm = Realm.getDefaultInstance();
        RealmResults<Restaurant> restaurants = realm.where(Restaurant.class).equalTo("id", restaurantID).findAll();
        realm.beginTransaction();
        RealmList<FoodItems> foodItems =restaurants.get(0).getFoodItemsArrayList();
//        Log.e("FoodItems : ",""+foodItems.size());
        for(int i=0;i<foodItems.size();i++){

            RealmList<PhotoModel> photoModels=foodItems.get(i).getPhotoModels();

//            Log.e("photoModels : ",""+photoModels.size());

            for (int j=0;j<photoModels.size();j++){
//                Log.e("Photo ID : ",""+photoModels.get(j).getId());
//                Log.e("Photo URL : ",""+photoModels.get(j).getUrl());
                itemsData.add(photoModels.get(j).getUrl());
            }
        }

        realm.commitTransaction();
        realm.close();

    }
}
