package com.dishcuss.foodies.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dishcuss.foodies.Activities.ProfilesDetailActivity;
import com.dishcuss.foodies.Model.MyFeeds;
import com.dishcuss.foodies.Model.User;
import com.dishcuss.foodies.R;
import com.dishcuss.foodies.Utils.Constants;
import com.dishcuss.foodies.Utils.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 8/25/2016.
 */
public class FindYourEatBuddiesAdapter extends RecyclerView.Adapter<FindYourEatBuddiesAdapter.ViewHolder> {

    private ArrayList<MyFeeds> imageViewArrayList=new ArrayList<>();
    Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public  de.hdodenhof.circleimageview.CircleImageView imageView;
        public TextView your_eat_buddies_user_name,follow_button_text;
        public LinearLayout follow_button_layout;
        public RelativeLayout eat_buddies_user_profile_layout;

        public ViewHolder(View v) {
            super(v);
            imageView = ( de.hdodenhof.circleimageview.CircleImageView) v.findViewById(R.id.your_eat_buddies_profile_image);
            your_eat_buddies_user_name = (TextView) v.findViewById(R.id.your_eat_buddies_user_name);
            follow_button_text = (TextView) v.findViewById(R.id.follow_button_text);
            follow_button_layout = (LinearLayout) v.findViewById(R.id.follow_button_layout);
            eat_buddies_user_profile_layout = (RelativeLayout) v.findViewById(R.id.eat_buddies_user_profile_layout);
        }
    }

    public FindYourEatBuddiesAdapter(ArrayList<MyFeeds> imageView, Context context) {
        imageViewArrayList.addAll(imageView);
        mContext=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.find_your_eat_buddies_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder,final int position) {
        holder.your_eat_buddies_user_name.setText(imageViewArrayList.get(position).getName());
        if(!imageViewArrayList.get(position).getAvatarPic().equals("")){
            String imageUri = imageViewArrayList.get(position).getAvatarPic();
            String newUrlString = imageUri.replaceAll(" ", "%20");
            Constants.PicassoImageSrc(newUrlString,holder.imageView,mContext);
        }

        if(!imageViewArrayList.get(position).isFollowing())
        {
            holder.follow_button_text.setText("  Follow");
        }
        else
        {
            holder.follow_button_text.setText("UnFollow");
        }


        holder.eat_buddies_user_profile_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, ProfilesDetailActivity.class);
                intent.putExtra("UserID", imageViewArrayList.get(position).getId());
                mContext.startActivity(intent);
            }
        });

        holder.follow_button_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!imageViewArrayList.get(position).isFollowing()) {
                    holder.follow_button_text.setText("UnFollow");
                    FollowUser(imageViewArrayList.get(position).getId(),position);
                    imageViewArrayList.get(position).setFollowing(true);
                }else {
                    holder.follow_button_text.setText("  Follow");
                    UnFollowUser(imageViewArrayList.get(position).getId(),position);
                    imageViewArrayList.get(position).setFollowing(false);
                }

                notifyDataSetChanged();
            }
        });

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return imageViewArrayList.size();
    }

    void FollowUser(int id,int pos){

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

                String objStr=response.body().string();
                try {
                    JSONObject jsonObj = new JSONObject(objStr);

                    if(jsonObj.has("message")){
                        String message= jsonObj.getString("message");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {

                }
            }
        });

        realm.commitTransaction();
    }


    void UnFollowUser(int id,int pos){
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
                        String message= jsonObj.getString("message");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                }
            }
        });
        realm.commitTransaction();
    }
}
