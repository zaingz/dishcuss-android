package com.holygon.dishcuss.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.holygon.dishcuss.Activities.ProfilesDetailActivity;
import com.holygon.dishcuss.Model.Comment;
import com.holygon.dishcuss.Model.PhotoModel;
import com.holygon.dishcuss.Model.Reply;
import com.holygon.dishcuss.Model.ReviewModel;
import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.Model.UserBeenThere;
import com.holygon.dishcuss.Model.UserFollowing;
import com.holygon.dishcuss.Model.UserProfile;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;
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
 * Created by Naeem Ibrahim on 8/30/2016.
 */
public class PersonalProfileFragment extends Fragment{

    private ViewPager viewPager;
    TabLayout tabLayout;
    int userID;
    Realm realm;
    AppCompatActivity activity;
    UserProfile userProfile=new UserProfile();
    boolean dataAlreadyExists = false;
    User user;

    int reviewsCount=0, followersCount =0, commentsCount =0;
    de.hdodenhof.circleimageview.CircleImageView profileImage;
    TextView userName, userLocation, review_count, follower_count, comments_count;
    ProgressBar progressBar;
    public static boolean isCalledOnce=false;


    //*******************PROGRESS******************************
    private ProgressDialog mSpinner;

    private void showSpinner(String title) {
        mSpinner = new ProgressDialog(getActivity());
        mSpinner.setTitle(title);
        mSpinner.show();
        mSpinner.setCancelable(false);
        mSpinner.setCanceledOnTouchOutside(false);
    }

    private void DismissSpinner(){
        if(mSpinner!=null){
            mSpinner.dismiss();
        }
    }

//*******************PROGRESS******************************


    public PersonalProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.personal_profile_fragment, container, false);
        final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
        activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        realm = Realm.getDefaultInstance();

        TextView header=(TextView) rootView.findViewById(R.id.toolbar_name);
        progressBar=(ProgressBar)rootView.findViewById(R.id.native_progress_bar);
        header.setText("   Profile");

        GetUI(rootView);



            user= realm.where(User.class).findFirst();
            userID = user.getId();
