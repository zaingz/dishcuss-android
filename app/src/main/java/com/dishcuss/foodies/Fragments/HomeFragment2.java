package com.dishcuss.foodies.Fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dishcuss.foodies.Activities.BookmarkActivity;
import com.dishcuss.foodies.Activities.LoginActivity;
import com.dishcuss.foodies.Activities.NotificationActivity;
import com.dishcuss.foodies.Activities.PunditSelectionActivity;
import com.dishcuss.foodies.Activities.SearchUserAndRestaurantActivity;
import com.dishcuss.foodies.Activities.SplashActivity;
import com.dishcuss.foodies.Adapters.HomeLocalFeedsAdapter;
import com.dishcuss.foodies.Adapters.HomeMyFeedsAdapter;
import com.dishcuss.foodies.Adapters.HomePeopleAroundAdapter;
import com.dishcuss.foodies.Helper.BusProvider;
import com.dishcuss.foodies.Helper.EndlessRecyclerOnScrollListener;
import com.dishcuss.foodies.Helper.LocalFeedsRecyclerOnScrollListener;
import com.dishcuss.foodies.Model.Comment;
import com.dishcuss.foodies.Model.FeaturedRestaurant;
import com.dishcuss.foodies.Model.LocalFeedCheckIn;
import com.dishcuss.foodies.Model.LocalFeedReview;
import com.dishcuss.foodies.Model.LocalFeeds;
import com.dishcuss.foodies.Model.MyFeeds;
import com.dishcuss.foodies.Model.Notifications;
import com.dishcuss.foodies.Model.PhotoModel;
import com.dishcuss.foodies.Model.Reply;
import com.dishcuss.foodies.Model.User;
import com.dishcuss.foodies.R;
import com.dishcuss.foodies.Utils.BadgeView;
import com.dishcuss.foodies.Utils.Constants;
import com.dishcuss.foodies.Utils.URLs;
import com.squareup.otto.Subscribe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 8/29/2016.
 */
public class HomeFragment2 extends Fragment implements AppBarLayout.OnOffsetChangedListener  {

    private AppBarLayout appBarLayout;
    public static boolean isRefreshCalled=false;
    OkHttpClient client;
    public static BadgeView badge;
    Realm realm;
    private ViewPager viewPager;
    AppCompatActivity activity;
    RecyclerView localFeedsRecyclerView,myFeedsRecyclerView,peopleAroundYouRecyclerView;
    private LinearLayoutManager localFeedsLayoutManager,peopleAroundYouLayoutManager,myFeedsLayoutManager;
    RelativeLayout local_feeds_layout,my_feeds_layout,people_around_you_layout;
    TextView local_feeds_text,my_feeds_text,peopleAroundYouTextView;
    ArrayList<Notifications> notificationsArrayList=new ArrayList<>();
    ArrayList<MyFeeds> peopleAroundYouListServerData;
    ArrayList<MyFeeds> peopleAroundYouListLoadedData;
    boolean dataAlreadyExists=false;
    Button Sign_Up_Click;
    Button follow_people_click;

    private int NUM_PAGES =1;
    private List<ImageView> dots;
    HomePeopleAroundAdapter homePeopleAroundAdapter;
    HomeLocalFeedsAdapter homeLocalFeedsAdapter;
    HomeMyFeedsAdapter homeMyFeedsAdapter;



    public static int localFeedsCheckIns=0;
    public static int localFeedsReviews=0;


    ArrayList<FeaturedRestaurant> featuredRestaurantArrayList=new ArrayList<>();
    RealmResults<FeaturedRestaurant> featuredRestaurantRealmResults;

    NestedScrollView empty_feed;
    NestedScrollView follow_people_feed;

    LinearLayout local_parent_linearLayout,my_feed_parent_linearLayout,people_parent_linearLayout;
    int myFeedReview=0;
    int myFeedCheckIns=0;

    boolean isFollowingSomeone=true;

    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.home_fragment2, container, false);
        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        activity= (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle("");
//        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        realm =Realm.getDefaultInstance();
        client = new OkHttpClient();
        viewPager = (ViewPager)rootView.findViewById(R.id.home_viewpager);

        localFeedsRecyclerView = (RecyclerView) rootView.findViewById(R.id.local_feeds_recycler_view);
        myFeedsRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_feeds_recycler_view);
        peopleAroundYouRecyclerView = (RecyclerView) rootView.findViewById(R.id.people_around_you_RecyclerView);

        local_feeds_layout=(RelativeLayout)rootView.findViewById(R.id.local_feeds_layout);
        my_feeds_layout=(RelativeLayout)rootView.findViewById(R.id.my_feeds_layout);
        people_around_you_layout=(RelativeLayout)rootView.findViewById(R.id.people_around_you_layout);
        empty_feed=(NestedScrollView)rootView.findViewById(R.id.empty_feed);
        follow_people_feed=(NestedScrollView)rootView.findViewById(R.id.follow_people_feed);

        local_parent_linearLayout=(LinearLayout)rootView.findViewById(R.id.local_parent_linearLayout);
        my_feed_parent_linearLayout=(LinearLayout)rootView.findViewById(R.id.my_feed_parent_linearLayout);
        people_parent_linearLayout=(LinearLayout)rootView.findViewById(R.id.people_parent_linearLayout);

        Sign_Up_Click=(Button)rootView.findViewById(R.id.Sign_Up_Click);
        follow_people_click=(Button)rootView.findViewById(R.id.Follow_People_Click);

        my_feeds_text=(TextView) rootView.findViewById(R.id.my_feeds_text);
        local_feeds_text=(TextView) rootView.findViewById(R.id.local_feeds_text);
        peopleAroundYouTextView=(TextView) rootView.findViewById(R.id.people_around_you);

        appBarLayout = (AppBarLayout) rootView.findViewById(R.id.appbar);

        mSwipeRefreshLayout = (SwipeRefreshLayout)rootView.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                HomeFragment2.isRefreshCalled=true;
                refreshContent(rootView);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        //Local Feed
        localFeedsLayoutManager = new LinearLayoutManager(activity);
        localFeedsRecyclerView.setLayoutManager(localFeedsLayoutManager);
        localFeedsRecyclerView.setOnScrollListener(new LocalFeedsRecyclerOnScrollListener(localFeedsLayoutManager) {
            @Override
            public void onLoadMore(int current_page,int current_item) {
                // do something...
//                Log.e("Current Item",""+current_item);
                FetchAllLocalFeedsDataOnScroll(current_item,rootView);
            }
        });
        if(Constants.isNetworkAvailable(getActivity())) {

            FetchAllLocalFeedsData(rootView);

        }
        else
        {
            if(!Constants.skipLogin) {
                GetFeedsData();
            }
        }



        //My Feed
        myFeedsLayoutManager = new LinearLayoutManager(activity);
        myFeedsRecyclerView.setLayoutManager(myFeedsLayoutManager);
        if(!Constants.skipLogin) {
            if(Constants.isNetworkAvailable(getActivity())){
                FetchMyFeedsData(rootView);
            }
        }

        //People Around You
        peopleAroundYouLayoutManager = new LinearLayoutManager(activity);
        peopleAroundYouRecyclerView.setLayoutManager(peopleAroundYouLayoutManager);
        peopleAroundYouListServerData =new ArrayList<>();
        peopleAroundYouListLoadedData =new ArrayList<>();
        peopleAroundYouRecyclerView.setOnScrollListener(new EndlessRecyclerOnScrollListener(peopleAroundYouLayoutManager) {
            @Override
            public void onLoadMore(int current_page,int current_item) {
                final ProgressBar progressBar;
                progressBar=(ProgressBar)rootView.findViewById(R.id.people_native_progress_below);
                progressBar.setVisibility(View.VISIBLE);


                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                    }
                }, 2*1000);
                // do something...
                peopleAroundYouListLoadedData =new ArrayList<>();
                int newLoad=current_item+10;
                if(peopleAroundYouListServerData.size()>=newLoad)
                {
                    for (int j = current_item; j <newLoad; j++) {
                        peopleAroundYouListLoadedData.add(peopleAroundYouListServerData.get(j));
                    }
                }
                else
                {
                    for (int j = current_item; j<peopleAroundYouListServerData.size() ; j++) {
                        peopleAroundYouListLoadedData.add(peopleAroundYouListServerData.get(j));
                    }
                }

                homePeopleAroundAdapter.UpdateList(peopleAroundYouListLoadedData);
            }
        });
        if(!Constants.skipLogin) {
            FetchPeoplesAroundYou(rootView);
        }

        //Features Restaurant
        if(Constants.isNetworkAvailable(getActivity()) && !SplashActivity.isFeatureRestaurantsLoaded )
        {
            FeaturedRestaurantData(rootView);
            SplashActivity.isFeatureRestaurantsLoaded=true;
        }
        else
        {

            featuredRestaurantRealmResults = realm.where(FeaturedRestaurant.class).findAll();
            featuredRestaurantArrayList.addAll(featuredRestaurantRealmResults);
            NUM_PAGES=featuredRestaurantArrayList.size();
//            Log.e("Else","Loaded "+NUM_PAGES);
            addDots(rootView);
            setupViewPager(viewPager,featuredRestaurantArrayList);
            selectDot(0);
        }

        local_feeds_text.setTextColor(Color.WHITE);
        my_feeds_text.setTextColor(getResources().getColor(R.color.black_3));

        local_feeds_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                local_feeds_layout.setBackgroundColor(getResources().getColor(R.color.colorAccent1));
                my_feeds_layout.setBackgroundColor(Color.WHITE);
                people_around_you_layout.setBackgroundColor(Color.WHITE);

                local_feeds_text.setTextColor(Color.WHITE);
                my_feeds_text.setTextColor(getResources().getColor(R.color.black_3));
                peopleAroundYouTextView.setTextColor(getResources().getColor(R.color.black_3));

                my_feed_parent_linearLayout.setVisibility(View.GONE);
                empty_feed.setVisibility(View.GONE);
                follow_people_feed.setVisibility(View.GONE);
                local_parent_linearLayout.setVisibility(View.VISIBLE);
                people_parent_linearLayout.setVisibility(View.GONE);


            }
        });


        people_around_you_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                local_feeds_layout.setBackgroundColor(Color.WHITE);
                my_feeds_layout.setBackgroundColor(Color.WHITE);
                people_around_you_layout.setBackgroundColor(getResources().getColor(R.color.colorAccent1));

                local_feeds_text.setTextColor(getResources().getColor(R.color.black_3));
                my_feeds_text.setTextColor(getResources().getColor(R.color.black_3));
                peopleAroundYouTextView.setTextColor(Color.WHITE);

                my_feed_parent_linearLayout.setVisibility(View.GONE);
                empty_feed.setVisibility(View.GONE);
                follow_people_feed.setVisibility(View.GONE);
                local_parent_linearLayout.setVisibility(View.GONE);
                people_parent_linearLayout.setVisibility(View.VISIBLE);

