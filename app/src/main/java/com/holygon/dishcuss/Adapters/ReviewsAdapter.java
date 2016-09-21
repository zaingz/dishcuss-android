package com.holygon.dishcuss.Adapters;

import android.app.Activity;
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
import android.widget.TextView;

import com.holygon.dishcuss.Activities.PostDetailActivity;
import com.holygon.dishcuss.Model.Comment;
import com.holygon.dishcuss.Model.ReviewModel;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;
import com.holygon.dishcuss.Utils.GenericRoutes;
import com.holygon.dishcuss.Utils.URLs;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.RealmList;

/**
 * Created by Naeem Ibrahim on 7/23/2016.
 */
public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

    RealmList<ReviewModel> mReviewModels;
    RealmList<Comment> commentRealmList;
    Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView reviewTitle,reviewTime,reviewSummary,reviewLikesCount,reviewCommentsCount,reviewSharesCount,review_comments_count_tv;
        LinearLayout layout_like,layout_comment;
        LinearLayout comment_row;
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
            layout_comment=(LinearLayout)v.findViewById(R.id.layout_comment);
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
                if (!Constants.skipLogin) {
                    if(Constants.isNetworkAvailable((Activity) mContext)) {
                        int prev = Integer.valueOf(holder.reviewLikesCount.getText().toString());
                        prev=prev + 1;
                        holder.reviewLikesCount.setText("" + prev);
                        if (!GenericRoutes.Like(mReviewModels.get(position).getReview_ID(), "review", (Activity) mContext)) {
                            prev=prev - 1;
                            holder.reviewLikesCount.setText("" + prev);
                        }
                    }
                }
            }
        });


       final ReviewModel reviewModel=  mReviewModels.get(position);

        holder.layout_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, PostDetailActivity.class);
                intent.putExtra("Type","Review2");
                intent.putExtra("MyClass",reviewModel );
                mContext.startActivity(intent);
            }
        });

        holder.reviewCommentsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, PostDetailActivity.class);
                intent.putExtra("Type","Review2");
                intent.putExtra("MyClass",reviewModel );
                mContext.startActivity(intent);
            }
        });


        holder.review_comments_count_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, PostDetailActivity.class);
                intent.putExtra("Type","Review2");
                intent.putExtra("MyClass",reviewModel );
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
}
