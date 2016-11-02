/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dishcuss.foodies.GCM;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.dishcuss.foodies.Activities.HomeActivity;
import com.dishcuss.foodies.Activities.MyWalletActivity;
import com.dishcuss.foodies.Activities.NotificationClickPostDetail;
import com.dishcuss.foodies.Activities.ProfilesDetailActivity;
import com.dishcuss.foodies.Helper.BusProvider;
import com.dishcuss.foodies.R;
import com.dishcuss.foodies.Utils.DishCussApplication;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    Handler handler;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]

    @Override
    public void onMessageReceived(String from, Bundle data) {

        String message = data.getString("message");
        String title = data.getString("title");
        String redirect_type=data.getString("redirect_type");
        String redirect_id=data.getString("redirect_id");
        Log.d(TAG, "DATA: " + data.toString());
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        Log.d(TAG, "App: " + title);
        Log.d(TAG, "redirect_type: " + redirect_type);

        handler = new Handler(Looper.getMainLooper());

        int id= Integer.parseInt(redirect_id);
        Log.d(TAG, "redirect_id: " +id);
        sendNotification(message,title,redirect_type,id);
        DishCussApplication app = (DishCussApplication) this.getApplication();
        if (!app.isAppIsInBackground(getApplicationContext()))
        {
//            NotificationActivity.newNotifications++;
//            Log.e("Notifications",""+NotificationActivity.newNotifications);
//            HomeFragment2.ShowNotifications();
           handler.post(new Runnable() {
               @Override
               public void run() {
                   BusProvider.getInstance().post("Notification");
               }
           });
        }
    }

    private void sendNotification(String message,String title,String type,int id) {

        Intent intent=new Intent(this,HomeActivity.class);




        if(type.equals("Credit")){
            intent=new Intent(this, MyWalletActivity.class);
        }

        if(type.equals("User")){
            if(id!=0) {
                intent = new Intent(this, ProfilesDetailActivity.class);
                intent.putExtra("UserID", id);
            }
        }

        if(type.equals("Post")){
            intent = new Intent(this, NotificationClickPostDetail.class);
            intent.putExtra("TypeID", id);
            intent.putExtra("Type", "Post");
        }

        if(type.equals("Review"))
        {
            intent = new Intent(this, NotificationClickPostDetail.class);
            intent.putExtra("TypeID", id);
            intent.putExtra("Type", "Review");
        }






        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
