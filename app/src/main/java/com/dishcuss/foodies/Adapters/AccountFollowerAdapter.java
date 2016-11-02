package com.dishcuss.foodies.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dishcuss.foodies.Activities.ProfilesDetailActivity;
import com.dishcuss.foodies.Model.UserFollowing;
import com.dishcuss.foodies.R;
import com.dishcuss.foodies.Utils.Constants;

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
        RelativeLayout user_profile_layout;
        public ViewHolder(View v) {
            super(v);
            user_profile_layout=(RelativeLayout)v.findViewById(R.id.user_profile_layout);
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
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.user_profile_follower_user_name.setText(userFollowings.get(position).getName());
        holder.user_profile_follower_user_followers_count.setText(""+userFollowings.get(position).getFollowerCount());
        if(userFollowings.get(position).getAvatar()!=null) {
            if (!userFollowings.get(position).getAvatar().equals("")) {
                Constants.PicassoImageSrc(userFollowings.get(position).getAvatar(), holder.profileImageView, mContext);
            }
        }


        holder.user_profile_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, ProfilesDetailActivity.class);
                intent.putExtra("UserID", userFollowings.get(position).getId());
                mContext.startActivity(intent);
            }
        });

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return userFollowings.size();
    }
}
