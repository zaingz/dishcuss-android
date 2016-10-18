package com.holygon.dishcuss.Activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.holygon.dishcuss.Model.Notifications;
import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;
import com.holygon.dishcuss.Utils.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 7/30/2016.
 */
public class SignInActivity extends AppCompatActivity {

    LinearLayout create_new_account_layout;
    TextView headerName;

    EditText userEmail,userPassword;
    String strUserEmail, strUserPassword;

    OkHttpClient client;
    LinearLayout loginLayout;
    Realm realm;
    String message="";

    AlertDialog.Builder alert;
    String Str_Referral_Code="";

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
        setContentView(R.layout.activity_signin);
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        alert = new AlertDialog.Builder(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        client = new OkHttpClient();
        FindViewsByID();
        OnClickItems();
    }


    void FindViewsByID(){
        create_new_account_layout=(LinearLayout)findViewById(R.id.create_new_account_layout);
        loginLayout=(LinearLayout)findViewById(R.id.login_button_layout);
        userEmail=(EditText) findViewById(R.id.edt_user_email);
        userPassword=(EditText) findViewById(R.id.edt_user_password);
        headerName=(TextView)findViewById(R.id.app_toolbar_name);
        headerName.setText("Login");
    }

    void OnClickItems(){
        create_new_account_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignInActivity.this,SignupActivity.class);
                startActivity(intent);
            }
        });


        loginLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strUserEmail=userEmail.getText().toString().trim();
                strUserPassword = userPassword.getText().toString().trim();
                if(!strUserEmail.equals("") && !strUserEmail.isEmpty() &&
                        !strUserPassword.equals("") && !strUserPassword.isEmpty())
                {
                    showSpinner("Please wait...");
                    NativeLoginSendDataOnServer();
                }
            }
        });

    }

    void NativeLoginSendDataOnServer(){

        FormBody body = new FormBody.Builder()
                .add("user[email]",strUserEmail)
                .add("user[password]", strUserPassword)
                .build();

        Request request = new Request.Builder()
                .url(URLs.Native_SignIn_URL)
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
                        user.setUsername(usersJsonObject.getString("username"));
                        user.setEmail(usersJsonObject.getString("email"));
                        user.setAvatar(usersJsonObject.getString("avatar"));
                        user.setGender(usersJsonObject.getString("gender"));
                        user.setProvider(usersJsonObject.getString("provider"));
                        user.setToken(usersJsonObject.getString("token"));
                        user.setReferral_code(usersJsonObject.getString("referral_code"));
                        realm.commitTransaction();


                        final String token=usersJsonObject.getString("token");
                        final boolean isVerified=usersJsonObject.getBoolean("email_verified");

                        if(usersJsonObject.getBoolean("email_verified")){

                            if(!usersJsonObject.getBoolean("referal_code_used")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog(token,isVerified);
                                    }
                                });
                            }else {
                                Notifications(usersJsonObject.getString("token"));
                                Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                DismissSpinner();
                                Constants.SetUserLoginStatus(SignInActivity.this, true);
                                finish();

                            }
                        }else{
                            if(!usersJsonObject.getBoolean("referal_code_used")) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog(token,isVerified);
                                    }
                                });
                            }else {
                                Notifications(usersJsonObject.getString("token"));
                                Intent intent=new Intent(SignInActivity.this,EmailConfirmationActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        }
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
            Crouton.makeText(SignInActivity.this,message, Style.ALERT).show();
        }
    }

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

    void AlertDialog(final String token, final boolean emailVerified){
        DismissSpinner();
        final EditText edittext = new EditText(SignInActivity.this);
        alert.setTitle("Enter Referral Code if any ");

        alert.setView(edittext);

        alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                Str_Referral_Code = edittext.getText().toString();
                if(!Str_Referral_Code.equals("") && Str_Referral_Code!=null) {

//                SocialLoginSendDataOnServer();
//                Constants.SetReferral(LoginActivity.this,true);
                    SendReferralOnServer(Str_Referral_Code, token);
                    if(emailVerified) {
                        Notifications(token);
                        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        Constants.SetUserLoginStatus(SignInActivity.this, true);
                        finish();
                    }else {
                        Notifications(token);
                        Intent intent=new Intent(SignInActivity.this,EmailConfirmationActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                    dialog.cancel();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
//                SocialLoginSendDataOnServer();
                if(emailVerified) {
                    Notifications(token);
                    Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Constants.SetUserLoginStatus(SignInActivity.this, true);
                    finish();
                }else {
                    Notifications(token);
                    Intent intent=new Intent(SignInActivity.this,EmailConfirmationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
                dialog.cancel();
            }
        });

        alert.show();
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
}
