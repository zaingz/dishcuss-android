package com.dishcuss.foodies.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dishcuss.foodies.Activities.RestaurantDetailActivity;
import com.dishcuss.foodies.Model.UserBeenThere;
import com.dishcuss.foodies.R;
import com.dishcuss.foodies.Utils.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import io.realm.Realm;
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
        public RelativeLayout local_feeds_restaurant_relative_layout;
        public ImageView user_profile_been_there_restaurant_image;
        public ViewHolder(View v) {
            super(v);
            local_feeds_restaurant_relative_layout=(RelativeLayout) v.findViewById(R.id.local_feeds_restaurant_relative_layout);
            user_profile_been_there_restaurant_name = (TextView) v.findViewById(R.id.user_profile_been_there_restaurant_name);
            user_profile_been_there_restaurant_address = (TextView) v.findViewById(R.id.user_profile_been_there_restaurant_address);
            user_profile_been_there_time = (TextView) v.findViewById(R.id.user_profile_been_there_time);
            user_profile_been_there_restaurant_image = (ImageView) v.findViewById(R.id.user_profile_been_there_restaurant_image);
        }
    }

    public AccountBeenThereAdapter(RealmList<UserBeenThere> userBeenTheres,Context mContext) {
        this.userBeenTheres=userBeenTheres;
        this.mContext=mContext;

        Realm realm=Realm.getDefaultInstance();
        realm.beginTransaction();
        Collections.sort(this.userBeenTheres, new Comparator<UserBeenThere>() {
            @Override
            public int compare(UserBeenThere lhs, UserBeenThere rhs) {
                return GetDate(rhs.getBeenThereTime()).compareTo(GetDate(lhs.getBeenThereTime()));
            }
        });
        realm.commitTransaction();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_been_there_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.user_profile_been_there_restaurant_name.setText(userBeenTheres.get(position).getRestaurantName());
        holder.user_profile_been_there_restaurant_address.setText(userBeenTheres.get(position).getRestaurantLocation());
        if(userBeenTheres.get(position).getCover_image_url()!=null) {
            if (!userBeenTheres.get(position).getCover_image_url().equals("")) {
                Constants.PicassoImageSrc(userBeenTheres.get(position).getCover_image_url(), holder.user_profile_been_there_restaurant_image, mContext);
            }
        }

        holder.local_feeds_restaurant_relative_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, RestaurantDetailActivity.class);
                intent.putExtra("RestaurantID", userBeenTheres.get(position).getId());
                mContext.startActivity(intent);
            }
        });

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

    Date GetDate(String date){

        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return convertedDate;
    }
}