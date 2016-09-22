package com.holygon.dishcuss.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

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
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ActivityEatBuddiesTest extends AppCompatActivity {

    CallbackManager callbackManager;
    String rawName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        facebookSDKInitialize();
        setContentView(R.layout.activity_eat_buddies_test);
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");

        if(isLoggedIn()) {
            loginButton.setVisibility(View.GONE);
            rawName= Constants.GetUserFacebookFriends(ActivityEatBuddiesTest.this);
            Intent intent = new Intent(ActivityEatBuddiesTest.this,FindYourEatBuddiesActivity.class);
            intent.putExtra("jsondata", rawName);
            startActivity(intent);
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
                GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
                        login_result.getAccessToken(),
                        //AccessToken.getCurrentAccessToken(),
                        "/me/friends",
                        null,
                        HttpMethod.GET,
                        new GraphRequest.Callback() {
                            public void onCompleted(GraphResponse response) {
                                Log.e("response: ", response + "");
                                Intent intent = new Intent(ActivityEatBuddiesTest.this,FindYourEatBuddiesActivity.class);
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
                            Intent intent = new Intent(ActivityEatBuddiesTest.this,FriendsList.class);
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
}