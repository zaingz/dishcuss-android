package com.holygon.dishcuss.Services;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.holygon.dishcuss.Activities.LoginActivity;
import com.holygon.dishcuss.Helper.BusProvider;
import com.holygon.dishcuss.Utils.Constants;

/**
 * Created by Naeem Ibrahim on 10/24/2016.
 */

public class MyService extends Service {

    public MyService() {
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
//        Toast.makeText(getBaseContext(),"OnTaskRemoved",Toast.LENGTH_LONG).show();
        if(!Constants.GetUserLoginStatus(getBaseContext())){
             Constants.clearApplicationData(getBaseContext());
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public int onStartCommand(Intent intent, int flags, int startId){
        return Service.START_STICKY;
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        BusProvider.getInstance().register(this);
    }

    @Override
    public void onDestroy() {
        BusProvider.getInstance().unregister(this);
        super.onDestroy();

    }


}
