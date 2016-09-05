package com.holygon.dishcuss.Chat;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.holygon.dishcuss.Adapters.NotificationAdapter;
import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.DishCussApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;

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
    private Socket mSocket;
    int selfID=129;


    User user;
    Realm realm;
    String userJoinID;
    private Boolean isConnected = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_screen_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DishCussApplication app = (DishCussApplication) this.getApplication();
        mSocket = app.getSocket();

        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("pandit_msg", onNewMessage);
        mSocket.on("welcome_msg", onJoin);

        mSocket.connect();

        realm=Realm.getDefaultInstance();
        user = realm.where(User.class).findFirst();

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

        chat_btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newMessage=chat_message.getText().toString();
                if(isConnected) {
                    if (!newMessage.equals("")) {
                        chat_message.setText("");
                        ChatMessage message = new ChatMessage();
                        message.setUserID(selfID);
                        message.setMessage(newMessage);

                        chatMessageArrayList.add(message);

                        JSONObject jsonObj = new JSONObject();
                        try {
                            jsonObj.put("id", userJoinID);
                            jsonObj.put("msg", newMessage);
                            jsonObj.put("user", user.getEmail());
                            jsonObj.put("img", "");
                            jsonObj.put("room", "pandit1");
                            mSocket.emit("p1_msg_app", jsonObj.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        messageAdapter.notifyItemInserted(chatMessageArrayList.size() - 1);

                        chatRecyclerView.scrollToPosition(chatMessageArrayList.size() - 1);

                    }
                }
                else
                {
                    Toast.makeText(ChatScreenActivity.this.getApplicationContext(),
                            "Pundit Busy Please Wait", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();

        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off("pandit_msg", onNewMessage);
        mSocket.off("welcome_msg", onJoin);
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


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isConnected) {
                       // if(null!=mUsername)
//                            mSocket.emit("add user", mUsername);
//                            Toast.makeText(ChatScreenActivity.this.getApplicationContext(),
//                                    "connect", Toast.LENGTH_LONG).show();
                        isConnected = true;
                    }

                    if(mSocket.connected()){
                        JSONObject jsonObj=new JSONObject();
                        try {
                            jsonObj.put("id",user.getId());
                            jsonObj.put("username",user.getName());
                            if(!user.getEmail().equals("")){
                                jsonObj.put("email",user.getEmail());
                            }
                            else
                            {
                                jsonObj.put("email","default@gmail.com");
                            }
                            jsonObj.put("room","pandit1");
                            mSocket.emit("p1_join",jsonObj.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isConnected = false;
//                    Toast.makeText(ChatScreenActivity.this.getApplicationContext(),
//                            "disconnect", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(ChatScreenActivity.this.getApplicationContext(),
//                            "error_connect", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];

                    Log.e("Data",""+data);

                    String username;
                    String message;
                    String sender;
                    try {
                        username = data.getString("pandit");
                        message = data.getString("rply");
                    } catch (JSONException e) {
                        return;
                    }

                    ChatMessage messages = new ChatMessage();
                    messages.setId(010101);
                    messages.setMessage(message);
                    chatMessageArrayList.add(messages);
                    messageAdapter.notifyDataSetChanged();
                    scrollToBottom();
                }
            });
        }
    };


    private Emitter.Listener onJoin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];

                    Log.e("Data",""+data);

                    String id;
                    String message;
                    try {
                        id = data.getString("id");
                        message = data.getString("msg");
                    } catch (JSONException e) {
                        return;
                    }

                    userJoinID=id;
                    ChatMessage messages = new ChatMessage();
                    messages.setId(010101);
                    messages.setMessage(message);
                    chatMessageArrayList.add(messages);
                    messageAdapter.notifyDataSetChanged();
                    scrollToBottom();
                }
            });
        }
    };



    private void scrollToBottom() {
        chatRecyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
    }
}
