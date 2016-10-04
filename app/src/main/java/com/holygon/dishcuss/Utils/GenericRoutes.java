package com.holygon.dishcuss.Utils;


import android.app.Activity;
import android.util.Log;
import com.holygon.dishcuss.Model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 8/15/2016.
 */
public class GenericRoutes {



    public static String message=null;
    public static OkHttpClient client;

    public static void Like(int id, String type, final Activity activity){

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

                final String objStr=response.body().string();


                /** check if activity still exist */
                if (activity == null) {
                    return;
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                    JSONObject jsonObj = new JSONObject(objStr);

                    if(jsonObj.has("message")){
                        message= jsonObj.getString("message");
                        Log.e("Message",""+message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                    }
                });
            }
        });


        realm.commitTransaction();
        //            return UnLike(id,type);
    }


    public static void UnLike(int id, String type){

        // Get a Realm instance for this thread
        Realm realm=Realm.getDefaultInstance();
        // Persist your data in a transaction
        realm.beginTransaction();
        final User user = realm.where(User.class).findFirst();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.UnLike_+id)
                .addHeader("Authorization", "Token token="+user.getToken())
                .build();

        realm.commitTransaction();
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
                        Log.e("Message",""+message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                }
            }
        });


    }


    public static void FollowRestaurant(int id){

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
                        Log.e("Message",""+message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {

                }
            }
        });
        realm.commitTransaction();
    }

    public static void UnFollowRestaurant(int id) {
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
            public void onResponse(Call call, Response response) throws IOException
            {
                String objStr=response.body().string();
                try {
                    JSONObject jsonObj = new JSONObject(objStr);

                    if(jsonObj.has("message")){
                        message= jsonObj.getString("message");
                        Log.e("Message",""+message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                }
            }
        });

        realm.commitTransaction();
    }


    public static boolean ReviewComment(String id, String title, String comment, String imageURL, String routeURL){

        if(SendCommentDataOnServer(id,title,comment,imageURL,routeURL).equals("Success")){
            return true;
        }else {
            return true;
        }

    }



    public static boolean CheckInComment(String id, String title, String comment, String imageURL, String routeURL){


        if(SendCommentDataOnServer(id,title,comment,imageURL,routeURL).equals("Success")){
            return true;
        }else {
            return true;
        }

    }




    static String SendCommentDataOnServer(String id, String title, String comment, String imageURL, String routeURL){

        client = new OkHttpClient();
        message=null;
        Realm realm = Realm.getDefaultInstance();
        // Persist your data in a transaction

        User user = realm.where(User.class).findFirst();

        FormBody body = new FormBody.Builder()
                .add("title",title)
                .add("comment",comment)
                .add("id", id)
                .add("image[]",imageURL)
                .build();

        Request request = new Request.Builder()
                .url(routeURL)
                .addHeader("Authorization", "Token token="+user.getToken())
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try{
                    String obj=response.body().string();
//                    Log.e("Obj",obj.toString());
                    JSONObject jsonObject=new JSONObject(obj);
                    if(jsonObject.has("comment")){
                        message="Success";
                    }
                    else {
                        message="UnSuccess";
                    }

                }catch (Exception e){
                    Log.i("Exception ::",""+ e.getMessage());
                } finally {
                }


            }
        });


        while (message==null){
//            Log.e("Loop","Working");
        }
       return message;
    }
}
