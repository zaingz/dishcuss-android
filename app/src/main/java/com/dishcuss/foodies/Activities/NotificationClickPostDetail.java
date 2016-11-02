package com.dishcuss.foodies.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dishcuss.foodies.Model.Comment;
import com.dishcuss.foodies.Model.Reply;
import com.dishcuss.foodies.Model.SpecificPostModel;
import com.dishcuss.foodies.Model.User;
import com.dishcuss.foodies.R;
import com.dishcuss.foodies.Utils.Constants;
import com.dishcuss.foodies.Utils.URLs;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 10/10/2016.
 */
public class NotificationClickPostDetail extends AppCompatActivity {


    RealmList<Comment> commentRealmList;
    boolean isCheckIn,FeedsReview=true;
    LinearLayout comments_row;
    TextView Name,time,status,likesCount,commentsCount,sharesCount;
    ImageView userProfile;
    File file=null;
    ImageView postImage;
    EditText post_add_comment_edit_text;
    LinearLayout post_add_comment_edit_text_parent;
    Realm realm;
    LayoutInflater inflater;
    ProgressBar image_spinner;
    SpecificPostModel specificPostModel;
    LinearLayout layout_share;
    LinearLayout layout_like;
    ImageView like_toggle_image;
    LinearLayout reply_row;
    LinearLayout add_reply_row;
    LayoutInflater rplyInflater;
    RealmList<Reply> replyRealmList;

    int typeID;
    String typeName;




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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        realm = Realm.getDefaultInstance();

        GetUI();
        showSpinner("Fetching...");

        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rplyInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            typeID = bundle.getInt("TypeID");
            typeName = bundle.getString("Type");

