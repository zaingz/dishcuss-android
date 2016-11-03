package com.dishcuss.foodie.hub.Activities;

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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.dishcuss.foodie.hub.Adapters.PlaceArrayAdapter;
import com.dishcuss.foodie.Model.User;
import com.dishcuss.foodie.hub.R;
import com.dishcuss.foodie.hub.Utils.Constants;
import com.dishcuss.foodie.hub.Utils.URLs;

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
 * Created by Naeem Ibrahim on 7/30/2016.
 */
public class SignupActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {


    LinearLayout back_to_sign_in_layout;
    TextView headerName;

    EditText userFullName,userName,userEmail,userPassword,userConfirmPassword,referral_code_id;
//            userGender;
    Spinner userGender;
    ArrayAdapter<String> adapterUserGender;
    AutoCompleteTextView userLocation;
    String strUserFullName,strUserName,strUserEmail, strUserPassword,strUserConfirmPassword,strUserLocation,strUserGender,Str_Referral_Code;

    OkHttpClient client;
    LinearLayout signUpLayout;

    Realm realm;
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
        mSpinner.setCancelable(false);
        mSpinner.setCanceledOnTouchOutside(false);
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
        setContentView(R.layout.activity_signup);
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        client = new OkHttpClient();
        FindViewsByID();
        OnClickItems();

