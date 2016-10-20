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
import com.holygon.dishcuss.Model.UserBeenThere;
import com.holygon.dishcuss.Model.UserProfile;
import com.holygon.dishcuss.R;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Naeem Ibrahim on 7/24/2016.
 */
public class AccountBeenThereFragment extends Fragment {

    AppCompatActivity activity;
    RecyclerView nearbySearchRecyclerView;
    private RecyclerView.LayoutManager nearbySearchLayoutManager;
    UserProfile userProfile=new UserProfile();
    Realm realm;
    int userID;
    RealmList<UserBeenThere> userBeenTheres;

    public AccountBeenThereFragment(int userID) {
        this.userID=userID;
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


        userProfile=GetUserData(userID);
        if(userProfile!=null){
            GetReviewsData();
        }


        nearbySearchLayoutManager = new LinearLayoutManager(activity);
        nearbySearchRecyclerView.setLayoutManager(nearbySearchLayoutManager);
        nearbySearchRecyclerView.setHasFixedSize(true);
        nearbySearchRecyclerView.setNestedScrollingEnabled(false);

        AccountBeenThereAdapter adapter = new AccountBeenThereAdapter(userBeenTheres,getActivity());
        nearbySearchRecyclerView.setAdapter(adapter);


        return rootView;
    }



    UserProfile GetUserData(int uid){
        realm = Realm.getDefaultInstance();
        RealmResults<UserProfile> userProfiles = realm.where(UserProfile.class).equalTo("id", uid).findAll();
        if(userProfiles.size()>0){
            realm.beginTransaction();
            realm.commitTransaction();
            return userProfiles.get(0);
        }
        return null;
    }

    void GetReviewsData(){
        userBeenTheres =userProfile.getUserBeenThereRealmList();
    }
}
