package com.holygon.dishcuss.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.URLs;

import org.json.JSONObject;

import java.io.IOException;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 10/13/2016.
 */
public class ReplyActivity  extends AppCompatActivity {

    EditText post_add_comment_edit_text;
    int commentID;
    int typeID;
    String typeName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rply_activity);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        post_add_comment_edit_text=(EditText)findViewById(R.id.post_add_rply_edit_text);
        TextView header=(TextView) findViewById(R.id.app_toolbar_name);
        header.setText("Reply");

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            commentID = bundle.getInt("CommentID");
            typeID = bundle.getInt("TypeID");
            typeName = bundle.getString("Type");
        }



        post_add_comment_edit_text.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String comment = post_add_comment_edit_text.getText().toString();
                    if (!comment.equals("")) {
                        post_add_comment_edit_text.setText("");
                        SendCommentDataOnServer(commentID,comment);
                    }

                    return true;
                }
                return false;
            }
        });
    }

    void SendCommentDataOnServer(int id,String comment){

        OkHttpClient client = new OkHttpClient();
        String message=null;
        Realm realm = Realm.getDefaultInstance();
        // Persist your data in a transaction
        User user = realm.where(User.class).findFirst();

        FormBody body = new FormBody.Builder()
                .add("reply",comment)
                .build();

        Request request = new Request.Builder()
                .url(URLs.commentReply+id)
                .addHeader("Authorization", "Token token="+user.getToken())
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String obj = response.body().string();
                Log.e("Reply",""+obj);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try
                        {
                            JSONObject jsonObject = new JSONObject(obj);

                            Intent intent = new Intent(ReplyActivity.this, NotificationClickPostDetail.class);
                            intent.putExtra("TypeID",typeID);
                            intent.putExtra("Type",typeName);
                            startActivity(intent);
                            finish();

                        } catch (Exception e){
                        } finally{
                        }
                    }
                });
            }
        });
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ReplyActivity.this, NotificationClickPostDetail.class);
        intent.putExtra("TypeID",typeID);
        intent.putExtra("Type",typeName);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // todo: goto back activity from here
                Intent intent = new Intent(ReplyActivity.this, NotificationClickPostDetail.class);
                intent.putExtra("TypeID",typeID);
                intent.putExtra("Type",typeName);
                startActivity(intent);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