//                if(peopleAroundYouListServerData.size()<=0 && !Constants.skipLogin){
                if(Constants.skipLogin){
                    people_parent_linearLayout.setVisibility(View.GONE);
                    empty_feed.setVisibility(View.VISIBLE);
                }
            }
        });

        my_feeds_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                local_feeds_layout.setBackgroundColor(Color.WHITE);
                my_feeds_layout.setBackgroundColor(getResources().getColor(R.color.colorAccent1));
                people_around_you_layout.setBackgroundColor(Color.WHITE);

                local_feeds_text.setTextColor(getResources().getColor(R.color.black_3));
                my_feeds_text.setTextColor(Color.WHITE);
                peopleAroundYouTextView.setTextColor(getResources().getColor(R.color.black_3));


                if(isFollowingSomeone){
                    my_feed_parent_linearLayout.setVisibility(View.VISIBLE);
                    follow_people_feed.setVisibility(View.GONE);
                }else {
                    follow_people_feed.setVisibility(View.VISIBLE);
                    my_feed_parent_linearLayout.setVisibility(View.GONE);
                }

                local_parent_linearLayout.setVisibility(View.GONE);
                empty_feed.setVisibility(View.GONE);
                people_parent_linearLayout.setVisibility(View.GONE);

//                if(myFeedCheckIns==0 && myFeedReview==0 && !Constants.skipLogin){
                if(Constants.skipLogin){
                    my_feed_parent_linearLayout.setVisibility(View.GONE);
                    empty_feed.setVisibility(View.VISIBLE);
                }
            }
        });


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                selectDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        mSwipeRefreshLayout.setEnabled(false);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        mSwipeRefreshLayout.setEnabled(true);
                        break;
                }
                return false;
            }
        });

        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mSwipeRefreshLayout.setEnabled(false);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        mSwipeRefreshLayout.setEnabled(true);
                        break;
                }
                return false;
            }
        });

        ImageView home_fragment_image_search=(ImageView)rootView.findViewById(R.id.home_fragment_image_search);
        home_fragment_image_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(), SearchUserAndRestaurantActivity.class);
                startActivity(intent);
            }
        });

        ImageView image_bookmark_icon=(ImageView)rootView.findViewById(R.id.image_bookmark_icon);
        image_bookmark_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Constants.skipLogin) {
                    Intent intent = new Intent(getActivity(), BookmarkActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Intent intent=new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Constants.skipLogin=false;
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            }
        });

        ImageView target =(ImageView) rootView.findViewById(R.id.image_notification);
        badge = new BadgeView(getActivity(), target);
        if(!Constants.skipLogin ) {
            Notifications();
        }

        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Constants.skipLogin) {
                        Intent intent = new Intent(getActivity(), NotificationActivity.class);
                        startActivity(intent);
                        if(badge.isShown()) {
                            badge.hide(true);
                        }
                }
                else
                {
                    Intent intent=new Intent(getActivity(), LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Constants.skipLogin=false;
                    getActivity().startActivity(intent);
                    getActivity().finish();
                }
            }
        });


        LinearLayout pundit_linear_layout=(LinearLayout) rootView.findViewById(R.id.pundit_linear_layout);

        pundit_linear_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PunditSelectionActivity.class);
                startActivity(intent);
            }
        });


        Sign_Up_Click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Constants.skipLogin=false;
                startActivity(intent);
                getActivity().finish();
            }
        });


        follow_people_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                local_feeds_layout.setBackgroundColor(Color.WHITE);
                my_feeds_layout.setBackgroundColor(Color.WHITE);
                people_around_you_layout.setBackgroundColor(getResources().getColor(R.color.colorAccent1));

                local_feeds_text.setTextColor(getResources().getColor(R.color.black_3));
                my_feeds_text.setTextColor(getResources().getColor(R.color.black_3));
                peopleAroundYouTextView.setTextColor(Color.WHITE);

                my_feed_parent_linearLayout.setVisibility(View.GONE);
                empty_feed.setVisibility(View.GONE);
                follow_people_feed.setVisibility(View.GONE);
                local_parent_linearLayout.setVisibility(View.GONE);
                people_parent_linearLayout.setVisibility(View.VISIBLE);

