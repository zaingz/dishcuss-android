package com.holygon.dishcuss.Posts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.holygon.dishcuss.Activities.ScanQRCodeActivity;
import com.holygon.dishcuss.R;

/**
 * Created by Naeem Ibrahim on 8/11/2016.
 */
public class PostSelectionActivity extends Activity {

    LinearLayout writeReview,uploadPhoto,CheckIn;
    ImageView crossButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity_selection_transparent);
        crossButton=(ImageView)findViewById(R.id.post_cross_button);
        writeReview=(LinearLayout) findViewById(R.id.write_review_layout);
        uploadPhoto=(LinearLayout) findViewById(R.id.upload_photo_layout);
        CheckIn=(LinearLayout) findViewById(R.id.check_in_layout);


        crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        writeReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PostSelectionActivity.this,CheckInPostActivity.class);
                intent.putExtra("PostCategory",CheckInPostActivity.Review);
                startActivity(intent);
                finish();
            }
        });

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PostSelectionActivity.this,PhotoPostActivity.class);
                intent.putExtra("PostCategory",CheckInPostActivity.UploadPic);
                startActivity(intent);
                finish();
            }
        });

        CheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PostSelectionActivity.this,CheckInPostActivity.class);
                intent.putExtra("PostCategory",CheckInPostActivity.CheckIn);
                startActivity(intent);
                finish();
            }
        });
    }
}
