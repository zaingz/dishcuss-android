package com.holygon.dishcuss.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.holygon.dishcuss.Model.Comment;
import com.holygon.dishcuss.Model.ReviewModel;
import com.holygon.dishcuss.Model.UserFollowing;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;

import java.util.ArrayList;

import io.realm.RealmList;

/**
 * Created by Naeem Ibrahim on 7/24/2016.
 */
public class AccountFollowerAdapter extends RecyclerView.Adapter<AccountFollowerAdapter.ViewHolder> {

    RealmList<UserFollowing> userFollowings;

    Context mContext;
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView user_profile_follower_user_name,user_profile_follower_user_followers_count;
        public de.hdodenhof.circleimageview.CircleImageView profileImageView;
        public ViewHolder(View v) {
            super(v);
            user_profile_follower_user_name = (TextView)v.findViewById(R.id.user_profile_follower_user_name);
            user_profile_follower_user_followers_count = (TextView)v.findViewById(R.id.user_profile_follower_user_followers_count);
            profileImageView=(de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.user_profile_follower_profile_image);
        }
    }

    public AccountFollowerAdapter(RealmList<UserFollowing> userFollowings,Context mContext) {
        this.userFollowings=userFollowings;
        this.mContext=mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.account_followers_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.user_profile_follower_user_name.setText(userFollowings.get(position).getName());
        holder.user_profile_follower_user_followers_count.setText(""+userFollowings.get(position).getFollowerCount());
        if(userFollowings.get(position).getAvatar()!=null) {
            if (!userFollowings.get(position).getAvatar().equals("")) {
                Constants.PicassoImageSrc(userFollowings.get(position).getAvatar(), holder.profileImageView, mContext);
            }
        }

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return userFollowings.size();
    }
}
