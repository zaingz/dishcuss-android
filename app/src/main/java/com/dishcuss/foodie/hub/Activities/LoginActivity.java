package com.dishcuss.foodie.hub.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.github.javiersantos.appupdater.AppUpdater;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.dishcuss.foodie.hub.Fragments.LoginIntroFragment;
import com.dishcuss.foodie.hub.Models.Comment;
import com.dishcuss.foodie.hub.Models.FbDataModel;
import com.dishcuss.foodie.hub.Models.FoodItems;
import com.dishcuss.foodie.hub.Models.KhabaHistoryModel;
import com.dishcuss.foodie.hub.Models.LocalFeedCheckIn;
import com.dishcuss.foodie.hub.Models.LocalFeedReview;
import com.dishcuss.foodie.hub.Models.LocalFeeds;
import com.dishcuss.foodie.hub.Models.Notifications;
import com.dishcuss.foodie.hub.Models.Restaurant;
import com.dishcuss.foodie.hub.Models.RestaurantForStatus;
import com.dishcuss.foodie.hub.Models.ReviewModel;
import com.dishcuss.foodie.hub.Models.UserProfile;
import com.dishcuss.foodie.hub.R;
import com.dishcuss.foodie.hub.Utils.Constants;
import com.dishcuss.foodie.hub.Utils.URLs;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener{

    // Facebook
    Button facebookLoginButton;
    LoginButton fbButton;
    FbDataModel fbDataModel;
    CallbackManager callbackManager;
    AlertDialog.Builder alert;

    //Google Plus Login
    Button googleLoginButton;
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;

    //Twitter Login
    Button twitterLoginButton;
    //This is your KEY and SECRET
    private static final String TWITTER_KEY = "fhRjMr0bKs82EfLrrgKfASg8l";
    private static final String TWITTER_SECRET = "9TtBWmEoE7KSKCdKschugrGwtq2ztkDQxGAJ3ocfm9BJefXqZE";
    //Tags to send the username and image url to next activity using intent
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PROFILE_IMAGE_URL = "image_url";
//    TwitterLoginButton twitterLogin;
    TwitterAuthClient mTwitterAuthClient;

    boolean isFacebookCalled=false;


    Button nativeLoginButton;
    private ViewPager viewPager;
    private final static int NUM_PAGES = 5;
    private List<ImageView> dots;
    OkHttpClient client;
    Realm realm;
    String message="";
    JSONArray rawName;

    String Str_Referral_Code="", email="",name="",username="",location="",profilePicURL="",gender="",provider="",uid="",profileURL="",token="",expires_at="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        setContentView(R.layout.login_app_intro);
        alert = new AlertDialog.Builder(this);
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        TextView skipLogin=(TextView)findViewById(R.id.skip_login_tv);

        AppUpdater appUpdater = new AppUpdater(this);
        appUpdater.start();

        facebookLoginButton = (Button) findViewById(R.id.facebook_login_button);
        googleLoginButton = (Button) findViewById(R.id.google_login_button);
        fbButton = (LoginButton) findViewById(R.id.facebook_login_button_invisible);
        client = new OkHttpClient();
        setupViewPager(viewPager);
        addDots();

        skipLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Constants.skipLogin=true;
                Intent intent=new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        DeleteAll();

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

        selectDot(0);



        //Facebook Login Button

        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();



        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(
                        LoginActivity.this,
                        Arrays.asList("email", "public_profile", "user_friends","user_location"));
                fbButton.performClick();
                fbButton.setPressed(true);
                fbButton.invalidate();
                fbButton.registerCallback(callbackManager, mCallBack);
                fbButton.setPressed(false);
                fbButton.invalidate();
            }
        });
//        fbButton.setReadPermissions("email");
//        fbButton.setReadPermissions(Arrays.asList("user_location"));
//        fbButton.setReadPermissions("public_profile", "email", "user_friends","user_location");


        //Google Login
        GoogleLogin();
        googleLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        //Twitter Login
        TwitterLoginProcess();

        //Native Login
        NativeLogin();

        if(isLoggedIn()) {
            LoginManager.getInstance().logOut();
        }

