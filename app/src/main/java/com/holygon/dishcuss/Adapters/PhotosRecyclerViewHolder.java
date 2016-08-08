package com.holygon.dishcuss.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.holygon.dishcuss.R;

/**
 * Created by Naeem Ibrahim on 7/25/2016.
 */
public class PhotosRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public ImageView countryPhoto;

    public PhotosRecyclerViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        countryPhoto = (ImageView)itemView.findViewById(R.id.account_photo_imageView);
    }

    @Override
    public void onClick(View view) {
        Toast.makeText(view.getContext(), "Clicked Country Position = " + getPosition(), Toast.LENGTH_SHORT).show();
    }
}
