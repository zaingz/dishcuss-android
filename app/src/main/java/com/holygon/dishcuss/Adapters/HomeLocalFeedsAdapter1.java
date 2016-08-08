package com.holygon.dishcuss.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.holygon.dishcuss.Model.LocalFeedCheckIn;
import com.holygon.dishcuss.Model.LocalFeedReview;
import com.holygon.dishcuss.Model.LocalFeeds;
import com.holygon.dishcuss.Model.MyFeeds;
import com.holygon.dishcuss.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by Naeem Ibrahim on 8/8/2016.
 */
public class HomeLocalFeedsAdapter1 extends RecyclerView.Adapter<HomeLocalFeedsAdapter1.ViewHolder> {

    private LocalFeeds localFeeds;
    private Context mContext;
    RealmList<LocalFeedReview> localFeedReviewRealmList;
    RealmList<LocalFeedCheckIn> localFeedCheckInRealmList;

    List<Object> objects=new ArrayList<>();
    List<Object> sortedObjects=new ArrayList<>();


//    int reviewListCount=0;
//    int checkInListCount=0;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView restaurantName,restaurantAddress,status;
        public TextView local_feeds_user_name,local_feeds_user_review_time;
        public TextView review_likes_count_tv,review_comments_count_tv,review_share_count_tv;
        public ImageView local_feeds_restaurant_image;
        public de.hdodenhof.circleimageview.CircleImageView profileImage;

        public ViewHolder(View v) {
            super(v);
            restaurantName = (TextView) v.findViewById(R.id.local_feeds_restaurant_name);
            restaurantAddress = (TextView) v.findViewById(R.id.local_feeds_restaurant_address);
            local_feeds_user_name = (TextView) v.findViewById(R.id.local_feeds_user_name);
            local_feeds_user_review_time = (TextView) v.findViewById(R.id.local_feeds_user_review_time);
            review_likes_count_tv = (TextView) v.findViewById(R.id.review_likes_count_tv);
            review_comments_count_tv = (TextView) v.findViewById(R.id.review_comments_count_tv);
            review_share_count_tv = (TextView) v.findViewById(R.id.review_share_count_tv);
            status = (TextView) v.findViewById(R.id.review_or_checkin_status);
            local_feeds_restaurant_image = (ImageView) v.findViewById(R.id.local_feeds_restaurant_image);
            profileImage = (de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.local_feeds_profile_image);
        }
    }

    public HomeLocalFeedsAdapter1(LocalFeeds localFeeds,Context context) {
        this.localFeeds=localFeeds;
        mContext=context;
        localFeedReviewRealmList=localFeeds.getLocalFeedReviewRealmList();
        localFeedCheckInRealmList=localFeeds.getLocalFeedCheckInRealmList();

        Collections.sort(localFeedReviewRealmList, new Comparator<LocalFeedReview>() {
            @Override
            public int compare(LocalFeedReview lhs, LocalFeedReview rhs) {
                    return GetDate(rhs.getUpdated_at()).compareTo(GetDate(lhs.getUpdated_at()));
            }
        });

        Collections.sort(localFeedCheckInRealmList, new Comparator<LocalFeedCheckIn>() {
            @Override
            public int compare(LocalFeedCheckIn lhs, LocalFeedCheckIn rhs) {
                return GetDate(rhs.getUpdated_at()).compareTo(GetDate(lhs.getUpdated_at()));
            }
        });

        objects=Merge(localFeedReviewRealmList,localFeedCheckInRealmList);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_local_feeds_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

                Log.e("LocalFeed",objects.get(position).getClass().getName());

                if (objects.get(position).getClass().equals(io.realm.LocalFeedReviewRealmProxy.class)){
                    LocalFeedReview localFeedReview= (LocalFeedReview) objects.get(position);

                    holder.restaurantName.setText(localFeedReview.getReviewOnName());
                    holder.restaurantAddress.setText(localFeedReview.getReviewOnLocation());

                    holder.local_feeds_user_review_time.setText(localFeedReview.getReviewerLocation());
                    holder.local_feeds_user_name.setText(localFeedReview.getReviewerName());

                    holder.review_likes_count_tv.setText(""+localFeedReview.getReviewLikesCount());
                    holder.review_comments_count_tv.setText(""+localFeedReview.getReviewCommentCount());
                    holder.review_share_count_tv.setText(""+localFeedReview.getReviewSharesCount());

                    holder.status.setText(localFeedReview.getSummary());

                    PicassoImage(localFeedReview.getReviewImage(),holder.local_feeds_restaurant_image);
                    PicassoImage(localFeedReview.getReviewerAvatar(),holder.profileImage);

                }
                else if(objects.get(position).getClass().equals(io.realm.LocalFeedCheckInRealmProxy.class))
                {
                    LocalFeedCheckIn localFeedCheckIn= (LocalFeedCheckIn) objects.get(position);
                    holder.restaurantName.setText(localFeedCheckIn.getCheckInOnName());
                    holder.restaurantAddress.setText(localFeedCheckIn.getCheckInOnLocation());

                    holder.local_feeds_user_review_time.setText(localFeedCheckIn.getCheckInWriterLocation());
                    holder.local_feeds_user_name.setText(localFeedCheckIn.getCheckInWriterName());

                    holder.review_likes_count_tv.setText(""+localFeedCheckIn.getReviewLikesCount());
                    holder.review_comments_count_tv.setText(""+localFeedCheckIn.getReviewCommentCount());
                    holder.review_share_count_tv.setText(""+localFeedCheckIn.getReviewSharesCount());

                    holder.status.setText(localFeedCheckIn.getCheckInStatus());

                    PicassoImage(localFeedCheckIn.getCheckInImage(),holder.local_feeds_restaurant_image);
                    PicassoImage(localFeedCheckIn.getCheckInWriterAvatar(),holder.profileImage);


                }
                else
                {
                    Log.e("LocalFeed","Why so?");
                }

    }

    @Override
    public int getItemCount() {
        return objects.size();
    }


    void PicassoImage(String URL,ImageView imageView){
        if(URL!=null && !URL.equals("")){
            Picasso.with(mContext).load(URL).into(imageView);
        }
    }


    Date GetDate(String date){
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(convertedDate);
        return convertedDate;
    }



    List<Object> Merge(RealmList<LocalFeedReview> A,RealmList<LocalFeedCheckIn> B) {

        List<Object> C=new ArrayList<>();
        int i, j, m, n,k;
        i = 0;
        j = 0;
        m = A.size();
        n = B.size();
        while (i < m && j < n) {
            if (GetDate(A.get(i).getUpdated_at()).compareTo(GetDate(B.get(j).getUpdated_at()))>0) {
                C.add(A.get(i));
                i++;
            } else {
                C.add(B.get(j));
                j++;
            }
        }
        if (i< m)
        {
            for (int p = i; p < m; p++) {
                C.add(A.get(p));
            }
        }
        else
        {
            for (int p = j; p < n; p++) {
                C.add(B.get(p));
            }
        }
        return C;
    }
}
