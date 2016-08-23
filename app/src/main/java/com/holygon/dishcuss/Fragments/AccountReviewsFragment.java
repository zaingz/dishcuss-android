package com.holygon.dishcuss.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.holygon.dishcuss.Adapters.ReviewsAdapter;
import com.holygon.dishcuss.Model.ReviewModel;
import com.holygon.dishcuss.Model.UserProfile;
import com.holygon.dishcuss.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Naeem Ibrahim on 8/17/2016.
 */
public class AccountReviewsFragment extends Fragment {

    AppCompatActivity activity;
    RecyclerView reviewRecyclerView;
    private RecyclerView.LayoutManager reviewLayoutManager;
    UserProfile userProfile=new UserProfile();
    Realm realm;
    int userID;
    RealmList<ReviewModel> reviewModels;

    public AccountReviewsFragment(int userID) {
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

        reviewRecyclerView = (RecyclerView) rootView.findViewById(R.id.simple_recycler_view_for_all);

        userProfile=GetUserData(userID);
        if(userProfile!=null){
            GetReviewsData();
        }

        reviewLayoutManager = new LinearLayoutManager(activity);
        reviewRecyclerView.setLayoutManager(reviewLayoutManager);
        reviewRecyclerView.setHasFixedSize(true);
        reviewRecyclerView.setNestedScrollingEnabled(false);


        ReviewsAdapter adapter = new ReviewsAdapter(reviewModels,getActivity());
        reviewRecyclerView.setAdapter(adapter);

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
        reviewModels =userProfile.getReviewModelRealmList();

//        Collections.sort(reviewModels, new Comparator<ReviewModel>() {
//            @Override
//            public int compare(ReviewModel lhs, ReviewModel rhs) {
//                return GetDate(rhs.getUpdated_at()).compareTo(GetDate(lhs.getUpdated_at()));
//            }
//        });
    }
    Date GetDate(String date){
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //  System.out.println(convertedDate);
        return convertedDate;
    }
}
