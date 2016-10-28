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
import com.holygon.dishcuss.Model.Restaurant;
import com.holygon.dishcuss.Model.ReviewModel;
import com.holygon.dishcuss.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Naeem Ibrahim on 7/23/2016.
 */
public class RestaurantReviewsFragment extends Fragment {

    AppCompatActivity activity;
    RecyclerView reviewRecyclerView;
    private RecyclerView.LayoutManager reviewLayoutManager;
    Restaurant restaurant=new Restaurant();
    Realm realm;
    int restaurantID;
    RealmList<ReviewModel> reviewModels;


    public RestaurantReviewsFragment() {
    }

    public RestaurantReviewsFragment(int restaurantID) {
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


        ReviewsAdapter adapter = new ReviewsAdapter(reviewModels,getActivity());
        reviewRecyclerView.setAdapter(adapter);


        return rootView;
    }


    Restaurant GetRestaurantData(int rID){
        realm = Realm.getDefaultInstance();
        RealmResults<Restaurant> restaurants = realm.where(Restaurant.class).equalTo("id", rID).findAll();
        if(restaurants.size()>0){
            realm.beginTransaction();
            realm.commitTransaction();
            return restaurants.get(restaurants.size()-1);
        }
        return null;
    }


    void GetReviewsData(){
        reviewModels =restaurant.getReviewModels();

//        Collections.sort(reviewModels, new Comparator<ReviewModel>() {
//            @Override
//            public int compare(ReviewModel lhs, ReviewModel rhs) {
//                return GetDate(rhs.getUpdated_at()).compareTo(GetDate(lhs.getUpdated_at()));
//            }
//        });
    }
    Date GetDate(String date){
//        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
//        String segments[] = date.split("\\+");
//        String d = segments[0];
//        String d2 = segments[1];
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
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
