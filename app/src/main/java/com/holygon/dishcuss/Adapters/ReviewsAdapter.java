package com.holygon.dishcuss.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.holygon.dishcuss.Activities.NotificationClickPostDetail;
import com.holygon.dishcuss.Activities.PostDetailActivity;
import com.holygon.dishcuss.Activities.ProfilesDetailActivity;
import com.holygon.dishcuss.Model.Comment;
import com.holygon.dishcuss.Model.ReviewModel;
import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;
import com.holygon.dishcuss.Utils.GenericRoutes;
import com.holygon.dishcuss.Utils.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 7/23/2016.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    RealmList<ReviewModel> mReviewModels;
    RealmList<Comment> commentRealmList;
    Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView reviewTitle,reviewTime,reviewSummary,reviewLikesCount,reviewCommentsCount,reviewSharesCount,review_comments_count_tv;
        LinearLayout layout_like,layout_comment,layout_share;
        LinearLayout comment_row;
        RelativeLayout user_profile_layout;
        public de.hdodenhof.circleimageview.CircleImageView profileImageView;
        public ViewHolder(View v) {
            super(v);
            reviewTitle = (TextView) v.findViewById(R.id.row_reviews_user_name);
            reviewTime = (TextView) v.findViewById(R.id.row_reviews_user_review_time);
            reviewSummary = (TextView) v.findViewById(R.id.row_review_post_summary);
            reviewLikesCount = (TextView) v.findViewById(R.id.row_review_like_count);
            review_comments_count_tv = (TextView) v.findViewById(R.id.review_comments_count_tv);
            reviewCommentsCount = (TextView) v.findViewById(R.id.row_review_comments_count);
            reviewSharesCount = (TextView) v.findViewById(R.id.row_review_shares_count);
            layout_like=(LinearLayout)v.findViewById(R.id.layout_like);
            layout_share=(LinearLayout)v.findViewById(R.id.layout_share);
            layout_comment=(LinearLayout)v.findViewById(R.id.layout_comment);
            user_profile_layout=(RelativeLayout)v.findViewById(R.id.user_profile_layout);
            profileImageView=(de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.account_reviews_profile_image);
            comment_row=(LinearLayout)v.findViewById(R.id.review_comments);
        }
    }

    public ReviewsAdapter(RealmList<ReviewModel> reviewModels, Context mContext) {
        mReviewModels=reviewModels;
        this.mContext=mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_reviews_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.reviewTitle.setText(mReviewModels.get(position).getReview_reviewer_Name());
        holder.reviewTime.setText(mReviewModels.get(position).getReview_reviewer_time());
        holder.reviewSummary.setText(mReviewModels.get(position).getReview_summary());
        holder.reviewLikesCount.setText(mReviewModels.get(position).getReview_Likes_count()+"");
        holder.reviewCommentsCount.setText(mReviewModels.get(position).getReview_comments_count()+"");
        holder.reviewSharesCount.setText(mReviewModels.get(position).getReview_shares_count()+"");

        if(mReviewModels.get(position).getReview_reviewer_Avatar()!=null) {
            if (!mReviewModels.get(position).getReview_reviewer_Avatar().equals("")) {
                Constants.PicassoImageSrc(mReviewModels.get(position).getReview_reviewer_Avatar(), holder.profileImageView, mContext);
            }
        }
        holder.layout_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Constants.skipLogin) {
                    if(Constants.isNetworkAvailable((Activity) mContext)) {
                        int prev = Integer.valueOf(holder.reviewLikesCount.getText().toString());
                        prev++;
                        holder.reviewLikesCount.setText("" + prev);
                        holder.layout_like.setEnabled(false);
                        Like(mReviewModels.get(position).getReview_ID(), "review",holder.reviewLikesCount,holder.layout_like);
                    }
                }
            }
        });


        holder.layout_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReviewShare(mReviewModels.get(position).getReview_summary(),mReviewModels.get(position).getReview_On_ID());
            }
        });

       final ReviewModel reviewModel=  mReviewModels.get(position);

        holder.layout_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(mContext, PostDetailActivity.class);
//                intent.putExtra("Type","Review2");
//                intent.putExtra("MyClass",reviewModel );
//                mContext.startActivity(intent);
                Intent intent = new Intent(mContext, NotificationClickPostDetail.class);
                intent.putExtra("TypeID",reviewModel.getReview_ID());
                intent.putExtra("Type","Review");
                mContext.startActivity(intent);
            }
        });

        holder.reviewCommentsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(mContext, PostDetailActivity.class);
