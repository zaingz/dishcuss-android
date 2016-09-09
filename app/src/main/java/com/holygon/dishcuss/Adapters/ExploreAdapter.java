package com.holygon.dishcuss.Adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.holygon.dishcuss.Activities.RestaurantDetailActivity;
import com.holygon.dishcuss.Model.FoodItems;
import com.holygon.dishcuss.Model.PhotoModel;
import com.holygon.dishcuss.Model.Restaurant;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Naeem Ibrahim on 7/22/2016.
 */
public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ViewHolder> {

    private ArrayList<Restaurant> restaurantRealmList;
    Context context;



    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView restaurantName,restaurantAddress,ratting;
        public TextView explore_restaurant_photos_count,explore_restaurant_likes_count;
        public ImageView explore_restaurant_image_1,explore_restaurant_image_2,explore_restaurant_image_3,explore_restaurant_image_4;
        public CardView CardView_explore_restaurant_image_1,CardView_explore_restaurant_image_2,CardView_explore_restaurant_image_3,CardView_explore_restaurant_image_4;
        public LinearLayout callLayout,images_parent_layout;
        public RelativeLayout explore_restaurant_name_parent;
        public ImageView coverImage;
        public ViewHolder(View v) {
            super(v);

            restaurantName = (TextView) v.findViewById(R.id.explore_restaurant_name);
            restaurantAddress = (TextView) v.findViewById(R.id.explore_restaurant_address);
            ratting = (TextView) v.findViewById(R.id.explore_restaurant_rating);
            explore_restaurant_photos_count = (TextView) v.findViewById(R.id.explore_restaurant_photos_count);
            explore_restaurant_likes_count = (TextView) v.findViewById(R.id.explore_restaurant_likes_counts);

            explore_restaurant_image_1=(ImageView) v.findViewById(R.id.explore_restaurant_image_1);
            explore_restaurant_image_2=(ImageView) v.findViewById(R.id.explore_restaurant_image_2);
            explore_restaurant_image_3=(ImageView) v.findViewById(R.id.explore_restaurant_image_3);
            explore_restaurant_image_4=(ImageView) v.findViewById(R.id.explore_restaurant_image_4);

            CardView_explore_restaurant_image_1=(CardView) v.findViewById(R.id.CardView_explore_restaurant_image_1);
            CardView_explore_restaurant_image_2=(CardView) v.findViewById(R.id.CardView_explore_restaurant_image_2);
            CardView_explore_restaurant_image_3=(CardView) v.findViewById(R.id.CardView_explore_restaurant_image_3);
            CardView_explore_restaurant_image_4=(CardView) v.findViewById(R.id.CardView_explore_restaurant_image_4);


            coverImage=(ImageView) v.findViewById(R.id.explore_restaurant_image_view);

            callLayout=(LinearLayout)v.findViewById(R.id.explore_restaurant_call_now_layout);

            images_parent_layout=(LinearLayout)v.findViewById(R.id.images_parent_layout);
            explore_restaurant_name_parent=(RelativeLayout)v.findViewById(R.id.explore_restaurant_name_parent);

        }
    }
    public ExploreAdapter(ArrayList<Restaurant> restaurantRealmList,Context context) {
        this.restaurantRealmList=restaurantRealmList;
        this.context=context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.explore_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ArrayList<String> itemsData = new ArrayList<>();
        itemsData=SetImageURL(restaurantRealmList.get(position).getId());
        holder.restaurantName.setText(restaurantRealmList.get(position).getName());
        holder.restaurantAddress.setText(restaurantRealmList.get(position).getLocation());
        holder.ratting.setText(""+restaurantRealmList.get(position).getRatting());
        holder.explore_restaurant_likes_count.setText(""+restaurantRealmList.get(position).getBookmark_count());
        holder.explore_restaurant_photos_count.setText(""+itemsData.size());


        HidesImages(holder,itemsData.size());

        Constants.PicassoImageBackground(restaurantRealmList.get(position).getCover_image_thumbnail(),holder.coverImage,context);

        if(itemsData.size()>0)
            Constants.PicassoImageBackground(itemsData.get(0),holder.explore_restaurant_image_1,context);
        if(itemsData.size()>1)
            Constants.PicassoImageBackground(itemsData.get(1),holder.explore_restaurant_image_2,context);
        if(itemsData.size()>2)
            Constants.PicassoImageBackground(itemsData.get(2),holder.explore_restaurant_image_3,context);
        if(itemsData.size()>3)
            Constants.PicassoImageBackground(itemsData.get(3),holder.explore_restaurant_image_4,context);


        holder.callLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" +restaurantRealmList.get(position).getNumbers()));
                if ( ContextCompat.checkSelfPermission( context, Manifest.permission.CALL_PHONE ) != PackageManager.PERMISSION_GRANTED ) {
                    return;
                }
                context.startActivity(intent);
            }
        });


        holder.explore_restaurant_name_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, RestaurantDetailActivity.class);
                intent.putExtra("RestaurantID", restaurantRealmList.get(position).getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return restaurantRealmList.size();
    }





    ArrayList<String> SetImageURL(int restaurantID){
        ArrayList<String> itemsData = new ArrayList<>();
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Restaurant> restaurants = realm.where(Restaurant.class).equalTo("id", restaurantID).findAll();
        realm.beginTransaction();
        RealmList<FoodItems> foodItems =restaurants.get(0).getFoodItemsArrayList();
        for(int i=0;i<foodItems.size();i++){
            RealmList<PhotoModel> photoModels=foodItems.get(i).getPhotoModels();
            for (int j=0;j<photoModels.size();j++){
                itemsData.add(photoModels.get(j).getUrl());
            }
        }
        realm.commitTransaction();
        realm.close();
        return itemsData;
    }

    void HidesImages(ViewHolder holder,int imagesCount){

        if(imagesCount>0){
            holder.images_parent_layout.setVisibility(View.VISIBLE);
        }

        if(imagesCount==1){
            holder.CardView_explore_restaurant_image_2.setVisibility(View.GONE);
            holder.CardView_explore_restaurant_image_3.setVisibility(View.GONE);
            holder.CardView_explore_restaurant_image_4.setVisibility(View.GONE);
        }else if(imagesCount==2){
            holder.CardView_explore_restaurant_image_3.setVisibility(View.GONE);
            holder.CardView_explore_restaurant_image_4.setVisibility(View.GONE);
        }else if(imagesCount==3){
            holder.CardView_explore_restaurant_image_4.setVisibility(View.GONE);
        }
    }
}
