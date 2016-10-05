package com.holygon.dishcuss.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.holygon.dishcuss.Adapters.PhotosAdapter;
import com.holygon.dishcuss.Model.FoodItems;
import com.holygon.dishcuss.Model.PhotoModel;
import com.holygon.dishcuss.Model.Restaurant;
import com.holygon.dishcuss.Model.UserProfile;
import com.holygon.dishcuss.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Naeem Ibrahim on 8/17/2016.
 */
public class AccountPhotosFragment extends Fragment {

    AppCompatActivity activity;
    RecyclerView nearbySearchRecyclerView;
    private GridLayoutManager gridLayout;
    ArrayList<String> itemsData = new ArrayList<>();
    int userID;

    public AccountPhotosFragment(int userID) {
        this.userID =userID;
        SetImageURL();
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



        nearbySearchRecyclerView.setNestedScrollingEnabled(false);
        PhotosAdapter adapter = new PhotosAdapter(itemsData,getActivity());
        nearbySearchRecyclerView.setAdapter(adapter);


        return rootView;
    }


    void SetImageURL(){

        Realm realm = Realm.getDefaultInstance();
        RealmResults<UserProfile> userProfiles = realm.where(UserProfile.class).equalTo("id", userID).findAll();
        realm.beginTransaction();

        if(userProfiles.size()>0)
        {
            RealmList<PhotoModel> photoModels = userProfiles.get(0).getPhotoModelRealmList();

            for (int j = 0; j < photoModels.size(); j++) {
                if(!photoModels.get(j).getUrl().equals("")) {
                    itemsData.add(photoModels.get(j).getUrl());
                }
            }
            realm.commitTransaction();
            realm.close();
        }
    }
}