//        if(PersonalProfileFragment.isCalledOnce || !Constants.isNetworkAvailable(getActivity())) {
        if(!Constants.isNetworkAvailable(getActivity())) {
            userProfile=GetUserData(userID);
            if(userProfile!=null)
            {
                reviewsCount=userProfile.getReviewsCount();
                followersCount=userProfile.getFollowersCount();
                commentsCount=userProfile.getCommentsCount();
                SetValues();
            }else {
                Log.e("","ELSE");
            }
        }
            if(!dataAlreadyExists) {

                UserData();
                PersonalProfileFragment.isCalledOnce=true;
            }


        return rootView;
    }

    void GetUI(View rootView){
        progressBar.setVisibility(View.VISIBLE);
        userName = (TextView)rootView.findViewById(R.id.user_profile_user_name);
        userLocation = (TextView) rootView.findViewById(R.id.user_profile_user_location);
        profileImage=(de.hdodenhof.circleimageview.CircleImageView)rootView.findViewById(R.id.user_detail_profile_image);
        review_count = (TextView) rootView.findViewById(R.id.user_profile_review_count);
        comments_count = (TextView) rootView.findViewById(R.id.user_profile_comments_count);
        follower_count = (TextView) rootView.findViewById(R.id.user_profile_followers_count);

        viewPager = (ViewPager)rootView.findViewById(R.id.viewpager);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);

        Button user_profile_follow_button=(Button)rootView.findViewById(R.id.user_profile_follow_button);
        user_profile_follow_button.setVisibility(View.INVISIBLE);
    }

    void SetValues(){

        userName.setText(userProfile.getName());
        userLocation.setText(userProfile.getLocation());

        review_count.setText(""+reviewsCount);
        comments_count.setText(""+commentsCount);
        follower_count.setText(""+followersCount);
        Constants.PicassoImageSrc(userProfile.getAvatar(),profileImage,getActivity());
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        tabLayout.setupWithViewPager(viewPager);
        progressBar.setVisibility(View.GONE);
    }


    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(activity.getSupportFragmentManager());
        adapter.clearAll();
        viewPager.setAdapter(null);
        adapter = new Adapter(activity.getSupportFragmentManager());
        adapter.addFragment(new AccountReviewsFragment(userID), "Reviews");
        adapter.addFragment(new AccountPhotosFragment(userID), "Photo");
        adapter.addFragment(new AccountFollowersFragment(userID), "Followers");
        adapter.addFragment(new AccountFollowingFragment(userID), "Following");
        adapter.addFragment(new AccountBeenThereFragment(userID), "Been There");
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

    void UserData() {


        showSpinner("Loading Data...");


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Get_User_data+userID)
                .addHeader("Authorization", "Token token="+user.getToken())
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr = response.body().string();

                Log.e("Object",""+objStr);

                if (getActivity()==null) {
                    return;
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject jsonObj = new JSONObject(objStr);

                            realm.beginTransaction();
                            RealmResults<UserProfile> userProfileRealmResults = realm.where(UserProfile.class).equalTo("id", userID).findAll();
                            userProfileRealmResults.deleteAllFromRealm();
                            realm.commitTransaction();



                            if(jsonObj.has("user"))
                            {
                                JSONObject userObj = jsonObj.getJSONObject("user");

                                realm.beginTransaction();
                                UserProfile userProfileRealm = realm.createObject(UserProfile.class);

                                userProfileRealm.setId(userObj.getInt("id"));
                                userProfileRealm.setName(userObj.getString("name"));
                                userProfileRealm.setUsername(userObj.getString("username"));
                                userProfileRealm.setEmail(userObj.getString("email"));
                                userProfileRealm.setAvatar(userObj.getString("avatar"));
                                userProfileRealm.setLocation(userObj.getString("location"));
                                userProfileRealm.setGender(userObj.getString("gender"));
                                userProfileRealm.setRole(userObj.getString("role"));

                                //Arrays
                                JSONArray jsonDataFollowingArray = userObj.getJSONArray("following");
                                JSONArray jsonDataFollowersArray = userObj.getJSONArray("followers");
                                JSONArray jsonDataPostsArray = userObj.getJSONArray("posts");
                                JSONArray jsonDataReviewsArray = userObj.getJSONArray("reviews");

                                reviewsCount=jsonDataReviewsArray.length();
                                followersCount =jsonDataFollowersArray.length();

                                for(int p=0;p<jsonDataPostsArray.length();p++){

                                    JSONObject postObj=jsonDataPostsArray.getJSONObject(p);

                                    JSONObject checkinObj = postObj.getJSONObject("checkin");

                                    if(checkinObj.has("restaurant")) {
                                        JSONObject restaurantObj = checkinObj.getJSONObject("restaurant");

                                        UserBeenThere userBeenThere = new UserBeenThere();
                                        userBeenThere.setId(restaurantObj.getInt("id"));
                                        userBeenThere.setRestaurantName(restaurantObj.getString("name"));
                                        userBeenThere.setRestaurantLocation(restaurantObj.getString("location"));
                                        userBeenThere.setCover_image_url(checkinObj.getString("restaurant_image"));
                                        userBeenThere.setBeenThereTime(checkinObj.getString("time"));
                                        final UserBeenThere beenThere = realm.copyToRealm(userBeenThere);
                                        userProfileRealm.getUserBeenThereRealmList().add(beenThere);
                                    }

                                    JSONArray jsonDataPhotosArray = postObj.getJSONArray("photos");
                                    for (int ph = 0; ph < jsonDataPhotosArray.length(); ph++) {
                                        JSONObject photo = jsonDataPhotosArray.getJSONObject(ph);
                                        PhotoModel photoModel = new PhotoModel();
                                        photoModel.setId(photo.getInt("id"));
                                        photoModel.setUrl(photo.getString("image_url"));
                                        final PhotoModel managedPhotoModel = realm.copyToRealm(photoModel);
                                        userProfileRealm.getPhotoModelRealmList().add(managedPhotoModel);
                                    }

                                    JSONArray jsonDataCommentsArray = postObj.getJSONArray("comments");
                                    commentsCount =jsonDataCommentsArray.length();
                                    for (int c = 0; c < jsonDataCommentsArray.length(); c++) {
                                        JSONObject commentObj = jsonDataCommentsArray.getJSONObject(c);
                                        Comment comment= realm.createObject(Comment.class);
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
                                        userProfileRealm.getCommentRealmList().add(managedComment);
                                    }
                                }

                                for (int r = 0; r < jsonDataReviewsArray.length();r++) {

                                    JSONObject reviewObj = jsonDataReviewsArray.getJSONObject(r);
                                    realm.commitTransaction();
                                    realm.beginTransaction();
                                    ReviewModel reviewModel=realm.createObject(ReviewModel.class);

                                    reviewModel.setReview_ID(reviewObj.getInt("id"));
                                    reviewModel.setReviewable_id(reviewObj.getInt("reviewable_id"));
                                    reviewModel.setReview_title(reviewObj.getString("title"));
                                    reviewModel.setUpdated_at(reviewObj.getString("updated_at"));
                                    reviewModel.setReview_summary(reviewObj.getString("summary"));
                                    reviewModel.setReviewable_type(reviewObj.getString("reviewable_type"));

                                    JSONObject reviewObjReviewer= reviewObj.getJSONObject("reviewer");

                                    reviewModel.setReview_reviewer_ID(reviewObjReviewer.getInt("id"));
                                    reviewModel.setReview_reviewer_Name(reviewObjReviewer.getString("name"));
                                    reviewModel.setReview_reviewer_Avatar(reviewObjReviewer.getString("avatar"));
                                    reviewModel.setReview_reviewer_time(reviewObjReviewer.getString("location"));

                                    JSONObject reviewOnObj=reviewObj.getJSONObject("review_on");
                                    if(reviewOnObj.has("id")) {
                                        reviewModel.setReview_On_ID(reviewOnObj.getInt("id"));
                                    }

                                    JSONArray reviewLikesArray = reviewObj.getJSONArray("likes");
                                    JSONArray reviewCommentsArray = reviewObj.getJSONArray("comments");
                                    JSONArray reviewShareArray = reviewObj.getJSONArray("reports");

                                    reviewModel.setReview_Likes_count(reviewLikesArray.length());
                                    reviewModel.setReview_comments_count(reviewCommentsArray.length());
                                    reviewModel.setReview_shares_count(reviewShareArray.length());



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
                                        reviewModel.getCommentRealmList().add(managedComment);
                                    }
                                    realm.commitTransaction();
                                    realm.beginTransaction();

                                    final ReviewModel managedReviewModel= realm.copyToRealm(reviewModel);
                                    userProfileRealm.getReviewModelRealmList().add(managedReviewModel);

                                }

                                userProfileRealm.setReviewsCount(reviewsCount);
                                userProfileRealm.setFollowersCount(followersCount);
                                userProfileRealm.setCommentsCount(commentsCount);

                                for(int fs=0;fs<jsonDataFollowingArray.length();fs++){
                                    JSONObject jsonFollowingObject = jsonDataFollowingArray.getJSONObject(fs);
                                    UserFollowing userFollowing=new UserFollowing();

                                    userFollowing.setId(jsonFollowingObject.getInt("id"));
                                    userFollowing.setLikesCount(jsonFollowingObject.getInt("likees_count"));
                                    userFollowing.setFollowerCount(jsonFollowingObject.getInt("followers_count"));
                                    userFollowing.setFollowingCount(jsonFollowingObject.getInt("followees_count"));

                                    userFollowing.setName(jsonFollowingObject.getString("name"));
                                    userFollowing.setUsername(jsonFollowingObject.getString("username"));
                                    userFollowing.setAvatar(jsonFollowingObject.getString("avatar"));
                                    userFollowing.setLocation(jsonFollowingObject.getString("location"));
                                    userFollowing.setEmail(jsonFollowingObject.getString("email"));
                                    userFollowing.setGender(jsonFollowingObject.getString("gender"));
                                    userFollowing.setRole(jsonFollowingObject.getString("role"));
                                    userFollowing.setReferral_code(jsonFollowingObject.getString("referal_code"));

                                    final UserFollowing managedUserFollowing = realm.copyToRealm(userFollowing);
                                    userProfileRealm.getUserFollowingRealmList().add(managedUserFollowing);
                                }

                                for(int fr=0;fr<jsonDataFollowersArray.length();fr++){
                                    JSONObject jsonFollowingObject = jsonDataFollowersArray.getJSONObject(fr);

                                    UserFollowing userFollowing=new UserFollowing();

                                    userFollowing.setId(jsonFollowingObject.getInt("id"));
                                    userFollowing.setLikesCount(jsonFollowingObject.getInt("likees_count"));
                                    userFollowing.setFollowerCount(jsonFollowingObject.getInt("followers_count"));
                                    userFollowing.setFollowingCount(jsonFollowingObject.getInt("followees_count"));

                                    userFollowing.setName(jsonFollowingObject.getString("name"));
                                    userFollowing.setUsername(jsonFollowingObject.getString("username"));
                                    userFollowing.setAvatar(jsonFollowingObject.getString("avatar"));
                                    userFollowing.setLocation(jsonFollowingObject.getString("location"));
                                    userFollowing.setEmail(jsonFollowingObject.getString("email"));
                                    userFollowing.setGender(jsonFollowingObject.getString("gender"));
                                    userFollowing.setRole(jsonFollowingObject.getString("role"));
                                    userFollowing.setReferral_code(jsonFollowingObject.getString("referal_code"));

                                    final UserFollowing managedUserFollowing = realm.copyToRealm(userFollowing);
                                    userProfileRealm.getUserFollowersRealmList().add(managedUserFollowing);
                                }


                                userProfile=userProfileRealm;
                                realm.commitTransaction();
                                SetValues();
                                DismissSpinner();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        realm.close();
                    }
                });
            }
        });
    }

    UserProfile GetUserData(int uid){
        realm = Realm.getDefaultInstance();
        RealmResults<UserProfile> userProfiles = realm.where(UserProfile.class).equalTo("id", uid).findAll();
        Log.e("Count",""+userProfiles.size());
        if(userProfiles.size()>0){
            dataAlreadyExists=true;
            return userProfiles.get(userProfiles.size()-1);
        }
        return null;
    }
}