//                if(peopleAroundYouListServerData.size()<=0 && !Constants.skipLogin){
                if(Constants.skipLogin){
                    people_parent_linearLayout.setVisibility(View.GONE);
                    empty_feed.setVisibility(View.VISIBLE);
                }
            }
        });

        return rootView;
    }

    private void setupViewPager(ViewPager viewPager, ArrayList<FeaturedRestaurant> arrayList) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.clearAll();
        viewPager.setAdapter(null);
        adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        for (int i=0;i<NUM_PAGES;i++){
            FeaturedRestaurant featuredRestaurants=arrayList.get(i);
            adapter.addFragment(new HomeViewPagerFragment(featuredRestaurants));
        }
        viewPager.setAdapter(adapter);
    }


    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        FragmentManager fragmentManager;

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
            fragmentManager=manager;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        public void clearAll() //Clear all page
        {
            for(int i=0; i<mFragmentList.size(); i++)
                fragmentManager.beginTransaction().remove(mFragmentList.get(i)).commit();
            mFragmentList.clear();
        }
    }


    public void addDots(View view) {
        dots = new ArrayList<>();
        LinearLayout dotsLayout = (LinearLayout) view.findViewById(R.id.dots);

        for (int i = 0; i < NUM_PAGES; i++) {
            ImageView dot = new ImageView(getActivity());
            dot.setImageDrawable(getResources().getDrawable(R.drawable.pager_dot_not_selected2));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            dotsLayout.addView(dot, params);

            dots.add(dot);
        }
    }
    public void selectDot(int idx) {
        Resources res = getResources();
        for(int i = 0; i < NUM_PAGES; i++) {
            int drawableId = (i==idx)?(R.drawable.pager_dot_selected2):(R.drawable.pager_dot_not_selected2);
            Drawable drawable = res.getDrawable(drawableId);
            dots.get(i).setImageDrawable(drawable);
        }
    }

    void FeaturedRestaurantData(final View view){


        Request request = new Request.Builder()
                .url(URLs.Featured_Restaurant_URL)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr=response.body().string();

                /** check if activity still exist */
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject jsonObj = new JSONObject(objStr);
                            JSONArray jsonDataArray=jsonObj.getJSONArray("restaurants");

                            realm =Realm.getDefaultInstance();
                            realm.beginTransaction();
                            RealmResults<FeaturedRestaurant> result = realm.where(FeaturedRestaurant.class).findAll();
                            result.deleteAllFromRealm();
                            realm.commitTransaction();

                            for (int i = 0; i < jsonDataArray.length(); i++) {
                                JSONObject featureRestaurantObj = jsonDataArray.getJSONObject(i);

                                dataAlreadyExists=false;

                                if(!dataAlreadyExists){

                                    realm.beginTransaction();
                                    FeaturedRestaurant featuredRestaurant=realm.createObject(FeaturedRestaurant.class);
                                    featuredRestaurant.setId(featureRestaurantObj.getInt("id"));
                                    featuredRestaurant.setName(featureRestaurantObj.getString("name"));
                                    featuredRestaurant.setLocation(featureRestaurantObj.getString("location"));

                                    JSONObject featureRestaurantCoverImage = featureRestaurantObj.getJSONObject("cover_image");

                                    featuredRestaurant.setCover_image_id(featureRestaurantCoverImage.getInt("id"));

                                    JSONObject CoverImage = featureRestaurantCoverImage.getJSONObject("image");
                                    JSONObject CoverImageURL = CoverImage.getJSONObject("image");

                                    featuredRestaurant.setCover_image_url(CoverImageURL.getString("url"));

                                    JSONObject CoverImageThumbnailURL = CoverImageURL.getJSONObject("thumbnail");

                                    featuredRestaurant.setCover_image_thumbnail(CoverImageThumbnailURL.getString("url"));

                                    JSONObject featureRestaurantOwner = featureRestaurantObj.getJSONObject("owner");

                                    featuredRestaurant.setOwnerID(featureRestaurantOwner.getInt("id"));

//                                    Log.e("Loop","Loop"+i);
                                    featuredRestaurantArrayList.add(featuredRestaurant);
                                    realm.commitTransaction();
                                }
                            }

                            realm.close();
//                            Log.e("feaResArrayList",""+featuredRestaurantArrayList.size());
                            NUM_PAGES=featuredRestaurantArrayList.size();
                            addDots(view);
                            setupViewPager(viewPager,featuredRestaurantArrayList);
                            selectDot(0);

                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    void FetchAllLocalFeedsData(View v){
        final ProgressBar progressBar;
        progressBar=(ProgressBar)v.findViewById(R.id.local_native_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        // Persist your data in a transaction
        realm.beginTransaction();
        User user=null;
        if(!Constants.skipLogin) {
             user= realm.where(User.class).findFirst();
            // Log.e("UT",""+user.getToken());
        }




        Request request;
        if(!Constants.skipLogin && user!=null) {
            request = new Request.Builder()
                    .url(URLs.All_LocalFeeds)
                    .addHeader("Authorization", "Token token=" + user.getToken())
                    .build();
        }
        else
        {
            request = new Request.Builder()
                    .url(URLs.All_LocalFeeds)
                    .build();
        }
        realm.commitTransaction();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr=response.body().string();

                /** check if activity still exist */
                if (getActivity() == null) {
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject jsonObj = new JSONObject(objStr);

                            if(jsonObj.has("message")){
                                return;
                            }

                            JSONArray jsonDataReviewsArray=jsonObj.getJSONArray("review");
                            JSONArray jsonDataCheckInArray=jsonObj.getJSONArray("checkin");

                            //    realm =Realm.getDefaultInstance();


                            realm.beginTransaction();
                            LocalFeeds localFeeds=realm.createObject(LocalFeeds.class);



                            for (int i = 0; i < jsonDataReviewsArray.length(); i++) {

                                JSONObject jsonDataReviewObj = jsonDataReviewsArray.getJSONObject(i);


                                JSONObject reviewerObj = jsonDataReviewObj.getJSONObject("reviewer");

                                JSONArray reviewLikesArray = jsonDataReviewObj.getJSONArray("likes");
                                JSONArray reviewCommentsArray = jsonDataReviewObj.getJSONArray("comments");
                                JSONArray reviewShareArray = jsonDataReviewObj.getJSONArray("reports");


//                                if(!dataAlreadyExists)
                                {

                                    realm.commitTransaction();
                                    realm.beginTransaction();
                                    LocalFeedReview localFeedReview=realm.createObject(LocalFeedReview.class);

                                    localFeedReview.setReviewID(jsonDataReviewObj.getInt("id"));
                                    localFeedReview.setUpdated_at(jsonDataReviewObj.getString("updated_at"));
                                    localFeedReview.setTitle(jsonDataReviewObj.getString("title"));
                                    localFeedReview.setBookmarked(jsonDataReviewObj.getBoolean("bookmark"));
                                    localFeedReview.setLiked(jsonDataReviewObj.getBoolean("review_likes"));
                                    localFeedReview.setSummary(jsonDataReviewObj.getString("summary"));
                                    if(!jsonDataReviewObj.isNull("rating")){
                                        localFeedReview.setRating(jsonDataReviewObj.getInt("rating"));
                                    }
                                    if(!jsonDataReviewObj.isNull("reviewable_id")) {
                                        localFeedReview.setReviewable_id(jsonDataReviewObj.getInt("reviewable_id"));
                                    }
                                    localFeedReview.setReviewable_type(jsonDataReviewObj.getString("reviewable_type"));


                                        JSONObject reviewOnObj = jsonDataReviewObj.getJSONObject("review_on");

                                        if(reviewOnObj.has("id")){
                                            localFeedReview.setReviewOnID(reviewOnObj.getInt("id"));
                                            localFeedReview.setReviewOnName(reviewOnObj.getString("name"));
                                            localFeedReview.setReviewOnLocation(reviewOnObj.getString("location"));
                                        }



                                    localFeedReview.setReviewImage(jsonDataReviewObj.getString("image"));

                                    localFeedReview.setReviewerID(reviewerObj.getInt("id"));
                                    localFeedReview.setReviewerName(reviewerObj.getString("name"));
                                    localFeedReview.setReviewerLocation(reviewerObj.getString("location"));
                                    localFeedReview.setReviewerAvatar(reviewerObj.getString("avatar"));

                                    localFeedReview.setReviewLikesCount(reviewLikesArray.length());
                                    localFeedReview.setReviewCommentCount(reviewCommentsArray.length());
                                    localFeedReview.setReviewSharesCount(jsonDataReviewObj.getInt("shares"));


                                    realm.commitTransaction();
                                    realm.beginTransaction();

                                    for (int c = 0; c < reviewCommentsArray.length(); c++) {
                                        JSONObject commentObj = reviewCommentsArray.getJSONObject(c);

                                        Comment comment=realm.createObject(Comment.class);

                                        comment.setCommentID(commentObj.getInt("id"));
                                        comment.setCommentTitle(commentObj.getString("title"));
                                        comment.setCommentUpdated_at(commentObj.getString("created_at"));
                                        comment.setCommentSummary(commentObj.getString("comment"));


                                        JSONObject commentatorObj = commentObj.getJSONObject("commentor");
                                        comment.setCommentatorID(commentatorObj.getInt("id"));
                                        comment.setCommentatorName(commentatorObj.getString("name"));
                                        comment.setCommentatorImage(commentatorObj.getString("avatar"));

                                        JSONArray commentLikeArray=commentObj.getJSONArray("likes");
                                        comment.setCommentLikesCount(commentLikeArray.length());

                                        JSONArray replyArray=commentObj.getJSONArray("replies");
                                        realm.commitTransaction();
                                        realm.beginTransaction();

                                        for(int r=0;r<replyArray.length();r++){
                                            JSONObject replyObj=replyArray.getJSONObject(r);

                                            Reply reply=realm.createObject(Reply.class);

                                            reply.setCommentID(replyObj.getInt("id"));
                                            reply.setCommentTitle(replyObj.getString("title"));
                                            reply.setCommentUpdated_at(replyObj.getString("created_at"));
                                            reply.setCommentSummary(replyObj.getString("comment"));

                                            JSONObject replyCommentatorObj = replyObj.getJSONObject("commentor");
                                            reply.setCommentatorID(replyCommentatorObj.getInt("id"));
                                            reply.setCommentatorName(replyCommentatorObj.getString("name"));
                                            reply.setCommentatorImage(replyCommentatorObj.getString("avatar"));

                                            JSONArray replyCommentLikeArray=replyObj.getJSONArray("likes");
                                            reply.setCommentLikesCount(replyCommentLikeArray.length());

                                            final Reply manageReply = realm.copyToRealm(reply);
                                            comment.getReplyRealmList().add(manageReply);

                                        }

                                        final Comment managedComment = realm.copyToRealm(comment);
                                        localFeedReview.getCommentRealmList().add(managedComment);

                                    }



                                    realm.commitTransaction();
                                    realm.beginTransaction();
                                    localFeeds.getLocalFeedReviewRealmList().add(localFeedReview);
                                }
                            }


                            for (int i = 0; i < jsonDataCheckInArray.length(); i++) {

                                JSONObject jsonDataCheckInObj = jsonDataCheckInArray.getJSONObject(i);

                                JSONObject writerObj = jsonDataCheckInObj.getJSONObject("writer");

                                JSONObject checkinObj = jsonDataCheckInObj.getJSONObject("checkin");




                                JSONArray checkinLikesArray = jsonDataCheckInObj.getJSONArray("likes");
                                JSONArray checkinCommentsArray = jsonDataCheckInObj.getJSONArray("comments");
                                JSONArray checkinPhotoArray = jsonDataCheckInObj.getJSONArray("photos");


//                                if(!dataAlreadyExists)
                                {

                                    realm.commitTransaction();
                                    realm.beginTransaction();

                                    LocalFeedCheckIn localFeedCheckIn=realm.createObject(LocalFeedCheckIn.class);

//                                    localFeedCheckIn.setCheckInID(checkinObj.getInt("id"));
                                    localFeedCheckIn.setCheckInID(jsonDataCheckInObj.getInt("id"));
                                    localFeedCheckIn.setUpdated_at(jsonDataCheckInObj.getString("updated_at"));
                                    localFeedCheckIn.setCheckInTitle(jsonDataCheckInObj.getString("title"));
                                    localFeedCheckIn.setBookmarked(jsonDataCheckInObj.getBoolean("bookmark"));
                                    localFeedCheckIn.setLiked(jsonDataCheckInObj.getBoolean("checkin_likes"));
                                    localFeedCheckIn.setCheckInStatus(jsonDataCheckInObj.getString("status"));

                                    if(!checkinObj.isNull("lat")){
                                        localFeedCheckIn.setCheckInLat(checkinObj.getDouble("lat"));
                                    }
                                    if(!checkinObj.isNull("long")) {
                                        localFeedCheckIn.setCheckInLong(checkinObj.getDouble("long"));
                                    }

                                    localFeedCheckIn.setCheckInWriterID(writerObj.getInt("id"));
                                    localFeedCheckIn.setCheckInWriterName(writerObj.getString("name"));
                                    localFeedCheckIn.setCheckInWriterLocation(writerObj.getString("location"));
                                    localFeedCheckIn.setCheckInWriterAvatar(writerObj.getString("avatar"));

                                    if(!checkinObj.isNull("restaurant")) {
                                        JSONObject restaurantObj = checkinObj.getJSONObject("restaurant");
                                        localFeedCheckIn.setCheckInOnID(restaurantObj.getInt("id"));
                                        localFeedCheckIn.setCheckInOnName(restaurantObj.getString("name"));
                                        localFeedCheckIn.setCheckInOnLocation(restaurantObj.getString("location"));
                                    }

                                    if (checkinObj.has("restaurant_image")) {
                                        if(!checkinObj.isNull("restaurant_image")) {
                                            localFeedCheckIn.setCheckInOnImage(checkinObj.getString("restaurant_image"));
                                        }
                                    }


                                    for (int p = 0; p < checkinPhotoArray.length(); p++) {

                                        JSONObject photo = checkinPhotoArray.getJSONObject(p);

                                        PhotoModel photoModel=new PhotoModel();
                                        photoModel.setId(photo.getInt("id"));
                                        photoModel.setUrl(photo.getString("image_url"));
                                        localFeedCheckIn.setCheckInImage(photo.getString("image_url"));
                                        final PhotoModel managedPhotoModel = realm.copyToRealm(photoModel);
                                        localFeedCheckIn.getPhotoModels().add(managedPhotoModel);
                                    }

                                    localFeedCheckIn.setReviewLikesCount(checkinLikesArray.length());
                                    localFeedCheckIn.setReviewCommentCount(checkinCommentsArray.length());
                                    localFeedCheckIn.setReviewSharesCount(jsonDataCheckInObj.getInt("shares"));




                                    realm.commitTransaction();
                                    realm.beginTransaction();

                                    for (int c = 0; c < checkinCommentsArray.length(); c++) {
                                        JSONObject commentObj = checkinCommentsArray.getJSONObject(c);

                                        Comment comment=realm.createObject(Comment.class);

                                        comment.setCommentID(commentObj.getInt("id"));
                                        comment.setCommentTitle(commentObj.getString("title"));
                                        comment.setCommentUpdated_at(commentObj.getString("created_at"));
                                        comment.setCommentSummary(commentObj.getString("comment"));


                                        JSONObject commentatorObj = commentObj.getJSONObject("commentor");
                                        comment.setCommentatorID(commentatorObj.getInt("id"));
                                        comment.setCommentatorName(commentatorObj.getString("name"));
                                        comment.setCommentatorImage(commentatorObj.getString("avatar"));

                                        JSONArray commentLikeArray=commentObj.getJSONArray("likes");
                                        comment.setCommentLikesCount(commentLikeArray.length());

                                        JSONArray replyArray=commentObj.getJSONArray("replies");
                                        realm.commitTransaction();
                                        realm.beginTransaction();

                                        for(int r=0;r<replyArray.length();r++){
                                            JSONObject replyObj=replyArray.getJSONObject(r);

                                            Reply reply=realm.createObject(Reply.class);

                                            reply.setCommentID(replyObj.getInt("id"));
                                            reply.setCommentTitle(replyObj.getString("title"));
                                            reply.setCommentUpdated_at(replyObj.getString("created_at"));
                                            reply.setCommentSummary(replyObj.getString("comment"));

                                            JSONObject replyCommentatorObj = replyObj.getJSONObject("commentor");
                                            reply.setCommentatorID(replyCommentatorObj.getInt("id"));
                                            reply.setCommentatorName(replyCommentatorObj.getString("name"));
                                            reply.setCommentatorImage(replyCommentatorObj.getString("avatar"));

                                            JSONArray replyCommentLikeArray=replyObj.getJSONArray("likes");
                                            reply.setCommentLikesCount(replyCommentLikeArray.length());

                                            final Reply manageReply = realm.copyToRealm(reply);
                                            comment.getReplyRealmList().add(manageReply);

                                        }

                                        final Comment managedComment = realm.copyToRealm(comment);
                                        localFeedCheckIn.getCommentRealmList().add(managedComment);
                                    }

                                    realm.commitTransaction();
                                    realm.beginTransaction();
                                    localFeeds.getLocalFeedCheckInRealmList().add(localFeedCheckIn);

                                }
                            }


                            if(localFeeds.getLocalFeedReviewRealmList().size()>0 &&localFeeds.getLocalFeedCheckInRealmList().size()>0)
                            {
                                localFeedsCheckIns=localFeeds.getLocalFeedCheckInRealmList().size();
                                localFeedsReviews=localFeeds.getLocalFeedReviewRealmList().size();
                            }
                            else if(localFeeds.getLocalFeedReviewRealmList().size()==0 &&localFeeds.getLocalFeedCheckInRealmList().size()>0)
                            {
                                localFeedsCheckIns=localFeeds.getLocalFeedCheckInRealmList().size();
                                localFeedsReviews=localFeeds.getLocalFeedReviewRealmList().size();
                            }
                            else if(localFeeds.getLocalFeedReviewRealmList().size()>0 &&localFeeds.getLocalFeedCheckInRealmList().size()==0)
                            {
                                localFeedsCheckIns=localFeeds.getLocalFeedCheckInRealmList().size();
                                localFeedsReviews=localFeeds.getLocalFeedReviewRealmList().size();
                            }


                            progressBar.setVisibility(View.GONE);
                            homeLocalFeedsAdapter = new HomeLocalFeedsAdapter(localFeeds,getActivity());
                            localFeedsRecyclerView.setAdapter(homeLocalFeedsAdapter);
                            realm.commitTransaction();
                            realm.close();
                            progressBar.setVisibility(View.GONE);
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }



    void FetchAllLocalFeedsDataOnScroll(int offset, View v){
        final ProgressBar progressBar;
        progressBar=(ProgressBar)v.findViewById(R.id.local_native_progress_below);
        progressBar.setVisibility(View.VISIBLE);
        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        // Persist your data in a transaction
        realm.beginTransaction();
        User user=null;
        if(!Constants.skipLogin) {
            user= realm.where(User.class).findFirst();
          //  Log.e("UT",""+user.getToken());
        }




        Request request;
        if(!Constants.skipLogin && user!=null) {
            request = new Request.Builder()
                    .url(URLs.All_LocalFeeds+"?offset="+offset)
                    .addHeader("Authorization", "Token token=" + user.getToken())
                    .build();
        }
        else
        {
            request = new Request.Builder()
                    .url(URLs.All_LocalFeeds)
                    .build();
        }
        realm.commitTransaction();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr=response.body().string();

                /** check if activity still exist */
                if (getActivity() == null) {
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject jsonObj = new JSONObject(objStr);

                            if(jsonObj.has("message")){
                                return;
                            }

                            JSONArray jsonDataReviewsArray=jsonObj.getJSONArray("review");
                            JSONArray jsonDataCheckInArray=jsonObj.getJSONArray("checkin");

                            //    realm =Realm.getDefaultInstance();


                            realm.beginTransaction();
                            LocalFeeds localFeeds=realm.createObject(LocalFeeds.class);


                            for (int i = 0; i < jsonDataReviewsArray.length(); i++) {

                                JSONObject jsonDataReviewObj = jsonDataReviewsArray.getJSONObject(i);


                                JSONObject reviewerObj = jsonDataReviewObj.getJSONObject("reviewer");

                                JSONArray reviewLikesArray = jsonDataReviewObj.getJSONArray("likes");
                                JSONArray reviewCommentsArray = jsonDataReviewObj.getJSONArray("comments");
                                JSONArray reviewShareArray = jsonDataReviewObj.getJSONArray("reports");


//                                if(!dataAlreadyExists)
                                {

                                    realm.commitTransaction();
                                    realm.beginTransaction();
                                    LocalFeedReview localFeedReview=realm.createObject(LocalFeedReview.class);

                                    localFeedReview.setReviewID(jsonDataReviewObj.getInt("id"));
                                    localFeedReview.setUpdated_at(jsonDataReviewObj.getString("updated_at"));
                                    localFeedReview.setTitle(jsonDataReviewObj.getString("title"));
                                    localFeedReview.setBookmarked(jsonDataReviewObj.getBoolean("bookmark"));
                                    localFeedReview.setLiked(jsonDataReviewObj.getBoolean("review_likes"));
                                    localFeedReview.setSummary(jsonDataReviewObj.getString("summary"));
                                    if(!jsonDataReviewObj.isNull("rating")){
                                        localFeedReview.setRating(jsonDataReviewObj.getInt("rating"));
                                    }
                                    if(!jsonDataReviewObj.isNull("reviewable_id")) {
                                        localFeedReview.setReviewable_id(jsonDataReviewObj.getInt("reviewable_id"));
                                    }
                                    localFeedReview.setReviewable_type(jsonDataReviewObj.getString("reviewable_type"));


                                    JSONObject reviewOnObj = jsonDataReviewObj.getJSONObject("review_on");

                                    if(reviewOnObj.has("id")){
                                        localFeedReview.setReviewOnID(reviewOnObj.getInt("id"));
                                        localFeedReview.setReviewOnName(reviewOnObj.getString("name"));
                                        localFeedReview.setReviewOnLocation(reviewOnObj.getString("location"));
                                    }



                                    localFeedReview.setReviewImage(jsonDataReviewObj.getString("image"));

                                    localFeedReview.setReviewerID(reviewerObj.getInt("id"));
                                    localFeedReview.setReviewerName(reviewerObj.getString("name"));
                                    localFeedReview.setReviewerLocation(reviewerObj.getString("location"));
                                    localFeedReview.setReviewerAvatar(reviewerObj.getString("avatar"));

                                    localFeedReview.setReviewLikesCount(reviewLikesArray.length());
                                    localFeedReview.setReviewCommentCount(reviewCommentsArray.length());
                                    localFeedReview.setReviewSharesCount(jsonDataReviewObj.getInt("shares"));


                                    realm.commitTransaction();
                                    realm.beginTransaction();

                                    for (int c = 0; c < reviewCommentsArray.length(); c++) {
                                        JSONObject commentObj = reviewCommentsArray.getJSONObject(c);

                                        Comment comment=realm.createObject(Comment.class);

                                        comment.setCommentID(commentObj.getInt("id"));
                                        comment.setCommentTitle(commentObj.getString("title"));
                                        comment.setCommentUpdated_at(commentObj.getString("created_at"));
                                        comment.setCommentSummary(commentObj.getString("comment"));


                                        JSONObject commentatorObj = commentObj.getJSONObject("commentor");
                                        comment.setCommentatorID(commentatorObj.getInt("id"));
                                        comment.setCommentatorName(commentatorObj.getString("name"));
                                        comment.setCommentatorImage(commentatorObj.getString("avatar"));

                                        JSONArray commentLikeArray=commentObj.getJSONArray("likes");
                                        comment.setCommentLikesCount(commentLikeArray.length());


                                        JSONArray replyArray=commentObj.getJSONArray("replies");
                                        realm.commitTransaction();
                                        realm.beginTransaction();

                                        for(int r=0;r<replyArray.length();r++){

                                            JSONObject replyObj=replyArray.getJSONObject(r);

                                            Reply reply=realm.createObject(Reply.class);

                                            reply.setCommentID(replyObj.getInt("id"));
                                            reply.setCommentTitle(replyObj.getString("title"));
                                            reply.setCommentUpdated_at(replyObj.getString("created_at"));
                                            reply.setCommentSummary(replyObj.getString("comment"));

                                            JSONObject replyCommentatorObj = replyObj.getJSONObject("commentor");
                                            reply.setCommentatorID(replyCommentatorObj.getInt("id"));
                                            reply.setCommentatorName(replyCommentatorObj.getString("name"));
                                            reply.setCommentatorImage(replyCommentatorObj.getString("avatar"));

                                            JSONArray replyCommentLikeArray=replyObj.getJSONArray("likes");
                                            reply.setCommentLikesCount(replyCommentLikeArray.length());

                                            final Reply manageReply = realm.copyToRealm(reply);
                                            comment.getReplyRealmList().add(manageReply);
                                        }

                                        final Comment managedComment = realm.copyToRealm(comment);
                                        localFeedReview.getCommentRealmList().add(managedComment);

                                    }



                                    realm.commitTransaction();
                                    realm.beginTransaction();
                                    localFeeds.getLocalFeedReviewRealmList().add(localFeedReview);
                                }
                            }


                            for (int i = 0; i < jsonDataCheckInArray.length(); i++) {

                                JSONObject jsonDataCheckInObj = jsonDataCheckInArray.getJSONObject(i);

                                JSONObject writerObj = jsonDataCheckInObj.getJSONObject("writer");

                                JSONObject checkinObj = jsonDataCheckInObj.getJSONObject("checkin");




                                JSONArray checkinLikesArray = jsonDataCheckInObj.getJSONArray("likes");
                                JSONArray checkinCommentsArray = jsonDataCheckInObj.getJSONArray("comments");
                                JSONArray checkinPhotoArray = jsonDataCheckInObj.getJSONArray("photos");


//                                if(!dataAlreadyExists)
                                {

                                    realm.commitTransaction();
                                    realm.beginTransaction();

                                    LocalFeedCheckIn localFeedCheckIn=realm.createObject(LocalFeedCheckIn.class);

//                                    localFeedCheckIn.setCheckInID(checkinObj.getInt("id"));
                                    localFeedCheckIn.setCheckInID(jsonDataCheckInObj.getInt("id"));
                                    localFeedCheckIn.setUpdated_at(jsonDataCheckInObj.getString("updated_at"));
                                    localFeedCheckIn.setCheckInTitle(jsonDataCheckInObj.getString("title"));
                                    localFeedCheckIn.setBookmarked(jsonDataCheckInObj.getBoolean("bookmark"));
                                    localFeedCheckIn.setLiked(jsonDataCheckInObj.getBoolean("checkin_likes"));
                                    localFeedCheckIn.setCheckInStatus(jsonDataCheckInObj.getString("status"));

                                    if(!checkinObj.isNull("lat")){
                                        localFeedCheckIn.setCheckInLat(checkinObj.getDouble("lat"));
                                    }
                                    if(!checkinObj.isNull("long")) {
                                        localFeedCheckIn.setCheckInLong(checkinObj.getDouble("long"));
                                    }

                                    localFeedCheckIn.setCheckInWriterID(writerObj.getInt("id"));
                                    localFeedCheckIn.setCheckInWriterName(writerObj.getString("name"));
                                    localFeedCheckIn.setCheckInWriterLocation(writerObj.getString("location"));
                                    localFeedCheckIn.setCheckInWriterAvatar(writerObj.getString("avatar"));

                                    if(!checkinObj.isNull("restaurant")) {
                                        JSONObject restaurantObj = checkinObj.getJSONObject("restaurant");
                                        localFeedCheckIn.setCheckInOnID(restaurantObj.getInt("id"));
                                        localFeedCheckIn.setCheckInOnName(restaurantObj.getString("name"));
                                        localFeedCheckIn.setCheckInOnLocation(restaurantObj.getString("location"));
                                    }

                                    if (checkinObj.has("restaurant_image")) {
                                        if(!checkinObj.isNull("restaurant_image")) {
                                            localFeedCheckIn.setCheckInOnImage(checkinObj.getString("restaurant_image"));
                                        }
                                    }


                                    for (int p = 0; p < checkinPhotoArray.length(); p++) {

                                        JSONObject photo = checkinPhotoArray.getJSONObject(p);

                                        PhotoModel photoModel=new PhotoModel();
                                        photoModel.setId(photo.getInt("id"));
                                        photoModel.setUrl(photo.getString("image_url"));
                                        localFeedCheckIn.setCheckInImage(photo.getString("image_url"));
                                        final PhotoModel managedPhotoModel = realm.copyToRealm(photoModel);
                                        localFeedCheckIn.getPhotoModels().add(managedPhotoModel);
                                    }

                                    localFeedCheckIn.setReviewLikesCount(checkinLikesArray.length());
                                    localFeedCheckIn.setReviewCommentCount(checkinCommentsArray.length());
                                    localFeedCheckIn.setReviewSharesCount(jsonDataCheckInObj.getInt("shares"));




                                    realm.commitTransaction();
                                    realm.beginTransaction();

                                    for (int c = 0; c < checkinCommentsArray.length(); c++) {
                                        JSONObject commentObj = checkinCommentsArray.getJSONObject(c);

                                        Comment comment=realm.createObject(Comment.class);

                                        comment.setCommentID(commentObj.getInt("id"));
                                        comment.setCommentTitle(commentObj.getString("title"));
                                        comment.setCommentUpdated_at(commentObj.getString("created_at"));
                                        comment.setCommentSummary(commentObj.getString("comment"));


                                        JSONObject commentatorObj = commentObj.getJSONObject("commentor");
                                        comment.setCommentatorID(commentatorObj.getInt("id"));
                                        comment.setCommentatorName(commentatorObj.getString("name"));
                                        comment.setCommentatorImage(commentatorObj.getString("avatar"));

                                        JSONArray commentLikeArray=commentObj.getJSONArray("likes");
                                        comment.setCommentLikesCount(commentLikeArray.length());


                                        JSONArray replyArray=commentObj.getJSONArray("replies");
                                        realm.commitTransaction();
                                        realm.beginTransaction();

                                        for(int r=0;r<replyArray.length();r++){
                                            JSONObject replyObj=replyArray.getJSONObject(r);

                                            Reply reply=realm.createObject(Reply.class);

                                            reply.setCommentID(replyObj.getInt("id"));
                                            reply.setCommentTitle(replyObj.getString("title"));
                                            reply.setCommentUpdated_at(replyObj.getString("created_at"));
                                            reply.setCommentSummary(replyObj.getString("comment"));

                                            JSONObject replyCommentatorObj = replyObj.getJSONObject("commentor");
                                            reply.setCommentatorID(replyCommentatorObj.getInt("id"));
                                            reply.setCommentatorName(replyCommentatorObj.getString("name"));
                                            reply.setCommentatorImage(replyCommentatorObj.getString("avatar"));

                                            JSONArray replyCommentLikeArray=replyObj.getJSONArray("likes");
                                            reply.setCommentLikesCount(replyCommentLikeArray.length());

                                            final Reply manageReply = realm.copyToRealm(reply);
                                            comment.getReplyRealmList().add(manageReply);

                                        }

                                        final Comment managedComment = realm.copyToRealm(comment);
                                        localFeedCheckIn.getCommentRealmList().add(managedComment);
                                    }

                                    realm.commitTransaction();
                                    realm.beginTransaction();
                                    localFeeds.getLocalFeedCheckInRealmList().add(localFeedCheckIn);
                                }
                            }


                            if(localFeeds.getLocalFeedReviewRealmList().size()>0 &&localFeeds.getLocalFeedCheckInRealmList().size()>0)
                            {
                                localFeedsCheckIns=localFeeds.getLocalFeedCheckInRealmList().size();
                                localFeedsReviews=localFeeds.getLocalFeedReviewRealmList().size();
                                HomeLocalFeedsAdapter.Notify(localFeeds.getLocalFeedReviewRealmList(),localFeeds.getLocalFeedCheckInRealmList());
                            }
                            else if(localFeeds.getLocalFeedReviewRealmList().size()==0 &&localFeeds.getLocalFeedCheckInRealmList().size()>0)
                            {
                                localFeedsCheckIns=localFeeds.getLocalFeedCheckInRealmList().size();
                                localFeedsReviews=localFeeds.getLocalFeedReviewRealmList().size();
                                HomeLocalFeedsAdapter.NotifyCheckIns(localFeeds.getLocalFeedCheckInRealmList());
                            }
                            else if(localFeeds.getLocalFeedReviewRealmList().size()>0 &&localFeeds.getLocalFeedCheckInRealmList().size()==0)
                            {
                                localFeedsCheckIns=localFeeds.getLocalFeedCheckInRealmList().size();
                                localFeedsReviews=localFeeds.getLocalFeedReviewRealmList().size();
                                HomeLocalFeedsAdapter.NotifyReviews(localFeeds.getLocalFeedReviewRealmList());
                            }

                            homeLocalFeedsAdapter.notifyDataSetChanged();
                            realm.commitTransaction();
                            realm.close();
                            progressBar.setVisibility(View.GONE);
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    //
    void FetchMyFeedsData(View view){

//        Log.e("FetchMyFeedsData","Called");
        final ProgressBar progressBar;
        progressBar=(ProgressBar)view.findViewById(R.id.my_native_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        // Persist your data in a transaction
        realm.beginTransaction();
        User user = realm.where(User.class).findFirst();
      //  Log.e("U",""+user.getToken());

        realm.commitTransaction();


        Request request = new Request.Builder()
                .url(URLs.MyFeeds_Restaurant_URL)
                .addHeader("Authorization", "Token token="+user.getToken())
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr=response.body().string();

                /** check if activity still exist */
                if (getActivity() == null) {
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject jsonObj = new JSONObject(objStr);

                            if(jsonObj.has("message")){
                                return;
                            }

                            JSONArray jsonDataReviewsArray=jsonObj.getJSONArray("review");
                            JSONArray jsonDataCheckInArray=jsonObj.getJSONArray("checkin");

                            //    realm =Realm.getDefaultInstance();


                            if(jsonDataReviewsArray.length()==0 && jsonDataCheckInArray.length()==0){
                                progressBar.setVisibility(View.GONE);
                                isFollowingSomeone=false;
                            }


                            realm.beginTransaction();
                            RealmResults<LocalFeeds> localFeedsRealmResults = realm.where(LocalFeeds.class).findAll();
                            localFeedsRealmResults.deleteAllFromRealm();
                            realm.commitTransaction();

                            realm.beginTransaction();
                            LocalFeeds localFeeds=realm.createObject(LocalFeeds.class);



                            for (int i = 0; i < jsonDataReviewsArray.length(); i++) {

                                JSONObject jsonDataReviewObj = jsonDataReviewsArray.getJSONObject(i);



                                JSONObject reviewerObj = jsonDataReviewObj.getJSONObject("reviewer");

                                JSONArray reviewLikesArray = jsonDataReviewObj.getJSONArray("likes");
                                JSONArray reviewCommentsArray = jsonDataReviewObj.getJSONArray("comments");
                                JSONArray reviewShareArray = jsonDataReviewObj.getJSONArray("reports");


//                                if(!dataAlreadyExists)
                                {

                                    realm.commitTransaction();
                                    realm.beginTransaction();
                                    LocalFeedReview localFeedReview=realm.createObject(LocalFeedReview.class);

                                    localFeedReview.setReviewID(jsonDataReviewObj.getInt("id"));
                                    localFeedReview.setUpdated_at(jsonDataReviewObj.getString("updated_at"));
                                    localFeedReview.setTitle(jsonDataReviewObj.getString("title"));
                                    localFeedReview.setBookmarked(jsonDataReviewObj.getBoolean("bookmark"));
                                    localFeedReview.setLiked(jsonDataReviewObj.getBoolean("review_likes"));
                                    localFeedReview.setSummary(jsonDataReviewObj.getString("summary"));
                                    if(!jsonDataReviewObj.isNull("rating")){
                                        localFeedReview.setRating(jsonDataReviewObj.getInt("rating"));
                                    }
                                    if(!jsonDataReviewObj.isNull("reviewable_id")) {
                                        localFeedReview.setReviewable_id(jsonDataReviewObj.getInt("reviewable_id"));
                                    }
                                    localFeedReview.setReviewable_type(jsonDataReviewObj.getString("reviewable_type"));

                                    JSONObject reviewOnObj = jsonDataReviewObj.getJSONObject("review_on");

                                        if(reviewOnObj.has("id")){
                                            localFeedReview.setReviewOnID(reviewOnObj.getInt("id"));
                                            localFeedReview.setReviewOnName(reviewOnObj.getString("name"));
                                            localFeedReview.setReviewOnLocation(reviewOnObj.getString("location"));
                                        }

                                    localFeedReview.setReviewImage(jsonDataReviewObj.getString("image"));

                                    localFeedReview.setReviewerID(reviewerObj.getInt("id"));
                                    localFeedReview.setReviewerName(reviewerObj.getString("name"));
                                    localFeedReview.setReviewerLocation(reviewerObj.getString("location"));
                                    localFeedReview.setReviewerAvatar(reviewerObj.getString("avatar"));

                                    localFeedReview.setReviewLikesCount(reviewLikesArray.length());
                                    localFeedReview.setReviewCommentCount(reviewCommentsArray.length());
                                    localFeedReview.setReviewSharesCount(jsonDataReviewObj.getInt("shares"));


                                    realm.commitTransaction();
                                    realm.beginTransaction();

                                    for (int c = 0; c < reviewCommentsArray.length(); c++) {
                                        JSONObject commentObj = reviewCommentsArray.getJSONObject(c);

                                        Comment comment=realm.createObject(Comment.class);

                                        comment.setCommentID(commentObj.getInt("id"));
                                        comment.setCommentTitle(commentObj.getString("title"));
                                        comment.setCommentUpdated_at(commentObj.getString("created_at"));
                                        comment.setCommentSummary(commentObj.getString("comment"));


                                        JSONObject commentatorObj = commentObj.getJSONObject("commentor");
                                        comment.setCommentatorID(commentatorObj.getInt("id"));
                                        comment.setCommentatorName(commentatorObj.getString("name"));
                                        comment.setCommentatorImage(commentatorObj.getString("avatar"));

                                        JSONArray commentLikeArray=commentObj.getJSONArray("likes");
                                        comment.setCommentLikesCount(commentLikeArray.length());

                                        JSONArray replyArray=commentObj.getJSONArray("replies");
                                        realm.commitTransaction();
                                        realm.beginTransaction();

                                        for(int r=0;r<replyArray.length();r++){
                                            JSONObject replyObj=replyArray.getJSONObject(r);

                                            Reply reply=realm.createObject(Reply.class);

                                            reply.setCommentID(replyObj.getInt("id"));
                                            reply.setCommentTitle(replyObj.getString("title"));
                                            reply.setCommentUpdated_at(replyObj.getString("created_at"));
                                            reply.setCommentSummary(replyObj.getString("comment"));

                                            JSONObject replyCommentatorObj = replyObj.getJSONObject("commentor");
                                            reply.setCommentatorID(replyCommentatorObj.getInt("id"));
                                            reply.setCommentatorName(replyCommentatorObj.getString("name"));
                                            reply.setCommentatorImage(replyCommentatorObj.getString("avatar"));

                                            JSONArray replyCommentLikeArray=replyObj.getJSONArray("likes");
                                            reply.setCommentLikesCount(replyCommentLikeArray.length());

                                            final Reply manageReply = realm.copyToRealm(reply);
                                            comment.getReplyRealmList().add(manageReply);

                                        }

                                        final Comment managedComment = realm.copyToRealm(comment);
                                        localFeedReview.getCommentRealmList().add(managedComment);

                                    }



                                    realm.commitTransaction();
                                    realm.beginTransaction();
                                    localFeeds.getLocalFeedReviewRealmList().add(localFeedReview);
                                }
                            }


                            for (int i = 0; i < jsonDataCheckInArray.length(); i++) {

                                JSONObject jsonDataCheckInObj = jsonDataCheckInArray.getJSONObject(i);

                                JSONObject writerObj = jsonDataCheckInObj.getJSONObject("writer");

                                JSONObject checkinObj = jsonDataCheckInObj.getJSONObject("checkin");



                                JSONArray checkinLikesArray = jsonDataCheckInObj.getJSONArray("likes");
                                JSONArray checkinCommentsArray = jsonDataCheckInObj.getJSONArray("comments");
                                JSONArray checkinPhotoArray = jsonDataCheckInObj.getJSONArray("photos");


//                                if(!dataAlreadyExists)
                                {

                                    realm.commitTransaction();
                                    realm.beginTransaction();

                                    LocalFeedCheckIn localFeedCheckIn=realm.createObject(LocalFeedCheckIn.class);

//                                    localFeedCheckIn.setCheckInID(checkinObj.getInt("id"));
                                    localFeedCheckIn.setCheckInID(jsonDataCheckInObj.getInt("id"));
                                    localFeedCheckIn.setUpdated_at(jsonDataCheckInObj.getString("updated_at"));
                                    localFeedCheckIn.setCheckInTitle(jsonDataCheckInObj.getString("title"));
                                    localFeedCheckIn.setBookmarked(jsonDataCheckInObj.getBoolean("bookmark"));
                                    localFeedCheckIn.setLiked(jsonDataCheckInObj.getBoolean("checkin_likes"));
                                    localFeedCheckIn.setCheckInStatus(jsonDataCheckInObj.getString("status"));

                                    if(!checkinObj.isNull("lat")){
                                        localFeedCheckIn.setCheckInLat(checkinObj.getDouble("lat"));
                                    }
                                    if(!checkinObj.isNull("long")) {
                                        localFeedCheckIn.setCheckInLong(checkinObj.getDouble("long"));
                                    }

                                    localFeedCheckIn.setCheckInWriterID(writerObj.getInt("id"));
                                    localFeedCheckIn.setCheckInWriterName(writerObj.getString("name"));
                                    localFeedCheckIn.setCheckInWriterLocation(writerObj.getString("location"));
                                    localFeedCheckIn.setCheckInWriterAvatar(writerObj.getString("avatar"));

                                    if(!checkinObj.isNull("restaurant")) {
                                        JSONObject restaurantObj = checkinObj.getJSONObject("restaurant");
                                        localFeedCheckIn.setCheckInOnID(restaurantObj.getInt("id"));
                                        localFeedCheckIn.setCheckInOnName(restaurantObj.getString("name"));
                                        localFeedCheckIn.setCheckInOnLocation(restaurantObj.getString("location"));
                                    }

                                    if (checkinObj.has("restaurant_image")) {
                                        if(!checkinObj.isNull("restaurant_image")) {
                                            localFeedCheckIn.setCheckInOnImage(checkinObj.getString("restaurant_image"));
                                        }
                                    }

                                    for (int p = 0; p < checkinPhotoArray.length(); p++) {

                                        JSONObject photo = checkinPhotoArray.getJSONObject(p);

                                        PhotoModel photoModel=new PhotoModel();
                                        photoModel.setId(photo.getInt("id"));
                                        photoModel.setUrl(photo.getString("image_url"));
                                        localFeedCheckIn.setCheckInImage(photo.getString("image_url"));
                                        final PhotoModel managedPhotoModel = realm.copyToRealm(photoModel);
                                        localFeedCheckIn.getPhotoModels().add(managedPhotoModel);
                                    }

                                    localFeedCheckIn.setReviewLikesCount(checkinLikesArray.length());
                                    localFeedCheckIn.setReviewCommentCount(checkinCommentsArray.length());
                                    localFeedCheckIn.setReviewSharesCount(jsonDataCheckInObj.getInt("shares"));




                                    realm.commitTransaction();
                                    realm.beginTransaction();

                                    for (int c = 0; c < checkinCommentsArray.length(); c++) {
                                        JSONObject commentObj = checkinCommentsArray.getJSONObject(c);

                                        Comment comment=realm.createObject(Comment.class);

                                        comment.setCommentID(commentObj.getInt("id"));
                                        comment.setCommentTitle(commentObj.getString("title"));
                                        comment.setCommentUpdated_at(commentObj.getString("created_at"));
                                        comment.setCommentSummary(commentObj.getString("comment"));


                                        JSONObject commentatorObj = commentObj.getJSONObject("commentor");
                                        comment.setCommentatorID(commentatorObj.getInt("id"));
                                        comment.setCommentatorName(commentatorObj.getString("name"));
                                        comment.setCommentatorImage(commentatorObj.getString("avatar"));

                                        JSONArray commentLikeArray=commentObj.getJSONArray("likes");
                                        comment.setCommentLikesCount(commentLikeArray.length());

                                        JSONArray replyArray=commentObj.getJSONArray("replies");
                                        realm.commitTransaction();
                                        realm.beginTransaction();

                                        for(int r=0;r<replyArray.length();r++){
                                            JSONObject replyObj=replyArray.getJSONObject(r);

                                            Reply reply=realm.createObject(Reply.class);

                                            reply.setCommentID(replyObj.getInt("id"));
                                            reply.setCommentTitle(replyObj.getString("title"));
                                            reply.setCommentUpdated_at(replyObj.getString("created_at"));
                                            reply.setCommentSummary(replyObj.getString("comment"));

                                            JSONObject replyCommentatorObj = replyObj.getJSONObject("commentor");
                                            reply.setCommentatorID(replyCommentatorObj.getInt("id"));
                                            reply.setCommentatorName(replyCommentatorObj.getString("name"));
                                            reply.setCommentatorImage(replyCommentatorObj.getString("avatar"));

                                            JSONArray replyCommentLikeArray=replyObj.getJSONArray("likes");
                                            reply.setCommentLikesCount(replyCommentLikeArray.length());

                                            final Reply manageReply = realm.copyToRealm(reply);
                                            comment.getReplyRealmList().add(manageReply);

                                        }

                                        final Comment managedComment = realm.copyToRealm(comment);
                                        localFeedCheckIn.getCommentRealmList().add(managedComment);
                                    }

                                    realm.commitTransaction();
                                    realm.beginTransaction();
                                    localFeeds.getLocalFeedCheckInRealmList().add(localFeedCheckIn);

                                }
                            }

                            progressBar.setVisibility(View.GONE);
                            myFeedCheckIns=localFeeds.getLocalFeedCheckInRealmList().size();
                            myFeedReview=localFeeds.getLocalFeedReviewRealmList().size();
                            homeMyFeedsAdapter = new HomeMyFeedsAdapter(localFeeds,getActivity());
                            myFeedsRecyclerView.setAdapter(homeMyFeedsAdapter);
                            realm.commitTransaction();
                            realm.close();
                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    //

    void FetchPeoplesAroundYou(View view){
        final ProgressBar progressBar;
        progressBar=(ProgressBar)view.findViewById(R.id.people_native_progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        // Persist your data in a transaction

        User user = realm.where(User.class).findFirst();
//        Log.e("UserT",""+user.getToken());


        Request request = new Request.Builder()
                .url(URLs.PeopleAroundFeed)
                .addHeader("Authorization", "Token token="+user.getToken())
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr=response.body().string();
//                Log.e("ObjStr",""+objStr);

                /** check if activity still exist */
                if (getActivity() == null) {
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                                JSONObject jsonObj = new JSONObject(objStr);
                                JSONArray jsonDataArray=jsonObj.getJSONArray("users");

                                for (int i = 0; i < jsonDataArray.length(); i++) {

                                    JSONObject c = jsonDataArray.getJSONObject(i);

                                    MyFeeds myFeeds=new MyFeeds();

                                    myFeeds.setId(c.getInt("id"));
                                    myFeeds.setName(c.getString("name"));
                                    myFeeds.setUsername(c.getString("username"));
                                    myFeeds.setAvatarPic(c.getString("avatar"));
                                    myFeeds.setLocation(c.getString("location"));
                                    myFeeds.setFollowing(c.getBoolean("follows"));
                                    myFeeds.setFollowers(c.getInt("followers"));

                                    peopleAroundYouListServerData.add(myFeeds);
                                }

                            for(int i=0;i<15;i++){
                                peopleAroundYouListLoadedData.add(peopleAroundYouListServerData.get(i));
                            }

                            progressBar.setVisibility(View.GONE);
                            homePeopleAroundAdapter = new HomePeopleAroundAdapter(peopleAroundYouListLoadedData,getActivity());
                            peopleAroundYouRecyclerView.setAdapter(homePeopleAroundAdapter);
                            mSwipeRefreshLayout.setRefreshing(false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });

    }


    void Notifications(){

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        // Persist your data in a transaction

        User user = realm.where(User.class).findFirst();


        Request request = new Request.Builder()
                .url(URLs.Get_Notification)
                .addHeader("Authorization", "Token token="+user.getToken())
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr=response.body().string();


                if(getActivity()==null){
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject jsonObj = new JSONObject(objStr);
                            JSONArray jsonDataArray = jsonObj.getJSONArray("users");

                            for (int i = 0; i < jsonDataArray.length(); i++) {

                                JSONObject c = jsonDataArray.getJSONObject(i);

                                boolean isDataExist=false;
                                realm.beginTransaction();
                                RealmResults<Notifications> localFeedsRealmResults =realm.where(Notifications.class).equalTo("id",c.getInt("id")).findAll();
                                if (localFeedsRealmResults.size() > 0) {
                                    notificationsArrayList.add(localFeedsRealmResults.get(0));
                                    isDataExist=true;
                                }
                                realm.commitTransaction();

                                if(!isDataExist) {
                                    realm.beginTransaction();
                                    Notifications notification = realm.createObject(Notifications.class);

                                    notification.setId(c.getInt("id"));
                                    notification.setBody(c.getString("body"));

                                    if (!c.isNull("notifier")) {
                                        JSONObject notifier = c.getJSONObject("notifier");
                                        notification.setUserID(notifier.getInt("id"));
                                        notification.setUsername(notifier.getString("username"));
                                        notification.setAvatarPic(notifier.getString("avatar"));
                                    }
                                    if (!c.isNull("redirect_to")) {
                                        JSONObject redirect = c.getJSONObject("redirect_to");
                                        notification.setRedirectID(redirect.getInt("id"));
                                        notification.setRedirectType(redirect.getString("typee"));
                                    }

                                    notificationsArrayList.add(notification);
                                    realm.commitTransaction();
                                }
                            }

                            if (notificationsArrayList.size() > 0) {
                                NotificationActivity.newNotifications = notificationsArrayList.size();
                                badge.show(true);
                                badge.setText("" + notificationsArrayList.size());
                            } else {
                                badge.hide(true);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {

                        }
                    }
                });
            }
        });
        realm.close();
    }


    void GetFeedsData(){

        if(!Constants.isNetworkAvailable(getActivity())) {
            Toast.makeText(getActivity(), "Check your internet connection", Toast.LENGTH_LONG).show();
        }

        realm.beginTransaction();
        RealmResults<LocalFeeds> localFeedsRealmResults =realm.where(LocalFeeds.class).findAll();
        homeLocalFeedsAdapter = new HomeLocalFeedsAdapter(localFeedsRealmResults.last(),getActivity());
        localFeedsRecyclerView.setAdapter(homeLocalFeedsAdapter);
        realm.commitTransaction();
    }

    public void ShowNotifications(){
//        badge.show();
        Notifications();
    }

    @Override
    public void onStart() {
        super.onStart();
        BusProvider.getInstance().register(this);
    }

    @Override
    public void onStop() {
        BusProvider.getInstance().unregister(this);
        super.onStop();
    }

    @Subscribe
    public void onMessageEvent(String event){
        notificationsArrayList=new ArrayList<>();
        Notifications();
    }

    @Subscribe
    public void onMessageEvent(int event){
//        notificationsArrayList=new ArrayList<>();
     //   Notifications();
    }


    @Override
    public void onResume() {
        super.onResume();
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        appBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (i == 0) {
            mSwipeRefreshLayout.setEnabled(true);
        } else {
            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    private void refreshContent(View view){

        if(!Constants.skipLogin){
            FetchMyFeedsData(view);
        }
        FetchAllLocalFeedsData(view);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
