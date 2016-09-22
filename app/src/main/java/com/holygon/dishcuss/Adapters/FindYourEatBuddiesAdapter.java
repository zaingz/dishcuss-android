package com.holygon.dishcuss.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.holygon.dishcuss.Model.MyFeeds;
import com.holygon.dishcuss.Model.UserOffersModel;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Naeem Ibrahim on 8/25/2016.
 */
public class FindYourEatBuddiesAdapter extends RecyclerView.Adapter<FindYourEatBuddiesAdapter.ViewHolder> {

    private ArrayList<MyFeeds> imageViewArrayList=new ArrayList<>();
    Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public  de.hdodenhof.circleimageview.CircleImageView imageView;
        public TextView your_eat_buddies_user_name;

        public ViewHolder(View v) {
            super(v);
            imageView = ( de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.your_eat_buddies_profile_image);
            your_eat_buddies_user_name = (TextView) v.findViewById(R.id.your_eat_buddies_user_name);
        }
    }

    public FindYourEatBuddiesAdapter(ArrayList<MyFeeds> imageView, Context context) {
        imageViewArrayList.addAll(imageView);
        mContext=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_your_eat_buddies_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.your_eat_buddies_user_name.setText(imageViewArrayList.get(position).getName());

        if(!imageViewArrayList.get(position).getAvatarPic().equals("")){
            String imageUri = imageViewArrayList.get(position).getAvatarPic();
            String newUrlString = imageUri.replaceAll(" ", "%20");
            Constants.PicassoImageSrc(newUrlString,holder.imageView,mContext);
        }
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return imageViewArrayList.size();
    }
}
