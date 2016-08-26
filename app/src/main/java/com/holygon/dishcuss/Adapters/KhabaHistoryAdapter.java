package com.holygon.dishcuss.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.holygon.dishcuss.Model.KhabaHistoryModel;
import com.holygon.dishcuss.Model.UserOffersModel;
import com.holygon.dishcuss.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Naeem Ibrahim on 8/25/2016.
 */
public class KhabaHistoryAdapter extends RecyclerView.Adapter<KhabaHistoryAdapter.ViewHolder> {

    private ArrayList<KhabaHistoryModel> imageViewArrayList=new ArrayList<>();
    Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public  de.hdodenhof.circleimageview.CircleImageView imageView;
        public TextView khaba_restaurant_name,khaba_restaurant_location,khaba_restaurant_time,khaba_price;
        public ViewHolder(View v) {

            super(v);
            imageView = ( de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.khaba_history_restaurant_image);
            khaba_restaurant_name = (TextView) v.findViewById(R.id.khaba_restaurant_name);
            khaba_restaurant_location = (TextView) v.findViewById(R.id.khaba_restaurant_location);
            khaba_restaurant_time = (TextView) v.findViewById(R.id.khaba_restaurant_time);
            khaba_price = (TextView) v.findViewById(R.id.khaba_price);
        }
    }

    public KhabaHistoryAdapter(ArrayList<KhabaHistoryModel> imageView, Context context) {
        imageViewArrayList.addAll(imageView);
        mContext=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.khaba_history_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
//        holder.imageView.setImageResource(imageViewArrayList.get(position));
        String imageUri = imageViewArrayList.get(position).getRestaurantImage();
        String newUrlString = imageUri.replaceAll(" ", "%20");
        Log.e("ImageUri",newUrlString);
        if(!newUrlString.equals(""))
            Picasso.with(mContext).load(newUrlString).into(holder.imageView);


        holder.khaba_restaurant_name.setText(imageViewArrayList.get(position).getRestaurantName());
        holder.khaba_restaurant_location.setText(imageViewArrayList.get(position).getRestaurantLocation());
        holder.khaba_restaurant_time.setText(imageViewArrayList.get(position).getCredit_time());
        holder.khaba_price.setText("PKR " +imageViewArrayList.get(position).getPrice());
    }

    @Override
    public int getItemCount() {
        return imageViewArrayList.size();
    }
}
