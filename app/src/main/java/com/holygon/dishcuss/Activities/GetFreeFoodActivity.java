package com.holygon.dishcuss.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;

import io.realm.Realm;

/**
 * Created by Naeem Ibrahim on 8/23/2016.
 */
public class GetFreeFoodActivity extends AppCompatActivity {

    Button facebookShareButton,twitterShareButton,whatsAppShareButton,localShareButton;
    Button referral_code;
    Realm realm;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.get_free_food);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView header=(TextView) findViewById(R.id.app_toolbar_name);
        header.setText("Get Free Food");
        if(!Constants.skipLogin) {
            realm = Realm.getDefaultInstance();
            user = realm.where(User.class).findFirst();
        }
        localShareButton=(Button)findViewById(R.id.all_share_button);
        whatsAppShareButton=(Button)findViewById(R.id.whatsApp_share_button);
        twitterShareButton=(Button)findViewById(R.id.twitter_share_button);
        facebookShareButton=(Button)findViewById(R.id.facebook_share_button);
        referral_code=(Button)findViewById(R.id.referral_code);

        if(!Constants.skipLogin) {
            referral_code.setText(user.getReferral_code());
        }else {
            referral_code.setText("Login first to get your code");
        }

        whatsAppShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                share.putExtra(Intent.EXTRA_TEXT, "Text...");
                share.setPackage("com.whatsapp");
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(share, "Share"));

            }
        });


        twitterShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                share.putExtra(Intent.EXTRA_TEXT, "Text...");
                share.setPackage("com.twitter.android");
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(share, "Share"));
            }
        });


        facebookShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                share.putExtra(Intent.EXTRA_TEXT, "Text...");
                share.setPackage("com.facebook.katana");
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(share, "Share"));

            }
        });

        localShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InviteFriends();
            }
        });
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

    private void InviteFriends(){
        Intent intent=new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "Lets Enjoy on Dishcuss");
        intent.putExtra(Intent.EXTRA_TEXT, "Lets Enjoy dishcuss Referral code is"+user.getReferral_code());
        startActivity(Intent.createChooser(intent, "Share Dishcuss With Friends"));
    }
}