//                intent.putExtra("Type","Review2");
//                intent.putExtra("MyClass",reviewModel );
//                mContext.startActivity(intent);
                Intent intent = new Intent(mContext, NotificationClickPostDetail.class);
                intent.putExtra("TypeID",reviewModel.getReview_ID());
                intent.putExtra("Type","Review");
                mContext.startActivity(intent);
            }
        });


        holder.review_comments_count_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(mContext, PostDetailActivity.class);
//                intent.putExtra("Type","Review2");
//                intent.putExtra("MyClass",reviewModel );
//                mContext.startActivity(intent);
                Intent intent = new Intent(mContext, NotificationClickPostDetail.class);
                intent.putExtra("TypeID",reviewModel.getReview_ID());
                intent.putExtra("Type","Review");
                mContext.startActivity(intent);
            }
        });

        holder.user_profile_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, ProfilesDetailActivity.class);
                intent.putExtra("UserID", mReviewModels.get(position).getReview_reviewer_ID());
                mContext.startActivity(intent);
            }
        });

        commentRealmList=mReviewModels.get(position).getCommentRealmList();

        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int i=0;i<commentRealmList.size();i++) {

            View child = inflater.inflate(R.layout.comment_row, null);
            TextView commentatorName=(TextView)child.findViewById(R.id.commentator_name);
            TextView commentatorComment=(TextView)child.findViewById(R.id.commentator_comment);
            TextView commentTime=(TextView)child.findViewById(R.id.comment_time);
            TextView commentLikesCount=(TextView)child.findViewById(R.id.comment_likes_count);

            commentatorName.setText(commentRealmList.get(i).getCommentatorName());
            commentatorComment.setText(commentRealmList.get(i).getCommentSummary());
            Date date= Constants.GetDate(commentRealmList.get(i).getCommentUpdated_at());

            SimpleDateFormat localDateFormatForTime = new SimpleDateFormat("h:mm a");
            String time = localDateFormatForTime.format(date);
            SimpleDateFormat localDateFormatForDay = new SimpleDateFormat("EEE");
            String day = localDateFormatForDay.format(date);
            SimpleDateFormat localDateFormatForDate = new SimpleDateFormat("MMM d");
            String dates = localDateFormatForDate.format(date);


            commentTime.setText(dates+" "+time);
            commentLikesCount.setText("  "+commentRealmList.get(i).getCommentLikesCount());
            holder.comment_row.addView(child);
        }

        View child = inflater.inflate(R.layout.add_comment_row, null);

        final EditText message=(EditText) child.findViewById(R.id.message);
        Button sendButton=(Button)child.findViewById(R.id.btn_send);
        holder.comment_row.addView(child);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment=message.getText().toString();
                if(!comment.equals("")){
                    if(GenericRoutes.ReviewComment(""+mReviewModels.get(position).getReview_ID(),"",comment,"", URLs.Add_Comment_Review)){
                        Log.e("Post","Success");
                        message.setText("");
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mReviewModels.size();
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


    //*******************PROGRESS******************************
    private ProgressDialog mSpinner;

    private void showSpinner(String title) {
        mSpinner = new ProgressDialog(mContext);
        mSpinner.setTitle(title);
        mSpinner.show();
        mSpinner.setCancelable(false);
        mSpinner.setCanceledOnTouchOutside(false);
    }

    private void DismissSpinner(){
        if(mSpinner!=null){
            mSpinner.dismiss();
        }
    }
//*******************PROGRESS******************************

    void ReviewShare(String statusStr, int restaurantID){
        showSpinner("Please Wait...");
        // Get a Realm instance for this thread
        Realm realm = Realm.getDefaultInstance();
        // Persist your data in a transaction

        User user = realm.where(User.class).findFirst();

        RequestBody requestBody;

        requestBody= new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("review[title]","Write Review")
                .addFormDataPart("review[summary]",statusStr)
                .addFormDataPart("review[rating]", "")
                .addFormDataPart("review[reviewable_id]",""+restaurantID)
                .build();

        Request request = new Request.Builder()
                .url(URLs.Restaurant_Review)
                .addHeader("Authorization", "Token token="+user.getToken())
                .post(requestBody)
                .build();

        OkHttpClient client;
        client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try{
                    String obj=response.body().string();
                    Log.e("Res",""+obj);
                    JSONObject jsonObject=new JSONObject(obj);
                    if(jsonObject.has("review")){
                        Log.e("","Post Successfully");
                    }
                    else  if(jsonObject.has("message")){
                        Log.e("","Not Posted");
                    }
                    DismissSpinner();
                }catch (Exception e){
                    Log.i("Exception ::",""+ e.getMessage());
                }
                finally
                {
                    DismissSpinner();
                }

            }
        });
    }
}
