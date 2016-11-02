package com.dishcuss.foodies.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dishcuss.foodies.Adapters.AccountFollowerAdapter;
import com.dishcuss.foodies.Model.UserFollowing;
import com.dishcuss.foodies.Model.UserProfile;
import com.dishcuss.foodies.R;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Naeem Ibrahim on 8/17/2016.
 */
public class AccountFollowingFragment extends Fragment {

    AppCompatActivity activity;
    RecyclerView nearbySearchRecyclerView;
    private RecyclerView.LayoutManager nearbySearchLayoutManager;
    UserProfile userProfile=new UserProfile();
    Realm realm;
    int userID;
    RealmList<UserFollowing> userFollowings;

    public AccountFollowingFragment() {
    }

    public AccountFollowingFragment(int userID) {
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
//        ArrayList<String> itemsData = new ArrayList<>();
//        for (int i = 0; i < 50; i++) {
//            itemsData.add("Local Feeds " + i + " / Item " + i);
//        }
        nearbySearchRecyclerView.setNestedScrollingEnabled(false);

        AccountFollowerAdapter adapter = new AccountFollowerAdapter(userFollowings,getActivity());
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
        userFollowings =userProfile.getUserFollowingRealmList();
    }
}
