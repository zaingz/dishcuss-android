package com.holygon.dishcuss.ExtraClasses;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.holygon.dishcuss.Activities.NotificationActivity;
import com.holygon.dishcuss.Activities.PunditSelectionActivity;
import com.holygon.dishcuss.Activities.SelectRestaurantSearchActivity;
import com.holygon.dishcuss.Adapters.HomeLocalFeedsAdapter;
import com.holygon.dishcuss.Adapters.HomePeopleAroundAdapter;
import com.holygon.dishcuss.Fragments.HomeViewPagerFragment;
import com.holygon.dishcuss.Model.Comment;
import com.holygon.dishcuss.Model.FeaturedRestaurant;
import com.holygon.dishcuss.Model.LocalFeedCheckIn;
import com.holygon.dishcuss.Model.LocalFeedReview;
import com.holygon.dishcuss.Model.LocalFeeds;
import com.holygon.dishcuss.Model.MyFeeds;
import com.holygon.dishcuss.Model.Notifications;
import com.holygon.dishcuss.Model.PhotoModel;
import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.BadgeView;
import com.holygon.dishcuss.Utils.URLs;

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
 * Created by Naeem Ibrahim on 7/21/2016.
 */
public class HomeFragment extends Fragment {

    BadgeView badge;
    Realm realm;
    private ViewPager viewPager;
    AppCompatActivity activity;
    RecyclerView localFeedsRecyclerView,myFeedsRecyclerView;
    private RecyclerView.LayoutManager localFeedsLayoutManager,myFeedsLayoutManager;
    RelativeLayout local_feeds_layout,my_feeds_layout;
    TextView local_feeds_text,my_feeds_text;

    ArrayList<MyFeeds> myFeedsArrayList;
    boolean dataAlreadyExists=false;

    private int NUM_PAGES =1;
    private List<ImageView> dots;
    HomePeopleAroundAdapter homePeopleAroundAdapter;
    HomeLocalFeedsAdapter homeLocalFeedsAdapter;


    ImageView home_fragment_image_search;

    ArrayList<FeaturedRestaurant> featuredRestaurantArrayList=new ArrayList<>();
    RealmResults<FeaturedRestaurant> featuredRestaurantRealmResults;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_fragment, container, false);
        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        activity= (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        home_fragment_image_search=(ImageView)rootView.findViewById(R.id.home_fragment_image_search);

//        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        viewPager = (ViewPager)rootView.findViewById(R.id.home_viewpager);

        localFeedsRecyclerView = (RecyclerView) rootView.findViewById(R.id.local_feeds_recycler_view);
        myFeedsRecyclerView = (RecyclerView) rootView.findViewById(R.id.my_feeds_recycler_view);

        local_feeds_layout=(RelativeLayout)rootView.findViewById(R.id.local_feeds_layout);
        my_feeds_layout=(RelativeLayout)rootView.findViewById(R.id.my_feeds_layout);

        my_feeds_text=(TextView) rootView.findViewById(R.id.my_feeds_text);
        local_feeds_text=(TextView) rootView.findViewById(R.id.local_feeds_text);


        //Local Feed
        localFeedsLayoutManager = new LinearLayoutManager(activity);
        localFeedsRecyclerView.setLayoutManager(localFeedsLayoutManager);
//        ArrayList<String> itemsData = new ArrayList<>();
//
//        for (int i = 0; i < 50; i++) {
//            itemsData.add("Local Feeds " + i + " / Item " + i);
//        }
        localFeedsRecyclerView.setNestedScrollingEnabled(false);
//        homeLocalFeedsAdapter = new HomeLocalFeedsAdapter(emptyLocalFeeds,getActivity());
//        localFeedsRecyclerView.setAdapter(homeLocalFeedsAdapter);
        FetchLocalFeedsData();



        //My Feed
        myFeedsLayoutManager = new LinearLayoutManager(activity);
        myFeedsRecyclerView.setLayoutManager(myFeedsLayoutManager);
        localFeedsRecyclerView.setNestedScrollingEnabled(true);
        myFeedsArrayList=new ArrayList<>();
        homePeopleAroundAdapter = new HomePeopleAroundAdapter(myFeedsArrayList,getActivity());
        myFeedsRecyclerView.setAdapter(homePeopleAroundAdapter);
        FetchMyFeedsData();


        //Features Restaurant
        realm =Realm.getDefaultInstance();
        featuredRestaurantRealmResults = realm.where(FeaturedRestaurant.class).findAll();
        featuredRestaurantArrayList.addAll(featuredRestaurantRealmResults);
        FeaturedRestaurantData(rootView);

        local_feeds_text.setTextColor(Color.WHITE);
        my_feeds_text.setTextColor(getResources().getColor(R.color.black_3));

        myFeedsRecyclerView.setVisibility(View.GONE);
        localFeedsRecyclerView.setVisibility(View.VISIBLE);

        local_feeds_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                local_feeds_layout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                my_feeds_layout.setBackgroundColor(Color.WHITE);
                local_feeds_text.setTextColor(Color.WHITE);
                my_feeds_text.setTextColor(getResources().getColor(R.color.black_3));

                myFeedsRecyclerView.setVisibility(View.GONE);
                localFeedsRecyclerView.setVisibility(View.VISIBLE);
            }
        });

        my_feeds_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                my_feeds_layout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                local_feeds_layout.setBackgroundColor(Color.WHITE);
                my_feeds_text.setTextColor(Color.WHITE);
                local_feeds_text.setTextColor(getResources().getColor(R.color.black_3));


                myFeedsRecyclerView.setVisibility(View.VISIBLE);
                localFeedsRecyclerView.setVisibility(View.GONE);
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

        home_fragment_image_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(getActivity(), SelectRestaurantSearchActivity.class);
                startActivity(intent);
            }
        });

        ImageView target =(ImageView) rootView.findViewById(R.id.image_notification);
