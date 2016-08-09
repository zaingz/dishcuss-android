package com.holygon.dishcuss.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.holygon.dishcuss.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;

/**
 * Created by Naeem Ibrahim on 7/25/2016.
 */

public class AccountPhotosAdapter extends RecyclerView.Adapter<AccountPhotosAdapter.ViewHolder> {

    private ArrayList<String> imageViewArrayList=new ArrayList<>();
    Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public ViewHolder(View v) {

            super(v);
            imageView = (ImageView) v.findViewById(R.id.account_photo_imageView);
        }
    }

    public AccountPhotosAdapter(ArrayList<String> imageView,Context context) {
        imageViewArrayList.addAll(imageView);
        mContext=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_photos_row_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
//        holder.imageView.setImageResource(imageViewArrayList.get(position));
        String imageUri = imageViewArrayList.get(position);
//        Log.e("ImageUri",imageUri);
        Picasso.with(mContext).load(imageUri).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageViewArrayList.size();
    }
}
