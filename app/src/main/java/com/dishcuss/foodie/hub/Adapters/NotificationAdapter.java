package com.dishcuss.foodie.hub.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dishcuss.foodie.hub.Activities.MyWalletActivity;
import com.dishcuss.foodie.hub.Activities.NotificationClickPostDetail;
import com.dishcuss.foodie.hub.Activities.ProfilesDetailActivity;
import com.dishcuss.foodie.Model.Notifications;
import com.dishcuss.foodie.Model.User;
import com.dishcuss.foodie.hub.R;
import com.dishcuss.foodie.hub.Utils.Constants;
import com.dishcuss.foodie.hub.Utils.URLs;

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
 * Created by Naeem Ibrahim on 8/18/2016.
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private ArrayList<Notifications> notificationsArrayList;
    Context context;



    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView notifierName, body;
        public ImageView userAvatar;
        public RelativeLayout parentLayout;
        public ViewHolder(View v) {
            super(v);

            notifierName = (TextView) v.findViewById(R.id.select_a_restaurant_name);
            body = (TextView) v.findViewById(R.id.select_a_restaurant_address);
            userAvatar =(ImageView) v.findViewById(R.id.select_a_restaurant_image_view);
            parentLayout =(RelativeLayout) v.findViewById(R.id.select_a_restaurant_name_parent);

        }
    }
    public NotificationAdapter(ArrayList<Notifications> notificationsArrayList,Context context) {
        this.notificationsArrayList=notificationsArrayList;
        this.context=context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_a_restaurant_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.notifierName.setText(notificationsArrayList.get(position).getUsername());

//        if(notificationsArrayList.get(position).getUsername().isEmpty()) {
//            holder.notifierName.setVisibility(View.GONE);
//        }
        holder.body.setText(notificationsArrayList.get(position).getBody());

        Constants.PicassoImageSrc(notificationsArrayList.get(position).getAvatarPic(),holder.userAvatar,context);




        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                Toast.makeText(context,notificationsArrayList.get(position).getRedirectType()+" "+notificationsArrayList.get(position).getRedirectID(),Toast.LENGTH_SHORT).show();

                if(notificationsArrayList.get(position).getRedirectType().toString().equals("Credit")){
                    Intent intent=new Intent(context, MyWalletActivity.class);
                    context.startActivity(intent);
                    //((Activity)(context)).finish();
                }


                if(notificationsArrayList.get(position).getRedirectType().toString().equals("User")){
                    if(notificationsArrayList.get(position).getRedirectID()!=0) {
                        Intent intent = new Intent(context, ProfilesDetailActivity.class);
                        intent.putExtra("UserID", notificationsArrayList.get(position).getRedirectID());
                        context.startActivity(intent);
                      //  ((Activity) (context)).finish();
                    }
                }

                if(notificationsArrayList.get(position).getRedirectType().toString().equals("Post")){
                    Intent intent = new Intent(context, NotificationClickPostDetail.class);
                    intent.putExtra("TypeID", notificationsArrayList.get(position).getRedirectID());
                    intent.putExtra("Type", "Post");
                    context.startActivity(intent);
                    //((Activity) (context)).finish();
                }
                if(notificationsArrayList.get(position).getRedirectType().toString().equals("Review")){
                    Intent intent = new Intent(context, NotificationClickPostDetail.class);
                    intent.putExtra("TypeID", notificationsArrayList.get(position).getRedirectID());
                    intent.putExtra("Type", "Review");
                    context.startActivity(intent);
                   // ((Activity) (context)).finish();
                }
            }
        });

        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return notificationsArrayList.size();
    }


    public void remove(int position) {
//        Log.e("Notification ID",""+notificationsArrayList.get(position).getId());
        //Notifications(notificationsArrayList.get(position).getId());
        notificationsArrayList.remove(position);
        notifyItemRemoved(position);
    }

    void Notifications(int id){
        Realm realm;

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        // Persist your data in a transaction

        User user = realm.where(User.class).findFirst();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Get_Notification+"/"+id)
                .addHeader("Authorization", "Token token="+user.getToken())
                .build();
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

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        realm.close();


    }
}
