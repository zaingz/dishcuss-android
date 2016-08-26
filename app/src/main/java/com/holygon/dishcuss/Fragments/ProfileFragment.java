package com.holygon.dishcuss.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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
public class ProfileFragment extends Fragment {


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

        TextView header=(TextView) rootView.findViewById(R.id.app_toolbar_name);
        header.setText("My Account");


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
                Intent intent=new Intent(getActivity(), FindYourEatBuddiesActivity.class);
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
                SignOutRequest(user.getToken());
            }
        });

        return rootView;
    }

    public void SignOutRequest(String token){

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
                    realm=Realm.getDefaultInstance();
                    realm.beginTransaction();
                    realm.deleteAll();
                    realm.commitTransaction();
                    Constants.SetUserLoginStatus(getActivity(),false);
                    Intent intent=new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }
        });
    }
}