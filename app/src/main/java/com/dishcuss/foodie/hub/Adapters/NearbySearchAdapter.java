package com.dishcuss.foodie.hub.Adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dishcuss.foodie.hub.Activities.RestaurantDetailActivity;
import com.dishcuss.foodie.hub.Models.FoodItems;
import com.dishcuss.foodie.hub.Models.FoodsCategory;
import com.dishcuss.foodie.hub.Models.PhotoModel;
import com.dishcuss.foodie.hub.Models.Restaurant;
import com.dishcuss.foodie.hub.R;
import com.dishcuss.foodie.hub.Utils.Constants;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Naeem Ibrahim on 7/23/2016.
 */
public class NearbySearchAdapter extends RecyclerView.Adapter<NearbySearchAdapter.ViewHolder> {

    private ArrayList<String> mDataset = new ArrayList<>();
    private ArrayList<Restaurant> restaurantRealmList;
    Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView,nearby_search_restaurant_distance;
        public TextView restaurantName,restaurantAddress,ratting,nearby_search_restaurant_cost,nearby_search_restaurant_cousine;
        public TextView nearby_search_photos_count, nearby_search_likes_count;
        public LinearLayout callLayout;
        public RelativeLayout nearby_search_layout_recycler_view;
        public ImageView coverImage;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.nearby_search_restaurant_name);

            restaurantName = (TextView) v.findViewById(R.id.nearby_search_restaurant_name);
            nearby_search_restaurant_distance = (TextView) v.findViewById(R.id.nearby_search_restaurant_distance);
            nearby_search_restaurant_cost = (TextView) v.findViewById(R.id.nearby_search_restaurant_cost);
            nearby_search_restaurant_cousine = (TextView) v.findViewById(R.id.nearby_search_restaurant_cousine);
            restaurantAddress = (TextView) v.findViewById(R.id.nearby_search_restaurant_address);
            ratting = (TextView) v.findViewById(R.id.nearby_search_restaurant_rating);
            nearby_search_photos_count = (TextView) v.findViewById(R.id.nearby_search_photos_count);
            nearby_search_likes_count = (TextView) v.findViewById(R.id.nearby_search_likes_count);

            coverImage=(ImageView) v.findViewById(R.id.nearby_search_restaurant_image);

            callLayout=(LinearLayout)v.findViewById(R.id.nearby_search_call_layout);
            nearby_search_layout_recycler_view=(RelativeLayout)v.findViewById(R.id.nearby_search_layout_recycler_view);

        }
    }

    public NearbySearchAdapter(ArrayList<Restaurant> restaurantRealmList,Context context) {
        this.restaurantRealmList=restaurantRealmList;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nearby_search_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder,final int position) {
        ArrayList<String> itemsData = new ArrayList<>();
        itemsData=SetImageURL(restaurantRealmList.get(position).getId());
        holder.restaurantName.setText(restaurantRealmList.get(position).getName());
        holder.nearby_search_restaurant_distance.setText(restaurantRealmList.get(position).getDistanceAway()+" KM Away");
        holder.nearby_search_restaurant_cost.setText(" "+restaurantRealmList.get(position).getPricePerHead()+"/head");
        holder.restaurantAddress.setText(restaurantRealmList.get(position).getLocation());
        holder.ratting.setText(""+restaurantRealmList.get(position).getRatting());
        holder.nearby_search_likes_count.setText(""+restaurantRealmList.get(position).getBookmark_count());
        holder.nearby_search_photos_count.setText(""+itemsData.size());


        for (int i = 0; i < restaurantRealmList.get(position).getFoodItemsArrayList().size(); i++) {
            RealmList<FoodsCategory> foodsCategoryRealmList = restaurantRealmList.get(position).getFoodItemsArrayList().get(i).getFoodsCategories();
            if (foodsCategoryRealmList.size() > 0) {
                if (holder.nearby_search_restaurant_cousine.getText().toString().equals("")) {
                    holder.nearby_search_restaurant_cousine.setText(foodsCategoryRealmList.get(0).getCategoryName());
                } else {
                    holder.nearby_search_restaurant_cousine.setText("," + foodsCategoryRealmList.get(0).getCategoryName());
                }
            }
        }

        Constants.PicassoImageSrc(restaurantRealmList.get(position).getCover_image_thumbnail(),holder.coverImage,context);

//
//        Log.e("Lat",""+restaurantRealmList.get(position).getRestaurantLat());
//        Log.e("Long",""+restaurantRealmList.get(position).getRestaurantLong());


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


        holder.nearby_search_layout_recycler_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, RestaurantDetailActivity.class);
                intent.putExtra("RestaurantID", restaurantRealmList.get(position).getId());
                context.startActivity(intent);
            }
        });
        holder.setIsRecyclable(false);
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
                if(!photoModels.get(j).getUrl().equals("")) {
                    itemsData.add(photoModels.get(j).getUrl());
                }
            }
        }
        realm.commitTransaction();
        realm.close();
        return itemsData;
    }
}
