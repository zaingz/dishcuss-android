package com.holygon.dishcuss.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.holygon.dishcuss.Model.KhabaHistoryModel;
import com.holygon.dishcuss.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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

        Date date1 = GetDate("" + imageViewArrayList.get(position).getCredit_time());
        SimpleDateFormat localDateFormatForTime1 = new SimpleDateFormat("h:mm a");
        String gettime = localDateFormatForTime1.format(date1);
        SimpleDateFormat localDateFormatForDay1 = new SimpleDateFormat("EEE");
        String getday = localDateFormatForDay1.format(date1);
        SimpleDateFormat localDateFormatForDate1 = new SimpleDateFormat("MMM d");
        String getdates = localDateFormatForDate1.format(date1);

        holder.khaba_restaurant_time.setText(getdates + ", " + gettime);
        holder.khaba_price.setText("PKR " +imageViewArrayList.get(position).getPrice());

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return imageViewArrayList.size();
    }


    Date GetDate(String date){
        Log.e("Post Detail Date",""+date);
//        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
//        String segments[] = date.split("\\+");
//        String d = segments[0];
//        String d2 = segments[1];
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //  System.out.println(convertedDate);
        return convertedDate;
    }
}
