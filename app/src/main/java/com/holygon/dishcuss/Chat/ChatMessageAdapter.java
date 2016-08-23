package com.holygon.dishcuss.Chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.holygon.dishcuss.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Naeem Ibrahim on 8/19/2016.
 */
public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

private static String TAG = ChatMessageAdapter.class.getSimpleName();

    private int userId;
    private int SELF = 100;
    private static String today;
    int lastMessageInfo=0;

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


    public ChatMessageAdapter(Context mContext, ArrayList<ChatMessage> messageArrayList, int userId) {
        this.mContext = mContext;
        this.messageArrayList = messageArrayList;
        this.userId = userId;

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
}
