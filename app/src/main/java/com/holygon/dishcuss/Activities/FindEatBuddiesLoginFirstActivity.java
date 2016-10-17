package com.holygon.dishcuss.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.holygon.dishcuss.Model.Comment;
import com.holygon.dishcuss.Model.KhabaHistoryModel;
import com.holygon.dishcuss.Model.LocalFeedCheckIn;
import com.holygon.dishcuss.Model.LocalFeedReview;
import com.holygon.dishcuss.Model.LocalFeeds;
import com.holygon.dishcuss.Model.Notifications;
import com.holygon.dishcuss.Model.Restaurant;
import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.Model.UserProfile;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;
import com.holygon.dishcuss.Utils.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FindEatBuddiesLoginFirstActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    String rawName;
    boolean isFacebookCalled=false;

    String Str_Referral_Code="", email="",name="",username="",location="",profilePicURL="",gender="",provider="",uid="",profileURL="",token="",expires_at="";
    AlertDialog.Builder alert;
    String message="";
    OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookSDKInitialize();
        setContentView(R.layout.activity_eat_buddies_test);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        client = new OkHttpClient();
        alert = new AlertDialog.Builder(this);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");

        TextView headerName=(TextView)findViewById(R.id.app_toolbar_name);
        headerName.setText("Login To Find EatBuddies");

        if(isLoggedIn()) {
            loginButton.setVisibility(View.GONE);
            rawName= Constants.GetUserFacebookFriends(FindEatBuddiesLoginFirstActivity.this);
            Intent intent = new Intent(FindEatBuddiesLoginFirstActivity.this,FindYourEatBuddiesActivity.class);
            intent.putExtra("jsondata", rawName);
            startActivity(intent);
            finish();
        }

        getLoginDetails(loginButton);
    }

    /*
     * Register a callback function with LoginButton to respond to the login result.
     */
    protected void getLoginDetails(LoginButton login_button){

        // Callback registration
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult login_result) {


                if(Constants.skipLogin) {
                    DeleteAll();
                    GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
                            login_result.getAccessToken(),
                            //AccessToken.getCurrentAccessToken(),
                            "/me/friends",
                            null,
                            HttpMethod.GET,
                            new GraphRequest.Callback() {
                                public void onCompleted(GraphResponse response) {
                                    Log.e("response: ", response + "");
//                                Intent intent = new Intent(FindEatBuddiesLoginFirstActivity.this,FindYourEatBuddiesActivity.class);
                                    try {
                                        JSONArray rawName = response.getJSONObject().getJSONArray("data");
                                        Constants.SetUserFacebookFriends(FindEatBuddiesLoginFirstActivity.this,rawName.toString());
//                                    intent.putExtra("jsondata", rawName.toString());
//                                    startActivity(intent);
//                                    finish();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                    ).executeAsync();
                    // App code
                    GraphRequest request = GraphRequest.newMeRequest(
                            login_result.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {

//                            Log.e("response: ", response + "");
//                            Log.e("object: ", object.toString());

                                    try {
                                        if (object.has("email")) {
                                            email = object.getString("email").toString();
                                        }
                                        name = object.getString("name").toString();
                                        uid = object.getString("id").toString();
                                        username = object.getString("name").toString();
                                        profilePicURL = "https://graph.facebook.com/" + uid + "/picture?type=large";
                                        if (object.has("location")) {
                                            location = object.getString("location").toString();
                                        }

                                        int rand = (int) (5 + (Math.random() * (99909 - 10000)));
                                        provider = "Facebook";
                                        profileURL = "https://www.facebook.com/app_scoped_user_id/" + uid + "/";
//                                token=loginResult.getAccessToken().getToken();
                                        token = "kfbookes" + uid + rand;
                                        Log.e("Token ", "" + token.length());
                                        gender = object.getString("gender").toString();
                                        expires_at = "";

                                        if (!isFacebookCalled) {
                                            if (!Constants.GetReferral(FindEatBuddiesLoginFirstActivity.this)) {
                                                AlertDialog();
                                            } else {
                                                SocialLoginSendDataOnServer();
                                            }
                                            isFacebookCalled = true;
                                        }

//                                String imageUrl = "https://graph.facebook.com/"+ loginUser.loginID +"/picture?type=large";
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                }

                            });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,email,gender, birthday");
                    request.setParameters(parameters);
                    request.executeAsync();


                }
                else
                {
                    GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
                            login_result.getAccessToken(),
                            //AccessToken.getCurrentAccessToken(),
                            "/me/friends",
                            null,
                            HttpMethod.GET,
                            new GraphRequest.Callback() {
                                public void onCompleted(GraphResponse response) {
                                    Log.e("response: ", response + "");
                                    Intent intent = new Intent(FindEatBuddiesLoginFirstActivity.this,FindYourEatBuddiesActivity.class);
                                    try {
                                        JSONArray rawName = response.getJSONObject().getJSONArray("data");
                                        Constants.SetUserFacebookFriends(FindEatBuddiesLoginFirstActivity.this,rawName.toString());
                                        intent.putExtra("jsondata", rawName.toString());
                                        startActivity(intent);
                                        finish();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                    ).executeAsync();
                }
            }

            @Override
            public void onCancel() {
                // code for cancellation
            }

            @Override
            public void onError(FacebookException exception) {
                //  code to handle error
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    protected void facebookSDKInitialize() {

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Logs 'install' and 'app activate' App Events.
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }


    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {


        @Override
        public void onSuccess(final LoginResult loginResult) {

            Log.e("response: ",loginResult+"");
            GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
                    loginResult.getAccessToken(),
                    //AccessToken.getCurrentAccessToken(),
                    "/me/friends",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            Log.e("response: ", response + "");
                            Intent intent = new Intent(FindEatBuddiesLoginFirstActivity.this,FriendsList.class);
                            try {
                                JSONArray rawName = response.getJSONObject().getJSONArray("data");
                                intent.putExtra("jsondata", rawName.toString());
                                startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            ).executeAsync();


        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };

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


    void SocialLoginSendDataOnServer(){


        Log.e("SHAN",Str_Referral_Code);
        FormBody body = new FormBody.Builder()
                .add("user[email]",email)
                .add("user[name]", name)
                .add("user[location]", location)
                .add("user[username]", username)
                .add("user[avatar]", profilePicURL)
                .add("user[gender]", gender)
                .add("user[referal_code]", Str_Referral_Code)
                .add("user[identities_attributes][0][provider]", provider)
                .add("user[identities_attributes][0][uid]", uid)
                .add("user[identities_attributes][0][url]", profileURL)
                .add("user[identities_attributes][0][token]", token)
                .add("user[identities_attributes][0][expires_at]", expires_at)
                .build();

        Request request = new Request.Builder()
                .url(URLs.Native_SignIn_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try{
                    String obj=response.body().string();
                    Log.e("Obj",obj.toString());
                    JSONObject jsonObject=new JSONObject(obj);
                    if(jsonObject.has("user")){
                        JSONObject usersJsonObject = jsonObject.getJSONObject("user");
//                   // Get a Realm instance for this thread
                        Realm realm = Realm.getDefaultInstance();
                        // Persist your data in a transaction
                        realm.beginTransaction();
                        com.holygon.dishcuss.Model.User user = realm.createObject(com.holygon.dishcuss.Model.User.class); // Create managed objects directly
                        user.setId(usersJsonObject.getInt("id"));
                        user.setName(usersJsonObject.getString("name"));
                        user.setDob(usersJsonObject.getString("date_of_birth"));
                        user.setLocation(usersJsonObject.getString("location"));
                        user.setUsername(usersJsonObject.getString("username"));
                        user.setAvatar(usersJsonObject.getString("avatar"));
                        user.setEmail(usersJsonObject.getString("email"));
                        user.setGender(usersJsonObject.getString("gender"));
                        user.setProvider(usersJsonObject.getString("provider"));
                        user.setToken(usersJsonObject.getString("token"));
                        user.setReferral_code(usersJsonObject.getString("referral_code"));
                        realm.commitTransaction();
                        realm.close();

                        Notifications(usersJsonObject.getString("token"));

                        rawName= Constants.GetUserFacebookFriends(FindEatBuddiesLoginFirstActivity.this);
                        Intent intent = new Intent(FindEatBuddiesLoginFirstActivity.this,FindYourEatBuddiesActivity.class);
                        Constants.SetUserLoginStatus(FindEatBuddiesLoginFirstActivity.this,true);
                        Constants.skipLogin=false;
                        intent.putExtra("jsondata", rawName);
                        startActivity(intent);
                        finish();
                    }
                    else  if(jsonObject.has("message")){
                        message= jsonObject.getString("message");
                    }

                }catch (Exception e){
                    Log.i("Exception ::",""+ e.getMessage());
                } finally {
                }
            }
        });

        if(!message.isEmpty() && !message.equals("")){
            Crouton.makeText(FindEatBuddiesLoginFirstActivity.this,message, Style.ALERT).show();
        }
    }

    void AlertDialog(){
        final EditText edittext = new EditText(FindEatBuddiesLoginFirstActivity.this);
        alert.setTitle("Enter Referral Code if any ");

        alert.setView(edittext);

        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                Str_Referral_Code = edittext.getText().toString();
                SocialLoginSendDataOnServer();
                Constants.SetReferral(FindEatBuddiesLoginFirstActivity.this,true);
                dialog.cancel();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                SocialLoginSendDataOnServer();
                dialog.cancel();
            }
        });

        alert.show();
    }


    void DeleteAll(){

        Realm realm= Realm.getDefaultInstance();
        realm.beginTransaction();

        RealmResults<User> users = realm.where(com.holygon.dishcuss.Model.User.class).findAll();
        users.deleteAllFromRealm();

        RealmResults<Notifications> notificationsRealmResults = realm.where(Notifications.class).findAll();
        notificationsRealmResults.deleteAllFromRealm();

        RealmResults<Comment> comments = realm.where(Comment.class).findAll();
        comments.deleteAllFromRealm();

        RealmResults<KhabaHistoryModel> khabaHistoryModelRealmResults = realm.where(KhabaHistoryModel.class).findAll();
        khabaHistoryModelRealmResults.deleteAllFromRealm();

        RealmResults<LocalFeedCheckIn> localFeedCheckInRealmResults = realm.where(LocalFeedCheckIn.class).findAll();
        localFeedCheckInRealmResults.deleteAllFromRealm();

        RealmResults<LocalFeedReview> localFeedReviewRealmResults = realm.where(LocalFeedReview.class).findAll();
        localFeedReviewRealmResults.deleteAllFromRealm();

        RealmResults<LocalFeeds> localFeedsRealmResults = realm.where(LocalFeeds.class).findAll();
        localFeedsRealmResults.deleteAllFromRealm();

        RealmResults<Restaurant> restaurantRealmResults = realm.where(Restaurant.class).findAll();
        restaurantRealmResults.deleteAllFromRealm();

        RealmResults<UserProfile> userProfileRealmResults = realm.where(UserProfile.class).findAll();
        userProfileRealmResults.deleteAllFromRealm();

        realm.commitTransaction();
    }

    void Notifications(String token){

        Realm realm = Realm.getDefaultInstance();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Get_Old_Notifications)
                .addHeader("Authorization", "Token token="+token)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr=response.body().string();



                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject jsonObj = new JSONObject(objStr);
                            JSONArray jsonDataArray = jsonObj.getJSONArray("users");

                            for (int i = 0; i < jsonDataArray.length(); i++) {

                                JSONObject c = jsonDataArray.getJSONObject(i);

                                boolean isDataExist=false;

                                if(!isDataExist) {
                                    Realm realm=Realm.getDefaultInstance();
                                    realm.beginTransaction();
                                    Notifications notification = realm.createObject(Notifications.class);

                                    notification.setId(c.getInt("id"));
                                    notification.setBody(c.getString("body"));

                                    if (!c.isNull("notifier")) {
                                        JSONObject notifier = c.getJSONObject("notifier");
                                        notification.setUserID(notifier.getInt("id"));
                                        notification.setUsername(notifier.getString("username"));
                                        notification.setAvatarPic(notifier.getString("avatar"));
                                    }
                                    if (!c.isNull("redirect_to")) {
                                        JSONObject redirect = c.getJSONObject("redirect_to");
                                        notification.setRedirectID(redirect.getInt("id"));
                                        notification.setRedirectType(redirect.getString("typee"));
                                    }

                                    realm.commitTransaction();
                                }
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