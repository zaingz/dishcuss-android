package com.holygon.dishcuss.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.URLs;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FriendsList extends AppCompatActivity {

    ArrayList<String> friendsID = new ArrayList<String>();
    OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);
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
        SendDataOnServer();
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.activity_listview, friends); // simple textview for list item
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }


    void SendDataOnServer(){

        Realm realm;
        User user;
        realm=Realm.getDefaultInstance();
        user= realm.where(User.class).findFirst();

//        JSONArray jsonArray = new JSONArray();
//        for (int i = 0; i < friendsID.size(); i++) {
//            String ID = friendsID.get(i);
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("id", ID);
//                jsonArray.put(jsonObject);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }

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
                final String obj=response.body().string();
                Log.e("Obj",obj.toString());
                try
                {

                }
                catch (Exception e){

                }

            }
        });

    }
}
