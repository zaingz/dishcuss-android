package com.holygon.dishcuss.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.holygon.dishcuss.Model.LocalFeedReview;
import com.holygon.dishcuss.Model.ReviewModel;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;
import com.holygon.dishcuss.Utils.GenericRoutes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import io.realm.RealmList;

/**
 * Created by Naeem Ibrahim on 7/23/2016.
 */
public class AccountReviewsAdapter extends RecyclerView.Adapter<AccountReviewsAdapter.ViewHolder> {

    RealmList<ReviewModel> mReviewModels;

    ArrayList<ReviewModel> reviewModelArrayList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView reviewTitle,reviewTime,reviewSummary,reviewLikesCount,reviewCommentsCount,reviewSharesCount;
        LinearLayout layout_like;
        public ViewHolder(View v) {
            super(v);
            reviewTitle = (TextView) v.findViewById(R.id.row_reviews_user_name);
            reviewTime = (TextView) v.findViewById(R.id.row_reviews_user_review_time);
            reviewSummary = (TextView) v.findViewById(R.id.row_review_post_summary);
            reviewLikesCount = (TextView) v.findViewById(R.id.row_review_like_count);
            reviewCommentsCount = (TextView) v.findViewById(R.id.row_review_comments_count);
            reviewSharesCount = (TextView) v.findViewById(R.id.row_review_shares_count);
            layout_like=(LinearLayout)v.findViewById(R.id.layout_like);
        }
    }

    public AccountReviewsAdapter(RealmList<ReviewModel> reviewModels) {
        mReviewModels=reviewModels;

        reviewModelArrayList.addAll(mReviewModels);

        Collections.sort(reviewModelArrayList, new Comparator<ReviewModel>() {
            @Override
            public int compare(ReviewModel lhs, ReviewModel rhs) {
                return GetDate(rhs.getUpdated_at()).compareTo(GetDate(lhs.getUpdated_at()));
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_reviews_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.reviewTitle.setText(reviewModelArrayList.get(position).getReview_reviewer_Name());
        holder.reviewTime.setText(reviewModelArrayList.get(position).getReview_reviewer_time());
        holder.reviewSummary.setText(reviewModelArrayList.get(position).getReview_summary());
        holder.reviewLikesCount.setText(reviewModelArrayList.get(position).getReview_Likes_count()+"");
        holder.reviewCommentsCount.setText(reviewModelArrayList.get(position).getReview_comments_count()+"");
        holder.reviewSharesCount.setText(reviewModelArrayList.get(position).getReview_shares_count()+"");

        holder.layout_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GenericRoutes.Like(reviewModelArrayList.get(position).getReview_ID(),"review")){
                    int prev=Integer.valueOf(holder.reviewLikesCount.getText().toString());
                    holder.reviewLikesCount.setText(""+prev+1);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewModelArrayList.size();
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
        //  System.out.println(convertedDate);
        return convertedDate;
    }
}
