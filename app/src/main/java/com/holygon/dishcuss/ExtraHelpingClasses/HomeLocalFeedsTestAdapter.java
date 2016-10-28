package com.holygon.dishcuss.ExtraHelpingClasses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.holygon.dishcuss.Activities.PhotoDetailActivity;
import com.holygon.dishcuss.Activities.PostDetailActivity;
import com.holygon.dishcuss.Activities.ProfilesDetailActivity;
import com.holygon.dishcuss.Activities.RestaurantDetailActivity;
import com.holygon.dishcuss.Model.Comment;
import com.holygon.dishcuss.Model.LocalFeedCheckIn;
import com.holygon.dishcuss.Model.LocalFeedReview;
import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;
import com.holygon.dishcuss.Utils.GenericRoutes;
import com.holygon.dishcuss.Utils.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 10/4/2016.
 */
public class HomeLocalFeedsTestAdapter extends RecyclerView.Adapter<HomeLocalFeedsTestAdapter.ViewHolder> {

    private Context mContext;
    RealmList<LocalFeedReview> localFeedReviewRealmList=new RealmList<>();
    RealmList<LocalFeedCheckIn> localFeedCheckInRealmList=new RealmList<>();
    RealmList<Comment> commentRealmList;

    List<Object> objects=new ArrayList<>();

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView restaurantName,restaurantAddress,status;
        ImageView image_bookmark;
        public TextView local_feeds_user_name,local_feeds_user_review_time;
        public TextView review_likes_count_tv,review_comments_count_tv,review_share_count_tv;
        public ImageView local_feeds_restaurant_image,feeds_post_image;
        public de.hdodenhof.circleimageview.CircleImageView profileImage;
        public RelativeLayout local_feeds_restaurant_relative_layout;
        LinearLayout layout_like;
        LinearLayout layout_comment;
        RelativeLayout user_profile_layout;
        TextView comment_TextView;
        ProgressBar image_spinner;

        //Comments Data
        public LinearLayout comments_row;
        public LinearLayout comments_add_row;


