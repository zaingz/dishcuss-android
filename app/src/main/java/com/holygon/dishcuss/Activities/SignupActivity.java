package com.holygon.dishcuss.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
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
 * Created by Naeem Ibrahim on 7/30/2016.
 */
public class SignupActivity extends AppCompatActivity {


    LinearLayout back_to_sign_in_layout;
    TextView headerName;

    EditText userFullName,userName,userEmail,userPassword,userConfirmPassword,userLocation,userGender;
    String strUserFullName,strUserName,strUserEmail, strUserPassword,strUserConfirmPassword,strUserLocation,strUserGender;

    OkHttpClient client;
    LinearLayout signUpLayout;

    Realm realm;
    String message="";



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
        setContentView(R.layout.activity_signup);
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        client = new OkHttpClient();
        FindViewsByID();
        OnClickItems();
    }


    void FindViewsByID(){
        back_to_sign_in_layout=(LinearLayout)findViewById(R.id.back_to_sign_in_layout);
        signUpLayout=(LinearLayout)findViewById(R.id.sign_up_layout);
        headerName=(TextView)findViewById(R.id.app_toolbar_name);
        headerName.setText("Signup");
        userFullName=(EditText) findViewById(R.id.edt_user_full_name);
        userName=(EditText) findViewById(R.id.edt_username);
        userEmail=(EditText) findViewById(R.id.edt_user_email);
        userLocation=(EditText) findViewById(R.id.edt_user_location);
        userPassword=(EditText) findViewById(R.id.edt_user_password);
        userConfirmPassword=(EditText) findViewById(R.id.edt_user_retype_password);
        userGender=(EditText) findViewById(R.id.edt_user_gender);


    }

    void OnClickItems(){
        back_to_sign_in_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignupActivity.this,SignInActivity.class);
                startActivity(intent);
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

        strUserFullName = userFullName.getText().toString().trim();
        strUserName = userName.getText().toString().trim();
        strUserEmail=userEmail.getText().toString().trim();
        strUserPassword = userPassword.getText().toString().trim();
        strUserConfirmPassword = userConfirmPassword.getText().toString().trim();
        strUserGender = userGender.getText().toString().trim();
        strUserLocation = userLocation.getText().toString().trim();


        if(!strUserName.isEmpty() && !strUserName.equals("")){

            if(!strUserFullName.isEmpty() && !strUserFullName.equals("")){

                if(!strUserEmail.isEmpty() && !strUserEmail.equals("")){

                    if(ValidateEmail(strUserEmail)){

                        if(!strUserLocation.isEmpty() && !strUserLocation.equals("")){

                            if(!strUserGender.isEmpty() && !strUserGender.equals("")){

                                if(!strUserPassword.isEmpty() && !strUserPassword.equals("")){

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
                                    Crouton.makeText(SignupActivity.this, "Enter Password for security", Style.ALERT).show();
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

        FormBody body = new FormBody.Builder()
                .add("user[name]",strUserFullName)
                .add("user[email]", strUserEmail)
                .add("user[username]", strUserName)
                .add("user[avatar]", "")
                .add("user[location]", strUserLocation)
                .add("user[gender]", strUserGender)
                .add("user[password]", strUserPassword)
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

                try{
                    String obj=response.body().string();
//                    Log.e("Obj",obj.toString());
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
                        user.setUsername(usersJsonObject.getString("username"));
                        user.setEmail(usersJsonObject.getString("email"));
                        user.setGender(usersJsonObject.getString("gender"));
                        user.setProvider(usersJsonObject.getString("provider"));
                        user.setToken(usersJsonObject.getString("token"));
                        user.setReferral_code(usersJsonObject.getString("referral_code"));
                        realm.commitTransaction();
                        realm.close();

                        Intent intent=new Intent(SignupActivity.this,HomeActivity.class);
                        startActivity(intent);
                        Constants.SetUserLoginStatus(SignupActivity.this,true);
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
            Crouton.makeText(SignupActivity.this,message, Style.ALERT).show();
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
}
