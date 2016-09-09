package com.holygon.dishcuss.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.holygon.dishcuss.Activities.ActivityEatBuddiesTest;
import com.holygon.dishcuss.Activities.FindYourEatBuddiesActivity;
import com.holygon.dishcuss.Activities.GetFreeFoodActivity;
import com.holygon.dishcuss.Activities.KhabaHistoryActivity;
import com.holygon.dishcuss.Activities.LoginActivity;
import com.holygon.dishcuss.Activities.MyWalletActivity;
import com.holygon.dishcuss.Activities.UpdateProfileActivity;
import com.holygon.dishcuss.Activities.UserOffersActivity;
import com.holygon.dishcuss.Adapters.FindYourEatBuddiesAdapter;
import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;
import com.holygon.dishcuss.Utils.URLs;
import com.twitter.sdk.android.Twitter;

import java.io.IOException;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
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
    OkHttpClient client;
    Realm realm;
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

        GoogleLogin();

        TextView header=(TextView) rootView.findViewById(R.id.app_toolbar_name);
        header.setText("     My Account");

        get_more_food=(LinearLayout)rootView.findViewById(R.id.get_more_food);
        signOut_layout=(LinearLayout)rootView.findViewById(R.id.sign_out_user_layout);
        profileSettings=(LinearLayout)rootView.findViewById(R.id.update_user_profile);
        user_offers=(LinearLayout)rootView.findViewById(R.id.offers_layout);
        find_your_buddy_layout=(LinearLayout)rootView.findViewById(R.id.find_your_buddy_layout);
        khaba_history_layout=(LinearLayout)rootView.findViewById(R.id.khaba_history_layout);
        my_wallet_layout=(LinearLayout)rootView.findViewById(R.id.my_wallet_layout);



        find_your_buddy_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(getActivity(), FindYourEatBuddiesActivity.class);
                Intent intent=new Intent(getActivity(), ActivityEatBuddiesTest.class);
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
                Intent intent=new Intent(getActivity(), UpdateProfileActivity.class);
                startActivity(intent);
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
                User user = realm.where(User.class).findFirst();
                SignOutRequest(user.getToken(),user.getProvider());
            }
        });

        return rootView;
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

    public void SignOutRequest(String token,final String provider){

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

                    realm=Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.deleteAll();
                    realm.commitTransaction();
                    Constants.SetUserLoginStatus(getActivity(),false);
                    Intent intent=new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    getActivity().finish();
                }
            }
        });
    }
}