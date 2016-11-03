package com.dishcuss.foodie.hub.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dishcuss.foodie.hub.Activities.BookmarkActivity;
import com.dishcuss.foodie.hub.Activities.LoginActivity;
import com.dishcuss.foodie.hub.Activities.NotificationActivity;
import com.dishcuss.foodie.hub.Activities.PunditSelectionActivity;
import com.dishcuss.foodie.hub.Activities.SearchUserAndRestaurantActivity;
import com.dishcuss.foodie.hub.Helper.BusProvider;
import com.dishcuss.foodie.Model.Notifications;
import com.dishcuss.foodie.Model.User;
import com.dishcuss.foodie.hub.R;
import com.dishcuss.foodie.hub.Utils.BadgeView;
import com.dishcuss.foodie.hub.Utils.Constants;
import com.dishcuss.foodie.hub.Utils.URLs;
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
 * Created by Naeem Ibrahim on 7/21/2016.
 */
public class NearbyFragment extends Fragment {

    private ViewPager viewPager;
    AppCompatActivity activity;
    TabLayout tabLayout;
    Realm realm;
    public NearbyFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.nearby_fragment, container, false);
        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);



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
//        ImageView ic_bookMark =(ImageView) rootView.findViewById(R.id.image_bookmark_icon);
        HomeFragment2.badge = new BadgeView(getActivity(), target);

        if(!Constants.skipLogin){

            Log.e("Notifications",""+NotificationActivity.newNotifications);
            if (NotificationActivity.newNotifications > 0) {
                HomeFragment2.badge.show(true);
                HomeFragment2.badge.setText("" + NotificationActivity.newNotifications);
            } else {
                HomeFragment2.badge.hide(true);
            }
        }

        target.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Constants.skipLogin) {
                        Intent intent = new Intent(getActivity(), NotificationActivity.class);
                        startActivity(intent);
                        if(HomeFragment2.badge.isShown()) {
                            HomeFragment2.badge.hide(true);
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



        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(activity.getSupportFragmentManager());
        adapter.clearAll();
        viewPager.setAdapter(null);
        adapter = new Adapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new NearbyFragmentSearch(), "List");
        adapter.addFragment(new NearbyFragmentGoogleMap(), "Map");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();
        FragmentManager fragmentManager;

        public Adapter(FragmentManager fm) {
            super(fm);
            fragmentManager=fm;
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }

        public void clearAll() //Clear all page
        {
            for(int i=0; i<mFragments.size(); i++)
                fragmentManager.beginTransaction().remove(mFragments.get(i)).commit();
            mFragments.clear();
        }
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
        Notifications();
    }
    void Notifications(){

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
                            ArrayList<Notifications> notificationsArrayList=new ArrayList<>();

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
                                HomeFragment2.badge.show(true);
                                HomeFragment2.badge.setText("" + notificationsArrayList.size());
                            } else {
                                HomeFragment2.badge.hide(true);
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
}