        public ViewHolder(View v) {
            super(v);
            restaurantName = (TextView) v.findViewById(R.id.local_feeds_restaurant_name);
            restaurantAddress = (TextView) v.findViewById(R.id.local_feeds_restaurant_address);
            image_bookmark = (ImageView) v.findViewById(R.id.image_bookmark);
            local_feeds_user_name = (TextView) v.findViewById(R.id.local_feeds_user_name);
            local_feeds_user_review_time = (TextView) v.findViewById(R.id.local_feeds_user_review_time);
            review_likes_count_tv = (TextView) v.findViewById(R.id.review_likes_count_tv);
            review_comments_count_tv = (TextView) v.findViewById(R.id.review_comments_count_tv);
            review_share_count_tv = (TextView) v.findViewById(R.id.review_share_count_tv);
            status = (TextView) v.findViewById(R.id.review_or_checkin_status);
            local_feeds_restaurant_image = (ImageView) v.findViewById(R.id.local_feeds_restaurant_image);
            feeds_post_image = (ImageView) v.findViewById(R.id.feeds_post_image);
            profileImage = (de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.local_feeds_profile_image);

            local_feeds_restaurant_relative_layout=(RelativeLayout) v.findViewById(R.id.local_feeds_restaurant_relative_layout);

            layout_like=(LinearLayout)v.findViewById(R.id.layout_like);
            layout_comment=(LinearLayout)v.findViewById(R.id.layout_comment);
            user_profile_layout=(RelativeLayout)v.findViewById(R.id.user_profile_layout);
            comment_TextView=(TextView) v.findViewById(R.id.comment_TextView);

            image_spinner=(ProgressBar) v.findViewById(R.id.image_spinner);


            comments_row=(LinearLayout)v.findViewById(R.id.comments_);
            comments_add_row=(LinearLayout)v.findViewById(R.id.comments_add);

        }
    }

    public HomeLocalFeedsTestAdapter(List<Object> localFeeds, Context context) {
        this.objects=localFeeds;

        Log.e("Objects",""+objects.size());

        for(int o=0;o<objects.size();o++) {
            if (objects.get(o).getClass().equals(io.realm.LocalFeedReviewRealmProxy.class)) {
                LocalFeedReview localFeedReview = (LocalFeedReview) objects.get(o);
                localFeedReviewRealmList.add(localFeedReview);
            } else if (objects.get(o).getClass().equals(io.realm.LocalFeedCheckInRealmProxy.class)) {
                LocalFeedCheckIn localFeedCheckIn= (LocalFeedCheckIn) objects.get(o);
                localFeedCheckInRealmList.add(localFeedCheckIn);
            }
        }

        mContext=context;
//        objects=Merge(localFeedReviewRealmList,localFeedCheckInRealmList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_local_feeds_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {

//              Log.e("LocalFeed",objects.get(position).getClass().getName());
        if (objects.get(position).getClass().equals(io.realm.LocalFeedReviewRealmProxy.class)){
            final LocalFeedReview localFeedReview= (LocalFeedReview) objects.get(position);

            holder.restaurantName.setText(localFeedReview.getReviewOnName());
            holder.restaurantAddress.setText(localFeedReview.getReviewOnLocation());

            holder.local_feeds_user_review_time.setText(localFeedReview.getReviewerLocation());
            holder.local_feeds_user_name.setText(localFeedReview.getReviewerName());

            holder.review_likes_count_tv.setText(""+localFeedReview.getReviewLikesCount());
            holder.review_comments_count_tv.setText(""+localFeedReview.getReviewCommentCount());
            holder.review_share_count_tv.setText(""+localFeedReview.getReviewSharesCount());

            holder.status.setText(localFeedReview.getSummary());

//                    if(!localFeedReview.getReviewImage().equals("")){
//
//                        holder.feeds_post_image.setVisibility(View.VISIBLE);
//                        Constants.PicassoImageBackground(localFeedReview.getReviewImage(),holder.feeds_post_image,mContext);
//                    }

            Constants.PicassoImageBackground(localFeedReview.getReviewImage(),holder.local_feeds_restaurant_image,mContext);
            Constants.PicassoImageSrc(localFeedReview.getReviewerAvatar(),holder.profileImage,mContext);

            holder.local_feeds_restaurant_relative_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, RestaurantDetailActivity.class);
                    intent.putExtra("RestaurantID", localFeedReview.getReviewOnID());
                    mContext.startActivity(intent);
                }
            });

            holder.layout_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!Constants.skipLogin) {
                        if(Constants.isNetworkAvailable((Activity) mContext)) {
                            int prev = Integer.valueOf(holder.review_likes_count_tv.getText().toString());
                            prev++;
                            holder.review_likes_count_tv.setText("" + prev);
                            holder.layout_like.setEnabled(false);
                            Like(localFeedReview.getReviewID(), "review",holder.review_likes_count_tv,holder.layout_like);
                        }
                    }
                }
            });

