package com.holygon.dishcuss.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;
import com.holygon.dishcuss.Utils.URLs;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 10/15/2016.
 */
public class EmailConfirmationActivity extends AppCompatActivity {

    OkHttpClient client;
    TextView headerName;
    LinearLayout confirm;
    EditText code;
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
        setContentView(R.layout.email_conformation_activity);
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        client = new OkHttpClient();
        headerName=(TextView)findViewById(R.id.app_toolbar_name);
        headerName.setText("Email Confirmation");

        confirm = (LinearLayout)findViewById(R.id.confirmation_code_layout);
        code = (EditText)findViewById(R.id.edt_code);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cc= code.getText().toString();
                if(!cc.equals("")){
                    SendDataOnServer(cc);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(EmailConfirmationActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        Constants.SetUserLoginStatus(EmailConfirmationActivity.this,true);
        finish();
    }


    void SendDataOnServer(String cc){

        showSpinner("Processing...");
        Request request = new Request.Builder()
                .url(URLs.Native_Email_verify+cc)
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
                            if(jsonObject.has("message")){
                                message= jsonObject.getString("message");
                            }

                            if(!message.isEmpty() && message.equals("Email Successfully Verified!")){
                                Intent intent=new Intent(EmailConfirmationActivity.this,HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                Constants.SetUserLoginStatus(EmailConfirmationActivity.this,true);
                                finish();
                            }

                        }catch (Exception e){
                            Log.i("Exception ::",""+ e.getMessage());
                        }

                    }
                });
                DismissSpinner();
            }
        });

        if(!message.isEmpty() && !message.equals("") && !message.equals("Email Successfully Verified!")){
            Toast.makeText(EmailConfirmationActivity.this,message,Toast.LENGTH_SHORT).show();
        }
    }
}
