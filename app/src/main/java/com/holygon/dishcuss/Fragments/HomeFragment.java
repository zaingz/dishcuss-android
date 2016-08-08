package com.holygon.dishcuss.Fragments;

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

import com.holygon.dishcuss.Adapters.HomeLocalFeedsAdapter1;
import com.holygon.dishcuss.Adapters.HomeMyFeedsAdapter1;
import com.holygon.dishcuss.Model.FeaturedRestaurant;
import com.holygon.dishcuss.Model.LocalFeedCheckIn;
import com.holygon.dishcuss.Model.LocalFeedReview;
import com.holygon.dishcuss.Model.LocalFeeds;
import com.holygon.dishcuss.Model.MyFeeds;
import com.holygon.dishcuss.Model.PhotoModel;
import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.R;
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
    HomeMyFeedsAdapter1 homeMyFeedsAdapter1;
    HomeLocalFeedsAdapter1 homeLocalFeedsAdapter1;

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
//        homeLocalFeedsAdapter1 = new HomeLocalFeedsAdapter1(emptyLocalFeeds,getActivity());
//        localFeedsRecyclerView.setAdapter(homeLocalFeedsAdapter1);
        FetchLocalFeedsData();



        //My Feed
        myFeedsLayoutManager = new LinearLayoutManager(activity);
        myFeedsRecyclerView.setLayoutManager(myFeedsLayoutManager);
        localFeedsRecyclerView.setNestedScrollingEnabled(true);
        myFeedsArrayList=new ArrayList<>();
        homeMyFeedsAdapter1 = new HomeMyFeedsAdapter1(myFeedsArrayList,getActivity());
        myFeedsRecyclerView.setAdapter(homeMyFeedsAdapter1);
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


    void FetchMyFeedsData(){
        // Get a Realm instance for this thread
         realm = Realm.getDefaultInstance();
        // Persist your data in a transaction

        User user = realm.where(User.class).findFirst();
        Log.e("",""+user.getToken());

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

                   getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            homeMyFeedsAdapter1 = new HomeMyFeedsAdapter1(myFeedsArrayList,getActivity());
                            myFeedsRecyclerView.setAdapter(homeMyFeedsAdapter1);
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
                            Log.e("feaResArrayList",""+featuredRestaurantArrayList.size());

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
                .addHeader("Authorization", "Token token=EAADM1jfMVKgBAPferax53OKvY39usRolF6O2p3KpUpDNHvyQzqVNlySqwYXM7K57Di8EMDgsZCMovL2BpnJpZAZAFGYRvur9XpKt1RRIJjgt6WEpdZB6xdjpCAN6tZC4oEugljOzGbXwqdUEDNgYaY7Sjmln88ycrobXRXn8K3xmql6EjJE33iMFETJuHSErm8aIMh5d7zj7i8gzlftUA")
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr=response.body().string();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject jsonObj = new JSONObject(objStr);
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
                                    localFeedReview.setRating(jsonDataReviewObj.getInt("rating"));
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

                                    localFeedCheckIn.setCheckInID(checkinObj.getInt("id"));
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
                                    localFeeds.getLocalFeedCheckInRealmList().add(localFeedCheckIn);

                                }
                            }
                            homeLocalFeedsAdapter1 = new HomeLocalFeedsAdapter1(localFeeds,getActivity());
                            localFeedsRecyclerView.setAdapter(homeLocalFeedsAdapter1);

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
