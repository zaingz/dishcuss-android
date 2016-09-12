package com.holygon.dishcuss.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.holygon.dishcuss.Adapters.HomeLocalFeedsAdapter;
import com.holygon.dishcuss.Adapters.NotificationAdapter;
import com.holygon.dishcuss.Fragments.HomeFragment2;
import com.holygon.dishcuss.Helper.NotificationTouchHelper;
import com.holygon.dishcuss.Model.LocalFeeds;
import com.holygon.dishcuss.Model.Notifications;
import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.BadgeView;
import com.holygon.dishcuss.Utils.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 8/18/2016.
 */
public class NotificationActivity extends AppCompatActivity {

    RecyclerView notificationRecyclerView;
    private RecyclerView.LayoutManager notificationLayoutManager;
    Realm realm;
    String message=null;

    NotificationAdapter notificationAdapter;
    private Paint p = new Paint();

    public ArrayList<Notifications> notificationsArrayList=new ArrayList<>();
    public static  int newNotifications;
    ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_a_restaurant);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        realm=Realm.getDefaultInstance();

        TextView headerName=(TextView)findViewById(R.id.app_toolbar_name);
        progressBar=(ProgressBar)findViewById(R.id.native_progress_bar);
        headerName.setText("Notifications");
        progressBar.setVisibility(View.VISIBLE);

        notificationRecyclerView = (RecyclerView) findViewById(R.id.select_restaurant_recycler_view);
        notificationLayoutManager = new LinearLayoutManager(this);
        notificationRecyclerView.setLayoutManager(notificationLayoutManager);
        notificationRecyclerView.setNestedScrollingEnabled(false);
        GetFeedsData();
        notificationAdapter = new NotificationAdapter(notificationsArrayList,NotificationActivity.this);
        notificationRecyclerView.setAdapter(notificationAdapter);
        progressBar.setVisibility(View.GONE);
//        ItemTouchHelper.Callback callback = new NotificationTouchHelper(notificationAdapter);
//        ItemTouchHelper helper = new ItemTouchHelper(callback);
//        helper.attachToRecyclerView(notificationRecyclerView);
     //   initSwipe();

        if(Read()){
//            NotificationActivity.notificationsArrayList=new ArrayList<Notifications>();
            newNotifications=0;
        }
//        NotificationActivity.notificationsArrayList=new ArrayList<Notifications>();
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


    private void initSwipe(){

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.RIGHT)
                {
                    notificationAdapter.remove(position);
                }
//                else if (direction == ItemTouchHelper.LEFT)
//                {
//                    notificationAdapter.notifyDataSetChanged();
//                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if(dX > 0){
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_tiick);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {
//                        p.setColor(Color.parseColor("#D32F2F"));
//                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
//                        c.drawRect(background,p);
//                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.icon_cross_notification);
//                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
//                        c.drawBitmap(icon,null,icon_dest,p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(notificationRecyclerView);
    }


    boolean Read(){

        // Get a Realm instance for this thread

        // Persist your data in a transaction
        realm.beginTransaction();
        final User user = realm.where(User.class).findFirst();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Get_Notification+"/seen")
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
        }
        realm.commitTransaction();
        if (message.equals("Notification Seen")) {
            return true;
        }
        else
        {
            return false;
        }
    }

    void GetFeedsData(){

        realm.beginTransaction();
        RealmResults<Notifications> notificationsRealmResults =realm.where(Notifications.class).findAll();

        for (int i=notificationsRealmResults.size()-1;i>=0;i--){
            notificationsArrayList.add(notificationsRealmResults.get(i));
        }


        realm.commitTransaction();
    }
}
