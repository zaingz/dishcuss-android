package com.holygon.dishcuss.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.holygon.dishcuss.Listners.OnLoadMoreListener;
import com.holygon.dishcuss.Model.Notifications;
import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;
import com.holygon.dishcuss.Utils.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.realm.Realm;
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
    private Paint p = new Paint();

    public ArrayList<Notifications> notificationsArrayList=new ArrayList<>();
    public ArrayList<Notifications> notificationsShowing=new ArrayList<>();
    public static  int newNotifications;
    ProgressBar progressBar;
    private UserAdapter notificationAdapter;


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
        notificationRecyclerView = (RecyclerView) findViewById(R.id.select_restaurant_recycler_view);
        notificationLayoutManager = new LinearLayoutManager(this);
        notificationRecyclerView.setLayoutManager(notificationLayoutManager);
        notificationRecyclerView.setNestedScrollingEnabled(false);
        GetFeedsData();

        notificationAdapter = new UserAdapter();
        notificationRecyclerView.setAdapter(notificationAdapter);


        notificationAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {


                notificationsShowing.add(null);
                notificationAdapter.notifyItemInserted(notificationsShowing.size() - 1);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        //Remove loading item
                        notificationsShowing.remove(notificationsShowing.size() - 1);
                        notificationAdapter.notifyItemRemoved(notificationsShowing.size());
                        //Load data
                        int index = notificationsShowing.size();
                        int end = index + 20;

                        if(notificationsArrayList.size()>end)
                        {
                            for (int i = index; i < end; i++)
                            {
                                notificationsShowing.add(notificationsArrayList.get(i));
                            }
                        }

                        else
                        {
                            for (int i = index; i < notificationsArrayList.size(); i++) {
                                notificationsShowing.add(notificationsArrayList.get(i));
                            }
                        }

                        notificationAdapter.notifyDataSetChanged();
                        notificationAdapter.setLoaded();
                    }
                }, 5000);
            }
        });

        newNotifications=0;
        Read();

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
                    //  notificationAdapter.remove(position);
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


    void Read(){
        // Get a Realm instance for this thread
        // Persist your data in a transaction
        realm.beginTransaction();
        final User user = realm.where(User.class).findFirst();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Get_Notification+"/seen")
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
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {

                }
            }
        });

    }

    void GetFeedsData(){
//    progressBar.setVisibility(View.VISIBLE);
        realm.beginTransaction();
        RealmResults<Notifications> notificationsRealmResults =realm.where(Notifications.class).findAll();

        for (int i=notificationsRealmResults.size()-1;i>=0;i--){
            notificationsArrayList.add(notificationsRealmResults.get(i));
        }


        if(notificationsArrayList.size()>20)
        {
            for(int i=0;i<20;i++){
                notificationsShowing.add(notificationsArrayList.get(i));
            }
        }
        else
        {
            for (int i = 0; i < notificationsArrayList.size(); i++) {
                notificationsShowing.add(notificationsArrayList.get(i));
            }
        }
        realm.commitTransaction();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        public TextView notifierName, body;
        public ImageView userAvatar;
        public RelativeLayout parentLayout;
        public UserViewHolder(View v) {
            super(v);
            notifierName = (TextView) v.findViewById(R.id.select_a_restaurant_name);
            body = (TextView) v.findViewById(R.id.select_a_restaurant_address);
            userAvatar =(ImageView) v.findViewById(R.id.select_a_restaurant_image_view);
            parentLayout =(RelativeLayout) v.findViewById(R.id.select_a_restaurant_name_parent);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }


    class UserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final int VIEW_TYPE_ITEM = 0;
        private final int VIEW_TYPE_LOADING = 1;

        private OnLoadMoreListener mOnLoadMoreListener;

        private boolean isLoading;
        private int visibleThreshold = 5;
        private int lastVisibleItem, totalItemCount;

        public UserAdapter() {
            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) notificationRecyclerView.getLayoutManager();
            notificationRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);

                    totalItemCount = linearLayoutManager.getItemCount();
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                    if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                        if (mOnLoadMoreListener != null) {
                            mOnLoadMoreListener.onLoadMore();
                        }
                        isLoading = true;
                    }
                }
            });
        }

        public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
            this.mOnLoadMoreListener = mOnLoadMoreListener;
        }

        @Override
        public int getItemViewType(int position) {
            return notificationsShowing.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == VIEW_TYPE_ITEM) {
                View view = LayoutInflater.from(NotificationActivity.this).inflate(R.layout.select_a_restaurant_row, parent, false);
                return new UserViewHolder(view);
            } else if (viewType == VIEW_TYPE_LOADING) {
                View view = LayoutInflater.from(NotificationActivity.this).inflate(R.layout.loading_progress_bar, parent, false);
                return new LoadingViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof UserViewHolder) {
//                Notifications notifications = notificationsShowing.get(position);
                UserViewHolder userViewHolder = (UserViewHolder) holder;
                userViewHolder.notifierName.setText(notificationsShowing.get(position).getUsername());
                userViewHolder.body.setText(notificationsShowing.get(position).getBody());

                Constants.PicassoImageBackground(notificationsShowing.get(position).getAvatarPic(),userViewHolder.userAvatar,NotificationActivity.this);

                userViewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(notificationsShowing.get(position).getRedirectType().toString().equals("Credit")){
                            Intent intent=new Intent(NotificationActivity.this, MyWalletActivity.class);
                            startActivity(intent);
                        }

                        if(notificationsShowing.get(position).getRedirectType().toString().equals("User")){
                            if(notificationsShowing.get(position).getRedirectID()!=0) {
                                Intent intent = new Intent(NotificationActivity.this, ProfilesDetailActivity.class);
                                intent.putExtra("UserID", notificationsShowing.get(position).getRedirectID());
                                startActivity(intent);
                            }
                        }

                        if(notificationsShowing.get(position).getRedirectType().toString().equals("Post")){
                            Intent intent = new Intent(NotificationActivity.this, NotificationClickPostDetail.class);
                            intent.putExtra("TypeID", notificationsShowing.get(position).getRedirectID());
                            intent.putExtra("Type", "Post");
                            startActivity(intent);
                        }

                        if(notificationsShowing.get(position).getRedirectType().toString().equals("Review"))
                        {
                            Intent intent = new Intent(NotificationActivity.this, NotificationClickPostDetail.class);
                            intent.putExtra("TypeID", notificationsShowing.get(position).getRedirectID());
                            intent.putExtra("Type", "Review");
                            startActivity(intent);
                        }
                        
                    }
                });

            } else if (holder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
            }

            holder.setIsRecyclable(false);
        }

        @Override
        public int getItemCount() {
            return notificationsShowing == null ? 0 : notificationsShowing.size();
        }

        public void setLoaded() {
            isLoading = false;
        }
    }
}
