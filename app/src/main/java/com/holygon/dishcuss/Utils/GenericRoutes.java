package com.holygon.dishcuss.Utils;

import android.widget.Toast;

import com.holygon.dishcuss.Model.User;

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
 * Created by Naeem Ibrahim on 8/15/2016.
 */
public class GenericRoutes {

    public static String message=null;


    public static boolean Like(int id,String type){
        GenericRoutes.message=null;
        // Get a Realm instance for this thread
        Realm realm=Realm.getDefaultInstance();
        // Persist your data in a transaction
        realm.beginTransaction();
        final User user = realm.where(User.class).findFirst();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Like_+type+"/"+id)
                .addHeader("Authorization", "Token token="+user.getToken())
                .build();

        realm.close();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String objStr=response.body().string();
                try {
                    JSONObject jsonObj = new JSONObject(objStr);

                    if(jsonObj.has("message")){
                        message= jsonObj.getString("message");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {

                }
            }
        });

        while (message==null){
//            Log.e("Loop","Working");
        }
        realm.commitTransaction();
        if (message.equals("Successfully liked!")) {
            return true;
        }
        else
        {
            return DisLike(id,type);
        }
    }


    public static boolean DisLike(int id,String type){
        GenericRoutes.message=null;
        // Get a Realm instance for this thread
        Realm realm=Realm.getDefaultInstance();
        // Persist your data in a transaction
        realm.beginTransaction();
        final User user = realm.where(User.class).findFirst();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.DisLike_+type+"/"+id)
                .addHeader("Authorization", "Token token="+user.getToken())
                .build();

        realm.close();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String objStr=response.body().string();
                try {
                    JSONObject jsonObj = new JSONObject(objStr);

                    if(jsonObj.has("message")){
                        message= jsonObj.getString("message");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                }
            }
        });

        while (message==null){
//            Log.e("Loop","Working");
        }
        realm.commitTransaction();
        if (message.equals("Successfully disliked!"))
        {
            return true;
        }
        else if (message.equals("You have liked that item!")){
            return false;
        }
        else
        {
            return false;
        }
    }





    public static boolean FollowRestaurant(int id){
        GenericRoutes.message=null;
        // Get a Realm instance for this thread
        Realm realm=Realm.getDefaultInstance();
        // Persist your data in a transaction
        realm.beginTransaction();
        final User user = realm.where(User.class).findFirst();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Follow_Restaurant+id)
                .addHeader("Authorization", "Token token="+user.getToken())
                .build();

        realm.close();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String objStr=response.body().string();
                try {
                    JSONObject jsonObj = new JSONObject(objStr);

                    if(jsonObj.has("message")){
                        message= jsonObj.getString("message");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {

                }
            }
        });

        while (message==null){
//            Log.e("Loop","Working");
        }
        realm.commitTransaction();
        if (message.equals("Successfully followed!")) {
            return true;
        }
        else
        {
           return UnFollowRestaurant(id);
        }
    }

    public static boolean UnFollowRestaurant(int id) {
        GenericRoutes.message=null;
        // Get a Realm instance for this thread
        Realm realm=Realm.getDefaultInstance();
        // Persist your data in a transaction
        realm.beginTransaction();
        final User user = realm.where(User.class).findFirst();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.UnFollow_Restaurant+id)
                .addHeader("Authorization", "Token token="+user.getToken())
                .build();

        realm.close();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String objStr=response.body().string();
                try {
                    JSONObject jsonObj = new JSONObject(objStr);

                    if(jsonObj.has("message")){
                        message= jsonObj.getString("message");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                }
            }
        });

        while (message==null){
//            Log.e("Loop","Working");
        }
        realm.commitTransaction();
        if (message.equals("Successfully unfollowed!"))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