            if(typeName.equals("Post")){
                PostDetails(typeID);
            }else if(typeName.equals("Review")){
                ReviewDetails(typeID);
            }
        }

        layout_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(!Constants.skipLogin) {
                    Realm realm=Realm.getDefaultInstance();
                    realm.beginTransaction();
                    if (typeName.equals("Post")) {
                        int shareCount = Integer.parseInt(sharesCount.getText().toString());
                        shareCount++;
                        sharesCount.setText(""+shareCount);
                        specificPostModel.setReviewSharesCount(shareCount);
                        SharePost(specificPostModel.getCheckInImage(), specificPostModel.getCheckInStatus(), specificPostModel.getCheckInOnLat(), specificPostModel.getCheckInOnLong(), specificPostModel.getCheckInOnID(),specificPostModel.getCheckInID());
                    } else if (typeName.equals("Review")) {
                        int shareCount = Integer.parseInt(sharesCount.getText().toString());
                        shareCount++;
                        sharesCount.setText(""+shareCount);
                        specificPostModel.setReviewSharesCount(shareCount);
                        ReviewShare(specificPostModel.getCheckInStatus(), specificPostModel.getCheckInOnID(),specificPostModel.getCheckInID());
                    }
                    realm.commitTransaction();
                }
                else
                {
                    Intent intent=new Intent(NotificationClickPostDetail.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Constants.skipLogin=false;
                    startActivity(intent);
                    finish();
                }
            }
        });



        layout_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Constants.skipLogin) {
                    if(Constants.isNetworkAvailable(NotificationClickPostDetail.this)) {
                        if((int)like_toggle_image.getTag()==0) {
                            int prev = Integer.valueOf(likesCount.getText().toString());
                            prev++;
                            likesCount.setText("" + prev);
//                            layout_like.setEnabled(false);
                            like_toggle_image.setTag(1);
                            like_toggle_image.setBackground(NotificationClickPostDetail.this.getResources().getDrawable(R.drawable.icon_likes_count));
                            if(typeName.equals("Review")) {
                                Like(specificPostModel.getCheckInID(), "review", likesCount, layout_like);
                            }else if(typeName.equals("Post")){
                                Like(specificPostModel.getCheckInID(), "post", likesCount, layout_like);
                            }
                        }
                        else
                        {
                            like_toggle_image.setTag(0);
                            like_toggle_image.setBackground(NotificationClickPostDetail.this.getResources().getDrawable(R.drawable.icon_for_like));
                            int prev = Integer.valueOf(likesCount.getText().toString());
                            prev--;
                            likesCount.setText("" + prev);
//                            layout_like.setEnabled(false);
                            if(typeName.equals("Review")) {
                                UnLike(specificPostModel.getCheckInID(),"review",likesCount,layout_like);
                            }else if(typeName.equals("Post")) {
                                UnLike(specificPostModel.getCheckInID(),"post",likesCount,layout_like);
                            }
                        }
                    }
                }
                else
                {
                    Intent intent=new Intent(NotificationClickPostDetail.this, LoginActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Constants.skipLogin=false;
                    startActivity(intent);
                    finish();
                }
            }
        });

    }


    Date GetDate(String date){

        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        //  System.out.println(convertedDate);
        return convertedDate;
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

    void GetUI(){
        comments_row=(LinearLayout)findViewById(R.id.comments_);
        image_spinner=(ProgressBar)findViewById(R.id.image_spinner);
        Name=(TextView)findViewById(R.id.post_detail_reviewer_user_name);
        time=(TextView)findViewById(R.id.post_detail_post_time);
        status=(TextView)findViewById(R.id.post_detail_post_status);
        likesCount=(TextView)findViewById(R.id.post_detail_review_likes_count_tv);
        commentsCount=(TextView)findViewById(R.id.post_detail_review_comments_count_tv);
        sharesCount=(TextView)findViewById(R.id.post_detail_review_share_count_tv);
        userProfile=(ImageView)findViewById(R.id.post_detail_reviewer_user_profile_image);
        postImage=(ImageView)findViewById(R.id.post_image);
        post_add_comment_edit_text=(EditText)findViewById(R.id.post_add_comment_edit_text);
        post_add_comment_edit_text_parent=(LinearLayout) findViewById(R.id.post_add_comment_edit_text_parent);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        postImage.setVisibility(View.GONE);
        layout_share=(LinearLayout)findViewById(R.id.layout_share);
        layout_like=(LinearLayout)findViewById(R.id.layout_like);
        like_toggle_image=(ImageView)findViewById(R.id.image_like_toggle);

        if(Constants.skipLogin){
            post_add_comment_edit_text.setFocusable(false);
        }
    }


    void PostDetails(int id) {
        realm.beginTransaction();
        User user=null;
        if(!Constants.skipLogin) {
            user= realm.where(User.class).findFirst();
        }
        OkHttpClient client = new OkHttpClient();
        Request request;
        if(!Constants.skipLogin && user!=null) {
            request = new Request.Builder()
                    .url(URLs.specificPost+id)
                    .addHeader("Authorization", "Token token=" + user.getToken())
                    .build();
        }
        else
        {
            request = new Request.Builder()
                    .url(URLs.specificPost+id)
                    .build();
        }
        realm.commitTransaction();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject jsonDataReviewObjs = new JSONObject(objStr);
                             JSONObject jsonDataReviewObj = jsonDataReviewObjs.getJSONObject("post");


                            realm.beginTransaction();
                            RealmResults<SpecificPostModel> specificPostModels = realm.where(SpecificPostModel.class).findAll();
                            specificPostModels.deleteAllFromRealm();
                            realm.commitTransaction();

                            //if(jsonDataReviewObj.has("post"))
                            {

                                JSONObject writerObj = jsonDataReviewObj.getJSONObject("writer");

                                JSONObject checkinObj = jsonDataReviewObj.getJSONObject("checkin");

                                JSONArray checkinLikesArray = jsonDataReviewObj.getJSONArray("likes");
                                JSONArray checkinCommentsArray = jsonDataReviewObj.getJSONArray("comments");
                                JSONArray checkinPhotoArray = jsonDataReviewObj.getJSONArray("photos");

//                                realm.commitTransaction();
                                realm.beginTransaction();
                                specificPostModel=realm.createObject(SpecificPostModel.class);



                                specificPostModel.setCheckInID(jsonDataReviewObj.getInt("id"));
                                specificPostModel.setUpdated_at(jsonDataReviewObj.getString("updated_at"));
                                specificPostModel.setLiked(jsonDataReviewObj.getBoolean("is_liked"));
                                specificPostModel.setCheckInStatus(jsonDataReviewObj.getString("status"));
                                for (int p = 0; p < checkinPhotoArray.length(); p++) {
                                    JSONObject photo = checkinPhotoArray.getJSONObject(p);
                                    specificPostModel.setCheckInImage(photo.getString("image_url"));
                                }

                                if(checkinPhotoArray.length()==0){
                                    specificPostModel.setCheckInImage("");
                                }

                                if(!checkinObj.isNull("lat")){
                                    specificPostModel.setCheckInOnLat(checkinObj.getDouble("lat"));
                                }else {
                                    specificPostModel.setCheckInOnLat(0.0);
                                }

                                if(!checkinObj.isNull("long")) {
                                    specificPostModel.setCheckInOnLong(checkinObj.getDouble("long"));
                                }else {
                                    specificPostModel.setCheckInOnLong(0.0);
                                }

                                if(!checkinObj.isNull("restaurant")) {
                                    JSONObject restaurantObj = checkinObj.getJSONObject("restaurant");
                                    specificPostModel.setCheckInOnID(restaurantObj.getInt("id"));
                                }

                                specificPostModel.setCheckInWriterID(writerObj.getInt("id"));
                                specificPostModel.setCheckInWriterName(writerObj.getString("name"));
                                specificPostModel.setCheckInWriterAvatar(writerObj.getString("avatar"));

                                specificPostModel.setReviewLikesCount(checkinLikesArray.length());
                                specificPostModel.setReviewCommentCount(checkinCommentsArray.length());
                                specificPostModel.setReviewSharesCount(jsonDataReviewObj.getInt("shares"));

                                realm.commitTransaction();
                                realm.beginTransaction();

                                for (int c = 0; c < checkinCommentsArray.length(); c++) {
                                    JSONObject commentObj = checkinCommentsArray.getJSONObject(c);

                                    Comment comment=realm.createObject(Comment.class);

                                    comment.setCommentID(commentObj.getInt("id"));
                                    comment.setCommentTitle(commentObj.getString("title"));
                                    comment.setCommentUpdated_at(commentObj.getString("created_at"));
                                    comment.setCommentSummary(commentObj.getString("comment"));


                                    JSONObject commentatorObj = commentObj.getJSONObject("commentor");
                                    comment.setCommentatorID(commentatorObj.getInt("id"));
                                    comment.setCommentatorName(commentatorObj.getString("name"));
                                    comment.setCommentatorImage(commentatorObj.getString("avatar"));

                                    JSONArray commentLikeArray=commentObj.getJSONArray("likes");
                                    comment.setCommentLikesCount(commentLikeArray.length());

                                    JSONArray replyArray=commentObj.getJSONArray("replies");
                                    realm.commitTransaction();
                                    realm.beginTransaction();

                                    for(int r=0;r<replyArray.length();r++){
                                        JSONObject replyObj=replyArray.getJSONObject(r);

                                        Reply reply=realm.createObject(Reply.class);

                                        reply.setCommentID(replyObj.getInt("id"));
                                        reply.setCommentTitle(replyObj.getString("title"));
                                        reply.setCommentUpdated_at(replyObj.getString("created_at"));
                                        reply.setCommentSummary(replyObj.getString("comment"));

                                        JSONObject replyCommentatorObj = replyObj.getJSONObject("commentor");
                                        reply.setCommentatorID(replyCommentatorObj.getInt("id"));
                                        reply.setCommentatorName(replyCommentatorObj.getString("name"));
                                        reply.setCommentatorImage(replyCommentatorObj.getString("avatar"));

                                        JSONArray replyCommentLikeArray=replyObj.getJSONArray("likes");
                                        reply.setCommentLikesCount(replyCommentLikeArray.length());

                                        final Reply manageReply = realm.copyToRealm(reply);
                                        comment.getReplyRealmList().add(manageReply);
                                    }

                                    final Comment managedComment = realm.copyToRealm(comment);
                                    specificPostModel.getCommentRealmList().add(managedComment);

                                }
                                SetUI();
                                realm.commitTransaction();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        realm.close();
                    }
                });
            }
        });
    }
    void ReviewDetails(int id) {

        realm.beginTransaction();
        User user=null;
        if(!Constants.skipLogin) {
            user= realm.where(User.class).findFirst();
        }
        OkHttpClient client = new OkHttpClient();
        Request request;
        if(!Constants.skipLogin && user!=null) {
            request = new Request.Builder()
                    .url(URLs.specificReview+id)
                    .addHeader("Authorization", "Token token=" + user.getToken())
                    .build();
        }
        else
        {
            request = new Request.Builder()
                    .url(URLs.specificReview+id)
                    .build();
        }
        realm.commitTransaction();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr = response.body().string();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject jsonDataReviewObjs = new JSONObject(objStr);

                            JSONObject jsonDataReviewObj = jsonDataReviewObjs.getJSONObject("review");
                            // JSONArray jsonDataArray = jsonObj.getJSONArray("restaurant");

                            realm = Realm.getDefaultInstance();
                            realm.beginTransaction();
                            RealmResults<SpecificPostModel> specificPostModels = realm.where(SpecificPostModel.class).findAll();
                            specificPostModels.deleteAllFromRealm();
                            realm.commitTransaction();
//                            if(jsonDataReviewObj.has("review"))
                            {

                                JSONObject reviewerObj = jsonDataReviewObj.getJSONObject("reviewer");

                                JSONObject reviewOnObj = jsonDataReviewObj.getJSONObject("review_on");

                                JSONArray reviewLikesArray = jsonDataReviewObj.getJSONArray("likes");
                                JSONArray reviewCommentsArray = jsonDataReviewObj.getJSONArray("comments");
                                JSONArray reviewShareArray = jsonDataReviewObj.getJSONArray("reports");


//                                if(!dataAlreadyExists)
                                {
                                    realm.beginTransaction();
                                    specificPostModel=realm.createObject(SpecificPostModel.class);


                                    specificPostModel.setCheckInID(jsonDataReviewObj.getInt("id"));
                                    specificPostModel.setUpdated_at(jsonDataReviewObj.getString("updated_at"));
                                    specificPostModel.setLiked(jsonDataReviewObj.getBoolean("is_liked"));
                                    specificPostModel.setCheckInStatus(jsonDataReviewObj.getString("summary"));
                                    specificPostModel.setCheckInImage(jsonDataReviewObj.getString("image"));

                                    specificPostModel.setCheckInWriterID(reviewerObj.getInt("id"));
                                    specificPostModel.setCheckInWriterName(reviewerObj.getString("name"));
                                    specificPostModel.setCheckInWriterAvatar(reviewerObj.getString("avatar"));


                                    specificPostModel.setCheckInOnID(reviewOnObj.getInt("id"));
                                    if(!reviewOnObj.isNull("lat")){
                                        specificPostModel.setCheckInOnLat(reviewOnObj.getDouble("lat"));
                                    }else {
                                        specificPostModel.setCheckInOnLat(0.0);
                                    }
                                    if(!reviewOnObj.isNull("long")) {
                                        specificPostModel.setCheckInOnLong(reviewOnObj.getDouble("long"));
                                    }else {
                                        specificPostModel.setCheckInOnLong(0.0);
                                    }

                                    specificPostModel.setReviewLikesCount(reviewLikesArray.length());
                                    specificPostModel.setReviewCommentCount(reviewCommentsArray.length());
                                    specificPostModel.setReviewSharesCount(jsonDataReviewObj.getInt("shares"));

                                    realm.commitTransaction();
                                    realm.beginTransaction();

                                    for (int c = 0; c < reviewCommentsArray.length(); c++) {
                                        JSONObject commentObj = reviewCommentsArray.getJSONObject(c);

                                        Comment comment=realm.createObject(Comment.class);

                                        comment.setCommentID(commentObj.getInt("id"));
                                        comment.setCommentTitle(commentObj.getString("title"));
                                        comment.setCommentUpdated_at(commentObj.getString("created_at"));
                                        comment.setCommentSummary(commentObj.getString("comment"));


                                        JSONObject commentatorObj = commentObj.getJSONObject("commentor");
                                        comment.setCommentatorID(commentatorObj.getInt("id"));
                                        comment.setCommentatorName(commentatorObj.getString("name"));
                                        comment.setCommentatorImage(commentatorObj.getString("avatar"));

                                        JSONArray commentLikeArray=commentObj.getJSONArray("likes");
                                        comment.setCommentLikesCount(commentLikeArray.length());

                                        JSONArray replyArray=commentObj.getJSONArray("replies");
                                        realm.commitTransaction();
                                        realm.beginTransaction();

                                        for(int r=0;r<replyArray.length();r++){
                                            JSONObject replyObj=replyArray.getJSONObject(r);

                                            Reply reply=realm.createObject(Reply.class);

                                            reply.setCommentID(replyObj.getInt("id"));
                                            reply.setCommentTitle(replyObj.getString("title"));
                                            reply.setCommentUpdated_at(replyObj.getString("created_at"));
                                            reply.setCommentSummary(replyObj.getString("comment"));

                                            JSONObject replyCommentatorObj = replyObj.getJSONObject("commentor");
                                            reply.setCommentatorID(replyCommentatorObj.getInt("id"));
                                            reply.setCommentatorName(replyCommentatorObj.getString("name"));
                                            reply.setCommentatorImage(replyCommentatorObj.getString("avatar"));

                                            JSONArray replyCommentLikeArray=replyObj.getJSONArray("likes");
                                            reply.setCommentLikesCount(replyCommentLikeArray.length());

                                            final Reply manageReply = realm.copyToRealm(reply);
                                            comment.getReplyRealmList().add(manageReply);

                                        }

                                        final Comment managedComment = realm.copyToRealm(comment);
                                        specificPostModel.getCommentRealmList().add(managedComment);

                                    }
                                    SetUI();
                                    realm.commitTransaction();
                                }

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        realm.close();
                    }
                });
            }
        });
    }


    void SetUI(){
        DismissSpinner();
        TextView headerName = (TextView) findViewById(R.id.app_toolbar_name);
        headerName.setText("" + specificPostModel.getCheckInWriterName());
        Name.setText("" + specificPostModel.getCheckInWriterName());

        Date date1 = GetDate("" + specificPostModel.getUpdated_at());
        SimpleDateFormat localDateFormatForTime1 = new SimpleDateFormat("h:mm a");
        String gettime = localDateFormatForTime1.format(date1);
        SimpleDateFormat localDateFormatForDay1 = new SimpleDateFormat("EEE");
        String getday = localDateFormatForDay1.format(date1);
        SimpleDateFormat localDateFormatForDate1 = new SimpleDateFormat("MMM d");
        String getdates = localDateFormatForDate1.format(date1);

        time.setText(getdates + ", " + gettime);

        status.setText("" + specificPostModel.getCheckInStatus());
        likesCount.setText("" + specificPostModel.getReviewLikesCount());
        commentsCount.setText("" + specificPostModel.getReviewCommentCount());
        sharesCount.setText("" + specificPostModel.getReviewSharesCount());

        Constants.PicassoImageSrc(specificPostModel.getCheckInWriterAvatar(), userProfile, NotificationClickPostDetail.this);

        if(typeName.equals("Review")) {
            postImage.setVisibility(View.GONE);
        }else {
            if(specificPostModel.getCheckInImage()!=null) {
                if (specificPostModel.getCheckInImage().equals("")) {
                    postImage.setVisibility(View.GONE);
                } else {
                    postImage.setVisibility(View.VISIBLE);
                    Constants.PicassoLargeImageBackgroundNewsFeed(specificPostModel.getCheckInImage(), postImage, image_spinner ,NotificationClickPostDetail.this);
                }
            }
        }


        commentRealmList = specificPostModel.getCommentRealmList();

        for (int i = commentRealmList.size() - 1; i >= 0; i--) {

            View child = inflater.inflate(R.layout.comment_row, null);
            TextView commentatorName = (TextView) child.findViewById(R.id.commentator_name);
            TextView commentatorComment = (TextView) child.findViewById(R.id.commentator_comment);
            TextView commentTime = (TextView) child.findViewById(R.id.comment_time);
            TextView commentLikesCount = (TextView) child.findViewById(R.id.comment_likes_count);
            de.hdodenhof.circleimageview.CircleImageView commentator_profile_image = (de.hdodenhof.circleimageview.CircleImageView)
                    child.findViewById(R.id.commentator_profile_image);

            if (!commentRealmList.get(i).getCommentatorImage().equals("")) {
                Constants.PicassoImageSrc(commentRealmList.get(i).getCommentatorImage(), commentator_profile_image, this);
            }

            TextView commentLikes = (TextView) child.findViewById(R.id.comment_Like);
            commentLikes.setOnClickListener(LikeClick);
            commentLikes.setTag(commentRealmList.get(i).getCommentID());

            TextView commentReply = (TextView) child.findViewById(R.id.comment_reply);
            commentReply.setOnClickListener(ReplyClick);
            commentReply.setTag(commentRealmList.get(i).getCommentID());

            commentator_profile_image.setOnClickListener(RedirectUser);
            commentator_profile_image.setTag(commentRealmList.get(i).getCommentatorID());
            commentatorName.setOnClickListener(RedirectUser);
            commentatorName.setTag(commentRealmList.get(i).getCommentatorID());

            commentatorName.setText(commentRealmList.get(i).getCommentatorName());
            commentatorComment.setText(commentRealmList.get(i).getCommentSummary());
            Date date = GetDate(commentRealmList.get(i).getCommentUpdated_at());

            SimpleDateFormat localDateFormatForTime = new SimpleDateFormat("h:mm a");
            String time = localDateFormatForTime.format(date);
            SimpleDateFormat localDateFormatForDay = new SimpleDateFormat("EEE");
            String day = localDateFormatForDay.format(date);
            SimpleDateFormat localDateFormatForDate = new SimpleDateFormat("MMM d");
            String dates = localDateFormatForDate.format(date);

            commentTime.setText(dates + " " + time);
            commentLikesCount.setText(""+ commentRealmList.get(i).getCommentLikesCount());

            reply_row=(LinearLayout)child.findViewById(R.id.comment_reply_);
            replyRealmList=commentRealmList.get(i).getReplyRealmList();
            for (int rp=replyRealmList.size()-1;rp>=0;rp--){

                View rplyChild = rplyInflater.inflate(R.layout.reply_row, null);

                TextView replyCommentatorName = (TextView) rplyChild.findViewById(R.id.commentator_name);
                TextView replyCommentatorComment = (TextView) rplyChild.findViewById(R.id.commentator_comment);
                TextView replyCommentTime = (TextView) rplyChild.findViewById(R.id.comment_time);
                TextView replyCommentLikesCount = (TextView) rplyChild.findViewById(R.id.comment_likes_count);
                de.hdodenhof.circleimageview.CircleImageView reply_commentator_profile_image = (de.hdodenhof.circleimageview.CircleImageView)
                        rplyChild.findViewById(R.id.commentator_profile_image);

                if (!replyRealmList.get(rp).getCommentatorImage().equals("")) {
                    Constants.PicassoImageSrc(replyRealmList.get(rp).getCommentatorImage(), reply_commentator_profile_image, this);
                }

                TextView replyCommentLikes = (TextView) rplyChild.findViewById(R.id.rply_Like);
                replyCommentLikes.setOnClickListener(LikeClick);
                replyCommentLikes.setTag(replyRealmList.get(rp).getCommentID());

                reply_commentator_profile_image.setOnClickListener(RedirectUser);
                reply_commentator_profile_image.setTag(replyRealmList.get(rp).getCommentatorID());
                replyCommentatorName.setOnClickListener(RedirectUser);
                replyCommentatorName.setTag(replyRealmList.get(rp).getCommentatorID());

                replyCommentatorName.setText(replyRealmList.get(rp).getCommentatorName());
                replyCommentatorComment.setText(replyRealmList.get(rp).getCommentSummary());
                Date replyDate = GetDate(replyRealmList.get(rp).getCommentUpdated_at());

                SimpleDateFormat rplylocalDateFormatForTime = new SimpleDateFormat("h:mm a");
                String replytime = rplylocalDateFormatForTime.format(replyDate);
                SimpleDateFormat replylocalDateFormatForDay = new SimpleDateFormat("EEE");
                String replyday = replylocalDateFormatForDay.format(replyDate);
                SimpleDateFormat replylocalDateFormatForDate = new SimpleDateFormat("MMM d");
                String replydates = replylocalDateFormatForDate.format(replyDate);

                replyCommentTime.setText(replydates + " " + replytime);
                replyCommentLikesCount.setText(""+ replyRealmList.get(rp).getCommentLikesCount());
                reply_row.addView(rplyChild);
            }

            comments_row.addView(child);
        }


        post_add_comment_edit_text.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String comment = post_add_comment_edit_text.getText().toString();
                    if (!comment.equals("")) {
                        post_add_comment_edit_text.setText("");
                        if(typeName.equals("Post")) {
                            SendCommentDataOnServer("" + specificPostModel.getCheckInID(), "Title", comment, "", URLs.Add_Comment_Post);
                        }else if(typeName.equals("Review")){
                            SendCommentDataOnServer("" + specificPostModel.getCheckInID(), "Title", comment, "", URLs.Add_Comment_Review);
                        }
                        int prev = Integer.valueOf(commentsCount.getText().toString());
                        prev++;
                        commentsCount.setText("" + prev);
                    }

                    return true;
                }
                return false;
            }
        });

        if(specificPostModel.isLiked()){
            like_toggle_image.setTag(1);
            like_toggle_image.setBackground(NotificationClickPostDetail.this.getResources().getDrawable(R.drawable.icon_likes_count));
        }else {
            like_toggle_image.setTag(0);
            like_toggle_image.setBackground(NotificationClickPostDetail.this.getResources().getDrawable(R.drawable.icon_for_like));
        }
    }

    void SendCommentDataOnServer(String id, String title, String comment, String imageURL, String routeURL){

        OkHttpClient  client = new OkHttpClient();
        String message=null;
        realm = Realm.getDefaultInstance();
        // Persist your data in a transaction

        User user = realm.where(User.class).findFirst();

        FormBody body = new FormBody.Builder()
                .add("title",title)
                .add("comment",comment)
                .add("id", id)
                .add("image[]",imageURL)
                .build();

        Request request = new Request.Builder()
                .url(routeURL)
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try
                        {
                            Log.e("Obj", obj.toString());
                            JSONObject jsonObject = new JSONObject(obj);
//                            if (jsonObject.has("comment")) {
//                                Comment comment = new Comment();
//                                JSONObject commentObj = jsonObject.getJSONObject("comment");
//                                comment.setCommentID(commentObj.getInt("id"));
//                                comment.setCommentTitle(commentObj.getString("title"));
//                                comment.setCommentUpdated_at(commentObj.getString("created_at"));
//                                comment.setCommentSummary(commentObj.getString("comment"));
//
//                                JSONObject commentatorObj = commentObj.getJSONObject("commentor");
//                                comment.setCommentatorID(commentatorObj.getInt("id"));
//                                comment.setCommentatorName(commentatorObj.getString("name"));
//                                comment.setCommentatorImage(commentatorObj.getString("avatar"));
//
//                                JSONArray commentLikeArray = commentObj.getJSONArray("likes");
//                                comment.setCommentLikesCount(commentLikeArray.length());
//                                View child = inflater.inflate(R.layout.comment_row, null);
//                                TextView commentatorName = (TextView) child.findViewById(R.id.commentator_name);
//                                TextView commentatorComment = (TextView) child.findViewById(R.id.commentator_comment);
//                                TextView commentTime = (TextView) child.findViewById(R.id.comment_time);
//                                TextView commentLikesCount = (TextView) child.findViewById(R.id.comment_likes_count);
//
//                                de.hdodenhof.circleimageview.CircleImageView commentator_profile_image=(de.hdodenhof.circleimageview.CircleImageView)
//                                        child.findViewById(R.id.commentator_profile_image);
//
//                                if(!comment.getCommentatorImage().equals("")){
//                                    Constants.PicassoImageSrc(comment.getCommentatorImage(),commentator_profile_image,NotificationClickPostDetail.this);
//                                }
//
//
//                                TextView commentLikes = (TextView) child.findViewById(R.id.comment_Like);
//                                commentLikes.setTag(comment.getCommentID());
//
//                                commentatorName.setText(comment.getCommentatorName());
//                                commentatorComment.setText(comment.getCommentSummary());
//                                Date date = GetDate(comment.getCommentUpdated_at());
//
//                                SimpleDateFormat localDateFormatForTime = new SimpleDateFormat("h:mm a");
//                                String time = localDateFormatForTime.format(date);
//                                SimpleDateFormat localDateFormatForDay = new SimpleDateFormat("EEE");
//                                String day = localDateFormatForDay.format(date);
//                                SimpleDateFormat localDateFormatForDate = new SimpleDateFormat("MMM d");
//                                String dates = localDateFormatForDate.format(date);
//
//
//                                commentTime.setText(dates + " " + time);
//                                commentLikesCount.setText("" + comment.getCommentLikesCount());
//                                comments_row.addView(child,0);
//                            }
                            Intent intent = new Intent(NotificationClickPostDetail.this, NotificationClickPostDetail.class);
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

    View.OnClickListener LikeClick = new View.OnClickListener() {
        public void onClick(View v) {
            int idxStr = (int) v.getTag();
            Like(idxStr,"comment");
//            Toast.makeText(NotificationClickPostDetail.this,"Liked "+idxStr,Toast.LENGTH_SHORT).show();
        }
    };

    View.OnClickListener ReplyClick = new View.OnClickListener() {
        public void onClick(View v) {
            int id=(int)v.getTag();
            Intent intent = new Intent(NotificationClickPostDetail.this, ReplyActivity.class);
            intent.putExtra("CommentID", id);
            intent.putExtra("TypeID",typeID);
            intent.putExtra("Type",typeName);
            startActivity(intent);
            finish();
        }
    };

    View.OnClickListener RedirectUser = new View.OnClickListener() {
        public void onClick(View v) {
            int idxStr = (int) v.getTag();
            Intent intent=new Intent(NotificationClickPostDetail.this, ProfilesDetailActivity.class);
            intent.putExtra("UserID", idxStr);
            startActivity(intent);
        }
    };


    public void Like(int id, String type, final TextView tv, final LinearLayout layout_like){

        Realm realm=Realm.getDefaultInstance();
        realm.beginTransaction();
        final User user = realm.where(User.class).findFirst();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Like_+type+"/"+id)
                .addHeader("Authorization", "Token token="+user.getToken())
                .build();

        realm.commitTransaction();
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
    }

    void ReviewShare(String statusStr, int restaurantID,int rid){
        showSpinner("Please Wait...");
        // Get a Realm instance for this thread
        Realm realm = Realm.getDefaultInstance();
        // Persist your data in a transaction

        User user = realm.where(User.class).findFirst();

        RequestBody requestBody;

        requestBody= new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("review[title]","Write Review")
                .addFormDataPart("review[summary]",statusStr)
                .addFormDataPart("review[rating]", "")
                .addFormDataPart("review[share_id]", ""+rid)
                .addFormDataPart("review[reviewable_id]",""+restaurantID)
                .build();

        Request request = new Request.Builder()
                .url(URLs.Restaurant_Review)
                .addHeader("Authorization", "Token token="+user.getToken())
                .post(requestBody)
                .build();

        OkHttpClient client;
        client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try{
                    String obj=response.body().string();
                    Log.e("Res",""+obj);
                    JSONObject jsonObject=new JSONObject(obj);
                    if(jsonObject.has("review")){
                        Log.e("","Post Successfully");
                    }
                    else  if(jsonObject.has("message")){
                        Log.e("","Not Posted");
                    }
                    DismissSpinner();
                }catch (Exception e){
                    Log.i("Exception ::",""+ e.getMessage());
                }
                finally
                {
                    DismissSpinner();
                }

            }
        });
    }

    void SharePost(String imageURL,String statusStr, double restaurantLatitude,double restaurantLongitude, int restaurantID, int rid){

        showSpinner("Please wait...");

        Picasso.with(this).load(imageURL).into(new Target(){
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                file=bitmapConvertToFile(bitmap);
            }
            @Override
            public void onBitmapFailed(final Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(final Drawable placeHolderDrawable) {
            }
        });

        // Get a Realm instance for this thread
        Realm realm = Realm.getDefaultInstance();
        // Persist your data in a transaction

        User user = realm.where(User.class).findFirst();

        RequestBody requestBody;
        if(file!=null){
            requestBody= new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("post[image][]", file.getName(),
                            RequestBody.create(MediaType.parse("text/csv"), file))
                    .addFormDataPart("post[title]","Post")
                    .addFormDataPart("post[status]",statusStr)
                    .addFormDataPart("post[share_id]",""+rid)
                    .addFormDataPart("post[checkin_attributes][address]", ""+user.getLocation())
                    .addFormDataPart("post[checkin_attributes][lat]",""+restaurantLatitude)
                    .addFormDataPart("post[checkin_attributes][long]",""+restaurantLongitude)
                    .addFormDataPart("post[checkin_attributes][restaurant_id]",""+restaurantID)
                    .build();

        }
        else
        {
            requestBody= new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
//                    .addFormDataPart("post[image][]", "")
                    .addFormDataPart("post[title]","Post")
                    .addFormDataPart("post[status]",statusStr)
                    .addFormDataPart("post[share_id]",""+rid)
                    .addFormDataPart("post[checkin_attributes][address]", ""+user.getLocation())
                    .addFormDataPart("post[checkin_attributes][lat]",""+restaurantLatitude)
                    .addFormDataPart("post[checkin_attributes][long]",""+restaurantLongitude)
                    .addFormDataPart("post[checkin_attributes][restaurant_id]",""+restaurantID)
                    .build();
        }


        Request request = new Request.Builder()
                .url(URLs.Posts)
                .addHeader("Authorization", "Token token="+user.getToken())
                .post(requestBody)
                .build();

        OkHttpClient client;
        client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try{
                    String obj=response.body().string();
                    Log.e("Res",""+obj);
                    JSONObject jsonObject=new JSONObject(obj);
                    if(jsonObject.has("post")){
                        Log.e("","Post Successfully");
                    }
                    else  if(jsonObject.has("message")){
                        Log.e("","Not Posted");
                    }
                    DismissSpinner();
                }catch (Exception e){
                    Log.i("Exception ::",""+ e.getMessage());
                }
                finally
                {
                }

            }
        });
    }

    public File bitmapConvertToFile(Bitmap bitmap) {
//        showSpinner("Croping...");
        FileOutputStream fileOutputStream = null;
        File files=null;
        try {
            final File file = new File(Environment.getExternalStoragePublicDirectory("image_crop_sample"),"");
            if (!file.exists()) {
                file.mkdir();
            }else {
                file.delete();
                file.mkdir();
            }


            files = new File(file, "IMG_1" + ".jpg");
            fileOutputStream = new FileOutputStream(files);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            MediaScannerConnection.scanFile(this, new String[]{files.getAbsolutePath()}, null, new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {

                }

                @Override
                public void onScanCompleted(String path, Uri uri) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try
                {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
                catch (Exception e)
                {

                }
            }
        }

        return files;
    }

    public void UnLike(int id, String type, final TextView tv, final LinearLayout layout_like){
        // Get a Realm instance for this thread
        Realm realm=Realm.getDefaultInstance();
        // Persist your data in a transaction
        realm.beginTransaction();
        final User user = realm.where(User.class).findFirst();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.RC_UN_Like_+type+"/"+id)
                .addHeader("Authorization", "Token token="+user.getToken())
                .build();

        realm.commitTransaction();
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
                                if(!message.equals("Successfully unliked!")) {
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
    }

    // Comment Like
    public void Like(int id, String type){

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
//                                    int prev = Integer.valueOf(textView.getText().toString());
//                                    prev--;
//                                    textView.setText("" + prev);
                                }
                                Intent intent = new Intent(NotificationClickPostDetail.this, NotificationClickPostDetail.class);
                                intent.putExtra("TypeID",typeID);
                                intent.putExtra("Type",typeName);
                                startActivity(intent);
                                finish();
                            }
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
