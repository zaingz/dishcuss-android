package com.dishcuss.foodie.hub.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dishcuss.foodie.hub.Activities.ScanQRCodeActivity;
import com.dishcuss.foodie.hub.Models.UserOffersModel;
import com.dishcuss.foodie.hub.R;
import com.dishcuss.foodie.hub.Utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Naeem Ibrahim on 8/25/2016.
 */
public class UserOffersAdapter extends RecyclerView.Adapter<UserOffersAdapter.ViewHolder> {

    private ArrayList<UserOffersModel> imageViewArrayList=new ArrayList<>();
    Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView offer_restaurant_name;
        public TextView offer_price;
        public LinearLayout offers_parent_layout;
        public ViewHolder(View v) {

            super(v);
            imageView = (ImageView) v.findViewById(R.id.offers_photo_imageView);
            offer_restaurant_name = (TextView) v.findViewById(R.id.offer_restaurant_name);
            offer_price = (TextView) v.findViewById(R.id.offer_price);
            offers_parent_layout = (LinearLayout) v.findViewById(R.id.offers_parent_layout);
        }
    }

    public UserOffersAdapter(ArrayList<UserOffersModel> imageView, Context context) {
        imageViewArrayList.addAll(imageView);
        mContext=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_offers_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.offer_restaurant_name.setText(imageViewArrayList.get(position).getRestaurantName());
        holder.offer_price.setText("PKR "+imageViewArrayList.get(position).getPoints());
        String imageUri = imageViewArrayList.get(position).getImg();
        String newUrlString = imageUri.replaceAll(" ", "%20");
//      Log.e("ImageUri",newUrlString);
        if(!newUrlString.equals(""))
            Picasso.with(mContext).load(newUrlString).into(holder.imageView);

        holder.offers_parent_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Constants.skipLogin) {
                    Intent intent = new Intent(mContext, ScanQRCodeActivity.class);
                    intent.putExtra("OfferID",imageViewArrayList.get(position).getId());
                    mContext.startActivity(intent);
                }
            }
        });

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return imageViewArrayList.size();
    }
}
