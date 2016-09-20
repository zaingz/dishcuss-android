package com.holygon.dishcuss.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.holygon.dishcuss.Model.LocalFeedCheckIn;
import com.holygon.dishcuss.Model.LocalFeedReview;
import com.holygon.dishcuss.Model.ReviewModel;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;
import com.holygon.dishcuss.Utils.GenericRoutes;

/**
 * Created by Naeem Ibrahim on 9/20/2016.
 */
public class PhotoDetailActivity extends Activity {

    ImageView crossButton,photo;
    String type;
    LocalFeedReview localFeedReview;
    LocalFeedCheckIn localFeedCheckIn;
    ReviewModel reviewModel;

    TextView likesCount,commentsCount,sharesCount,comment_layout;
    LinearLayout addComment;
    LinearLayout photo_layout_like;
    LinearLayout photo_bottom_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_image_detail);
        crossButton = (ImageView) findViewById(R.id.close_photo);

        likesCount = (TextView) findViewById(R.id.photo_likes_count);
        commentsCount = (TextView) findViewById(R.id.photo_comments_count);
        sharesCount = (TextView) findViewById(R.id.photo_share_counts);
        comment_layout = (TextView) findViewById(R.id.photo_comment_layout);

        addComment = (LinearLayout) findViewById(R.id.photo_layout_add_comment);
        photo_layout_like = (LinearLayout) findViewById(R.id.photo_layout_like);
        photo_bottom_layout = (LinearLayout) findViewById(R.id.photo_bottom_layout);

        photo = (ImageView) findViewById(R.id.photo);

        crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            byte[] bytes = bundle.getByteArray("Bitmap");
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            BitmapDrawable ob = new BitmapDrawable(getResources(), bmp);
            photo.setBackground(ob);
            type=bundle.getString("Type");
            if(type.equals("CheckIn")){

                localFeedCheckIn=(LocalFeedCheckIn) bundle.getParcelable("MyClass");
                likesCount.setText(""+localFeedCheckIn.getReviewLikesCount());
                commentsCount.setText(""+localFeedCheckIn.getReviewCommentCount());
                sharesCount.setText(""+localFeedCheckIn.getReviewSharesCount());

            }else if(type.equals("Photo")){
                photo_bottom_layout.setVisibility(View.GONE);
            }
        }

//        photo.setImageBitmap(bmp);


        photo_layout_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Constants.skipLogin) {

                    if(Constants.isNetworkAvailable(PhotoDetailActivity.this)) {
                        int prev = Integer.valueOf(likesCount.getText().toString());
                        prev++;
                        Log.e("Like : ",""+prev);
                        likesCount.setText("" + prev);

                        if (!GenericRoutes.Like(localFeedCheckIn.getCheckInID(), "post")) {
                            prev--;
                            likesCount.setText("" + prev);
                        }
                    }
                }
            }
        });

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PhotoDetailActivity.this, PostDetailActivity.class);
                intent.putExtra("Type","CheckIn");
                intent.putExtra("MyClass", localFeedCheckIn);
                startActivity(intent);

            }
        });
        comment_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PhotoDetailActivity.this, PostDetailActivity.class);
                intent.putExtra("Type","CheckIn");
                intent.putExtra("MyClass", localFeedCheckIn);
                startActivity(intent);
            }
        });
        commentsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(PhotoDetailActivity.this, PostDetailActivity.class);
                intent.putExtra("Type","CheckIn");
                intent.putExtra("MyClass", localFeedCheckIn);
                startActivity(intent);
            }
        });
    }
}