//        ImageView ic_bookMark =(ImageView) rootView.findViewById(R.id.image_bookmark_icon);
        badge = new BadgeView(getActivity(), target);
        Notifications();

        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NotificationActivity.notificationsArrayList.size()>0) {
                    Intent intent = new Intent(getActivity(), NotificationActivity.class);
                    startActivity(intent);
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
            dot.setImageDrawable(getResources().getDrawable(R.drawable.pager_dot_not_selected1));
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
            int drawableId = (i==idx)?(R.drawable.pager_dot_selected1):(R.drawable.pager_dot_not_selected1);
            Drawable drawable = res.getDrawable(drawableId);
            dots.get(i).setImageDrawable(drawable);
        }
    }

    void FeaturedRestaurantData(final View view){

        OkHttpClient client = new OkHttpClient();
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

                try
                {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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



                                for (int i = 0; i < jsonDataArray.length(); i++) {
                                    JSONObject featureRestaurantObj = jsonDataArray.getJSONObject(i);

                                    dataAlreadyExists=false;

                                    for(int r=0;r<featuredRestaurantRealmResults.size();r++){

                                        if(featuredRestaurantRealmResults.get(i).getId()==featureRestaurantObj.getInt("id")){
                                            dataAlreadyExists=true;
                                        }
                                    }

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

                                        Log.e("Loop","Loop"+i);
                                        featuredRestaurantArrayList.add(featuredRestaurant);
                                        realm.commitTransaction();
                                    }
                                }

                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                            realm.close();
//                            Log.e("feaResArrayList",""+featuredRestaurantArrayList.size());

                            NUM_PAGES=featuredRestaurantArrayList.size();
                            addDots(view);
                            setupViewPager(viewPager,featuredRestaurantArrayList);
                            selectDot(0);
                        }
                    });
            }
        });
    }


    void FetchLocalFeedsData(){

        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        // Persist your data in a transaction
        realm.beginTransaction();
        User user = realm.where(User.class).findFirst();
        Log.e("",""+user.getToken());
        realm.commitTransaction();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.LocalFeeds_Restaurant_URL)
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

                try
                {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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

                                JSONObject reviewOnObj = jsonDataReviewObj.getJSONObject("review_on");

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
                                    localFeedReview.setSummary(jsonDataReviewObj.getString("summary"));
                                    if(!jsonDataReviewObj.isNull("rating")){
                                        localFeedReview.setRating(jsonDataReviewObj.getInt("rating"));
                                    }
                                    localFeedReview.setReviewable_id(jsonDataReviewObj.getInt("reviewable_id"));
                                    localFeedReview.setReviewable_type(jsonDataReviewObj.getString("reviewable_type"));

                                    localFeedReview.setReviewOnID(reviewOnObj.getInt("id"));
                                    localFeedReview.setReviewOnName(reviewOnObj.getString("name"));
                                    localFeedReview.setReviewOnLocation(reviewOnObj.getString("location"));

                                    localFeedReview.setReviewImage(jsonDataReviewObj.getString("image"));

                                    localFeedReview.setReviewerID(reviewerObj.getInt("id"));
                                    localFeedReview.setReviewerName(reviewerObj.getString("name"));
                                    localFeedReview.setReviewerLocation(reviewerObj.getString("location"));
                                    localFeedReview.setReviewerAvatar(reviewerObj.getString("avatar"));

                                    localFeedReview.setReviewLikesCount(reviewLikesArray.length());
                                    localFeedReview.setReviewCommentCount(reviewCommentsArray.length());
                                    localFeedReview.setReviewSharesCount(reviewShareArray.length());


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

                                JSONObject restaurantObj = checkinObj.getJSONObject("restaurant");

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

                                    localFeedCheckIn.setCheckInOnID(restaurantObj.getInt("id"));
                                    localFeedCheckIn.setCheckInOnName(restaurantObj.getString("name"));
                                    localFeedCheckIn.setCheckInOnLocation(restaurantObj.getString("location"));

                                    localFeedCheckIn.setCheckInImage(checkinObj.getString("restaurant_image"));

                                    for (int p = 0; p < checkinPhotoArray.length(); p++) {

                                        JSONObject photo = checkinPhotoArray.getJSONObject(p);

                                        PhotoModel photoModel=new PhotoModel();
                                        photoModel.setId(photo.getInt("id"));
                                        photoModel.setUrl(photo.getString("image_url"));

                                        final PhotoModel managedPhotoModel = realm.copyToRealm(photoModel);
                                        localFeedCheckIn.getPhotoModels().add(managedPhotoModel);
                                    }

                                    localFeedCheckIn.setReviewLikesCount(checkinLikesArray.length());
                                    localFeedCheckIn.setReviewCommentCount(checkinCommentsArray.length());
                                    localFeedCheckIn.setReviewSharesCount(checkinPhotoArray.length());




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

                                        final Comment managedComment = realm.copyToRealm(comment);
                                        localFeedCheckIn.getCommentRealmList().add(managedComment);
                                    }

                                    realm.commitTransaction();
                                    realm.beginTransaction();
                                    localFeeds.getLocalFeedCheckInRealmList().add(localFeedCheckIn);

                                }
                            }
                            homeLocalFeedsAdapter = new HomeLocalFeedsAdapter(localFeeds,getActivity());
                            localFeedsRecyclerView.setAdapter(homeLocalFeedsAdapter);

                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                        realm.commitTransaction();
                        realm.close();
                    }
                });
            }
        });
    }


    void FetchMyFeedsData(){
        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        // Persist your data in a transaction

        User user = realm.where(User.class).findFirst();
        Log.e("UserT",""+user.getToken());

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.MyFeed_Restaurant_URL)
                .addHeader("Authorization", "Token token="+user.getToken())
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String objStr=response.body().string();
                Log.e("ObjStr",""+objStr);
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

                        myFeedsArrayList.add(myFeeds);
                    }

                    try
                    {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    /** check if activity still exist */
                    if (getActivity() == null) {
                        return;
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            homePeopleAroundAdapter = new HomePeopleAroundAdapter(myFeedsArrayList,getActivity());
                            myFeedsRecyclerView.setAdapter(homePeopleAroundAdapter);
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally
                {

                }
            }
        });
        realm.close();


    }


    void Notifications(){

        NotificationActivity.notificationsArrayList=new ArrayList<>();
        // Get a Realm instance for this thread
        realm = Realm.getDefaultInstance();
        // Persist your data in a transaction

        User user = realm.where(User.class).findFirst();

        OkHttpClient client = new OkHttpClient();
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

                String objStr=response.body().string();
                try {
                    JSONObject jsonObj = new JSONObject(objStr);
                    JSONArray jsonDataArray=jsonObj.getJSONArray("users");

                    for (int i = 0; i < jsonDataArray.length(); i++) {

                        JSONObject c = jsonDataArray.getJSONObject(i);

                        Notifications notification=new Notifications();

                        notification.setId(c.getInt("id"));
                        notification.setBody(c.getString("body"));

                        if(!c.isNull("notifier")) {

                            JSONObject notifier = c.getJSONObject("notifier");
                            notification.setUserID(notifier.getInt("id"));
                            notification.setUsername(notifier.getString("username"));
                            notification.setAvatarPic(notifier.getString("avatar"));

                        }

                        NotificationActivity.notificationsArrayList.add(notification);
                    }

                    try
                    {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(getActivity()==null){
                        return;
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(NotificationActivity.notificationsArrayList.size()>0) {
                                badge.show(true);
                                badge.setText("" + NotificationActivity.notificationsArrayList.size());
                            }else {
                                badge.hide(true);
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finally
                {

                }
            }
        });
        realm.close();


    }

    void  Test(){
//        LocalFeedReview localFeedReview=realm.createObject(LocalFeedReview.class);
//
//        localFeedReview.setReviewID(checkinObj.getInt("id"));
//        localFeedReview.setUpdated_at(jsonDataCheckInObj.getString("updated_at"));
//        localFeedReview.setTitle(jsonDataCheckInObj.getString("title"));
//        localFeedReview.setSummary(jsonDataCheckInObj.getString("status"));
//        localFeedReview.setRating(jsonDataReviewObj.getInt("rating"));
//        localFeedReview.setReviewable_id(jsonDataReviewObj.getInt("reviewable_id"));
//        localFeedReview.setReviewable_type(jsonDataReviewObj.getString("reviewable_type"));
//
//        localFeedReview.setReviewOnID(restaurantObj.getInt("id"));
//        localFeedReview.setReviewOnName(restaurantObj.getString("name"));
//        localFeedReview.setReviewOnLocation(restaurantObj.getString("location"));
//
//        localFeedReview.setReviewImage(checkinObj.getString("restaurant_image"));
//
//        localFeedReview.setReviewerID(writerObj.getInt("id"));
//        localFeedReview.setReviewerName(writerObj.getString("name"));
//        localFeedReview.setReviewerLocation(writerObj.getString("location"));
//        localFeedReview.setReviewerAvatar(writerObj.getString("avatar"));
//
//        localFeedReview.setReviewLikesCount(checkinLikesArray.length());
//        localFeedReview.setReviewCommentCount(checkinCommentsArray.length());
//        localFeedReview.setReviewSharesCount(checkinPhotoArray.length());
    }
}