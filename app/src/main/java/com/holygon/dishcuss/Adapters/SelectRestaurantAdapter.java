package com.holygon.dishcuss.Adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
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
 * Created by Naeem Ibrahim on 8/11/2016.
 */
public class SelectRestaurantAdapter extends RecyclerView.Adapter<SelectRestaurantAdapter.ViewHolder> {

    private ArrayList<Restaurant> restaurantRealmList;
    Context context;



    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView restaurantName,restaurantAddress;
        public ImageView coverImage;
        public RelativeLayout select_restaurant_name_parent;
        public ViewHolder(View v) {
            super(v);

            restaurantName = (TextView) v.findViewById(R.id.select_a_restaurant_name);
            restaurantAddress = (TextView) v.findViewById(R.id.select_a_restaurant_address);
            coverImage=(ImageView) v.findViewById(R.id.select_a_restaurant_image_view);
            select_restaurant_name_parent=(RelativeLayout) v.findViewById(R.id.select_a_restaurant_name_parent);

        }
    }
    public SelectRestaurantAdapter(ArrayList<Restaurant> restaurantRealmList,Context context) {
        this.restaurantRealmList=restaurantRealmList;
        this.context=context;
        Log.e("RestaurantRealmList",""+restaurantRealmList.size());
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_a_restaurant_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.restaurantName.setText(restaurantRealmList.get(position).getName());
        holder.restaurantAddress.setText(restaurantRealmList.get(position).getLocation());

        Constants.PicassoImageBackground(restaurantRealmList.get(position).getCover_image_thumbnail(),holder.coverImage,context);



        holder.select_restaurant_name_parent.setOnClickListener(new View.OnClickListener() {
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
}
