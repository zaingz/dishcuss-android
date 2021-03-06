package com.dishcuss.foodie.hub.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.dishcuss.foodie.hub.Activities.FindEatBuddiesLoginFirstActivity;
import com.dishcuss.foodie.hub.Activities.GetFreeFoodActivity;
import com.dishcuss.foodie.hub.Activities.KhabaHistoryActivity;
import com.dishcuss.foodie.hub.Activities.LoginActivity;
import com.dishcuss.foodie.hub.Activities.MyWalletActivity;
import com.dishcuss.foodie.hub.Activities.SplashActivity;
import com.dishcuss.foodie.hub.Activities.UpdateProfileActivity;
import com.dishcuss.foodie.hub.Activities.UserOffersActivity;
import com.dishcuss.foodie.hub.GCM.QuickstartPreferences;
import com.dishcuss.foodie.hub.Models.User;
import com.dishcuss.foodie.hub.R;
import com.dishcuss.foodie.hub.Utils.Constants;
import com.dishcuss.foodie.hub.Utils.URLs;
import com.twitter.sdk.android.Twitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 7/21/2016.
 */
public class ProfileFragment extends Fragment{


    AppCompatActivity activity;
    LinearLayout get_more_food;
    LinearLayout profileSettings;
    LinearLayout find_your_buddy_layout;
    LinearLayout my_wallet_layout;
    LinearLayout khaba_history_layout;
    LinearLayout signOut_layout;
    LinearLayout user_offers;
    TextView login_first,my_wallet_TextView;
    OkHttpClient client;
    Realm realm;
    int balance;
    private GoogleApiClient mGoogleApiClient;

    public ProfileFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_fragment, container, false);
        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        realm=Realm.getDefaultInstance();
        client = new OkHttpClient();

        if(!Constants.skipLogin) {
            GoogleLogin();
        }

        TextView header=(TextView) rootView.findViewById(R.id.app_toolbar_name);
        header.setText("     My Account");

        get_more_food=(LinearLayout)rootView.findViewById(R.id.get_more_food);
        signOut_layout=(LinearLayout)rootView.findViewById(R.id.sign_out_user_layout);
        profileSettings=(LinearLayout)rootView.findViewById(R.id.update_user_profile);
        user_offers=(LinearLayout)rootView.findViewById(R.id.offers_layout);
        find_your_buddy_layout=(LinearLayout)rootView.findViewById(R.id.find_your_buddy_layout);
        khaba_history_layout=(LinearLayout)rootView.findViewById(R.id.khaba_history_layout);
        login_first=(TextView) rootView.findViewById(R.id.login_first);
        my_wallet_TextView=(TextView) rootView.findViewById(R.id.my_wallet_TextView);
        my_wallet_layout=(LinearLayout)rootView.findViewById(R.id.my_wallet_layout);


        if(Constants.skipLogin){
            login_first.setText("Login or SignUp");
            profileSettings.setVisibility(View.GONE);
            my_wallet_TextView.setText("My Wallet");
        }
        else
        {
            FetchMyFeedsData();
        }


        find_your_buddy_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(getActivity(), FindYourEatBuddiesActivity.class);
                Intent intent=new Intent(getActivity(), FindEatBuddiesLoginFirstActivity.class);
                startActivity(intent);
            }
        });





        my_wallet_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), MyWalletActivity.class);
                startActivity(intent);
            }
        });
        khaba_history_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), KhabaHistoryActivity.class);
                startActivity(intent);
            }
        });

        profileSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Constants.skipLogin) {
                    Intent intent = new Intent(getActivity(), UpdateProfileActivity.class);
                    startActivity(intent);
                }
            }
        });

        user_offers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), UserOffersActivity.class);
                startActivity(intent);
            }
        });

        get_more_food.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), GetFreeFoodActivity.class);
                startActivity(intent);
            }
        });


        signOut_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isLoggedIn()){
                    LoginManager.getInstance().logOut();
                }

                if(!Constants.skipLogin) {
                    LoginManager.getInstance().logOut();
                    realm = Realm.getDefaultInstance();
                    User user = realm.where(User.class).findFirst();
                    SignOutRequest(user.getToken(), user.getProvider());
                }
                else
                {
                    Intent intent=new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Constants.skipLogin=false;
                    Constants.deleteCache(getActivity());
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        return rootView;
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("Resumed","Called");

        if(Constants.skipLogin){
            login_first.setText("Login or SignUp");
            profileSettings.setVisibility(View.GONE);
            my_wallet_TextView.setText("My Wallet");
        }
        else
        {
            login_first.setText("Signout");
            profileSettings.setVisibility(View.VISIBLE);
            FetchMyFeedsData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.stopAutoManage(getActivity());
            mGoogleApiClient.disconnect();
        }
    }


    private void GoogleLogin(){


        // [START configure_signin]
        // Configure sign-in to request the loginUser's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(),0, (GoogleApiClient.OnConnectionFailedListener) getActivity())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
//
//        SignInButton signInButton = (SignInButton) findViewById(R.id.btn_sign_in_plus);
//        signInButton.setSize(SignInButton.SIZE_STANDARD);
//        signInButton.setScopes(gso.getScopeArray());
    }
    //*******************PROGRESS******************************
    private ProgressDialog mSpinner;

    private void showSpinner(String title) {
        mSpinner = new ProgressDialog(getActivity());
        mSpinner.setTitle(title);
        mSpinner.show();
        mSpinner.setCancelable(false);
        mSpinner.setCanceledOnTouchOutside(false);
    }

    private void DismissSpinner(){
        if(mSpinner!=null){
            mSpinner.dismiss();
        }
    }

//*******************PROGRESS******************************

    public void SignOutRequest(String token,final String provider){
        showSpinner("Please wait...");
        Constants.SetUserLoginStatus(getActivity(),false);
        SplashActivity.isFeatureRestaurantsLoaded=false;
        Request request = new Request.Builder()
                .addHeader("Authorization", "Token token=" + token)
                .url(URLs.SIGN_OUT)
                .delete()
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){

                    LoginManager.getInstance().logOut();

                    if(provider.equals("Facebook")) {
                        LoginManager.getInstance().logOut();
                    }

                    if(provider.equals("Twitter")) {
                        CookieSyncManager.createInstance(getActivity());
                        CookieManager cookieManager = CookieManager.getInstance();
                        cookieManager.removeSessionCookie();
                        Twitter.getSessionManager().clearActiveSession();
                        Twitter.logOut();
                    }


                    if(provider.equals("Google")) {
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                                new ResultCallback<Status>() {
                                    @Override
                                    public void onResult(Status status) {
                                    }
                                });
                    }

                    SharedPreferences sharedPreferences;
                    sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    sharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER,false).apply();
                    Intent intent=new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Constants.deleteCache(getActivity());
                    DismissSpinner();
                    startActivity(intent);
                    getActivity().finish();

                }
            }
        });
    }


    void FetchMyFeedsData(){
        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        // Persist your data in a transaction

        User user = realm.where(User.class).findFirst();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.CREDITS)
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
                Log.e("ObjStr",""+objStr);
                try {
                    JSONObject jsonObj = new JSONObject(objStr);

                    JSONObject c=jsonObj.getJSONObject("credit");

                    balance=c.getInt("balance");

                    if(getActivity()==null){
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            my_wallet_TextView.setText("My Wallet("+balance+" PKR)");

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally
                {

                }
            }
        });
        realm.close();


    }
}