//                    IsRestaurantFollowedData(localFeedReview.getReviewOnID(),holder.image_bookmark);

            if(localFeedReview.getBookmarked()){
                holder.image_bookmark.setTag(1);
                holder.image_bookmark.setBackground(mContext.getResources().getDrawable(R.drawable.icon_bookmarked));
            }else {
                holder.image_bookmark.setTag(0);
                holder.image_bookmark.setBackground(mContext.getResources().getDrawable(R.drawable.icon_bookmark));
            }

            holder.image_bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if((int)holder.image_bookmark.getTag()==0) {
                        holder.image_bookmark.setTag(1);
                        Realm realm=Realm.getDefaultInstance();
                        realm.beginTransaction();
                        localFeedReview.setBookmarked(true);
                        realm.commitTransaction();
                        holder.image_bookmark.setBackground(mContext.getResources().getDrawable(R.drawable.icon_bookmarked));
                        if (Constants.isNetworkAvailable((Activity) mContext)) {
                            if (!Constants.skipLogin) {
                                RestaurantBookmarked(localFeedReview.getReviewOnID(), "restaurant");
                            }
                        }
                    }else {
                        holder.image_bookmark.setTag(0);
                        Realm realm=Realm.getDefaultInstance();
                        realm.beginTransaction();
                        localFeedReview.setBookmarked(false);
                        realm.commitTransaction();
                        holder.image_bookmark.setBackground(mContext.getResources().getDrawable(R.drawable.icon_bookmark));
                        if (Constants.isNetworkAvailable((Activity) mContext)) {
                            if (!Constants.skipLogin) {
                                RestaurantBookmarked(localFeedReview.getReviewOnID(), "restaurant");
                            }
                        }
                    }
                    notifyDataSetChanged();
                }
            });


            holder.review_comments_count_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent=new Intent(mContext, PostDetailActivity.class);
                    intent.putExtra("Type","Review");
                    intent.putExtra("MyClass", localFeedReview);
                    mContext.startActivity(intent);
                }
            });

            holder.comment_TextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent=new Intent(mContext, PostDetailActivity.class);
                    intent.putExtra("Type","Review");
                    intent.putExtra("MyClass", localFeedReview);
                    mContext.startActivity(intent);
                }
            });


            holder.layout_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, PostDetailActivity.class);
                    intent.putExtra("Type","Review");
                    intent.putExtra("MyClass", localFeedReview);
                    mContext.startActivity(intent);
                }
            });

            holder.user_profile_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, ProfilesDetailActivity.class);
                    intent.putExtra("UserID", localFeedReview.getReviewerID());
                    mContext.startActivity(intent);
                }
            });

            commentRealmList=localFeedReview.getCommentRealmList();

            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int i=0;i<commentRealmList.size();i++) {

                View child = inflater.inflate(R.layout.comment_row, null);
                TextView commentatorName=(TextView)child.findViewById(R.id.commentator_name);
                TextView commentatorComment=(TextView)child.findViewById(R.id.commentator_comment);
                TextView commentTime=(TextView)child.findViewById(R.id.comment_time);
                TextView commentLikesCount=(TextView)child.findViewById(R.id.comment_likes_count);

                commentatorName.setText(commentRealmList.get(i).getCommentatorName());
                commentatorComment.setText(commentRealmList.get(i).getCommentSummary());
                Date date= GetDate(commentRealmList.get(i).getCommentUpdated_at());

                SimpleDateFormat localDateFormatForTime = new SimpleDateFormat("h:mm a");
                String time = localDateFormatForTime.format(date);
                SimpleDateFormat localDateFormatForDay = new SimpleDateFormat("EEE");
                String day = localDateFormatForDay.format(date);
                SimpleDateFormat localDateFormatForDate = new SimpleDateFormat("MMM d");
                String dates = localDateFormatForDate.format(date);

                commentTime.setText(dates+" "+time);
                commentLikesCount.setText("  "+commentRealmList.get(i).getCommentLikesCount());
                holder.comments_row.addView(child);

            }
        }
        else if(objects.get(position).getClass().equals(io.realm.LocalFeedCheckInRealmProxy.class))
        {
            final LocalFeedCheckIn localFeedCheckIn= (LocalFeedCheckIn) objects.get(position);
            holder.restaurantName.setText(localFeedCheckIn.getCheckInOnName());
            holder.restaurantAddress.setText(localFeedCheckIn.getCheckInOnLocation());

            holder.local_feeds_user_review_time.setText(localFeedCheckIn.getCheckInWriterLocation());
            holder.local_feeds_user_name.setText(localFeedCheckIn.getCheckInWriterName());

            holder.review_likes_count_tv.setText(""+localFeedCheckIn.getReviewLikesCount());
            holder.review_comments_count_tv.setText(""+localFeedCheckIn.getReviewCommentCount());
            holder.review_share_count_tv.setText(""+localFeedCheckIn.getReviewSharesCount());

            holder.status.setText(localFeedCheckIn.getCheckInStatus());


            if(localFeedCheckIn.getCheckInImage()!=null) {
                if (!localFeedCheckIn.getCheckInImage().equals("")) {
                    holder.image_spinner.setVisibility(View.VISIBLE);
                    Constants.PicassoLargeImageBackgroundNewsFeed(localFeedCheckIn.getCheckInImage(),holder.feeds_post_image,holder.image_spinner,mContext);
                }
            }


            holder.feeds_post_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    holder.feeds_post_image.setDrawingCacheEnabled(true);
                    Bitmap b=holder.feeds_post_image.getDrawingCache();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    b.compress(Bitmap.CompressFormat.PNG,75, stream);
                    byte[] bytes = stream.toByteArray();
                    Intent i = new Intent(mContext, PhotoDetailActivity.class);
                    i.putExtra("Type","CheckIn");
                    i.putExtra("MyClass", localFeedCheckIn);
                    //  i.putExtra("Bitmap", bytes);
                    mContext.startActivity(i);
                }

            });

            Constants.PicassoImageBackground(localFeedCheckIn.getCheckInOnImage(),holder.local_feeds_restaurant_image,mContext);
            Constants.PicassoImageSrc(localFeedCheckIn.getCheckInWriterAvatar(),holder.profileImage,mContext);

            holder.local_feeds_restaurant_relative_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, RestaurantDetailActivity.class);
                    intent.putExtra("RestaurantID", localFeedCheckIn.getCheckInOnID());
                    mContext.startActivity(intent);
                }
            });

            holder.layout_like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!Constants.skipLogin) {
                        if(Constants.isNetworkAvailable((Activity) mContext)) {
                            int prev = Integer.valueOf(holder.review_likes_count_tv.getText().toString());
                            prev++;
                            holder.review_likes_count_tv.setText("" + prev);
                            holder.layout_like.setEnabled(false);
                            Like(localFeedCheckIn.getCheckInID(), "post",holder.review_likes_count_tv,holder.layout_like);
                        }
                    }
                }
            });

