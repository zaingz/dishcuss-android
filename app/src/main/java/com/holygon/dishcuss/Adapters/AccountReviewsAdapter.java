package com.holygon.dishcuss.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.holygon.dishcuss.Model.ReviewModel;
import com.holygon.dishcuss.R;

import io.realm.RealmList;

/**
 * Created by Naeem Ibrahim on 7/23/2016.
 */
public class AccountReviewsAdapter extends RecyclerView.Adapter<AccountReviewsAdapter.ViewHolder> {

    RealmList<ReviewModel> mReviewModels;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView reviewTitle,reviewTime,reviewSummary,reviewLikesCount,reviewCommentsCount,reviewSharesCount;
        public ViewHolder(View v) {
            super(v);
            reviewTitle = (TextView) v.findViewById(R.id.row_reviews_user_name);
            reviewTime = (TextView) v.findViewById(R.id.row_reviews_user_review_time);
            reviewSummary = (TextView) v.findViewById(R.id.row_review_post_summary);
            reviewLikesCount = (TextView) v.findViewById(R.id.row_review_like_count);
            reviewCommentsCount = (TextView) v.findViewById(R.id.row_review_comments_count);
            reviewSharesCount = (TextView) v.findViewById(R.id.row_review_shares_count);
        }
    }

    public AccountReviewsAdapter(RealmList<ReviewModel> reviewModels) {
        mReviewModels=reviewModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_reviews_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.reviewTitle.setText(mReviewModels.get(position).getReview_reviewer_Name());
        holder.reviewTime.setText(mReviewModels.get(position).getReview_reviewer_time());
        holder.reviewSummary.setText(mReviewModels.get(position).getReview_summary());
        holder.reviewLikesCount.setText(mReviewModels.get(position).getReview_Likes_count()+"");
        holder.reviewCommentsCount.setText(mReviewModels.get(position).getReview_comments_count()+"");
        holder.reviewSharesCount.setText(mReviewModels.get(position).getReview_shares_count()+"");
    }

    @Override
    public int getItemCount() {
        return mReviewModels.size();
    }
}
