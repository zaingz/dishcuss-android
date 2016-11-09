package com.dishcuss.foodie.hub.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
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
import com.dishcuss.foodie.hub.Adapters.PlaceArrayAdapter;
import com.dishcuss.foodie.hub.Models.User;
import com.dishcuss.foodie.hub.R;
import com.dishcuss.foodie.hub.Utils.Constants;
import com.dishcuss.foodie.hub.Utils.URLs;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.regex.Pattern;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import io.realm.Realm;
import io.realm.RealmObject;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 8/24/2016.
 */
public class UpdateProfileActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {


    TextView headerName;

    de.hdodenhof.circleimageview.CircleImageView profileImage;

    EditText userFullName,userName,userEmail,userPassword,userConfirmPassword,userGender;
    static EditText userDOB;
    AutoCompleteTextView userLocation;
    String strUserFullName,strUserName,strUserEmail, strUserPassword,strUserConfirmPassword,strUserLocation,strUserGender,strUserDOB;
    LinearLayout Password_parent,ConfirmPassword_parent;



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

        profileImage=(de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.update_user_profile_image);

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

        if(user.getProvider().equals("Facebook") || user.getProvider().equals("Twitter") ||user.getProvider().equals("Google")) {
            ConfirmPassword_parent.setVisibility(View.GONE);
            Password_parent.setVisibility(View.GONE);

            userConfirmPassword.setText("123");
            userPassword.setText("123");
        }

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(userDOB.getWindowToken(), 0);

        userDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(userDOB.getWindowToken(), 0);
                showDatePickerDialog();
            }
        });


        if(!user.getAvatar().equals("")){
            Constants.PicassoImageSrc(user.getAvatar(),profileImage,UpdateProfileActivity.this);
        }

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(UpdateProfileActivity.this, SelectImage.class);
                startActivityForResult(i, 2);
            }
        });
    }

    File file=null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 2) {
                File pictureFile = (File) data.getExtras().get("result");

                Log.e("File: ", "" + pictureFile);
                file = new File(pictureFile.getPath());
                Bitmap bitmap = BitmapFactory.decodeFile(pictureFile.toString());
                profileImage.setImageBitmap(bitmap);
                UpdateImage();
            }
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

        Password_parent=(LinearLayout) findViewById(R.id.Password_parent);
        ConfirmPassword_parent=(LinearLayout) findViewById(R.id.ConfirmPassword_parent);


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
        FormBody body;
        if(user.getProvider().equals("Facebook") || user.getProvider().equals("Twitter") ||user.getProvider().equals("Google")) {
            body = new FormBody.Builder()
                    .add("user[name]",strUserFullName)
                    .add("user[location]", strUserLocation)
                    .add("user[gender]", strUserGender)
                    .add("user[dob]", strUserDOB)
                    .build();
        }
        else
        {
            body = new FormBody.Builder()
                    .add("user[name]",strUserFullName)
                    .add("user[location]", strUserLocation)
                    .add("user[gender]", strUserGender)
                    .add("user[dob]", strUserDOB)
                    .add("user[password]", strUserPassword)
                    .build();

        }
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
    public void showDatePickerDialog() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(this.getFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            month = month + 1;
            userDOB.setText(day + "-" + month++ + "-" + year);
        }
    }


    void UpdateImage(){
        showSpinner("Please wait...");
        // Get a Realm instance for this thread
        Realm realm = Realm.getDefaultInstance();
        // Persist your data in a transaction
        User user = realm.where(User.class).findFirst();

        RequestBody requestBody;

            requestBody= new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("user[avatar]", file.getName(),
                            RequestBody.create(MediaType.parse("text/csv"), file))
                    .build();

        Request request = new Request.Builder()
                .url(URLs.USER_IMAGE_UPLOAD)
                .addHeader("Authorization", "Token token="+user.getToken())
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try{
                    String obj=response.body().string();
                    Log.e("Res",""+obj);
                    JSONObject jsonObject=new JSONObject(obj);
                    if(jsonObject.has("user")){

                        JSONObject usersJsonObject = jsonObject.getJSONObject("user");

                        Realm realm1=Realm.getDefaultInstance();
                        User user1 = realm1.where(User.class).equalTo("id", usersJsonObject.getInt("id")).findFirst(); // Create managed objects directly
                        realm1.beginTransaction();
                        user1.setAvatar(usersJsonObject.getString("avatar"));
                        realm1.commitTransaction();
                        realm1.close();

                    }
                    DismissSpinner();
                }catch (Exception e){
                    Log.i("Exception ::",""+ e.getMessage());
                }
                finally
                {
                }

            }
        });
    }
}