//                    IsRestaurantFollowedData(localFeedCheckIn.getCheckInOnID(),holder.image_bookmark);
            if(localFeedCheckIn.getBookmarked()){
                holder.image_bookmark.setTag(1);
                holder.image_bookmark.setBackground(mContext.getResources().getDrawable(R.drawable.icon_bookmarked));
            }else {
                holder.image_bookmark.setTag(0);
                holder.image_bookmark.setBackground(mContext.getResources().getDrawable(R.drawable.icon_bookmark));
            }

            holder.image_bookmark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if((int)holder.image_bookmark.getTag()==0) {
                        holder.image_bookmark.setTag(1);
                        holder.image_bookmark.setBackground(mContext.getResources().getDrawable(R.drawable.icon_bookmarked));
                        Realm realm=Realm.getDefaultInstance();
                        realm.beginTransaction();
                        localFeedCheckIn.setBookmarked(true);
                        realm.commitTransaction();
                        if (!Constants.skipLogin) {
                            if (Constants.isNetworkAvailable((Activity) mContext)) {
                                RestaurantBookmarked(localFeedCheckIn.getCheckInOnID(), "restaurant");
                            }
                        }
                    }
                    else
                    {
                        holder.image_bookmark.setTag(0);
                        holder.image_bookmark.setBackground(mContext.getResources().getDrawable(R.drawable.icon_bookmark));
                        Realm realm=Realm.getDefaultInstance();
                        realm.beginTransaction();
                        localFeedCheckIn.setBookmarked(false);
                        realm.commitTransaction();

                        if (!Constants.skipLogin) {
                            if (Constants.isNetworkAvailable((Activity) mContext)) {
                                RestaurantBookmarked(localFeedCheckIn.getCheckInOnID(), "restaurant");
                            }
                        }
                    }
                    notifyDataSetChanged();
                }
            });


            holder.user_profile_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, ProfilesDetailActivity.class);
                    intent.putExtra("UserID", localFeedCheckIn.getCheckInWriterID());
                    mContext.startActivity(intent);
                }
            });


            final LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            holder.review_comments_count_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, PostDetailActivity.class);
                    intent.putExtra("Type","CheckIn");
                    intent.putExtra("MyClass", localFeedCheckIn);
                    mContext.startActivity(intent);
                }
            });

            holder.comment_TextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, PostDetailActivity.class);
                    intent.putExtra("Type","CheckIn");
                    intent.putExtra("MyClass", localFeedCheckIn);
                    mContext.startActivity(intent);
                }
            });
            holder.layout_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(mContext, PostDetailActivity.class);
                    intent.putExtra("Type","CheckIn");
                    intent.putExtra("MyClass", localFeedCheckIn);
                    mContext.startActivity(intent);
                }
            });

            commentRealmList=localFeedCheckIn.getCommentRealmList();


            for (int i=0;i<commentRealmList.size();i++) {

                View child = inflater.inflate(R.layout.comment_row, null);
                TextView commentatorName=(TextView)child.findViewById(R.id.commentator_name);
                TextView commentatorComment=(TextView)child.findViewById(R.id.commentator_comment);
                TextView commentTime=(TextView)child.findViewById(R.id.comment_time);
                TextView commentLikesCount=(TextView)child.findViewById(R.id.comment_likes_count);

                commentatorName.setText(commentRealmList.get(i).getCommentatorName());
                commentatorComment.setText(commentRealmList.get(i).getCommentSummary());
                Date date= GetDate(commentRealmList.get(i).getCommentUpdated_at());

                SimpleDateFormat localDateFormatForTime = new SimpleDateFormat("h:mm a");
                String time = localDateFormatForTime.format(date);
                SimpleDateFormat localDateFormatForDay = new SimpleDateFormat("EEE");
                String day = localDateFormatForDay.format(date);
                SimpleDateFormat localDateFormatForDate = new SimpleDateFormat("MMM d");
                String dates = localDateFormatForDate.format(date);


                commentTime.setText(dates+" "+time);
                commentLikesCount.setText("  "+commentRealmList.get(i).getCommentLikesCount());
                holder.comments_row.addView(child);
            }
        }
        else
        {
            Log.e("LocalFeed","Why so?");
        }

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }







    Date GetDate(String date){

//        String segments[] = date.split("\\+");
//        String d = segments[0];
//        String d2 = segments[1];
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
//        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
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

    public Bitmap scaleDownBitmap(Bitmap photo, int newHeight, Context context) {

        final float densityMultiplier = context.getResources().getDisplayMetrics().density;

        int h= (int) (newHeight*densityMultiplier);
        int w= (int) (h * photo.getWidth()/((double) photo.getHeight()));

        photo=Bitmap.createScaledBitmap(photo, w, h, true);

        return photo;
    }

    public void Like(int id, String type, final TextView tv, final LinearLayout layout_like){

        // Get a Realm instance for this thread
        Realm realm=Realm.getDefaultInstance();
        // Persist your data in a transaction
        realm.beginTransaction();
        final User user = realm.where(User.class).findFirst();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Like_+type+"/"+id)
                .addHeader("Authorization", "Token token="+user.getToken())
                .build();

        realm.close();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr=response.body().string();


                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObj = new JSONObject(objStr);

                            if(jsonObj.has("message")){

                                String message= jsonObj.getString("message");
                                if(!message.equals("Successfully liked!")) {
                                    int prev = Integer.valueOf(tv.getText().toString());
                                    prev--;
                                    tv.setText("" + prev);
                                }
                            }
                            layout_like.setEnabled(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        realm.commitTransaction();
        //            return UnLike(id,type);
    }

    public void RestaurantBookmarked(final int id, final String type){

        // Get a Realm instance for this thread
        Realm realm=Realm.getDefaultInstance();
        // Persist your data in a transaction
        realm.beginTransaction();
        final User user = realm.where(User.class).findFirst();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Like_+type+"/"+id)
                .addHeader("Authorization", "Token token="+user.getToken())
                .build();

        realm.close();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr=response.body().string();


                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject jsonObj = new JSONObject(objStr);

                            if(jsonObj.has("message")){

                                String message= jsonObj.getString("message");
                                if(!message.equals("Successfully liked!")) {
                                    //  tv.setBackground(mContext.getResources().getDrawable(R.drawable.icon_bookmark));
                                    GenericRoutes.UnLike(id,type);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        realm.commitTransaction();
        //            return UnLike(id,type);
    }

    void IsRestaurantFollowedData(int rid,final ImageView bookmark){
        // Get a Realm instance for this thread
        Realm realm = Realm.getDefaultInstance();
        // Persist your data in a transaction
        User user = realm.where(User.class).findFirst();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.IsRestaurantFollowed+rid)
                .addHeader("Authorization", "Token token="+user.getToken())
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr=response.body().string();
                Log.e("Follows",""+objStr);
                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject jsonObj = new JSONObject(objStr);

                            if(jsonObj.has("follows")) {
                                boolean f = jsonObj.getBoolean("follows");
                                boolean b = jsonObj.getBoolean("likes");

                                if (b)
                                {
                                    bookmark.setTag(1);
                                    bookmark.setBackground(mContext.getResources().getDrawable(R.drawable.icon_bookmarked));
                                }
                                else
                                {
                                    bookmark.setTag(0);
                                    bookmark.setBackground(mContext.getResources().getDrawable(R.drawable.icon_bookmark));
                                }

                                if (f) {
//                                    follow_button_text.setText("Unfollow");
                                } else {
//                                    follow_button_text.setText("  Follow");
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
