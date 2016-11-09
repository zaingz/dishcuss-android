package com.dishcuss.foodie.hub.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dishcuss.foodie.hub.Adapters.BookmarkedRestaurantAdapter;
import com.dishcuss.foodie.hub.Models.BookmarkData;
import com.dishcuss.foodie.hub.Models.User;

import com.dishcuss.foodie.hub.R;
import com.dishcuss.foodie.hub.Utils.URLs;

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
 * Created by Naeem Ibrahim on 9/5/2016.
 */
public class BookmarkActivity  extends AppCompatActivity {

    RecyclerView selectRestaurantRecyclerView;
    private RecyclerView.LayoutManager selectRestaurantLayoutManager;
    Realm realm;

    ArrayList<BookmarkData> restaurantRealmList=new ArrayList<>();


    ProgressBar progressBar;

    //*******************PROGRESS******************************
    private ProgressDialog mSpinner;

    private void showSpinner(String title) {
        mSpinner = new ProgressDialog(this);
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_a_restaurant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView headerName=(TextView)findViewById(R.id.app_toolbar_name);
        headerName.setText("Bookmarks");
        progressBar=(ProgressBar)findViewById(R.id.native_progress_bar);

        selectRestaurantRecyclerView = (RecyclerView) findViewById(R.id.select_restaurant_recycler_view);
        selectRestaurantLayoutManager = new LinearLayoutManager(this);
        selectRestaurantRecyclerView.setLayoutManager(selectRestaurantLayoutManager);
        selectRestaurantRecyclerView.setNestedScrollingEnabled(false);

        RestaurantData();
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

    void RestaurantData() {
        progressBar.setVisibility(View.VISIBLE);
        OkHttpClient client = new OkHttpClient();

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        // Persist your data in a transaction

        User user = realm.where(User.class).findFirst();

        Request request = new Request.Builder()
                .url(URLs.Bookmarked)
                .addHeader("Authorization", "Token token="+user.getToken())
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject jsonObj = new JSONObject(objStr);
                            JSONArray jsonDataArray = jsonObj.getJSONArray("restaurant");


                            for (int i = 0; i < jsonDataArray.length(); i++) {

                                JSONObject restaurantObj = jsonDataArray.getJSONObject(i);
                                BookmarkData bookmarkData=new BookmarkData();
                                bookmarkData.setId(restaurantObj.getInt("id"));
                                bookmarkData.setName(restaurantObj.getString("name"));
                                bookmarkData.setLocation(restaurantObj.getString("location"));
                                bookmarkData.setCover_image(restaurantObj.getString("cover_image"));

                                restaurantRealmList.add(bookmarkData);
                            }

                            BookmarkedRestaurantAdapter adapter = new BookmarkedRestaurantAdapter(restaurantRealmList,BookmarkActivity.this);
                            selectRestaurantRecyclerView.setAdapter(adapter);
                            progressBar.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }
}
