package com.dishcuss.foodie.hub.Chat;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dishcuss.foodie.hub.Models.User;
import com.dishcuss.foodie.hub.Models.UserProfile;
import com.dishcuss.foodie.hub.R;
import com.dishcuss.foodie.hub.Utils.Constants;
import com.dishcuss.foodie.hub.Utils.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 8/19/2016.
 */
public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

private static String TAG = ChatMessageAdapter.class.getSimpleName();

    private int userId;
    private int SELF = 100;
    private static String today;
    int lastMessageInfo=0;
    private String profileImage=null;
    int PunditNumber;
    int uID;
    Realm realm;

private Context mContext;
private ArrayList<ChatMessage> messageArrayList;

public class ViewHolder extends RecyclerView.ViewHolder {
    TextView message, timestamp;
    de.hdodenhof.circleimageview.CircleImageView profileImage;

    public ViewHolder(View view) {
        super(view);
        message = (TextView) itemView.findViewById(R.id.chat_message);
        timestamp = (TextView) itemView.findViewById(R.id.timestamp);
        profileImage=(de.hdodenhof.circleimageview.CircleImageView)itemView.findViewById(R.id.chat_user_profile_image);
    }
}


    public ChatMessageAdapter(Context mContext, ArrayList<ChatMessage> messageArrayList, int userId,int PunditNumber) {
        this.mContext = mContext;
        this.messageArrayList = messageArrayList;
        this.userId = userId;
        this.PunditNumber=PunditNumber;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;

        // view type is to identify where to render the menu_chat message
        // left or right
        if (viewType == SELF) {
            // self message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_self, parent, false);
        } else {
            // others message
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_other, parent, false);
        }


        return new ViewHolder(itemView);
    }


    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messageArrayList.get(position);
        if (message.userID==userId) {
            return SELF;
        }
        return position;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ChatMessage lastMessage;
        ChatMessage message = messageArrayList.get(position);

        if(!Constants.skipLogin) {
            realm= Realm.getDefaultInstance();
            User user = realm.where(User.class).findFirst();
            uID=user.getId();
        }else {
            uID=0;
        }

        Log.e("Message",""+message.userID);

        if (message.userID ==uID )
        {
            if(!Constants.skipLogin)
            {
                RealmResults<UserProfile> userProfiles = realm.where(UserProfile.class).equalTo("id", uID).findAll();

                if (userProfiles.size() > 0)
                {
                    UserProfile userProfile = userProfiles.get(userProfiles.size()-1);
                    if (userProfile.getAvatar() != null)
                    {
                        if (!userProfile.getAvatar().equals(""))
                        {
                            Constants.PicassoImageSrc(userProfile.getAvatar(), ((ViewHolder) holder).profileImage, mContext);
                        }
                    }
                }
                else
                {
                    Log.e("User ","Else");
                    UserData(uID, ((ViewHolder) holder).profileImage);
                }
            }
            else
            {
              //  UserData(uID, ((ViewHolder) holder).profileImage);
            }
        }
        else
        {
            if(PunditNumber==1) {
                ((ViewHolder) holder).profileImage.setImageResource(R.drawable.ic_desi_pundit_selection);
            }else if(PunditNumber==2) {
                ((ViewHolder) holder).profileImage.setImageResource(R.drawable.ic_sasta_pundit_selection);
            }else if(PunditNumber==3) {
                ((ViewHolder) holder).profileImage.setImageResource(R.drawable.ic_fastfood_pundit_selection);
            }else if(PunditNumber==4) {
                ((ViewHolder) holder).profileImage.setImageResource(R.drawable.ic_continental_pundit_selection);
            }else if(PunditNumber==5) {
                ((ViewHolder) holder).profileImage.setImageResource(R.drawable.ic_foreign_pundit_selection);
            }
        }

        if(position>0){
            lastMessage  = messageArrayList.get(position-1);
            if(lastMessage.userID==message.userID)
            {
                ((ViewHolder) holder).profileImage.setVisibility(View.INVISIBLE);
            }else {
                ((ViewHolder) holder).profileImage.setVisibility(View.VISIBLE);
            }
        }


        ((ViewHolder) holder).message.setText(message.getMessage());
        if(message.getCreatedAt()==null){
            Calendar c = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
            today = sdf.format(c.getTime());
            ((ViewHolder) holder).timestamp.setText(today);
        }else {
            ((ViewHolder) holder).timestamp.setText(message.getCreatedAt());
        }
    }

    @Override
    public int getItemCount() {
        return messageArrayList.size();
    }


    public static String getTimeStamp(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = "";

        today = today.length() < 2 ? "0" + today : today;

        try {
            Date date = format.parse(dateStr);
            SimpleDateFormat todayFormat = new SimpleDateFormat("dd");
            String dateToday = todayFormat.format(date);
            format = dateToday.equals(today) ? new SimpleDateFormat("hh:mm a") : new SimpleDateFormat("dd LLL, hh:mm a");
            String date1 = format.format(date);
            timestamp = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timestamp;
    }
    void UserData(int userID, final de.hdodenhof.circleimageview.CircleImageView profileImg) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Get_User_data+userID)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr = response.body().string();

                ((Activity)mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject jsonObj = new JSONObject(objStr);
                            if(jsonObj.has("user"))
                            {
                                JSONObject userObj = jsonObj.getJSONObject("user");
                                Constants.PicassoImageSrc(userObj.getString("avatar"),profileImg , mContext);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
