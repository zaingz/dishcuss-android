package com.holygon.dishcuss.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.holygon.dishcuss.Adapters.FindYourEatBuddiesAdapter;
import com.holygon.dishcuss.Adapters.HomePeopleAroundAdapter;
import com.holygon.dishcuss.Adapters.UserOffersAdapter;
import com.holygon.dishcuss.Model.MyFeeds;
import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.Model.UserOffersModel;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;
import com.holygon.dishcuss.Utils.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 8/25/2016.
 */
public class FindYourEatBuddiesActivity extends AppCompatActivity {

    RecyclerView offerList;
    private RecyclerView.LayoutManager gridLayout;
    ArrayList<MyFeeds> itemsData = new ArrayList<>();
    int userID;
    Realm realm;
    ProgressBar progressBar;
    ArrayList<String> friendsID = new ArrayList<String>();
    OkHttpClient client;
    FindYourEatBuddiesAdapter findYourEatBuddiesAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_your_eat_buddies_activity);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView header=(TextView) findViewById(R.id.app_toolbar_name);
        progressBar=(ProgressBar)findViewById(R.id.native_progress_bar);
        header.setText("Find Your Eat Buddies");

        gridLayout = new LinearLayoutManager(this);
        offerList = (RecyclerView)findViewById(R.id.eat_buddies_recycler_view);

        offerList.setLayoutManager(gridLayout);
        offerList.setHasFixedSize(true);
        offerList.setNestedScrollingEnabled(false);

        client = new OkHttpClient();

        Intent intent = getIntent();
        String jsondata = intent.getStringExtra("jsondata");
        Log.e("jsondata",""+jsondata);
        JSONArray friendslist;
        ArrayList<String> friends = new ArrayList<String>();
        try {
            friendslist = new JSONArray(jsondata);
            for (int l=0; l < friendslist.length(); l++) {
                friends.add(friendslist.getJSONObject(l).getString("name"));
                friendsID.add(friendslist.getJSONObject(l).getString("id"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(!Constants.skipLogin) {
            SendDataOnServer();
        }

    }

    void SendDataOnServer(){
        progressBar.setVisibility(View.VISIBLE);
        Realm realm;
        User user;
        realm=Realm.getDefaultInstance();
        user= realm.where(User.class).findFirst();
        FormBody body = new FormBody.Builder()
//                .add("user", String.valueOf(jsonArray))
                .add("user", String.valueOf(friendsID))
                .build();

        Request request = new Request.Builder()
                .url(URLs.EatBuddies)
                .addHeader("Authorization", "Token token="+user.getToken())
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr=response.body().string();
                Log.e("ObjStr",""+objStr);


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject jsonObj = new JSONObject(objStr);
                            JSONArray jsonDataArray=jsonObj.getJSONArray("search");

                            for (int i = 0; i < jsonDataArray.length(); i++) {

                                JSONObject c = jsonDataArray.getJSONObject(i);

                                MyFeeds myFeeds=new MyFeeds();

                                myFeeds.setId(c.getInt("id"));
                                myFeeds.setName(c.getString("name"));
                                myFeeds.setUsername(c.getString("username"));
                                myFeeds.setAvatarPic(c.getString("avatar"));
                                myFeeds.setLocation(c.getString("location"));
                                myFeeds.setFollowing(c.getBoolean("follows"));
                                myFeeds.setFollowers(c.getInt("followers"));

                                itemsData.add(myFeeds);
                            }

                            findYourEatBuddiesAdapter = new FindYourEatBuddiesAdapter(itemsData,FindYourEatBuddiesActivity.this);
                            offerList.setAdapter(findYourEatBuddiesAdapter);
                            progressBar.setVisibility(View.GONE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

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
}
