package com.dishcuss.foodie.hub.Posts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.dishcuss.foodie.hub.Models.User;
import com.dishcuss.foodie.hub.R;

import io.realm.Realm;

/**
 * Created by Naeem Ibrahim on 8/11/2016.
 */
public class PostSelectionActivity extends Activity {

    LinearLayout writeReview,uploadPhoto,CheckIn;
    ImageView crossButton;
    User user;
    Realm realm;

    int RestaurantID=0;
    String RestaurantName="";
    double RestaurantLat=0.0;
    double RestaurantLong=0.0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity_selection_transparent);
        crossButton=(ImageView)findViewById(R.id.post_cross_button);
        writeReview=(LinearLayout) findViewById(R.id.write_review_layout);
        uploadPhoto=(LinearLayout) findViewById(R.id.upload_photo_layout);
        CheckIn=(LinearLayout) findViewById(R.id.check_in_layout);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if(bundle.containsKey("RestaurantName")) {
                RestaurantName = bundle.getString("RestaurantName");
                RestaurantID = bundle.getInt("RestaurantID");
                RestaurantLat = bundle.getDouble("RestaurantLat");
                RestaurantLong = bundle.getDouble("RestaurantLong");
            }
        }


        crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        writeReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RestaurantName.equals("")) {
                    Intent intent = new Intent(PostSelectionActivity.this, WriteReviewPostActivity.class);
//                intent.putExtra("PostCategory",CheckInPostActivity.Review);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(PostSelectionActivity.this, WriteReviewPostActivity.class);
                    intent.putExtra("RestaurantID",RestaurantID);
                    intent.putExtra("RestaurantName",RestaurantName);
                    intent.putExtra("RestaurantLat",RestaurantLat);
                    intent.putExtra("RestaurantLong",RestaurantLong);
                    startActivity(intent);
                    finish();
                }
            }
        });

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RestaurantName.equals("")) {
                    Intent intent = new Intent(PostSelectionActivity.this, PhotoUpload.class);
//                intent.putExtra("PostCategory",CheckInPostActivity.UploadPic);
                    startActivity(intent);
                    finish();
                }
                else
                {
                    Intent intent = new Intent(PostSelectionActivity.this, PhotoUpload.class);
                    intent.putExtra("RestaurantID",RestaurantID);
                    intent.putExtra("RestaurantName",RestaurantName);
                    intent.putExtra("RestaurantLat",RestaurantLat);
                    intent.putExtra("RestaurantLong",RestaurantLong);
                    startActivity(intent);
                    finish();
                }
            }
        });

        CheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(RestaurantName.equals("")) {
                    Intent intent = new Intent(PostSelectionActivity.this, CheckIn.class);
//                intent.putExtra("PostCategory",CheckInPostActivity.CheckIn);
                    startActivity(intent);
                    finish();
                }
                else
                {
                        Intent intent = new Intent(PostSelectionActivity.this, CheckIn.class);
                        intent.putExtra("RestaurantID",RestaurantID);
                        intent.putExtra("RestaurantName",RestaurantName);
                        intent.putExtra("RestaurantLat",RestaurantLat);
                        intent.putExtra("RestaurantLong",RestaurantLong);
                        startActivity(intent);
                        finish();
                }
            }
        });
    }
}

