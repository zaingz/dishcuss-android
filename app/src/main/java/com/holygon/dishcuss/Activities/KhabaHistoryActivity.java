package com.holygon.dishcuss.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.holygon.dishcuss.Adapters.KhabaHistoryAdapter;
import com.holygon.dishcuss.Model.KhabaHistoryModel;
import com.holygon.dishcuss.Model.User;
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
public class KhabaHistoryActivity extends AppCompatActivity {

    RecyclerView offerList;
    private RecyclerView.LayoutManager gridLayout;
    ArrayList<KhabaHistoryModel> itemsData = new ArrayList<>();
    int userID;
    Realm realm;
    ProgressBar progressBar;
    LinearLayout userDataLayout,sign_up_first;
    Button Sign_Up_Click;
    TextView More_Food;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.khaba_history_activity);


        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView header=(TextView) findViewById(R.id.app_toolbar_name);
        progressBar=(ProgressBar)findViewById(R.id.native_progress_bar);
        header.setText("Khaba History");

        gridLayout = new LinearLayoutManager(this);
        offerList = (RecyclerView)findViewById(R.id.khaba_history_recycler_view);
        sign_up_first = (LinearLayout) findViewById(R.id.sign_up_first);
        userDataLayout = (LinearLayout) findViewById(R.id.user_data_layout);
        More_Food=(TextView)findViewById(R.id.More_Food);



        offerList.setLayoutManager(gridLayout);
        offerList.setHasFixedSize(true);
        offerList.setNestedScrollingEnabled(false);
        if(!Constants.skipLogin)
        {
            sign_up_first.setVisibility(View.GONE);
            userDataLayout.setVisibility(View.VISIBLE);
            FetchMyFeedsData();
        }
        else
        {
            sign_up_first.setVisibility(View.VISIBLE);
            userDataLayout.setVisibility(View.GONE);
        }

        Sign_Up_Click=(Button)findViewById(R.id.Sign_Up_Click);

        Sign_Up_Click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(KhabaHistoryActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Constants.skipLogin=false;
                startActivity(intent);
                startActivity(intent);
                finish();
            }
        });
    }



    void FetchMyFeedsData(){
        progressBar.setVisibility(View.VISIBLE);
        More_Food.setVisibility(View.GONE);
        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        // Persist your data in a transaction

        User user = realm.where(User.class).findFirst();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.KHABA_HISTORY_)
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
                Log.e("KhabaHis",""+objStr);
                try {
                    JSONObject jsonObj = new JSONObject(objStr);
                    JSONArray jsonDataArray=jsonObj.getJSONArray("khaba_history");

                    for (int i = 0; i < jsonDataArray.length(); i++) {

                        JSONObject c = jsonDataArray.getJSONObject(i);

                        KhabaHistoryModel khabaHistoryModel=new KhabaHistoryModel();

                        khabaHistoryModel.setId(c.getInt("id"));

                        if(!c.isNull("price")) {
                            khabaHistoryModel.setPrice(c.getInt("price"));
                        }
                        khabaHistoryModel.setCredit_time(c.getString("credit_time"));
                        if(!c.isNull("offer_image")) {
                            khabaHistoryModel.setRestaurantImage(c.getString("offer_image"));
                        }
                        JSONObject r = c.getJSONObject("restaurant");

                        khabaHistoryModel.setRestaurantID(r.getInt("id"));
                        khabaHistoryModel.setRestaurantName(r.getString("name"));
                        khabaHistoryModel.setRestaurantOpeningTime(r.getString("opening_time"));
                        khabaHistoryModel.setRestaurantClosingTime(r.getString("closing_time"));
                        khabaHistoryModel.setRestaurantLocation(r.getString("location"));

                        itemsData.add(khabaHistoryModel);
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
                            KhabaHistoryAdapter adapter = new KhabaHistoryAdapter(itemsData,KhabaHistoryActivity.this);
                            offerList.setAdapter(adapter);
                            progressBar.setVisibility(View.GONE);
                            More_Food.setVisibility(View.VISIBLE);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally{

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
