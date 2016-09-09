package com.holygon.dishcuss.Activities;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.holygon.dishcuss.Fragments.LoginIntroFragment;
import com.holygon.dishcuss.Model.FbDataModel;
import com.holygon.dishcuss.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.holygon.dishcuss.Utils.Constants;
import com.holygon.dishcuss.Utils.URLs;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
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

    String email="",name="",username="",location="",profilePicURL="",gender="",provider="",uid="",profileURL="",token="",expires_at="";

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

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        TextView skipLogin=(TextView)findViewById(R.id.skip_login_tv);

        facebookLoginButton = (Button) findViewById(R.id.facebook_login_button);
        googleLoginButton = (Button) findViewById(R.id.google_login_button);
        fbButton = (LoginButton) findViewById(R.id.facebook_login_button_invisible);
        client = new OkHttpClient();
        setupViewPager(viewPager);
        addDots();

        skipLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this,"Skip is Clicked",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
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
    }

    //Facebook Callback
    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(final LoginResult loginResult) {

            //   progressDialog.dismiss();
//            Log.e("loginResult ",""+loginResult.getAccessToken().getToken());
            // App code
            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {

                            Log.e("response: ", response + "");
                            Log.e("object: ", object.toString());

                            try {

                                if(object.has("email")){
                                    email=object.getString("email").toString();
                                }
                                name= object.getString("name").toString();
                                uid=object.getString("id").toString();
                                username=object.getString("name").toString();
                                profilePicURL="https://graph.facebook.com/"+ uid +"/picture?type=large";
                                if(object.has("location")){
                                    location=object.getString("location").toString();
                                }
                                provider="Facebook";
                                profileURL="https://www.facebook.com/app_scoped_user_id/"+uid+"/";
                                token=loginResult.getAccessToken().getToken();
//                                token="Facebook"+uid;
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
                token=acct.getIdToken();
                token="google"+uid;
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


//        //Adding callback to the button
//        twitterLogin.setCallback(new Callback<TwitterSession>() {
//            @Override
//            public void success(Result<TwitterSession> result) {
//                //If login succeeds passing the Calling the login method and passing Result object
//                login(result);
//            }
//
//            @Override
//            public void failure(TwitterException exception) {
//                //If failure occurs while login handle it here
//                Log.d("TwitterKit", "Login with Twitter failure", exception);
//            }
//        });
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
                        }
                        name=tUser.name;
                        location=tUser.location;
                        provider="Twitter";
                        gender="";
                        expires_at="";
                        uid=tUser.idStr;
                        token="twitter"+tUser.idStr;
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
                    Log.e("Obj",obj.toString());
                    JSONObject jsonObject=new JSONObject(obj);
                    if(jsonObject.has("user")){
                        JSONObject usersJsonObject = jsonObject.getJSONObject("user");
//                   // Get a Realm instance for this thread
                        realm = Realm.getDefaultInstance();
                        // Persist your data in a transaction
                        realm.beginTransaction();
                        com.holygon.dishcuss.Model.User user = realm.createObject(com.holygon.dishcuss.Model.User.class); // Create managed objects directly
                        user.setId(usersJsonObject.getInt("id"));
                        user.setName(usersJsonObject.getString("name"));
                        user.setDob(usersJsonObject.getString("date_of_birth"));
                        user.setLocation(usersJsonObject.getString("location"));
                        user.setUsername(usersJsonObject.getString("username"));
                        user.setEmail(usersJsonObject.getString("email"));
                        user.setGender(usersJsonObject.getString("gender"));
                        user.setProvider(usersJsonObject.getString("provider"));
                        user.setToken(usersJsonObject.getString("token"));
                        user.setReferral_code(usersJsonObject.getString("referral_code"));
                        realm.commitTransaction();
                        realm.close();

                        Intent intent=new Intent(LoginActivity.this,HomeActivity.class);
                        startActivity(intent);
                        Constants.SetUserLoginStatus(LoginActivity.this,true);
                        finish();
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
}
