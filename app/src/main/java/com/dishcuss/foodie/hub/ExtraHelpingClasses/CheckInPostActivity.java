package com.dishcuss.foodie.hub.ExtraHelpingClasses;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dishcuss.foodie.Model.Comment;
import com.dishcuss.foodie.Model.PhotoModel;
import com.dishcuss.foodie.Model.ReviewModel;
import com.dishcuss.foodie.Model.User;
import com.dishcuss.foodie.Model.UserBeenThere;
import com.dishcuss.foodie.Model.UserFollowing;
import com.dishcuss.foodie.Model.UserProfile;
import com.dishcuss.foodie.hub.R;
import com.dishcuss.foodie.hub.Utils.Constants;
import com.dishcuss.foodie.hub.Utils.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 8/15/2016.
 */
public class CheckInPostActivity extends AppCompatActivity{

    OkHttpClient client;
    AutoCompleteTextView userLocation;
    EditText status;
    String statusStr="";
    String imagePath="";
    ImageView imageView;
    ImageView select_photo_layout;
    String loc="";
    double restaurantLongitude=0.0;
    double restaurantLatitude=0.0;
    int restaurantID=0;
    File file=null;
    UserProfile userProfile=new UserProfile();
    Realm realm;

    TextView headerName,postClick;

    ArrayList<String> places=new ArrayList<>();
    ArrayList<Integer> resID=new ArrayList<>();
    ArrayList<Double> placeLat=new ArrayList<>();
    ArrayList<Double> placeLong=new ArrayList<>();

    ArrayAdapter<String> placeAdapter;
    TextView write_reviewer_user_name;
    de.hdodenhof.circleimageview.CircleImageView write_reviewer_user_profile_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity_photo_post);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        client = new OkHttpClient();
        realm = Realm.getDefaultInstance();
        User user = realm.where(User.class).findFirst();
        userProfile=GetUserData(user.getId());


        userLocation=(AutoCompleteTextView) findViewById(R.id.write_reviewer_address_auto);
        imageView= (ImageView) findViewById(R.id.imageView_pic_upload_photo);
        select_photo_layout= (ImageView) findViewById(R.id.select_photo);
        status=(EditText)findViewById(R.id.post_status);
        headerName=(TextView)findViewById(R.id.toolbar_name);
        postClick=(TextView)findViewById(R.id.click_post);
        headerName.setText("Check In");


        write_reviewer_user_name=(TextView)findViewById(R.id.write_reviewer_user_name);
        write_reviewer_user_profile_image=(de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.write_reviewer_user_profile_image);

        if(userProfile==null){
            UserData(user.getId());
        }else {
            write_reviewer_user_name.setText(userProfile.getName());
            if (!userProfile.getAvatar().equals(""))
            {
                Constants.PicassoImageSrc(userProfile.getAvatar(),write_reviewer_user_profile_image ,CheckInPostActivity.this);
            }
        }

        postClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!status.getText().toString().equals("")){
                    statusStr=status.getText().toString();
                }
                if( restaurantID!=0) {

                }else {
                    Toast.makeText(CheckInPostActivity.this,"Data Missing",Toast.LENGTH_LONG).show();
                }
            }
        });

        select_photo_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setVisibility(View.VISIBLE);
                SelectImage();
            }
        });



//        for (int i=0;i< SplashActivity.restaurantForStatusArrayList.size();i++){
//            places.add(SplashActivity.restaurantForStatusArrayList.get(i).getName());
//            resID.add(SplashActivity.restaurantForStatusArrayList.get(i).getId());
//            placeLat.add(SplashActivity.restaurantForStatusArrayList.get(i).getRestaurantLat());
//            placeLong.add(SplashActivity.restaurantForStatusArrayList.get(i).getRestaurantLong());
//            Log.e("Place "+i,""+SplashActivity.restaurantForStatusArrayList.get(i).getName());
//        }



        placeAdapter = new ArrayAdapter<String>(CheckInPostActivity.this, android.R.layout.simple_list_item_1, places);
        userLocation.setThreshold(1);
        userLocation.setOnItemClickListener(mAutocompleteClickListenerLocationSelection);
        userLocation.setAdapter(placeAdapter);


