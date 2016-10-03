package com.holygon.dishcuss.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;
import com.holygon.dishcuss.Utils.URLs;

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
                        user.setGender(usersJsonObject.getString("gender"));
                        user.setProvider(usersJsonObject.getString("provider"));
                        user.setToken(usersJsonObject.getString("token"));
                        user.setReferral_code(usersJsonObject.getString("referral_code"));
                        realm.commitTransaction();
                        realm.close();

                        Intent intent=new Intent(SignInActivity.this,HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        DismissSpinner();
                        Constants.SetUserLoginStatus(SignInActivity.this,true);
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
}
