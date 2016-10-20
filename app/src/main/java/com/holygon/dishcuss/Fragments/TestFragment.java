package com.holygon.dishcuss.Fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.holygon.dishcuss.Adapters.PhotosAdapter;
import com.holygon.dishcuss.Model.PhotoModel;
import com.holygon.dishcuss.Model.UserProfile;
import com.holygon.dishcuss.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


/**
 * Created by Naeem Ibrahim on 10/19/2016.
 */
public class TestFragment extends Fragment {

    AppCompatActivity activity;
    RecyclerView nearbySearchRecyclerView;
    PhotosAdapter adapter;
    private GridLayoutManager gridLayout;
    ArrayList<String> itemsData = new ArrayList<>();
    ArrayList<String> showingItemsData = new ArrayList<>();
    int userID;


    //Scroll Load Data
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 1;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    ProgressBar progressBar;

    public TestFragment(int userID) {
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
        View rootView = inflater.inflate(R.layout.photos_recyclerview, container, false);
        activity = (AppCompatActivity) getActivity();



        gridLayout = new GridLayoutManager(getActivity(),3);
        nearbySearchRecyclerView = (RecyclerView) rootView.findViewById(R.id.simple_recycler_view_for_all);
        progressBar=(ProgressBar)rootView.findViewById(R.id.native_progress_bar);
        nearbySearchRecyclerView.setLayoutManager(gridLayout);
        nearbySearchRecyclerView.setHasFixedSize(true);
        nearbySearchRecyclerView.setNestedScrollingEnabled(false);
        adapter = new PhotosAdapter(showingItemsData,getActivity());
        nearbySearchRecyclerView.setAdapter(adapter);


        nearbySearchRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                progressBar.setVisibility(View.VISIBLE);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {

                        progressBar.setVisibility(View.GONE);
                        visibleItemCount = nearbySearchRecyclerView.getChildCount();
                        totalItemCount = gridLayout.getItemCount();
                        firstVisibleItem = gridLayout.findFirstVisibleItemPosition();
                        if (loading) {
                            if (totalItemCount > previousTotal) {
                                loading = false;
                                previousTotal = totalItemCount;
                            }
                        }
                        if (!loading && (totalItemCount - visibleItemCount)
                                <= (firstVisibleItem + visibleThreshold)) {
                            // End has been reached
                            showingItemsData = new ArrayList<>();
                            int newLoad=totalItemCount+6;

                            if(itemsData.size()>newLoad){
                                for (int j = totalItemCount; j <newLoad; j++) {
                                    if (!itemsData.get(j).equals("")) {
                                        showingItemsData.add(itemsData.get(j));
                                    }
                                }
                            }
                            else
                            {
                                for (int j =totalItemCount; j < itemsData.size(); j++) {
                                    if (!itemsData.get(j).equals("")) {
                                        showingItemsData.add(itemsData.get(j));
                                    }
                                }
                            }
                            // Do something
                            adapter.UpdateList(showingItemsData);
                            loading = true;
                        }
                    }
                }, 10000);
            }
        });

        return rootView;
    }


    void SetImageURL(){

        Realm realm = Realm.getDefaultInstance();
        RealmResults<UserProfile> userProfiles = realm.where(UserProfile.class).equalTo("id", userID).findAll();
        realm.beginTransaction();

        if(userProfiles.size()>0)
        {
            RealmList<PhotoModel> photoModels = userProfiles.get(0).getPhotoModelRealmList();

            Log.e("Photos Count",""+photoModels.size());

            for (int j = 0; j < photoModels.size(); j++) {
                if (!photoModels.get(j).getUrl().equals("")) {
                    itemsData.add(photoModels.get(j).getUrl());
                }
            }


            if(itemsData.size()>6){
                for (int j = 0; j <6; j++) {
                    if (!itemsData.get(j).equals("")) {
                        showingItemsData.add(itemsData.get(j));
                    }
                }
            }
            else
            {
                for (int j = 0; j < itemsData.size(); j++) {
                    if (!itemsData.get(j).equals("")) {
                        showingItemsData.add(itemsData.get(j));
                    }
                }
            }

            realm.commitTransaction();
            realm.close();
        }
    }
}