//        userLocation.addTextChangedListener(new CheckPercentage());
//        userLocation.setOnItemClickListener(mAutocompleteClickListenerLocationSelection);

    }


    private AdapterView.OnItemClickListener mAutocompleteClickListenerLocationSelection
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){

            restaurantID=resID.get(position);
            restaurantLatitude=placeLat.get(position);
            restaurantLongitude=placeLong.get(position);
            Log.e("","ok");
        }
    };

    private void SelectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckInPostActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {

                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try
                {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), bitmapOptions);


                    imageView.setImageBitmap(bitmap);
                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";

                    f.delete();
                    OutputStream outFile = null;
                    file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (requestCode == 2) {

                Uri selectedImageUri = data.getData();
                String[] projection = { MediaStore.MediaColumns.DATA };
                Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                        null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();

                String selectedImagePath = cursor.getString(column_index);
                Log.e("path", selectedImagePath);
                file=new File(selectedImagePath);

                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);

                imageView.setImageBitmap(bm);
            }
        }
    }


    UserProfile GetUserData(int uid){
        realm = Realm.getDefaultInstance();
        RealmResults<UserProfile> userProfiles = realm.where(UserProfile.class).equalTo("id", uid).findAll();
        Log.e("Count",""+userProfiles.size());
        if(userProfiles.size()>0){
            realm.beginTransaction();
            realm.commitTransaction();
            return userProfiles.get(userProfiles.size()-1);
        }
        return null;
    }

    void UserData(int userID) {

//        Log.e("User","Post Selection");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Get_User_data+userID)
                .build();
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

                            JSONObject jsonObj = new JSONObject(objStr);

                            if(jsonObj.has("user"))
                            {
                                JSONObject userObj = jsonObj.getJSONObject("user");

                                realm.beginTransaction();
                                UserProfile userProfileRealm = realm.createObject(UserProfile.class);

                                userProfileRealm.setId(userObj.getInt("id"));
                                userProfileRealm.setName(userObj.getString("name"));
                                userProfileRealm.setUsername(userObj.getString("username"));
                                userProfileRealm.setEmail(userObj.getString("email"));
                                userProfileRealm.setAvatar(userObj.getString("avatar"));
                                userProfileRealm.setLocation(userObj.getString("location"));
                                userProfileRealm.setGender(userObj.getString("gender"));
                                userProfileRealm.setRole(userObj.getString("role"));

                                //Arrays
                                JSONArray jsonDataFollowingArray = userObj.getJSONArray("following");
                                JSONArray jsonDataFollowersArray = userObj.getJSONArray("followers");
                                JSONArray jsonDataPostsArray = userObj.getJSONArray("posts");
                                JSONArray jsonDataReviewsArray = userObj.getJSONArray("reviews");



                                for(int p=0;p<jsonDataPostsArray.length();p++){
                                    JSONObject postObj=jsonDataPostsArray.getJSONObject(p);
                                    JSONObject checkinObj = postObj.getJSONObject("checkin");

                                    if(checkinObj.has("restaurant")) {
                                        JSONObject restaurantObj = checkinObj.getJSONObject("restaurant");

                                        UserBeenThere userBeenThere = new UserBeenThere();
                                        userBeenThere.setId(restaurantObj.getInt("id"));
                                        userBeenThere.setRestaurantName(restaurantObj.getString("name"));
                                        userBeenThere.setRestaurantLocation(restaurantObj.getString("location"));
                                        userBeenThere.setCover_image_url(checkinObj.getString("restaurant_image"));
                                        userBeenThere.setBeenThereTime(checkinObj.getString("time"));
                                        final UserBeenThere beenThere = realm.copyToRealm(userBeenThere);
                                        userProfileRealm.getUserBeenThereRealmList().add(beenThere);
                                    }


                                    JSONArray jsonDataPhotosArray = postObj.getJSONArray("photos");
                                    for (int ph = 0; ph < jsonDataPhotosArray.length(); ph++) {
                                        JSONObject photo = jsonDataPhotosArray.getJSONObject(ph);
                                        PhotoModel photoModel = new PhotoModel();
                                        photoModel.setId(photo.getInt("id"));
                                        photoModel.setUrl(photo.getString("image_url"));
                                        final PhotoModel managedPhotoModel = realm.copyToRealm(photoModel);
                                        userProfileRealm.getPhotoModelRealmList().add(managedPhotoModel);
                                    }

                                    JSONArray jsonDataCommentsArray = postObj.getJSONArray("comments");
                                    for (int c = 0; c < jsonDataCommentsArray.length(); c++) {
                                        JSONObject commentObj = jsonDataCommentsArray.getJSONObject(c);
                                        Comment comment= new Comment();
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
                                        final Comment managedComment = realm.copyToRealm(comment);
                                        userProfileRealm.getCommentRealmList().add(managedComment);
                                    }
                                }

                                for (int r = 0; r < jsonDataReviewsArray.length();r++) {

                                    JSONObject reviewObj = jsonDataReviewsArray.getJSONObject(r);
                                    realm.commitTransaction();
                                    realm.beginTransaction();
                                    ReviewModel reviewModel=realm.createObject(ReviewModel.class);

                                    reviewModel.setReview_ID(reviewObj.getInt("id"));
                                    reviewModel.setReviewable_id(reviewObj.getInt("reviewable_id"));
                                    reviewModel.setReview_title(reviewObj.getString("title"));
                                    reviewModel.setUpdated_at(reviewObj.getString("updated_at"));
                                    reviewModel.setReview_summary(reviewObj.getString("summary"));
                                    reviewModel.setReviewable_type(reviewObj.getString("reviewable_type"));

                                    JSONObject reviewObjReviewer= reviewObj.getJSONObject("reviewer");

                                    reviewModel.setReview_reviewer_ID(reviewObjReviewer.getInt("id"));
                                    reviewModel.setReview_reviewer_Name(reviewObjReviewer.getString("name"));
                                    reviewModel.setReview_reviewer_Avatar(reviewObjReviewer.getString("avatar"));
                                    reviewModel.setReview_reviewer_time(reviewObjReviewer.getString("location"));

                                    JSONArray reviewLikesArray = reviewObj.getJSONArray("likes");
                                    JSONArray reviewCommentsArray = reviewObj.getJSONArray("comments");
                                    JSONArray reviewShareArray = reviewObj.getJSONArray("reports");

                                    reviewModel.setReview_Likes_count(reviewLikesArray.length());
                                    reviewModel.setReview_comments_count(reviewCommentsArray.length());
                                    reviewModel.setReview_shares_count(reviewShareArray.length());



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
                                        final Comment managedComment = realm.copyToRealm(comment);
                                        reviewModel.getCommentRealmList().add(managedComment);
                                    }
                                    realm.commitTransaction();
                                    realm.beginTransaction();

                                    final ReviewModel managedReviewModel= realm.copyToRealm(reviewModel);
                                    userProfileRealm.getReviewModelRealmList().add(managedReviewModel);

                                }


                                for(int fs=0;fs<jsonDataFollowingArray.length();fs++){
                                    JSONObject jsonFollowingObject = jsonDataFollowingArray.getJSONObject(fs);
                                    UserFollowing userFollowing=new UserFollowing();

                                    userFollowing.setId(jsonFollowingObject.getInt("id"));
                                    userFollowing.setLikesCount(jsonFollowingObject.getInt("likees_count"));
                                    userFollowing.setFollowerCount(jsonFollowingObject.getInt("followers_count"));
                                    userFollowing.setFollowingCount(jsonFollowingObject.getInt("followees_count"));

                                    userFollowing.setName(jsonFollowingObject.getString("name"));
                                    userFollowing.setUsername(jsonFollowingObject.getString("username"));
                                    userFollowing.setAvatar(jsonFollowingObject.getString("avatar"));
                                    userFollowing.setLocation(jsonFollowingObject.getString("location"));
                                    userFollowing.setEmail(jsonFollowingObject.getString("email"));
                                    userFollowing.setGender(jsonFollowingObject.getString("gender"));
                                    userFollowing.setRole(jsonFollowingObject.getString("name"));
                                    userFollowing.setReferral_code(jsonFollowingObject.getString("referal_code"));

                                    final UserFollowing managedUserFollowing = realm.copyToRealm(userFollowing);
                                    userProfileRealm.getUserFollowingRealmList().add(managedUserFollowing);
                                }

                                for(int fr=0;fr<jsonDataFollowersArray.length();fr++){
                                    JSONObject jsonFollowingObject = jsonDataFollowersArray.getJSONObject(fr);

                                    UserFollowing userFollowing=new UserFollowing();

                                    userFollowing.setId(jsonFollowingObject.getInt("id"));
                                    userFollowing.setLikesCount(jsonFollowingObject.getInt("likees_count"));
                                    userFollowing.setFollowerCount(jsonFollowingObject.getInt("followers_count"));
                                    userFollowing.setFollowingCount(jsonFollowingObject.getInt("followees_count"));

                                    userFollowing.setName(jsonFollowingObject.getString("name"));
                                    userFollowing.setUsername(jsonFollowingObject.getString("username"));
                                    userFollowing.setAvatar(jsonFollowingObject.getString("avatar"));
                                    userFollowing.setLocation(jsonFollowingObject.getString("location"));
                                    userFollowing.setEmail(jsonFollowingObject.getString("email"));
                                    userFollowing.setGender(jsonFollowingObject.getString("gender"));
                                    userFollowing.setRole(jsonFollowingObject.getString("name"));
                                    userFollowing.setReferral_code(jsonFollowingObject.getString("referal_code"));

                                    final UserFollowing managedUserFollowing = realm.copyToRealm(userFollowing);
                                    userProfileRealm.getUserFollowersRealmList().add(managedUserFollowing);
                                }

                                userProfile=userProfileRealm;

                                write_reviewer_user_name.setText(userProfile.getName());
                                if (!userProfile.getAvatar().equals(""))
                                {
                                    Constants.PicassoImageSrc(userProfile.getAvatar(),write_reviewer_user_profile_image ,CheckInPostActivity.this);
                                }
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

}
