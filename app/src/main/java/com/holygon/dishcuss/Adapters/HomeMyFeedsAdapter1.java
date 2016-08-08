package com.holygon.dishcuss.Adapters;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.holygon.dishcuss.Model.MyFeeds;
import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.URLs;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 7/30/2016.
 */
public class HomeMyFeedsAdapter1 extends RecyclerView.Adapter<HomeMyFeedsAdapter1.ViewHolder> {

    private ArrayList<MyFeeds> myFeeds = new ArrayList<>();
    private Context mContext;
    String message=null;
    String objStr=null;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView userName,reviewTime;
        public de.hdodenhof.circleimageview.CircleImageView profileImageView;
        public ImageView followedImageView;

        public ViewHolder(View v) {
            super(v);
            userName = (TextView) v.findViewById(R.id.my_feeds_user_name);
            reviewTime = (TextView) v.findViewById(R.id.my_feeds_user_review_time);
            followedImageView = (ImageView) v.findViewById(R.id.my_feed_followed_info_image);
            profileImageView=(de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.my_feeds_profile_image);
        }
    }

    public HomeMyFeedsAdapter1(ArrayList<MyFeeds> myFeedsData,Context context) {
        myFeeds.clear();
        myFeeds.addAll(myFeedsData);
        mContext=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_my_feeds_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Resources res = mContext.getResources();
        holder.userName.setText(myFeeds.get(position).getName());
        holder.reviewTime.setText(String.valueOf(myFeeds.get(position).getId()));
        if(myFeeds.get(position).isFollowing()){
            holder.followedImageView.setImageDrawable(res.getDrawable(R.drawable.icon_already_followed));
        }
        else {
            holder.followedImageView.setImageDrawable(res.getDrawable(R.drawable.icon_my_feed_account));
        }

//        String imageUri = myFeeds.get(position).getAvatarPic();
//        Picasso.with(mContext).load(imageUri).into(holder.profileImageView);

        holder.followedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!myFeeds.get(position).isFollowing()){

                    boolean st = FollowUser(myFeeds.get(position).getId());
                    if(st){
                        holder.followedImageView.setImageDrawable(res.getDrawable(R.drawable.icon_already_followed));
                        myFeeds.get(position).setFollowing(true);
                    }
                }else
                {
                    boolean st =UnFollowUser(myFeeds.get(position).getId());
                    if(st){
                        holder.followedImageView.setImageDrawable(res.getDrawable(R.drawable.icon_my_feed_account));
                        myFeeds.get(position).setFollowing(false);
                    }
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return myFeeds.size();
    }

    boolean FollowUser(int id){

        // Get a Realm instance for this thread
        Realm realm=Realm.getDefaultInstance();
        // Persist your data in a transaction
        realm.beginTransaction();
        final User user = realm.where(User.class).findFirst();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Follow_User+id)
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

                objStr=response.body().string();
                try {
                    JSONObject jsonObj = new JSONObject(objStr);

                    if(jsonObj.has("message")){
                        message= jsonObj.getString("message");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {

                }
            }
        });

        while (message==null){
//            Log.e("Loop","Working");
        }
        if (message.equals("Successfully followed!")) {
            return true;
        }
        else
        {
            return false;
        }
    }

    boolean UnFollowUser(int id){
        // Get a Realm instance for this thread
        Realm realm=Realm.getDefaultInstance();
        // Persist your data in a transaction
        realm.beginTransaction();
        final User user = realm.where(User.class).findFirst();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.UnFollow_User+id)
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

                String objStr=response.body().string();
                try {
                    JSONObject jsonObj = new JSONObject(objStr);

                    if(jsonObj.has("message")){
                        message= jsonObj.getString("message");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                }
            }
        });

        while (message==null){
//            Log.e("Loop","Working");
        }
        if (message.equals("Successfully unfollowed!"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
