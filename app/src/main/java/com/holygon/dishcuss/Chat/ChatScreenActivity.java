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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;
import com.holygon.dishcuss.Activities.LoginActivity;
import com.holygon.dishcuss.Adapters.NotificationAdapter;
import com.holygon.dishcuss.Model.Comment;
import com.holygon.dishcuss.Model.PhotoModel;
import com.holygon.dishcuss.Model.ReviewModel;
import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.Model.UserBeenThere;
import com.holygon.dishcuss.Model.UserFollowing;
import com.holygon.dishcuss.Model.UserProfile;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;
import com.holygon.dishcuss.Utils.DishCussApplication;
import com.holygon.dishcuss.Utils.URLs;

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
    int selfID=0;

    String punditType;
    ImageView pundit_image;


    User user;
    Realm realm;
    String userJoinID;
    private Boolean isConnected = false;
    int punditNumber=0;

    String guestEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.chat_screen_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DishCussApplication app = (DishCussApplication) this.getApplication();
        mSocket = app.getSocket();
        int rand= (int) (5 + (Math.random() * (99909 - 10000)));
        guestEmail="guest"+rand+"@gmail.com";
        mSocket.on(Socket.EVENT_CONNECT,onConnect);
        mSocket.on(Socket.EVENT_DISCONNECT,onDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.on("pandit_msg", onNewMessage);
        mSocket.on("welcome_msg", onJoin);

        mSocket.connect();

        if(!Constants.skipLogin) {
            realm = Realm.getDefaultInstance();
            user = realm.where(User.class).findFirst();
            Log.e("User : ", "" + user);
            UserProfile userProfile=new UserProfile();
            userProfile=GetUserData(user.getId());
            if (userProfile == null) {
                UserData(user.getId());
            }
        }else {

        }

        TextView headerName = (TextView) findViewById(R.id.app_toolbar_name);
        TextView chat_pundit_type = (TextView) findViewById(R.id.chat_pundit_type);
        pundit_image = (ImageView)findViewById(R.id.pundit_image);
        chat_message=(EditText)findViewById(R.id.chat_message_edt);
        chat_btn_send=(Button)findViewById(R.id.chat_btn_send);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            headerName.setText(bundle.getString("PunditName"));
            punditNumber=bundle.getInt("PunditNumber");
            punditType=bundle.getString("PunditType");

            if(punditNumber==1) {
                chat_pundit_type.setText("Desi Pundit");
                pundit_image.setImageDrawable(getResources().getDrawable(R.drawable.pundit_ic_desi));
            }else if(punditNumber==2) {
                chat_pundit_type.setText("Sasta Pundit");
                pundit_image.setImageDrawable(getResources().getDrawable(R.drawable.pundit_ic_sasta));
            }else if(punditNumber==3) {
                chat_pundit_type.setText("Fast Food Pundit");
                pundit_image.setImageDrawable(getResources().getDrawable(R.drawable.pundit_ic_fastfood));
            }else if(punditNumber==4) {
                chat_pundit_type.setText("Continental Pundit");
                pundit_image.setImageDrawable(getResources().getDrawable(R.drawable.pundit_ic_continental));
            }else if(punditNumber==5) {
                chat_pundit_type.setText("Foreign Pundit");
                pundit_image.setImageDrawable(getResources().getDrawable(R.drawable.pundit_ic_foriegn));
            }
        }


        chatRecyclerView = (RecyclerView) findViewById(R.id.chat_recycleView);
        chatLayoutManager = new LinearLayoutManager(this);
        chatRecyclerView.setLayoutManager(chatLayoutManager);
        chatRecyclerView.setNestedScrollingEnabled(false);

        if(user!=null) {
            messageAdapter = new ChatMessageAdapter(ChatScreenActivity.this, chatMessageArrayList, user.getId(), punditNumber);
            chatRecyclerView.setAdapter(messageAdapter);
        }else {
            messageAdapter = new ChatMessageAdapter(ChatScreenActivity.this, chatMessageArrayList,selfID, punditNumber);
            chatRecyclerView.setAdapter(messageAdapter);
        }

        chat_btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newMessage=chat_message.getText().toString();
                if(isConnected) {
                    if (!newMessage.equals("")) {
                        chat_message.setText("");
                        ChatMessage message = new ChatMessage();

                        if(user!=null) {
                            message.setUserID(user.getId());
                        }else {
                            message.setUserID(selfID);
                        }

                        message.setMessage(newMessage);

                        chatMessageArrayList.add(message);

                        JSONObject jsonObj = new JSONObject();
                        try {
                            jsonObj.put("id", userJoinID);
                            jsonObj.put("msg", newMessage);
                            if(user!=null) {
                                jsonObj.put("user", user.getEmail());
                            }else {
                                jsonObj.put("user", guestEmail);
                            }
                            jsonObj.put("img", "");
                            jsonObj.put("room", punditType);
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

                    }

                    if(mSocket.connected()){
                        JSONObject jsonObj=new JSONObject();
                        try {

                            if(user!=null) {
                                jsonObj.put("id", user.getId());
                                jsonObj.put("username",user.getName());
                                if(!user.getEmail().equals("")){
                                    jsonObj.put("email",user.getEmail());
                                }
                                else
                                {
                                    jsonObj.put("email",guestEmail);
                                }
                            }else {
                                jsonObj.put("id", selfID);
                                jsonObj.put("username","Test User");
                                jsonObj.put("email",guestEmail);
                            }

                            jsonObj.put("room",punditType);
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
                    messages.setId(10101);
                    messages.setUserID(10101);
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
                    messages.setId(10101);
                    messages.setUserID(10101);
                    messages.setMessage(message);
                    chatMessageArrayList.add(messages);
                    messageAdapter.notifyDataSetChanged();
                    isConnected = true;
                    scrollToBottom();
                }
            });
        }
    };



    private void scrollToBottom() {
        chatRecyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
    }

    void UserData(int userID) {

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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject jsonObj = new JSONObject(objStr);

                            if(jsonObj.has("user"))
                            {
                                JSONObject userObj = jsonObj.getJSONObject("user");

                                realm.beginTransaction();
                                UserProfile userProfileRealm = realm.createObject(UserProfile.class);

                                userProfileRealm.setId(userObj.getInt("id"));
                                userProfileRealm.setName(userObj.getString("name"));
                                userProfileRealm.setUsername(userObj.getString("username"));
                                userProfileRealm.setEmail(userObj.getString("email"));
                                userProfileRealm.setAvatar(userObj.getString("avatar"));
                                userProfileRealm.setLocation(userObj.getString("location"));
                                userProfileRealm.setGender(userObj.getString("gender"));
                                userProfileRealm.setRole(userObj.getString("role"));

                                //Arrays
                                JSONArray jsonDataFollowingArray = userObj.getJSONArray("following");
                                JSONArray jsonDataFollowersArray = userObj.getJSONArray("followers");
                                JSONArray jsonDataPostsArray = userObj.getJSONArray("posts");
                                JSONArray jsonDataReviewsArray = userObj.getJSONArray("reviews");



                                for(int p=0;p<jsonDataPostsArray.length();p++){
                                    JSONObject postObj=jsonDataPostsArray.getJSONObject(p);
                                    JSONObject checkinObj = postObj.getJSONObject("checkin");

                                    if(checkinObj.has("restaurant")) {
                                        JSONObject restaurantObj = checkinObj.getJSONObject("restaurant");

                                        UserBeenThere userBeenThere = new UserBeenThere();
                                        userBeenThere.setId(restaurantObj.getInt("id"));
                                        userBeenThere.setRestaurantName(restaurantObj.getString("name"));
                                        userBeenThere.setRestaurantLocation(restaurantObj.getString("location"));
                                        userBeenThere.setCover_image_url(checkinObj.getString("restaurant_image"));
                                        userBeenThere.setBeenThereTime(checkinObj.getString("time"));
                                        final UserBeenThere beenThere = realm.copyToRealm(userBeenThere);
                                        userProfileRealm.getUserBeenThereRealmList().add(beenThere);
                                    }


                                    JSONArray jsonDataPhotosArray = postObj.getJSONArray("photos");
                                    for (int ph = 0; ph < jsonDataPhotosArray.length(); ph++) {
                                        JSONObject photo = jsonDataPhotosArray.getJSONObject(ph);
                                        PhotoModel photoModel = new PhotoModel();
                                        photoModel.setId(photo.getInt("id"));
                                        photoModel.setUrl(photo.getString("image_url"));
                                        final PhotoModel managedPhotoModel = realm.copyToRealm(photoModel);
                                        userProfileRealm.getPhotoModelRealmList().add(managedPhotoModel);
                                    }

                                    JSONArray jsonDataCommentsArray = postObj.getJSONArray("comments");
                                    for (int c = 0; c < jsonDataCommentsArray.length(); c++) {
                                        JSONObject commentObj = jsonDataCommentsArray.getJSONObject(c);
                                        Comment comment= new Comment();
                                        comment.setCommentID(commentObj.getInt("id"));
                                        comment.setCommentTitle(commentObj.getString("title"));
                                        comment.setCommentUpdated_at(commentObj.getString("created_at"));
                                        comment.setCommentSummary(commentObj.getString("comment"));
                                        JSONObject commentatorObj = commentObj.getJSONObject("commentor");
                                        comment.setCommentatorID(commentatorObj.getInt("id"));
                                        comment.setCommentatorName(commentatorObj.getString("name"));
                                        comment.setCommentatorImage(commentatorObj.getString("avatar"));
                                        JSONArray commentLikeArray=commentObj.getJSONArray("likes");
                                        comment.setCommentLikesCount(commentLikeArray.length());
                                        final Comment managedComment = realm.copyToRealm(comment);
                                        userProfileRealm.getCommentRealmList().add(managedComment);
                                    }
                                }

                                for (int r = 0; r < jsonDataReviewsArray.length();r++) {

                                    JSONObject reviewObj = jsonDataReviewsArray.getJSONObject(r);
                                    realm.commitTransaction();
                                    realm.beginTransaction();
                                    ReviewModel reviewModel=realm.createObject(ReviewModel.class);

                                    reviewModel.setReview_ID(reviewObj.getInt("id"));
                                    reviewModel.setReviewable_id(reviewObj.getInt("reviewable_id"));
                                    reviewModel.setReview_title(reviewObj.getString("title"));
                                    reviewModel.setUpdated_at(reviewObj.getString("updated_at"));
                                    reviewModel.setReview_summary(reviewObj.getString("summary"));
                                    reviewModel.setReviewable_type(reviewObj.getString("reviewable_type"));

                                    JSONObject reviewObjReviewer= reviewObj.getJSONObject("reviewer");

                                    reviewModel.setReview_reviewer_ID(reviewObjReviewer.getInt("id"));
                                    reviewModel.setReview_reviewer_Name(reviewObjReviewer.getString("name"));
                                    reviewModel.setReview_reviewer_Avatar(reviewObjReviewer.getString("avatar"));
                                    reviewModel.setReview_reviewer_time(reviewObjReviewer.getString("location"));

                                    JSONArray reviewLikesArray = reviewObj.getJSONArray("likes");
                                    JSONArray reviewCommentsArray = reviewObj.getJSONArray("comments");
                                    JSONArray reviewShareArray = reviewObj.getJSONArray("reports");

                                    reviewModel.setReview_Likes_count(reviewLikesArray.length());
                                    reviewModel.setReview_comments_count(reviewCommentsArray.length());
                                    reviewModel.setReview_shares_count(reviewShareArray.length());



                                    realm.commitTransaction();
                                    realm.beginTransaction();

                                    for (int c = 0; c < reviewCommentsArray.length(); c++) {

                                        JSONObject commentObj = reviewCommentsArray.getJSONObject(c);
                                        Comment comment=realm.createObject(Comment.class);
                                        comment.setCommentID(commentObj.getInt("id"));
                                        comment.setCommentTitle(commentObj.getString("title"));
                                        comment.setCommentUpdated_at(commentObj.getString("created_at"));
                                        comment.setCommentSummary(commentObj.getString("comment"));
                                        JSONObject commentatorObj = commentObj.getJSONObject("commentor");
                                        comment.setCommentatorID(commentatorObj.getInt("id"));
                                        comment.setCommentatorName(commentatorObj.getString("name"));
                                        comment.setCommentatorImage(commentatorObj.getString("avatar"));
                                        JSONArray commentLikeArray=commentObj.getJSONArray("likes");
                                        comment.setCommentLikesCount(commentLikeArray.length());
                                        final Comment managedComment = realm.copyToRealm(comment);
                                        reviewModel.getCommentRealmList().add(managedComment);
                                    }
                                    realm.commitTransaction();
                                    realm.beginTransaction();

                                    final ReviewModel managedReviewModel= realm.copyToRealm(reviewModel);
                                    userProfileRealm.getReviewModelRealmList().add(managedReviewModel);

                                }


                                for(int fs=0;fs<jsonDataFollowingArray.length();fs++){
                                    JSONObject jsonFollowingObject = jsonDataFollowingArray.getJSONObject(fs);
                                    UserFollowing userFollowing=new UserFollowing();

                                    userFollowing.setId(jsonFollowingObject.getInt("id"));
                                    userFollowing.setLikesCount(jsonFollowingObject.getInt("likees_count"));
                                    userFollowing.setFollowerCount(jsonFollowingObject.getInt("followers_count"));
                                    userFollowing.setFollowingCount(jsonFollowingObject.getInt("followees_count"));

                                    userFollowing.setName(jsonFollowingObject.getString("name"));
                                    userFollowing.setUsername(jsonFollowingObject.getString("username"));
                                    userFollowing.setAvatar(jsonFollowingObject.getString("avatar"));
                                    userFollowing.setLocation(jsonFollowingObject.getString("location"));
                                    userFollowing.setEmail(jsonFollowingObject.getString("email"));
                                    userFollowing.setGender(jsonFollowingObject.getString("gender"));
                                    userFollowing.setRole(jsonFollowingObject.getString("name"));
                                    userFollowing.setReferral_code(jsonFollowingObject.getString("referal_code"));

                                    final UserFollowing managedUserFollowing = realm.copyToRealm(userFollowing);
                                    userProfileRealm.getUserFollowingRealmList().add(managedUserFollowing);
                                }

                                for(int fr=0;fr<jsonDataFollowersArray.length();fr++){
                                    JSONObject jsonFollowingObject = jsonDataFollowersArray.getJSONObject(fr);

                                    UserFollowing userFollowing=new UserFollowing();

                                    userFollowing.setId(jsonFollowingObject.getInt("id"));
                                    userFollowing.setLikesCount(jsonFollowingObject.getInt("likees_count"));
                                    userFollowing.setFollowerCount(jsonFollowingObject.getInt("followers_count"));
                                    userFollowing.setFollowingCount(jsonFollowingObject.getInt("followees_count"));

                                    userFollowing.setName(jsonFollowingObject.getString("name"));
                                    userFollowing.setUsername(jsonFollowingObject.getString("username"));
                                    userFollowing.setAvatar(jsonFollowingObject.getString("avatar"));
                                    userFollowing.setLocation(jsonFollowingObject.getString("location"));
                                    userFollowing.setEmail(jsonFollowingObject.getString("email"));
                                    userFollowing.setGender(jsonFollowingObject.getString("gender"));
                                    userFollowing.setRole(jsonFollowingObject.getString("name"));
                                    userFollowing.setReferral_code(jsonFollowingObject.getString("referal_code"));

                                    final UserFollowing managedUserFollowing = realm.copyToRealm(userFollowing);
                                    userProfileRealm.getUserFollowersRealmList().add(managedUserFollowing);
                                }

                                realm.commitTransaction();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        realm.close();
                    }
                });
            }
        });
    }

    UserProfile GetUserData(int uid){
        realm = Realm.getDefaultInstance();
        RealmResults<UserProfile> userProfiles = realm.where(UserProfile.class).equalTo("id", uid).findAll();
        Log.e("Count",""+userProfiles.size());
        if(userProfiles.size()>0){
            realm.beginTransaction();
            realm.commitTransaction();
            return userProfiles.get(userProfiles.size()-1);
        }
        return null;
    }
}
