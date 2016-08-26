package com.holygon.dishcuss.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.holygon.dishcuss.Adapters.PlaceArrayAdapter;
import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;
import com.holygon.dishcuss.Utils.URLs;

import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import io.realm.Realm;
import io.realm.RealmObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 8/24/2016.
 */
public class UpdateProfileActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {


    TextView headerName;

    EditText userFullName,userName,userEmail,userPassword,userConfirmPassword,userGender,userDOB;
    AutoCompleteTextView userLocation;
    String strUserFullName,strUserName,strUserEmail, strUserPassword,strUserConfirmPassword,strUserLocation,strUserGender,strUserDOB;

    OkHttpClient client;
    LinearLayout signUpLayout;

    Realm realm;
    User user;
    String message="";


    //Location Auto Selection
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private GoogleApiClient mGoogleApiClientLoction;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(31.5497, 74.3436), new LatLng(31.5497, 74.3436));
    String loc="";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    //Location Auto Selection



    //*******************PROGRESS******************************
    private ProgressDialog mSpinner;

    private void showSpinner(String title) {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle(title);
        mSpinner.show();
    }
    private void DismissSpinner(){
        if(mSpinner!=null){
            mSpinner.dismiss();
        }
    }

//*******************PROGRESS******************************

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_user_profile_activity);
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        client = new OkHttpClient();
        realm=Realm.getDefaultInstance();
        user= realm.where(User.class).findFirst();
        FindViewsByID();
        OnClickItems();

        if(Constants.checkPlayServices(this)) {
            buildGoogleApiClient();
        }
    }


    void FindViewsByID(){
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);

        signUpLayout=(LinearLayout)findViewById(R.id.update_layout);
        headerName=(TextView)findViewById(R.id.app_toolbar_name);
        headerName.setText("Update Profile");
        userFullName=(EditText) findViewById(R.id.update_edt_user_full_name);
        userName=(EditText) findViewById(R.id.update_edt_username);
        userEmail=(EditText) findViewById(R.id.update_edt_user_email);
        userDOB=(EditText) findViewById(R.id.update_edt_user_dob);

        userLocation=(AutoCompleteTextView) findViewById(R.id.update_edt_user_location);

        userPassword=(EditText) findViewById(R.id.update_edt_user_password);
        userConfirmPassword=(EditText) findViewById(R.id.update_edt_user_retype_password);
        userGender=(EditText) findViewById(R.id.update_edt_user_gender);


        userLocation.setOnItemClickListener(mAutocompleteClickListenerLocationSelection);
        userLocation.setTextSize(20);
        userLocation.setThreshold(3);
        userLocation.setTextColor(Color.parseColor("#FFE4770A"));
        userLocation.setAdapter(mPlaceArrayAdapter);

        if(!loc.equals("")){
            userLocation.setText(loc);
        }



        User user = realm.where(User.class).findFirst();

        userFullName.setText(user.getName());
        userName.setText(user.getUsername());
        userEmail.setText(user.getEmail());

        userDOB.setText(""+user.getDob());
        userLocation.setText(user.getLocation());
        userGender.setText(user.getGender());

    }

    void OnClickItems(){


        signUpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpClick();
            }
        });

    }

    void SignUpClick(){

        strUserFullName = userFullName.getText().toString().trim();
        strUserName = userName.getText().toString().trim();
        strUserEmail=userEmail.getText().toString().trim();
        strUserPassword = userPassword.getText().toString().trim();
        strUserConfirmPassword = userConfirmPassword.getText().toString().trim();
        strUserGender = userGender.getText().toString().trim();
        strUserDOB = userDOB.getText().toString().trim();
        strUserLocation = userLocation.getText().toString().trim();


        if(!strUserName.isEmpty() && !strUserName.equals("")){

            if(!strUserFullName.isEmpty() && !strUserFullName.equals("")){

                if(!strUserFullName.isEmpty() && !strUserFullName.equals("")){

                    //if(ValidateEmail(strUserEmail))
                    {

                        if(!strUserLocation.isEmpty() && !strUserLocation.equals("")){

                            if(!strUserGender.isEmpty() && !strUserGender.equals("")){

                                if(!strUserPassword.isEmpty() && !strUserPassword.equals("")){

                                    if(!strUserConfirmPassword.isEmpty() && !strUserConfirmPassword.equals("")){

                                        if(strUserPassword.equals(strUserConfirmPassword)){

                                            NativeSignUp();


                                        }else {
                                            Crouton.makeText(UpdateProfileActivity.this, "Password not matched", Style.ALERT).show();
                                        }


                                    }else {
                                        Crouton.makeText(UpdateProfileActivity.this, "Confirm your Password", Style.ALERT).show();
                                    }

                                }else {
                                    Crouton.makeText(UpdateProfileActivity.this, "Enter Password for security", Style.ALERT).show();
                                }



                            }else {
                                Crouton.makeText(UpdateProfileActivity.this, "Gender field missing ", Style.ALERT).show();
                            }

                        }else {
                            Crouton.makeText(UpdateProfileActivity.this, "Provide your current Location", Style.ALERT).show();
                        }

                    }

                }
                else
                {
                    Crouton.makeText(UpdateProfileActivity.this, "User Full Name missing", Style.ALERT).show();
                }

            }
            else
            {
                Crouton.makeText(UpdateProfileActivity.this, "User Full Name missing", Style.ALERT).show();
            }
        }
        else
        {
            Crouton.makeText(UpdateProfileActivity.this, "User Name missing", Style.ALERT).show();
        }
    }



    void NativeSignUp(){
        showSpinner("Please wait...");
        SendDataOnServer();
    }

    public final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    private boolean ValidateEmail(String email) {
        return EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }


    void SendDataOnServer(){

        FormBody body = new FormBody.Builder()
                .add("user[name]",strUserFullName)
                .add("user[location]", strUserLocation)
                .add("user[gender]", strUserGender)
                .add("user[dob]", strUserDOB)
                .add("user[password]", strUserPassword)
                .build();

        Request request = new Request.Builder()
                .url(URLs.UPDATE_PROFILE)
                .addHeader("Authorization", "Token token="+user.getToken())
                .put(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
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
                        // Persist your data in a transaction

                        Realm realm1=Realm.getDefaultInstance();
                        User user1 = realm1.where(User.class).equalTo("id", usersJsonObject.getInt("id")).findFirst(); // Create managed objects directly
//                        User user1=new User();
                        realm1.beginTransaction();
                        user1.setName(usersJsonObject.getString("name"));
                        user1.setDob(usersJsonObject.getString("date_of_birth"));
                        user1.setLocation(usersJsonObject.getString("location"));
                        user1.setGender(usersJsonObject.getString("gender"));
//                        realm.copyToRealmOrUpdate(user1);
                        realm1.commitTransaction();
                        realm1.close();
                        finish();

                    }
                    else  if(jsonObject.has("message")){
                        message= jsonObject.getString("message");
                    }

                }catch (Exception e){
                    Log.i("Exception ::",""+ e.getMessage());
                } finally {
                }

                DismissSpinner();
            }
        });

        if(!message.isEmpty() && !message.equals("")){
            Crouton.makeText(UpdateProfileActivity.this,message, Style.ALERT).show();
        }
    }


    public Gson getGsonObject(){
        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();
        return gson;
    }


    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);



    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        mGoogleApiClientLoction.connect();
    }



    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClientLoction = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient = new GoogleApiClient.Builder(UpdateProfileActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListenerLocationSelection
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);


            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallbackUserLocation);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallbackUserLocation
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();
            userLocation.setText(place.getAddress());
            userLocation.dismissDropDown();
            userLocation.setSelection(place.getAddress().length());
            loc=place.getName().toString();
        }
    };

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
}