//        AlertDialog();
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(LoginIntroFragment.newInstance(R.layout.login_intro_fragment,R.drawable.intro1_logo,R.drawable.ic_bell));
        adapter.addFragment(LoginIntroFragment.newInstance(R.layout.login_intro_fragment,R.drawable.intro2_logo,R.drawable.ic_bell));
        adapter.addFragment(LoginIntroFragment.newInstance(R.layout.login_intro_fragment,R.drawable.intro3_logo,R.drawable.ic_bell));
        adapter.addFragment(LoginIntroFragment.newInstance(R.layout.login_intro_fragment,R.drawable.intro4_logo,R.drawable.ic_bell));
        adapter.addFragment(LoginIntroFragment.newInstance(R.layout.login_intro_fragment,R.drawable.intro5_logo,R.drawable.ic_bell));
        viewPager.setAdapter(adapter);
    }




    class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
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
    }


    public void addDots() {
        dots = new ArrayList<>();
        LinearLayout dotsLayout = (LinearLayout)findViewById(R.id.dots);

        for (int i = 0; i < NUM_PAGES; i++) {
            ImageView dot = new ImageView(this);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        //Adding the login result back to the button
        mTwitterAuthClient.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    //Facebook Callback
    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(final LoginResult loginResult) {


            GraphRequestAsyncTask graphRequestAsyncTask = new GraphRequest(
                    loginResult.getAccessToken(),
                    //AccessToken.getCurrentAccessToken(),
                    "/me/friends",
                    null,
                    HttpMethod.GET,
                    new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            Log.e("response: ", response + "");
                            try {
                                rawName=response.getJSONObject().getJSONArray("data");

                                Constants.SetUserFacebookFriends(LoginActivity.this,rawName.toString());

                                Log.e("jsondata",""+Constants.GetUserFacebookFriends(LoginActivity.this));

//                                rawName= Constants.GetUserFacebookFriends(FindEatBuddiesLoginFirstActivity.this);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            ).executeAsync();

            // App code
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {

//                            Log.e("response: ", response + "");
//                            Log.e("object: ", object.toString());

                            try {

                                String newUrlString = object.getString("name").toString().replaceAll(" ", "_");
                                if(object.has("email")){
                                    email=object.getString("email").toString();
                                }else {
                                    email=object.getString("id").toString()+"@facebook.com";
                                }
                                name= object.getString("name").toString();
                                uid=object.getString("id").toString();
                                username=newUrlString;
                                profilePicURL="https://graph.facebook.com/"+ uid +"/picture?type=large";
                                if(object.has("location")){
                                    location=object.getString("location").toString();
                                }

                                int rand= (int) (5 + (Math.random() * (99909 - 10000)));
                                provider="Facebook";
                                profileURL="https://www.facebook.com/app_scoped_user_id/"+uid+"/";
//                                token=loginResult.getAccessToken().getToken();
                                token="kfbookes"+uid+rand;
                                Log.e("Token ",""+token.length());
                                gender=object.getString("gender").toString();
                                expires_at="";

                                if(!isFacebookCalled){
                                    SocialLoginSendDataOnServer();
                                    isFacebookCalled=true;
                                }

//                                String imageUrl = "https://graph.facebook.com/"+ loginUser.loginID +"/picture?type=large";
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }

                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException error) {

        }
    };



    //Google +
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void GoogleLogin(){


        // [START configure_signin]
        // Configure sign-in to request the loginUser's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,0, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
//
//        SignInButton signInButton = (SignInButton) findViewById(R.id.btn_sign_in_plus);
//        signInButton.setSize(SignInButton.SIZE_STANDARD);
//        signInButton.setScopes(gso.getScopeArray());
    }


    @Override
    protected void onStart() {
        super.onStart();
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the loginUser's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the loginUser has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the loginUser silently.  Cross-device
            // single sign-on will occur in this branch.

            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        try {
            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();

                uid = acct.getId();
                email = acct.getEmail();
                name = acct.getDisplayName();
                username = acct.getFamilyName()+acct.getId();
                gender = "";
                profilePicURL = String.valueOf(acct.getPhotoUrl());
                provider="Google";
                int rand= (int) (5 + (Math.random() * (99909 - 10000)));
//                token=acct.getIdToken();
                token="goelog"+uid+rand;
                location="";
                profileURL=String.valueOf(acct.getPhotoUrl());
                expires_at="";

                Log.e("G token",""+token);

                SocialLoginSendDataOnServer();



            } else {
                // Signed out, show unauthenticated UI.

            }
        }catch (Exception e){
            Log.d(TAG, "Exception:" + e.getMessage());
        }
    }
    // [END handleSignInResult]

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }
    // [END signIn]

    // [START signOut]
    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }
    // [END signOut]

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        //  updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]


    //Google + End


    //Twitter Login
    void TwitterLoginProcess(){
        //Initializing TwitterAuthConfig, these two line will also added automatically while configuration we did
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        //Initializing twitter login button
//        twitterLogin = (TwitterLoginButton) findViewById(R.id.twitterLogin);
        twitterLoginButton=(Button) findViewById(R.id.twitter_login_button);
        mTwitterAuthClient= new TwitterAuthClient();

        twitterLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mTwitterAuthClient.authorize(LoginActivity.this, new com.twitter.sdk.android.core.Callback<TwitterSession>() {

                    @Override
                    public void success(Result<TwitterSession> twitterSessionResult) {
                        // Success
                        login(twitterSessionResult);
                    }

                    @Override
                    public void failure(TwitterException e) {
                        e.printStackTrace();
                    }
                });


            }
        });

    }

    //The login function accepting the result object
    public void login(Result<TwitterSession> result) {

        //Creating a twitter session with result's data
        TwitterSession session = result.data;

        //Getting the username from session
        username = session.getUserName();

        //This code will fetch the profile image URL
        //Getting the account service of the user logged in
        Twitter.getApiClient(session).getAccountService()
                .verifyCredentials(true, false, new Callback<User>() {
                    @Override
                    public void failure(TwitterException e) {
                        //If any error occurs handle it here
                    }

                    @Override
                    public void success(Result<User> userResult) {
                        //If it succeeds creating a User object from userResult.data
                        User tUser = userResult.data;
                        Log.e("userResult :: ",userResult.response.toString());
                        //Getting the profile image url
//                        String profileImage = tUser.profileImageUrl.replace("_normal", "");
//                        Log.e("Twitter",username+" "+profileImage);
                        if(tUser.email!=null){
                            email=tUser.email;
                        }else {
                            email=username+"@twitter.com";
                        }

                        name=tUser.name;
                        location=tUser.location;
                        provider="Twitter";
                        gender="";
                        expires_at="";
                        uid=tUser.idStr;
                        int rand= (int) (5 + (Math.random() * (99909 - 10000)));
                        token="twsaiswahtter"+tUser.idStr+rand;
                        profilePicURL=tUser.profileImageUrl;
                        profileURL=tUser.profileImageUrl;
                        SocialLoginSendDataOnServer();
                    }
                });
    }

    //Native Login
    void NativeLogin(){
        nativeLoginButton=(Button)findViewById(R.id.native_login_button);
        nativeLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });
    }


    void SocialLoginSendDataOnServer(){

        FormBody body = new FormBody.Builder()
                .add("user[email]",email)
                .add("user[name]", name)
                .add("user[location]", location)
                .add("user[username]", username)
                .add("user[avatar]", profilePicURL)
                .add("user[gender]", gender)
                .add("user[referal_code]", Str_Referral_Code)
                .add("user[identities_attributes][0][provider]", provider)
                .add("user[identities_attributes][0][uid]", uid)
                .add("user[identities_attributes][0][url]", profileURL)
                .add("user[identities_attributes][0][token]", token)
                .add("user[identities_attributes][0][expires_at]", expires_at)
                .build();

        Request request = new Request.Builder()
                .url(URLs.Native_SignIn_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try{
                    String obj=response.body().string();
                    Log.e("Server_Res",obj.toString());
                    JSONObject jsonObject=new JSONObject(obj);
                    if(jsonObject.has("user")){
                        JSONObject usersJsonObject = jsonObject.getJSONObject("user");
//                   // Get a Realm instance for this thread
                        realm = Realm.getDefaultInstance();
                        // Persist your data in a transaction
                        realm.beginTransaction();
                        com.dishcuss.foodie.hub.Models.User user = realm.createObject(com.dishcuss.foodie.hub.Models.User.class); // Create managed objects directly
                        user.setId(usersJsonObject.getInt("id"));
                        user.setName(usersJsonObject.getString("name"));
                        user.setDob(usersJsonObject.getString("date_of_birth"));
                        user.setLocation(usersJsonObject.getString("location"));
                        user.setUsername(usersJsonObject.getString("username"));
                        user.setAvatar(usersJsonObject.getString("avatar"));
                        user.setEmail(usersJsonObject.getString("email"));
                        user.setGender(usersJsonObject.getString("gender"));
                        user.setProvider(usersJsonObject.getString("provider"));
                        user.setToken(usersJsonObject.getString("token"));
                        user.setReferral_code(usersJsonObject.getString("referral_code"));

                        final String token=usersJsonObject.getString("token");

                        realm.commitTransaction();

                        if(!usersJsonObject.getBoolean("referal_code_used")) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog(token);
                                }
                            });


                        }else {
                            Notifications(usersJsonObject.getString("token"));
                            StatusRestaurantsData();
                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(intent);
                            Constants.SetUserLoginStatus(LoginActivity.this, true);
                            finish();
                        }
                    }
                    else  if(jsonObject.has("message")){
                        message= jsonObject.getString("message");
                    }

                }catch (Exception e){
                    Log.i("Exception ::",""+ e.getMessage());
                } finally {
                }
            }
        });

        if(!message.isEmpty() && !message.equals("")){
            Crouton.makeText(LoginActivity.this,message, Style.ALERT).show();
        }
    }

    void DeleteAll(){

        Realm realm= Realm.getDefaultInstance();
        realm.beginTransaction();

        RealmResults<com.dishcuss.foodie.hub.Models.User> users = realm.where(com.dishcuss.foodie.hub.Models.User.class).findAll();
        users.deleteAllFromRealm();

        RealmResults<Notifications> notificationsRealmResults = realm.where(Notifications.class).findAll();
        notificationsRealmResults.deleteAllFromRealm();

        RealmResults<Comment> comments = realm.where(Comment.class).findAll();
        comments.deleteAllFromRealm();

        RealmResults<KhabaHistoryModel> khabaHistoryModelRealmResults = realm.where(KhabaHistoryModel.class).findAll();
        khabaHistoryModelRealmResults.deleteAllFromRealm();

        RealmResults<LocalFeedCheckIn> localFeedCheckInRealmResults = realm.where(LocalFeedCheckIn.class).findAll();
        localFeedCheckInRealmResults.deleteAllFromRealm();

        RealmResults<LocalFeedReview> localFeedReviewRealmResults = realm.where(LocalFeedReview.class).findAll();
        localFeedReviewRealmResults.deleteAllFromRealm();

        RealmResults<LocalFeeds> localFeedsRealmResults = realm.where(LocalFeeds.class).findAll();
        localFeedsRealmResults.deleteAllFromRealm();

        RealmResults<Restaurant> restaurantRealmResults = realm.where(Restaurant.class).findAll();
        restaurantRealmResults.deleteAllFromRealm();

        RealmResults<ReviewModel> reviewModels = realm.where(ReviewModel.class).findAll();
        reviewModels.deleteAllFromRealm();

        RealmResults<FoodItems> foodItemsRealmResults = realm.where(FoodItems.class).findAll();
        foodItemsRealmResults.deleteAllFromRealm();



        RealmResults<UserProfile> userProfileRealmResults = realm.where(UserProfile.class).findAll();
        userProfileRealmResults.deleteAllFromRealm();

        realm.commitTransaction();

         SplashActivity.isFeatureRestaurantsLoaded=false;
    }

    void AlertDialog(final String token){
        final EditText edittext = new EditText(LoginActivity.this);
        alert.setTitle("Enter Referral Code if any ");

        alert.setView(edittext);

        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                Str_Referral_Code = edittext.getText().toString();
                if(!Str_Referral_Code.equals("") && Str_Referral_Code!=null) {

//                SocialLoginSendDataOnServer();
//                Constants.SetReferral(LoginActivity.this,true);
                    SendReferralOnServer(Str_Referral_Code, token);
                    Notifications(token);
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    Constants.SetUserLoginStatus(LoginActivity.this, true);
                    finish();
                    dialog.cancel();
                }else {
                    Notifications(token);
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    Constants.SetUserLoginStatus(LoginActivity.this, true);
                    finish();
                    dialog.cancel();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
//                SocialLoginSendDataOnServer();
                Notifications(token);
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                Constants.SetUserLoginStatus(LoginActivity.this, true);
                finish();
                dialog.cancel();
            }
        });

        alert.show();
    }

    void Notifications(String token){

        realm = Realm.getDefaultInstance();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Get_Old_Notifications)
                .addHeader("Authorization", "Token token="+token)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr=response.body().string();



                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            JSONObject jsonObj = new JSONObject(objStr);
                            JSONArray jsonDataArray = jsonObj.getJSONArray("users");

                            for (int i = 0; i < jsonDataArray.length(); i++) {

                                JSONObject c = jsonDataArray.getJSONObject(i);

                                boolean isDataExist=false;

                                if(!isDataExist) {
                                    Realm realm=Realm.getDefaultInstance();
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

                                    realm.commitTransaction();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    void SendReferralOnServer(String cc, String token){

        Request request = new Request.Builder()
                .url(URLs.Send_Referral_Code+cc)
                .addHeader("Authorization", "Token token="+token)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String obj=response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{

                            Log.e("Obj",obj.toString());
                            JSONObject jsonObject=new JSONObject(obj);


                        }catch (Exception e){
                            Log.i("Exception ::",""+ e.getMessage());
                        }

                    }
                });
            }
        });
    }

    void StatusRestaurantsData(){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Get_Restaurants)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr=response.body().string();

                /** check if activity still exist */

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject jsonObj = new JSONObject(objStr);

                            JSONArray jsonDataArray=jsonObj.getJSONArray("restaurants");


                            realm =Realm.getDefaultInstance();
                            realm.beginTransaction();
                            RealmResults<RestaurantForStatus> result = realm.where(RestaurantForStatus.class).findAll();
                            result.deleteAllFromRealm();
                            realm.commitTransaction();

                            for (int i = 0; i < jsonDataArray.length(); i++) {
                                JSONObject c = jsonDataArray.getJSONObject(i);

                                realm =Realm.getDefaultInstance();
                                realm.beginTransaction();
                                RestaurantForStatus restaurantForStatus=realm.createObject(RestaurantForStatus.class);
                                restaurantForStatus.setId(c.getInt("id"));
                                restaurantForStatus.setName(c.getString("name"));
                                if(!c.isNull("lat")) {
                                    restaurantForStatus.setRestaurantLat(c.getDouble("lat"));
                                } else {
                                    restaurantForStatus.setRestaurantLat(0.0);
                                }
                                if(!c.isNull("long")) {
                                    restaurantForStatus.setRestaurantLong(c.getDouble("long"));
                                } else {
                                    restaurantForStatus.setRestaurantLong(0.0);
                                }

                                realm.commitTransaction();
//                                SplashActivity.restaurantForStatusArrayList.add(restaurantForStatus);

                            }


                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
