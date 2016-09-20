package com.holygon.dishcuss.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.holygon.dishcuss.Activities.PhotoDetailActivity;
import com.holygon.dishcuss.R;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Naeem Ibrahim on 7/25/2016.
 */

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.ViewHolder> {

    private ArrayList<String> imageViewArrayList=new ArrayList<>();
    Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.account_photo_imageView);
        }
    }

    public PhotosAdapter(ArrayList<String> imageView, Context context) {
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
        if(imageUri!=null && !imageUri.equals("")) {
            Picasso.with(mContext).load(imageUri).into(holder.imageView);
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                holder.imageView.setDrawingCacheEnabled(true);
                Bitmap b=holder.imageView.getDrawingCache();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.PNG, 75, stream);
                byte[] bytes = stream.toByteArray();
                Intent i = new Intent(((Activity)mContext), PhotoDetailActivity.class);
                i.putExtra("Bitmap", bytes);
                i.putExtra("Type","Photo");
                ((Activity)mContext).startActivity(i);

            }
        });
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return imageViewArrayList.size();
    }
}
