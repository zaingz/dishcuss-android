package com.dishcuss.foodie.hub.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dishcuss.foodie.hub.Activities.ProfilesDetailActivity;
import com.dishcuss.foodie.hub.Activities.RestaurantDetailActivity;
import com.dishcuss.foodie.hub.Models.RecentSearchModel;
import com.dishcuss.foodie.hub.Models.Restaurant;
import com.dishcuss.foodie.hub.Models.SearchModel;
import com.dishcuss.foodie.hub.Models.UserProfile;
import com.dishcuss.foodie.hub.R;
import com.dishcuss.foodie.hub.Utils.Constants;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Naeem Ibrahim on 9/7/2016.
 */
public class CurrentSearchAdapter  extends RecyclerView.Adapter<CurrentSearchAdapter.ViewHolder> {

    private ArrayList<Restaurant> restaurantRealmList;
    private ArrayList<UserProfile> userProfileArrayList;
    SearchModel searchModel;
    Context context;
    List<Object> objects=new ArrayList<>();


    public static class ViewHolder extends RecyclerView.ViewHolder {

        //Restaurant
        public TextView restaurantName,restaurantAddress;
        public de.hdodenhof.circleimageview.CircleImageView coverImage;
        public RelativeLayout select_restaurant_name_parent;
        public Button rattingButton;

        //User
        public TextView userName,userFollowersCount;
        public de.hdodenhof.circleimageview.CircleImageView profileImageView;
        public CardView search_user_card_view;


        public ViewHolder(View v, int viewType) {
            super(v);

            if(viewType==0) {
                restaurantName = (TextView) v.findViewById(R.id.search_bar_restaurant_name);
                restaurantAddress = (TextView) v.findViewById(R.id.search_bar_restaurant_address);
                coverImage = (de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.search_bar_restaurant_profile_image);
                rattingButton = (Button) v.findViewById(R.id.search_bar_restaurant_rating);
                select_restaurant_name_parent = (RelativeLayout) v.findViewById(R.id.search_bar_restaurant_name_parent);
            }
            else
            {
                userName = (TextView) v.findViewById(R.id.search_bar_user_name);
                userFollowersCount = (TextView) v.findViewById(R.id.search_bar_user_followers_count);
                profileImageView=(de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.search_bar_user_profile_image);
                search_user_card_view=(CardView) v.findViewById(R.id.search_user_card_view);
            }
        }
    }

    public CurrentSearchAdapter(SearchModel searchModel,Context context) {

        this.searchModel=searchModel;
        this.context=context;
        restaurantRealmList=searchModel.getRestaurantArrayList();
        userProfileArrayList=searchModel.getUserProfileArrayList();
        objects.addAll(restaurantRealmList);
        objects.addAll(userProfileArrayList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        // view type is to identify where to render the menu_chat message
        // left or right
        if (viewType == 0) {
            // self message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.search_restaurant_result_row, parent, false);
        }
        else
        {
            // others message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.search_user_result_row, parent, false);
        }

        ViewHolder vh = new ViewHolder(itemView,viewType);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


        if (holder.getItemViewType()==0) {
            if(objects.get(position).getClass().equals(io.realm.RestaurantRealmProxy.class)) {
                final Restaurant restaurant=(Restaurant)objects.get(position);
                holder.restaurantName.setText(restaurant.getName());
                holder.restaurantAddress.setText(restaurant.getLocation());
                holder.rattingButton.setText("" + restaurant.getRatting());
                Constants.PicassoImageSrc(restaurant.getCover_image_thumbnail(), holder.coverImage, context);


                holder.select_restaurant_name_parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Realm realm=Realm.getDefaultInstance();
                        realm.beginTransaction();
                        RecentSearchModel recentSearchModel = realm.createObject(RecentSearchModel.class);
                        recentSearchModel.setId(restaurant.getId());
                        recentSearchModel.setName(restaurant.getName());
                        recentSearchModel.setAvatar(restaurant.getCover_image_thumbnail());
                        recentSearchModel.setType("Restaurant");
                        realm.commitTransaction();
                        realm.close();
                        Intent intent = new Intent(context, RestaurantDetailActivity.class);
                        intent.putExtra("RestaurantID", restaurant.getId());
                        context.startActivity(intent);
                    }
                });
            }
        }
        else
        {
            if (objects.get(position).getClass().equals(io.realm.UserProfileRealmProxy.class)) {
                final UserProfile userProfile = (UserProfile) objects.get(position);
                holder.userName.setText(userProfile.getName());
                holder.userFollowersCount.setText("" + userProfile.getFollowersCount());
                Constants.PicassoImageSrc(userProfile.getAvatar(), holder.profileImageView, context);

                holder.search_user_card_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Realm realm=Realm.getDefaultInstance();
                        realm.beginTransaction();
                        RecentSearchModel recentSearchModel = realm.createObject(RecentSearchModel.class);
                        recentSearchModel.setId(userProfile.getId());
                        recentSearchModel.setName(userProfile.getName());
                        recentSearchModel.setAvatar(userProfile.getAvatar());
                        recentSearchModel.setType("Big Foodie");
                        realm.commitTransaction();
                        realm.close();
                        Intent intent = new Intent(context, ProfilesDetailActivity.class);
                        intent.putExtra("UserID", userProfile.getId());
                        context.startActivity(intent);
                    }
                });
            }
        }
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemViewType(int position) {
        int viewType = 1; //Default is 1
        if (position < restaurantRealmList.size()) viewType = 0; //if zero, it will be a header view
        return viewType;
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }
}
