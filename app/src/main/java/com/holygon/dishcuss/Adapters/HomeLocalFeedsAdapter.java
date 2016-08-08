package com.holygon.dishcuss.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;

import java.util.ArrayList;

/**
 * Created by Naeem Ibrahim on 7/22/2016.
 */
public class HomeLocalFeedsAdapter extends RecyclerView.Adapter<HomeLocalFeedsAdapter.ViewHolder> {

    private ArrayList<String> mDataset = new ArrayList<>();
    private ImageView mImageView;
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.local_feeds_restaurant_name);
            imageView = (ImageView) v.findViewById(R.id.local_feeds_restaurant_image);
        }
    }

    public HomeLocalFeedsAdapter(ArrayList<String> dataset,Context context) {
        mDataset.clear();
        mDataset.addAll(dataset);
        mContext=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_local_feeds_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mDataset.get(position));
//        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),R.drawable.intro_bg);
//        Bitmap circularBitmap = Constants.getRoundedCornerBitmap(bitmap, 10);
//        holder.imageView.setImageBitmap(circularBitmap);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
