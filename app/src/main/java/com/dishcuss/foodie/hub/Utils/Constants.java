package com.dishcuss.foodie.hub.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.dishcuss.foodie.hub.Models.LocalFeedCheckIn;
import com.dishcuss.foodie.hub.Models.LocalFeedReview;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by Naeem Ibrahim on 7/23/2016.
 */
public final class Constants{

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    public static GoogleApiClient mGoogleApiClient;
    public static final String SENDER_ID_GCM ="289789351313";
    // broadcast receiver intent filters
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // type of push messages
    public static final int PUSH_TYPE_CHATROOM = 1;
    public static final int PUSH_TYPE_USER = 2;

    public static boolean skipLogin;


    public static boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(activity,
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
            }
            return false;
        }
        return true;
    }


    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    public static void SetUserFacebookFriends(Context ctx, String friends){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("UserFacebookFriends",friends);
        editor.commit();
    }

    public static String GetUserFacebookFriends(Context ctx){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        String isLogin = sharedPreferences.getString("UserFacebookFriends", "");
        return isLogin;
    }



    public static void SetUserLoginStatus(Context ctx, boolean isLogin){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("UserLoginStatus",isLogin);
        editor.commit();
    }

    public static boolean GetUserLoginStatus(Context ctx){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        boolean isLogin = sharedPreferences.getBoolean("UserLoginStatus", false);
        return isLogin;
    }

    public static void SetReferral(Context ctx, boolean isAdded){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("UserReferral",isAdded);
        editor.commit();
    }

    public static boolean GetReferral(Context ctx){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        boolean isLogin = sharedPreferences.getBoolean("UserReferral", false);
        return isLogin;
    }


    public static void PicassoImageSrc(String URL, ImageView imageView, final Context context){
        if(URL!=null && !URL.equals("")){
            Picasso.with(context).load(URL)
                    .resize(60,60)
                    .onlyScaleDown()
                    .centerCrop()
                    .into(imageView);
        }
    }

    public static void PicassoImageBackground(String URL, final ImageView imageView, final Context context){
        if(URL!=null && !URL.equals("")){

            Target target=new Target(){

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    imageView.setBackground(new BitmapDrawable(context.getResources(), bitmap));
                }

                @Override
                public void onBitmapFailed(final Drawable errorDrawable) {
//                    Log.d("TAG", "FAILED");
                }

                @Override
                public void onPrepareLoad(final Drawable placeHolderDrawable) {
//                    Log.d("TAG", "Prepare Load");
                }
            };

            Picasso.with(context).load(URL)
//                    .resize(60,60)
//                    .onlyScaleDown()
//                    .centerCrop()
                    .into(target);
        }
    }

    public static void PicassoLargeImageBackground(String URL, final ImageView imageView, final Context context){
        if(URL!=null && !URL.equals("")){

            Target target=new Target(){

                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    imageView.setBackground(new BitmapDrawable(context.getResources(), bitmap));
                }

                @Override
                public void onBitmapFailed(final Drawable errorDrawable) {
//                    Log.d("TAG", "FAILED");
                }

                @Override
                public void onPrepareLoad(final Drawable placeHolderDrawable) {
//                    Log.d("TAG", "Prepare Load");
                }
            };

            Picasso.with(context).load(URL)
                    .resize(400,250)
                    .onlyScaleDown()
                    .into(target);
        }
    }

    public static void PicassoLargeImageBackgroundNewsFeed(String URL, final ImageView imageView, final ProgressBar pb, final Context context){
        if(URL!=null && !URL.equals("")){

            Target target=new Target(){
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    imageView.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                    imageView.setBackground(new BitmapDrawable(context.getResources(), bitmap));
                }

                @Override
                public void onBitmapFailed(final Drawable errorDrawable) {
//                    Log.d("TAG", "FAILED");
                }

                @Override
                public void onPrepareLoad(final Drawable placeHolderDrawable) {
//                    Log.d("TAG", "Prepare Load");
                }
            };

            Picasso.with(context).load(URL)
                    .placeholder(imageView.getDrawable())
//                    .resize(0,imageView.getHeight())
                    .into(target);
        }
    }

    public static void PicassoLargeImageBackgroundPhotoDetail(String URL, final ImageView imageView, final Context context){
        if(URL!=null && !URL.equals("")){

            Target target=new Target(){
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setBackground(new BitmapDrawable(context.getResources(), bitmap));
                }

                @Override
                public void onBitmapFailed(final Drawable errorDrawable) {
//                    Log.d("TAG", "FAILED");
                }

                @Override
                public void onPrepareLoad(final Drawable placeHolderDrawable) {
//                    Log.d("TAG", "Prepare Load");
                }
            };

            Picasso.with(context).load(URL)
                    .placeholder(imageView.getDrawable())
                    .into(target);
        }
    }


    public static Date GetDate(String date){
//        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
//        String segments[] = date.split("\\+");
//        String d = segments[0];
//        String d2 = segments[1];
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
      //  System.out.println(convertedDate);
        return convertedDate;
    }




    List<Object> Merge(RealmList<LocalFeedReview> A, RealmList<LocalFeedCheckIn> B) {

        List<Object> C=new ArrayList<>();
        int i, j, m, n,k;
        i = 0;
        j = 0;
        m = A.size();
        n = B.size();
        while (i < m && j < n) {
            if (GetDate(A.get(i).getUpdated_at()).compareTo(GetDate(B.get(j).getUpdated_at()))>0) {
                C.add(A.get(i));
                i++;
            } else {
                C.add(B.get(j));
                j++;
            }
        }

        if (i< m)
        {
            for (int p = i; p < m; p++) {
                C.add(A.get(p));
            }

        }
        else
        {
            for (int p = j; p < n; p++) {
                C.add(B.get(p));
            }
        }

        return C;
    }



    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }



    public static void clearApplicationData(Context context)
    {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    DeleteDataDir(new File(appDir,s));
                }
            }
        }
    }

    public static boolean DeleteDataDir(File dir)
    {
        if (dir != null && dir.isDirectory()) {
        String[] children = dir.list();
        for (int i = 0; i < children.length; i++) {
            boolean success = DeleteDataDir(new File(dir, children[i]));
            if (!success) {
                return false;
            }
        }
    }
        return dir.delete();
    }

}


