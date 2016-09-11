package com.holygon.dishcuss.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.holygon.dishcuss.Model.Comment;
import com.holygon.dishcuss.Model.LocalFeedCheckIn;
import com.holygon.dishcuss.Model.LocalFeedReview;
import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;
import com.holygon.dishcuss.Utils.GenericRoutes;
import com.holygon.dishcuss.Utils.URLs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 9/8/2016.
 */
public class PostDetailActivity extends AppCompatActivity {

    LocalFeedReview localFeedReview;
    LocalFeedCheckIn localFeedCheckIn;
    RealmList<Comment> commentRealmList;
    boolean isCheckIn;
    LinearLayout comments_row;
    TextView Name,time,status,likesCount,commentsCount,sharesCount;
    ImageView userProfile;
    ImageView postImage;
    EditText post_add_comment_edit_text;
    LinearLayout post_add_comment_edit_text_parent;
    Realm realm;
    LayoutInflater inflater;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        GetUI();
        inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String type=bundle.getString("Type");
            if(type.equals("CheckIn")){
                isCheckIn=true;
                localFeedCheckIn=(LocalFeedCheckIn) bundle.getParcelable("MyClass");
            }else {
                isCheckIn=false;
                localFeedReview=(LocalFeedReview) bundle.getParcelable("MyClass");
            }
        }

        post_add_comment_edit_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Constants.skipLogin) {
                    post_add_comment_edit_text.setFocusableInTouchMode(true);
                    post_add_comment_edit_text.setFocusable(true);
                }
            }
        });

        if(!isCheckIn){

            TextView headerName=(TextView)findViewById(R.id.app_toolbar_name);
            headerName.setText(""+localFeedReview.getReviewerName());
            Name.setText(""+localFeedReview.getReviewerName());

            Date date1= GetDate(""+localFeedReview.getUpdated_at());
            SimpleDateFormat localDateFormatForTime1 = new SimpleDateFormat("h:mm a");
            String gettime = localDateFormatForTime1.format(date1);
            SimpleDateFormat localDateFormatForDay1 = new SimpleDateFormat("EEE");
            String getday = localDateFormatForDay1.format(date1);
            SimpleDateFormat localDateFormatForDate1 = new SimpleDateFormat("MMM d");
            String getdates = localDateFormatForDate1.format(date1);

            time.setText(getdates+", "+gettime);

            status.setText(""+localFeedReview.getSummary());
            likesCount.setText(""+localFeedReview.getReviewLikesCount());
            commentsCount.setText(""+localFeedReview.getReviewCommentCount());
            sharesCount.setText(""+localFeedReview.getReviewSharesCount());

            Constants.PicassoImageBackground(localFeedReview.getReviewerAvatar(),userProfile,PostDetailActivity.this);
            postImage.setVisibility(View.GONE);

//            if(localFeedReview.getReviewImage().equals("")){
//                postImage.setVisibility(View.GONE);
//            }else {
//                Constants.PicassoImageBackground(localFeedReview.getReviewImage(),postImage,PostDetailActivity.this);
//            }

            commentRealmList=localFeedReview.getCommentRealmList();

            for (int i=commentRealmList.size()-1;i>=0;i--) {
                View child = inflater.inflate(R.layout.comment_row, null);
                TextView commentatorName=(TextView)child.findViewById(R.id.commentator_name);
                TextView commentatorComment=(TextView)child.findViewById(R.id.commentator_comment);
                TextView commentTime=(TextView)child.findViewById(R.id.comment_time);
                TextView commentLikesCount=(TextView)child.findViewById(R.id.comment_likes_count);

                commentatorName.setText(commentRealmList.get(i).getCommentatorName());
                commentatorComment.setText(commentRealmList.get(i).getCommentSummary());
                Date date= GetDate(commentRealmList.get(i).getCommentUpdated_at());

                SimpleDateFormat localDateFormatForTime = new SimpleDateFormat("h:mm a");
                String time = localDateFormatForTime.format(date);
                SimpleDateFormat localDateFormatForDay = new SimpleDateFormat("EEE");
                String day = localDateFormatForDay.format(date);
                SimpleDateFormat localDateFormatForDate = new SimpleDateFormat("MMM d");
                String dates = localDateFormatForDate.format(date);

                commentTime.setText(dates+" "+time);
                commentLikesCount.setText("  "+commentRealmList.get(i).getCommentLikesCount());
                comments_row.addView(child);
            }


            post_add_comment_edit_text.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        String comment=post_add_comment_edit_text.getText().toString();
                        if(!comment.equals("")){
                            post_add_comment_edit_text.setText("");
                            post_add_comment_edit_text.clearFocus();
                            post_add_comment_edit_text.setFocusable(false);
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(post_add_comment_edit_text.getWindowToken(),
                                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
                            imm.hideSoftInputFromWindow(post_add_comment_edit_text.getWindowToken(), 0);
                            SendCommentDataOnServer(""+localFeedReview.getReviewID(),"Title",comment,"", URLs.Add_Comment_Review);
                            int prev=Integer.valueOf(commentsCount.getText().toString());
                            prev++;
                            commentsCount.setText(""+prev);
                        }
                    }
                    return true;
                }
            });
        }
        else {
            TextView headerName=(TextView)findViewById(R.id.app_toolbar_name);
            headerName.setText(""+localFeedCheckIn.getCheckInWriterName());
            Name.setText(""+localFeedCheckIn.getCheckInWriterName());


            Date date1= GetDate(""+localFeedCheckIn.getUpdated_at());
            SimpleDateFormat localDateFormatForTime1 = new SimpleDateFormat("h:mm a");
            String gettime = localDateFormatForTime1.format(date1);
            SimpleDateFormat localDateFormatForDay1 = new SimpleDateFormat("EEE");
            String getday = localDateFormatForDay1.format(date1);
            SimpleDateFormat localDateFormatForDate1 = new SimpleDateFormat("MMM d");
            String getdates = localDateFormatForDate1.format(date1);

            time.setText(getdates+", "+gettime);
            status.setText(""+localFeedCheckIn.getCheckInStatus());
            likesCount.setText(""+localFeedCheckIn.getReviewLikesCount());
            commentsCount.setText(""+localFeedCheckIn.getReviewCommentCount());
            sharesCount.setText(""+localFeedCheckIn.getReviewSharesCount());

            Constants.PicassoImageBackground(""+localFeedCheckIn.getCheckInWriterAvatar(),userProfile,PostDetailActivity.this);

            if(localFeedCheckIn.getCheckInImage().equals(""))
            {
                postImage.setVisibility(View.GONE);
            }
            else
            {
                Constants.PicassoImageBackground(""+localFeedCheckIn.getCheckInImage(),postImage,PostDetailActivity.this);
            }

            commentRealmList=localFeedCheckIn.getCommentRealmList();


            for (int i=commentRealmList.size()-1;i>=0;i--) {

                View child = inflater.inflate(R.layout.comment_row, null);
                TextView commentatorName=(TextView)child.findViewById(R.id.commentator_name);
                TextView commentatorComment=(TextView)child.findViewById(R.id.commentator_comment);
                TextView commentTime=(TextView)child.findViewById(R.id.comment_time);
                TextView commentLikesCount=(TextView)child.findViewById(R.id.comment_likes_count);

                commentatorName.setText(commentRealmList.get(i).getCommentatorName());
                commentatorComment.setText(commentRealmList.get(i).getCommentSummary());
                Date date= GetDate(commentRealmList.get(i).getCommentUpdated_at());

                SimpleDateFormat localDateFormatForTime = new SimpleDateFormat("h:mm a");
                String time = localDateFormatForTime.format(date);
                SimpleDateFormat localDateFormatForDay = new SimpleDateFormat("EEE");
                String day = localDateFormatForDay.format(date);
                SimpleDateFormat localDateFormatForDate = new SimpleDateFormat("MMM d");
                String dates = localDateFormatForDate.format(date);


                commentTime.setText(dates+" "+time);
                commentLikesCount.setText("  "+commentRealmList.get(i).getCommentLikesCount());
                comments_row.addView(child);
            }

            post_add_comment_edit_text.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        String comment=post_add_comment_edit_text.getText().toString();
                        if(!comment.equals("")){
                            post_add_comment_edit_text.setText("");
                            post_add_comment_edit_text.clearFocus();
                            post_add_comment_edit_text.setFocusable(false);
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(post_add_comment_edit_text.getWindowToken(),
                                    InputMethodManager.RESULT_UNCHANGED_SHOWN);
                            imm.hideSoftInputFromWindow(post_add_comment_edit_text.getWindowToken(), 0);
                            SendCommentDataOnServer(""+localFeedCheckIn.getCheckInID(),"",comment,"", URLs.Add_Comment_Post);
                            int prev=Integer.valueOf(commentsCount.getText().toString());
                            prev++;
                            commentsCount.setText(""+prev);
                        }
                    }
                    return true;
                }
            });
        }

    }

    Date GetDate(String date){
        String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
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
        post_add_comment_edit_text.clearFocus();
        post_add_comment_edit_text.setFocusable(false);
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
//                            Log.e("Obj", obj.toString());
                            JSONObject jsonObject = new JSONObject(obj);
                            if (jsonObject.has("comment")) {
                                Comment comment = new Comment();
                                JSONObject commentObj = jsonObject.getJSONObject("comment");
                                comment.setCommentID(commentObj.getInt("id"));
                                comment.setCommentTitle(commentObj.getString("title"));
                                comment.setCommentUpdated_at(commentObj.getString("created_at"));
                                comment.setCommentSummary(commentObj.getString("comment"));


                                JSONObject commentatorObj = commentObj.getJSONObject("commentor");
                                comment.setCommentatorID(commentatorObj.getInt("id"));
                                comment.setCommentatorName(commentatorObj.getString("name"));
                                comment.setCommentatorImage(commentatorObj.getString("avatar"));

                                JSONArray commentLikeArray = commentObj.getJSONArray("likes");
                                comment.setCommentLikesCount(commentLikeArray.length());
                                View child = inflater.inflate(R.layout.comment_row, null);
                                TextView commentatorName = (TextView) child.findViewById(R.id.commentator_name);
                                TextView commentatorComment = (TextView) child.findViewById(R.id.commentator_comment);
                                TextView commentTime = (TextView) child.findViewById(R.id.comment_time);
                                TextView commentLikesCount = (TextView) child.findViewById(R.id.comment_likes_count);

                                commentatorName.setText(comment.getCommentatorName());
                                commentatorComment.setText(comment.getCommentSummary());
                                Date date = GetDate(comment.getCommentUpdated_at());

                                SimpleDateFormat localDateFormatForTime = new SimpleDateFormat("h:mm a");
                                String time = localDateFormatForTime.format(date);
                                SimpleDateFormat localDateFormatForDay = new SimpleDateFormat("EEE");
                                String day = localDateFormatForDay.format(date);
                                SimpleDateFormat localDateFormatForDate = new SimpleDateFormat("MMM d");
                                String dates = localDateFormatForDate.format(date);


                                commentTime.setText(dates + " " + time);
                                commentLikesCount.setText("  " + comment.getCommentLikesCount());
                                comments_row.addView(child,0);
                            }
                        } catch (Exception e){
                        } finally{
                        }
                    }
                });
            }
        });
    }
}