        if(Constants.checkPlayServices(this)) {
            buildGoogleApiClient();
        }
    }


    void FindViewsByID(){
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);




        back_to_sign_in_layout=(LinearLayout)findViewById(R.id.back_to_sign_in_layout);
        signUpLayout=(LinearLayout)findViewById(R.id.sign_up_layout);
        headerName=(TextView)findViewById(R.id.app_toolbar_name);
        headerName.setText("Signup");
        userFullName=(EditText) findViewById(R.id.edt_user_full_name);
        userName=(EditText) findViewById(R.id.edt_username);
        userEmail=(EditText) findViewById(R.id.edt_user_email);

        referral_code_id=(EditText) findViewById(R.id.referral_code_id);

        userLocation=(AutoCompleteTextView) findViewById(R.id.edt_user_location);

        userPassword=(EditText) findViewById(R.id.edt_user_password);
        userConfirmPassword=(EditText) findViewById(R.id.edt_user_retype_password);
        userGender=(Spinner) findViewById(R.id.user_gender);

        final String[] genders = new String[]{
                "male",
                "female",
        };


        adapterUserGender   = new ArrayAdapter<String>(SignupActivity.this,
                android.R.layout.simple_spinner_item, genders);
        userGender.setAdapter(adapterUserGender);

        userLocation.setOnItemClickListener(mAutocompleteClickListenerLocationSelection);
        userLocation.setTextSize(20);
        userLocation.setThreshold(3);
        userLocation.setTextColor(Color.parseColor("#FFE4770A"));
        userLocation.setAdapter(mPlaceArrayAdapter);

        userGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strUserGender=genders[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }



        });


    }

    void OnClickItems(){
        back_to_sign_in_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(SignupActivity.this,SignInActivity.class);
//                startActivity(intent);
                finish();
            }
        });

        signUpLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUpClick();
            }
        });

    }

    void SignUpClick(){

        if(!loc.equals("")){
            userLocation.setText(loc);
        }else {
            userLocation.setText("");
        }
        strUserFullName = userFullName.getText().toString().trim();
        strUserName = userName.getText().toString().trim();
        strUserEmail=userEmail.getText().toString().trim();
        strUserPassword = userPassword.getText().toString().trim();
        strUserConfirmPassword = userConfirmPassword.getText().toString().trim();
        strUserGender=strUserGender.toLowerCase();
        strUserLocation = userLocation.getText().toString().trim();
        Str_Referral_Code=referral_code_id.getText().toString().trim();


        if(!strUserName.isEmpty() && !strUserName.equals("") && strUserName.length()>=4){

            if(!strUserFullName.isEmpty() && !strUserFullName.equals("")){

                if(!strUserEmail.isEmpty() && !strUserEmail.equals("")){

                    if(ValidateEmail(strUserEmail)){

                        if(!strUserLocation.isEmpty() && !strUserLocation.equals("")){

                            if(!strUserGender.isEmpty() && !strUserGender.equals("")){

                                if(!strUserPassword.isEmpty() && !strUserPassword.equals("") && strUserPassword.length()>=8){

                                    if(!strUserConfirmPassword.isEmpty() && !strUserConfirmPassword.equals("")){

                                        if(strUserPassword.equals(strUserConfirmPassword)){

                                            NativeSignUp();

                                        }else {
                                            Crouton.makeText(SignupActivity.this, "Password not matched", Style.ALERT).show();
                                        }


                                    }else {
                                        Crouton.makeText(SignupActivity.this, "Confirm your Password", Style.ALERT).show();
                                    }

                                }else {
                                    Crouton.makeText(SignupActivity.this, "Enter Large Password for security", Style.ALERT).show();
                                }



                            }else {
                                Crouton.makeText(SignupActivity.this, "Gender field missing ", Style.ALERT).show();
                            }

                        }else {
                            Crouton.makeText(SignupActivity.this, "Provide your current Location", Style.ALERT).show();
                        }

                    }else {
                        Crouton.makeText(SignupActivity.this, "Please Provide correct email", Style.ALERT).show();
                    }

                }
                else
                {
                    Crouton.makeText(SignupActivity.this, "Please Provide email", Style.ALERT).show();
                }

            }
            else
            {
                Crouton.makeText(SignupActivity.this, "User Full Name missing", Style.ALERT).show();
            }
        }
        else
        {
            Crouton.makeText(SignupActivity.this, "User Name missing", Style.ALERT).show();
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

                                       if(!Str_Referral_Code.equals("")){
                                    Constants.SetReferral(SignupActivity.this,true);
                                }

                                FormBody body = new FormBody.Builder()
                                        .add("user[name]",strUserFullName)
                                        .add("user[email]", strUserEmail)
                                        .add("user[username]", strUserName)
                                        .add("user[avatar]", "")
                                        .add("user[location]", strUserLocation)
                                        .add("user[gender]", strUserGender)
                                        .add("user[password]", strUserPassword)
                                        .add("user[referal_code]", Str_Referral_Code)
                                        .build();

                                Request request = new Request.Builder()
                                        .url(URLs.Native_SignUp_URL)
                                        .post(body)
                                        .build();

                                client.newCall(request).enqueue(new Callback() {
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
                                                    if(jsonObject.has("user")){
                                                        JSONObject usersJsonObject = jsonObject.getJSONObject("user");
                                                        //                   // Get a Realm instance for this thread
                                                        realm = Realm.getDefaultInstance();
                                                        // Persist your data in a transaction
                                                        realm.beginTransaction();
                                                        User user = realm.createObject(User.class); // Create managed objects directly
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
                                                        realm.commitTransaction();
                                                        realm.close();

                                Intent intent=new Intent(SignupActivity.this,EmailConfirmationActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                            else  if(jsonObject.has("message")){
                                message= jsonObject.getString("message");
                            }

                        }catch (Exception e){
                            Log.i("Exception ::",""+ e.getMessage());
                        }

                    }
                });

                DismissSpinner();
            }
        });

//        if(!message.isEmpty() && !message.equals("")){
//            Crouton.makeText(SignupActivity.this,message, Style.ALERT).show();
//        }
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

        mGoogleApiClient = new GoogleApiClient.Builder(SignupActivity.this)
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


    public class YourItemSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            String selected = parent.getItemAtPosition(pos).toString();
        }

        public void onNothingSelected(AdapterView parent) {
            // Do nothing.
        }
    }
}
