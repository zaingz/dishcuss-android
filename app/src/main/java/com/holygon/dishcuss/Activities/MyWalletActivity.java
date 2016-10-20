package com.holygon.dishcuss.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;
import com.holygon.dishcuss.Utils.URLs;

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
 * Created by Naeem Ibrahim on 8/26/2016.
 */
public class MyWalletActivity extends AppCompatActivity {

    int userID;
    Realm realm;
    TextView total_credits_textview,used_credits_textview,credits_balance_textview;
    LinearLayout more_credits;
    int total_credits=0,used_credits=0,balance=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_wallet_activity);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        total_credits_textview=(TextView) findViewById(R.id.total_credits_textview);
        used_credits_textview=(TextView) findViewById(R.id.used_credits_textview);
        credits_balance_textview=(TextView) findViewById(R.id.credits_balance_textview);
        more_credits=(LinearLayout) findViewById(R.id.more_credits);



        TextView header=(TextView) findViewById(R.id.app_toolbar_name);
        header.setText("My Wallet");

        if(!Constants.skipLogin) {
            FetchMyFeedsData();
        }

        more_credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MyWalletActivity.this,GetFreeFoodActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    void FetchMyFeedsData(){
        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        // Persist your data in a transaction

        User user = realm.where(User.class).findFirst();
        Log.e("UserT",""+user.getToken());

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


                        total_credits=c.getInt("total_credits");
                        used_credits=c.getInt("used_credits");
                        balance=c.getInt("balance");

                    try
                    {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            total_credits_textview.setText("PKR "+total_credits);
                            used_credits_textview.setText("PKR "+used_credits);
                            credits_balance_textview.setText("PKR "+balance);

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
