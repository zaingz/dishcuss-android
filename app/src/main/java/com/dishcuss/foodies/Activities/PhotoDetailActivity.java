package com.dishcuss.foodies.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dishcuss.foodies.Model.LocalFeedCheckIn;
import com.dishcuss.foodies.Model.LocalFeedReview;
import com.dishcuss.foodies.Model.ReviewModel;
import com.dishcuss.foodies.Model.User;
import com.dishcuss.foodies.R;
import com.dishcuss.foodies.Utils.Constants;
import com.dishcuss.foodies.Utils.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
        photo.setVisibility(View.GONE);

        crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if(bundle.containsKey("Bitmap")) {
                photo.setVisibility(View.VISIBLE);
                photo_bottom_layout.setVisibility(View.GONE);
                byte[] bytes = bundle.getByteArray("Bitmap");
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                BitmapDrawable ob = new BitmapDrawable(getResources(), bmp);
                photo.setBackground(ob);
            }else {

            }

            type=bundle.getString("Type");
            if(type.equals("CheckIn")){
                localFeedCheckIn= bundle.getParcelable("MyClass");
                likesCount.setText(""+localFeedCheckIn.getReviewLikesCount());
                commentsCount.setText(""+localFeedCheckIn.getReviewCommentCount());
                sharesCount.setText(""+localFeedCheckIn.getReviewSharesCount());
                if(!bundle.containsKey("Bitmap")) {
                    Constants.PicassoLargeImageBackgroundPhotoDetail(localFeedCheckIn.getCheckInImage(), photo, PhotoDetailActivity.this);
                }
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
                        likesCount.setText("" + prev);
                        Like(localFeedCheckIn.getCheckInID(), "post",likesCount,photo_layout_like);
                        photo_layout_like.setEnabled(false);
                    }
                }
            }
        });

        addComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(PhotoDetailActivity.this, PostDetailActivity.class);
//                intent.putExtra("Type","CheckIn");
//                intent.putExtra("MyClass", localFeedCheckIn);
//                startActivity(intent);
                Intent intent = new Intent(PhotoDetailActivity.this, NotificationClickPostDetail.class);
                intent.putExtra("TypeID",localFeedCheckIn.getCheckInID());
                intent.putExtra("Type","Post");
                startActivity(intent);

            }
        });
        comment_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(PhotoDetailActivity.this, PostDetailActivity.class);
//                intent.putExtra("Type","CheckIn");
//                intent.putExtra("MyClass", localFeedCheckIn);
//                startActivity(intent);
                Intent intent = new Intent(PhotoDetailActivity.this, NotificationClickPostDetail.class);
                intent.putExtra("TypeID",localFeedCheckIn.getCheckInID());
                intent.putExtra("Type","Post");
                startActivity(intent);
            }
        });
        commentsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(PhotoDetailActivity.this, PostDetailActivity.class);
//                intent.putExtra("Type","CheckIn");
//                intent.putExtra("MyClass", localFeedCheckIn);
//                startActivity(intent);
                Intent intent = new Intent(PhotoDetailActivity.this, NotificationClickPostDetail.class);
                intent.putExtra("TypeID",localFeedCheckIn.getCheckInID());
                intent.putExtra("Type","Post");
                startActivity(intent);
            }
        });
    }


    public void Like(int id, String type, final TextView tv, final LinearLayout layout_like){

        // Get a Realm instance for this thread
        Realm realm=Realm.getDefaultInstance();
        // Persist your data in a transaction
        realm.beginTransaction();
        final User user = realm.where(User.class).findFirst();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Like_+type+"/"+id)
                .addHeader("Authorization", "Token token="+user.getToken())
                .build();

        realm.close();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
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

                            if(jsonObj.has("message")){

                                String message= jsonObj.getString("message");
                                if(!message.equals("Successfully liked!")) {
                                    int prev = Integer.valueOf(tv.getText().toString());
                                    prev--;
                                    tv.setText("" + prev);
                                }
                            }
                            layout_like.setEnabled(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        realm.commitTransaction();
        //            return UnLike(id,type);
    }
}
