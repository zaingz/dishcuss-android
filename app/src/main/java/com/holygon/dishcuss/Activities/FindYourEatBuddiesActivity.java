package com.holygon.dishcuss.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.holygon.dishcuss.Adapters.FindYourEatBuddiesAdapter;
import com.holygon.dishcuss.Adapters.UserOffersAdapter;
import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.Model.UserOffersModel;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 8/25/2016.
 */
public class FindYourEatBuddiesActivity extends AppCompatActivity {

    RecyclerView offerList;
    private RecyclerView.LayoutManager gridLayout;
    ArrayList<UserOffersModel> itemsData = new ArrayList<>();
    int userID;
    Realm realm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_your_eat_buddies_activity);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView header=(TextView) findViewById(R.id.app_toolbar_name);
        header.setText("Find Your Eat Buddies");

        gridLayout = new LinearLayoutManager(this);
        offerList = (RecyclerView)findViewById(R.id.eat_buddies_recycler_view);


        offerList.setLayoutManager(gridLayout);
        offerList.setHasFixedSize(true);
        offerList.setNestedScrollingEnabled(false);

        for(int i=0;i<10;i++){

            UserOffersModel userOffersModel=new UserOffersModel();
            userOffersModel.setId(i);
            itemsData.add(userOffersModel);
        }

        FindYourEatBuddiesAdapter adapter = new FindYourEatBuddiesAdapter(itemsData,FindYourEatBuddiesActivity.this);
        offerList.setAdapter(adapter);

//        FetchMyFeedsData();
    }



    void FetchMyFeedsData(){
        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        // Persist your data in a transaction

        User user = realm.where(User.class).findFirst();
        Log.e("UserT",""+user.getToken());

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.USER_OFFER)
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
                    JSONArray jsonDataArray=jsonObj.getJSONArray("offers");

                    for (int i = 0; i < jsonDataArray.length(); i++) {

                        JSONObject c = jsonDataArray.getJSONObject(i);

                        UserOffersModel userOffersModel=new UserOffersModel();

                        userOffersModel.setId(c.getInt("id"));

                        if(!c.isNull("points")) {
                            userOffersModel.setPoints(c.getInt("points"));
                        }
                        userOffersModel.setDescription(c.getString("description"));
                        userOffersModel.setImg(c.getString("image"));

                        JSONObject r = c.getJSONObject("restaurant");

                        userOffersModel.setRestaurantID(r.getInt("id"));
                        userOffersModel.setRestaurantName(r.getString("name"));
                        userOffersModel.setRestaurantOpeningTime(r.getString("opening_time"));
                        userOffersModel.setRestaurantClosingTime(r.getString("closing_time"));
                        userOffersModel.setRestaurantLocation(r.getString("location"));

                        itemsData.add(userOffersModel);
                    }

                    try
                    {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UserOffersAdapter adapter = new UserOffersAdapter(itemsData,FindYourEatBuddiesActivity.this);
                            offerList.setAdapter(adapter);
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
