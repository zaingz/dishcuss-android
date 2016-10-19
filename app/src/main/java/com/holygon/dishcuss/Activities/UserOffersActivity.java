package com.holygon.dishcuss.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.holygon.dishcuss.Adapters.UserOffersAdapter;
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
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 8/25/2016.
 */
public class UserOffersActivity extends AppCompatActivity {

    RecyclerView offerList;
    private GridLayoutManager gridLayout;
    ArrayList<UserOffersModel> itemsData = new ArrayList<>();
    ProgressBar progressBar;
    int userID;
    Realm realm;

    LinearLayout userDataLayout,sign_up_first;
    Button Sign_Up_Click;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_offer_activity);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView header=(TextView) findViewById(R.id.app_toolbar_name);
        progressBar=(ProgressBar)findViewById(R.id.native_progress_bar);
        header.setText("Offers");

        sign_up_first = (LinearLayout) findViewById(R.id.sign_up_first);
        userDataLayout = (LinearLayout) findViewById(R.id.user_data_layout);
        gridLayout = new GridLayoutManager(UserOffersActivity.this,2);
        offerList = (RecyclerView)findViewById(R.id.user_offer_list);

        offerList.setLayoutManager(gridLayout);
        offerList.setHasFixedSize(true);
//        offerList.setNestedScrollingEnabled(false);

        if(!Constants.skipLogin){
            sign_up_first.setVisibility(View.GONE);
            userDataLayout.setVisibility(View.VISIBLE);
            FetchMyFeedsData();
        }else {
            sign_up_first.setVisibility(View.VISIBLE);
            userDataLayout.setVisibility(View.GONE);
        }

        Sign_Up_Click=(Button)findViewById(R.id.Sign_Up_Click);

        Sign_Up_Click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserOffersActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Constants.skipLogin=false;
                startActivity(intent);
                finish();
            }
        });
    }




    void FetchMyFeedsData(){
        progressBar.setVisibility(View.VISIBLE);
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
                        userOffersModel.setImg(c.getString("offer_image"));

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
                            UserOffersAdapter adapter = new UserOffersAdapter(itemsData,UserOffersActivity.this);
                            offerList.setAdapter(adapter);
                            progressBar.setVisibility(View.GONE);
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
