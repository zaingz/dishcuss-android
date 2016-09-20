package com.holygon.dishcuss.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.holygon.dishcuss.Model.UserBeenThere;
import com.holygon.dishcuss.Model.UserFollowing;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.RealmList;

/**
 * Created by Naeem Ibrahim on 7/24/2016.
 */
public class AccountBeenThereAdapter extends RecyclerView.Adapter<AccountBeenThereAdapter.ViewHolder> {


    RealmList<UserBeenThere> userBeenTheres;
    Context mContext;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView user_profile_been_there_restaurant_name,user_profile_been_there_restaurant_address;
        public TextView user_profile_been_there_time;
        public ImageView user_profile_been_there_restaurant_image;
        public ViewHolder(View v) {
            super(v);
            user_profile_been_there_restaurant_name = (TextView) v.findViewById(R.id.user_profile_been_there_restaurant_name);
            user_profile_been_there_restaurant_address = (TextView) v.findViewById(R.id.user_profile_been_there_restaurant_address);
            user_profile_been_there_time = (TextView) v.findViewById(R.id.user_profile_been_there_time);
            user_profile_been_there_restaurant_image = (ImageView) v.findViewById(R.id.user_profile_been_there_restaurant_image);
        }
    }

    public AccountBeenThereAdapter(RealmList<UserBeenThere> userBeenTheres,Context mContext) {
        this.userBeenTheres=userBeenTheres;
        this.mContext=mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_been_there_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.user_profile_been_there_restaurant_name.setText(userBeenTheres.get(position).getRestaurantName());
        holder.user_profile_been_there_restaurant_address.setText(userBeenTheres.get(position).getRestaurantLocation());
        if(userBeenTheres.get(position).getCover_image_url()!=null) {
            if (!userBeenTheres.get(position).getCover_image_url().equals("")) {
                Constants.PicassoImageBackground(userBeenTheres.get(position).getCover_image_url(), holder.user_profile_been_there_restaurant_image, mContext);
            }
        }

        Date date=Constants.GetDate(userBeenTheres.get(position).getBeenThereTime());
        SimpleDateFormat localDateFormatForTime = new SimpleDateFormat("h:mm a");
        String time = localDateFormatForTime.format(date);
        SimpleDateFormat localDateFormatForDay = new SimpleDateFormat("EEE");
        String day = localDateFormatForDay.format(date);
        SimpleDateFormat localDateFormatForDate = new SimpleDateFormat("MMM d");
        String dates = localDateFormatForDate.format(date);
        holder.user_profile_been_there_time.setText(dates+" "+time);
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return userBeenTheres.size();
    }
}