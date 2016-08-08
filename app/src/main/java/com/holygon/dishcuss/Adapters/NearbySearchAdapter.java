package com.holygon.dishcuss.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.holygon.dishcuss.R;

import java.util.ArrayList;

/**
 * Created by Naeem Ibrahim on 7/23/2016.
 */
public class NearbySearchAdapter   extends RecyclerView.Adapter<NearbySearchAdapter.ViewHolder> {

    private ArrayList<String> mDataset = new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;
        public ViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.nearby_search_restaurant_name);
        }
    }

    public NearbySearchAdapter(ArrayList<String> dataset) {
        mDataset.clear();
        mDataset.addAll(dataset);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.nearby_search_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(mDataset.get(position));

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
