package com.holygon.dishcuss.Chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.holygon.dishcuss.Adapters.NotificationAdapter;
import com.holygon.dishcuss.R;

import java.util.ArrayList;

/**
 * Created by Naeem Ibrahim on 8/19/2016.
 */
public class ChatScreenActivity extends AppCompatActivity {

    RecyclerView chatRecyclerView;
    private RecyclerView.LayoutManager chatLayoutManager;
    ChatMessageAdapter messageAdapter;
    ArrayList<ChatMessage> chatMessageArrayList=new ArrayList<>();
    EditText chat_message;
    Button chat_btn_send;
    int selfID=129;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_screen_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView headerName = (TextView) findViewById(R.id.app_toolbar_name);
        TextView chat_pundit_type = (TextView) findViewById(R.id.chat_pundit_type);
        chat_message=(EditText)findViewById(R.id.chat_message_edt);
        chat_btn_send=(Button) findViewById(R.id.chat_btn_send);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            headerName.setText(bundle.getString("PunditName"));
            chat_pundit_type.setText(bundle.getString("PunditType"));
        }


        chatRecyclerView = (RecyclerView) findViewById(R.id.chat_recycleView);
        chatLayoutManager = new LinearLayoutManager(this);
        chatRecyclerView.setLayoutManager(chatLayoutManager);
        chatRecyclerView.setNestedScrollingEnabled(false);

        messageAdapter = new ChatMessageAdapter(ChatScreenActivity.this,chatMessageArrayList,129);
        chatRecyclerView.setAdapter(messageAdapter);

        LoadData();

        chat_btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newMessage=chat_message.getText().toString();
                if(!newMessage.equals("")){
                    chat_message.setText("");
                    ChatMessage message = new ChatMessage();
                    message.setUserID(selfID);
                    message.setMessage(newMessage);

                    chatMessageArrayList.add(message);

                    messageAdapter.notifyItemInserted(chatMessageArrayList.size() - 1);

                    chatRecyclerView.scrollToPosition(chatMessageArrayList.size()-1);

                }
            }
        });
    }


    void LoadData(){


        for(int i=0;i<3;i++) {
            if(i%3==0)
            {
                ChatMessage message = new ChatMessage();
                message.setUserID(selfID);
                message.setMessage("This is new message");
                message.setCreatedAt("4:00 PM");
                chatMessageArrayList.add(message);
                messageAdapter.notifyDataSetChanged();
            }
            else
            {
                ChatMessage message = new ChatMessage();
                message.setId(i);
                message.setMessage("This is new message");
                message.setCreatedAt("7:30 PM");
                chatMessageArrayList.add(message);
                messageAdapter.notifyDataSetChanged();
            }
        }

        scrollToBottom();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void scrollToBottom() {
        chatRecyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
    }
}
