package com.dishcuss.foodie.hub.Posts;

import android.app.ProgressDialog;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dishcuss.foodie.hub.Helper.RestaurantNameAdapter;
import com.dishcuss.foodie.Model.Comment;
import com.dishcuss.foodie.Model.FoodItems;
import com.dishcuss.foodie.Model.FoodsCategory;
import com.dishcuss.foodie.Model.PhotoModel;
import com.dishcuss.foodie.Model.Restaurant;
import com.dishcuss.foodie.Model.RestaurantForStatus;
import com.dishcuss.foodie.Model.ReviewModel;
import com.dishcuss.foodie.Model.SearchRestaurant;
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
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 8/11/2016.
 */
public class WriteReviewPostActivity extends AppCompatActivity {


    OkHttpClient client;
    AutoCompleteTextView userLocation;
    EditText status;
    String statusStr="";
    String imagePath="";
    ImageView imageView;
    String loc="";
    double restaurantLongitude;
    double restaurantLatitude;
    int restaurantID;
    String RestaurantName="";
    File file=null;
    UserProfile userProfile=new UserProfile();
    ImageView rattingDialog;
    TextView headerName,postClick;

    ArrayList<String> places=new ArrayList<>();
    ArrayList<Integer> resID=new ArrayList<>();
    ArrayList<Double> placeLat=new ArrayList<>();
    ArrayList<Double> placeLong=new ArrayList<>();

    ArrayAdapter<String> placeAdapter;

    Realm realm;
    RestaurantNameAdapter restaurantNameAdapter;
    TextView write_reviewer_user_name;
    de.hdodenhof.circleimageview.CircleImageView write_reviewer_user_profile_image;

    String rattingValue="";
    RatingBar rate;




    //*******************PROGRESS******************************
    private ProgressDialog mSpinner;

    private void showSpinner(String title) {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle(title);
        mSpinner.show();
//        mSpinner.setCancelable(false);
//        mSpinner.setCanceledOnTouchOutside(false);
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
        setContentView(R.layout.post_activity_write_review_post);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        client = new OkHttpClient();

        realm = Realm.getDefaultInstance();
        User user = realm.where(User.class).findFirst();
        userProfile=GetUserData(user.getId());

        userLocation=(AutoCompleteTextView) findViewById(R.id.write_reviewer_address_auto);
        status=(EditText)findViewById(R.id.post_status);
        headerName=(TextView)findViewById(R.id.toolbar_name);
        postClick=(TextView)findViewById(R.id.click_post);
        headerName.setText("Write a review");

        write_reviewer_user_name=(TextView)findViewById(R.id.write_reviewer_user_name);
        rattingDialog=(ImageView)findViewById(R.id.restaurant_ratting);
        write_reviewer_user_profile_image=(de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.write_reviewer_user_profile_image);


        if(userProfile==null){
            UserData(user.getId());
        }
        else
        {
            write_reviewer_user_name.setText(userProfile.getName());
            if (!userProfile.getAvatar().equals(""))
            {
                Constants.PicassoImageSrc(userProfile.getAvatar(),write_reviewer_user_profile_image ,WriteReviewPostActivity.this);
            }
        }


        postClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!status.getText().toString().equals("")){
                    statusStr=status.getText().toString();
                }

                if(!statusStr.equals("") && restaurantID!=0) {
                    SendDataOnServer();
                }else {
                    Toast.makeText(WriteReviewPostActivity.this,"Data Missing",Toast.LENGTH_LONG).show();
                }
            }
        });


        RealmResults<RestaurantForStatus> restaurantForStatusRealmResults=GetRestaurants();
        ArrayList<SearchRestaurant> searchRestaurantArrayList=new ArrayList<>();
        for(int i=0;i<restaurantForStatusRealmResults.size();i++){
            SearchRestaurant searchRestaurant=new SearchRestaurant();
            realm.beginTransaction();
            searchRestaurant.setName(restaurantForStatusRealmResults.get(i).getName());
            searchRestaurant.setId(restaurantForStatusRealmResults.get(i).getId());
            searchRestaurant.setRestaurantLat(restaurantForStatusRealmResults.get(i).getRestaurantLat());
            searchRestaurant.setRestaurantLong(restaurantForStatusRealmResults.get(i).getRestaurantLong());
            realm.commitTransaction();
            searchRestaurantArrayList.add(searchRestaurant);
        }

        userLocation.setThreshold(1);
        restaurantNameAdapter = new RestaurantNameAdapter(this,R.layout.post_activity_write_review_post, R.id.lbl_name, searchRestaurantArrayList);
        userLocation.setAdapter(restaurantNameAdapter);
        userLocation.setOnItemClickListener(mAutocompleteClickListenerLocationSelection);

        rattingDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowRattingDialog();
            }
        });


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if(bundle.containsKey("RestaurantName")) {
                RestaurantName = bundle.getString("RestaurantName");
                restaurantID = bundle.getInt("RestaurantID");
                restaurantLatitude = bundle.getDouble("RestaurantLat");
                restaurantLongitude = bundle.getDouble("RestaurantLong");

                userLocation.setText(RestaurantName);
                userLocation.setFocusable(false);
            }
        }
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


    private AdapterView.OnItemClickListener mAutocompleteClickListenerLocationSelection
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){

            SearchRestaurant restaurantForStatus = (SearchRestaurant) parent.getItemAtPosition(position);
            restaurantID=restaurantForStatus.getId();
            restaurantLatitude=restaurantForStatus.getRestaurantLat();
            restaurantLongitude=restaurantForStatus.getRestaurantLong();

        }
    };

    void SendDataOnServer(){
        showSpinner("Please wait...");
        // Get a Realm instance for this thread
        Realm realm = Realm.getDefaultInstance();
        // Persist your data in a transaction

        User user = realm.where(User.class).findFirst();

        RequestBody requestBody;

            requestBody= new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("review[title]","Write Review")
                    .addFormDataPart("review[summary]",statusStr)
                    .addFormDataPart("review[rating]", rattingValue)
                    .addFormDataPart("review[reviewable_id]",""+restaurantID)
                    .build();

        Request request = new Request.Builder()
                .url(URLs.Restaurant_Review)
                .addHeader("Authorization", "Token token="+user.getToken())
                .post(requestBody)
                .build();

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
                        finish();
                    }
                    else  if(jsonObject.has("message")){
                        Log.e("","Not Posted");
                    }
                    DismissSpinner();
                    finish();
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

    private void SelectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(WriteReviewPostActivity.this);
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
                try {
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
                    File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
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
                File f=new File(selectedImagePath);

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


    class CheckPercentage implements TextWatcher {

        public void afterTextChanged(Editable s) {
            Log.e("AutoComplete :: ",""+s.length());
            if(s.length()>=1){
                RestaurantData(s.toString());
            }
            if(s.length()==0){
                //  places=new ArrayList<>();
            }
        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Not used, details on text just before it changed
            // used to track in detail changes made to text, e.g. implement an undo
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Not used, details on text at the point change made
            Log.e("AutoComplete Count :: ",""+count);
        }
    }


    void RestaurantData(String type) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(URLs.Select_Search_restaurants+type)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                final String objStr = response.body().string();
                Log.e("Response :: ",""+objStr);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            JSONObject jsonObj = new JSONObject(objStr);
                            JSONArray jsonDataArray = jsonObj.getJSONArray("restaurant");

                            realm = Realm.getDefaultInstance();

                            if(jsonDataArray.length()>0){
                                places=new ArrayList<String>();
                                resID=new ArrayList<Integer>();
                                placeLong=new ArrayList<Double>();
                                placeLat=new ArrayList<Double>();
                            }
                            for (int i = 0; i < jsonDataArray.length(); i++)
                            {
                                JSONObject restaurantObj = jsonDataArray.getJSONObject(i);

                                realm.beginTransaction();
                                Restaurant realmRestaurant = realm.createObject(Restaurant.class);

                                realmRestaurant.setId(restaurantObj.getInt("id"));
                                realmRestaurant.setName(restaurantObj.getString("name"));
                                realmRestaurant.setType(restaurantObj.getString("typee"));

                                places.add(restaurantObj.getString("name"));
                                resID.add(restaurantObj.getInt("id"));

                                Log.e("RES Name",restaurantObj.getString("name"));

                                realmRestaurant.setLocation(restaurantObj.getString("location"));
                                realmRestaurant.setOpening_time(restaurantObj.getString("opening"));
                                realmRestaurant.setClosing_time(restaurantObj.getString("closing"));
                                realmRestaurant.setRatting(restaurantObj.getDouble("rating"));
                                realmRestaurant.setPricePerHead(restaurantObj.getInt("price_per_head"));

                                if(!restaurantObj.isNull("latitude")) {
                                    realmRestaurant.setRestaurantLat(restaurantObj.getDouble("latitude"));
                                    placeLat.add(restaurantObj.getDouble("latitude"));
                                }else {
                                    realmRestaurant.setRestaurantLat(0.0);
                                    placeLat.add(0.0);
                                }
                                if(!restaurantObj.isNull("longitude")) {
                                    realmRestaurant.setRestaurantLong(restaurantObj.getDouble("longitude"));
                                    placeLong.add(restaurantObj.getDouble("longitude"));
                                }else {
                                    realmRestaurant.setRestaurantLong(0.0);
                                    placeLong.add(0.0);
                                }

                                //Arrays
                                JSONArray jsonDataLikesArray = restaurantObj.getJSONArray("like");
//                                JSONArray jsonDataCheckInsArray = restaurantObj.getJSONArray("checkins");
//                                JSONArray jsonDataReviewsArray = restaurantObj.getJSONArray("reviews");
                                JSONArray jsonDataCallsArray = restaurantObj.getJSONArray("call_nows");

//                                realmRestaurant.setReview_count(jsonDataReviewsArray.length());
                                realmRestaurant.setBookmark_count(jsonDataLikesArray.length());
//                                realmRestaurant.setBeen_here_count(jsonDataCheckInsArray.length());

                                if(!restaurantObj.isNull("cover_image")) {
                                    JSONObject restaurantCoverImage = restaurantObj.getJSONObject("cover_image");
                                    realmRestaurant.setCover_image_id(restaurantCoverImage.getInt("id"));
                                    JSONObject CoverImage = restaurantCoverImage.getJSONObject("image");
                                    JSONObject CoverImageURL = CoverImage.getJSONObject("image");
                                    realmRestaurant.setCover_image_url(CoverImageURL.getString("url"));
                                    JSONObject CoverImageThumbnailURL = CoverImageURL.getJSONObject("thumbnail");
                                    realmRestaurant.setCover_image_thumbnail(CoverImageThumbnailURL.getString("url"));
                                }
//                                    for (int c = 0; c < jsonDataCheckInsArray.length(); c++) {
//                                        JSONObject checkInsObj = jsonDataCheckInsArray.getJSONObject(c);
//                                        realmRestaurant.setCheck_Ins_ID(checkInsObj.getInt("id"));
//                                        realmRestaurant.setCheck_Ins_Address(checkInsObj.getString("address"));
//                                        realmRestaurant.setCheck_In_time(checkInsObj.getString("time"));
//                                        realmRestaurant.setCheck_In_lat(checkInsObj.getDouble("lat"));
//                                        realmRestaurant.setCheck_In_long(checkInsObj.getDouble("long"));
//
//                                        JSONObject checkInsObjUser= checkInsObj.getJSONObject("user");
//                                        JSONObject checkInsObjRestaurant= checkInsObj.getJSONObject("restaurant");
//
//                                        realmRestaurant.setCheck_Ins_user_ID(checkInsObjUser.getInt("id"));
//                                        realmRestaurant.setCheck_Ins_restaurant_ID(checkInsObjRestaurant.getInt("id"));
//                                    }

                                for (int c = 0; c < jsonDataCallsArray.length();c++) {

                                    JSONObject callObj = jsonDataCallsArray.getJSONObject(c);
                                    realmRestaurant.setNumbers(callObj.getString("number"));
                                }

//                                for (int r = 0; r < jsonDataReviewsArray.length();r++) {
//
//                                    JSONObject reviewObj = jsonDataReviewsArray.getJSONObject(r);
//
//                                    ReviewModel reviewModel=new ReviewModel();
//
//                                    reviewModel.setReview_ID(reviewObj.getInt("id"));
//                                    reviewModel.setReviewable_id(reviewObj.getInt("reviewable_id"));
//                                    reviewModel.setReview_title(reviewObj.getString("title"));
//                                    reviewModel.setReview_summary(reviewObj.getString("summary"));
//                                    reviewModel.setReviewable_type(reviewObj.getString("reviewable_type"));
//
//                                    JSONObject reviewObjReviewer= reviewObj.getJSONObject("reviewer");
//
//                                    reviewModel.setReview_reviewer_ID(reviewObjReviewer.getInt("id"));
//                                    reviewModel.setReview_reviewer_Name(reviewObjReviewer.getString("name"));
//                                    reviewModel.setReview_reviewer_Avatar(reviewObjReviewer.getString("avatar"));
//                                    reviewModel.setReview_reviewer_time(reviewObjReviewer.getString("location"));
//
//                                    JSONArray reviewLikesArray = reviewObj.getJSONArray("likes");
//                                    JSONArray reviewCommentsArray = reviewObj.getJSONArray("comments");
//                                    JSONArray reviewShareArray = reviewObj.getJSONArray("reports");
//
//                                    reviewModel.setReview_Likes_count(reviewLikesArray.length());
//                                    reviewModel.setReview_comments_count(reviewCommentsArray.length());
//                                    reviewModel.setReview_shares_count(reviewShareArray.length());
//
//                                    final ReviewModel managedReviewModel= realm.copyToRealm(reviewModel);
//
//                                    realmRestaurant.getReviewModels().add(managedReviewModel);
//
//                                }

                                if(!restaurantObj.isNull("menu")) {
                                    JSONObject restaurantMenu = restaurantObj.getJSONObject("menu");
                                    if(restaurantMenu.has("id")) {
                                        realmRestaurant.setMenuID(restaurantMenu.getInt("id"));
                                        realmRestaurant.setMenuName("name");
                                        realmRestaurant.setMenuSummary("summary");

                                        //Sessions Array
                                        JSONArray jsonDataMenuSessionsArray = restaurantMenu.getJSONArray("sections");

                                        for (int s = 0; s < jsonDataMenuSessionsArray.length(); s++) {

                                            JSONObject sessionObj = jsonDataMenuSessionsArray.getJSONObject(s);

                                            String sessionTitle = sessionObj.getString("title");

                                            //Arrays food_items
                                            JSONArray jsonDataMenuFoodItemsArray = sessionObj.getJSONArray("food_items");

                                            for (int f = 0; f < jsonDataMenuFoodItemsArray.length(); f++) {

                                                JSONObject menuFoodItem = jsonDataMenuFoodItemsArray.getJSONObject(f);


                                                realm.commitTransaction();
                                                realm.beginTransaction();

//                                        FoodItems foodItems = new FoodItems();
                                                FoodItems foodItems = realm.createObject(FoodItems.class);
                                                foodItems.setFoodID(menuFoodItem.getInt("id"));
                                                foodItems.setName(menuFoodItem.getString("name"));
                                                foodItems.setPrice(menuFoodItem.getInt("price"));
                                                foodItems.setSections_Title(sessionTitle);

                                                JSONArray menuFoodItemCategoryArray = menuFoodItem.getJSONArray("category");

                                                for (int fc = 0; fc < menuFoodItemCategoryArray.length(); fc++) {

                                                    JSONObject foodCategory = menuFoodItemCategoryArray.getJSONObject(fc);
                                                    FoodsCategory foodsCategory = new FoodsCategory();

                                                    foodsCategory.setId(foodCategory.getInt("id"));
                                                    foodsCategory.setCategoryName(foodCategory.getString("name"));

                                                    Log.e("ID", "" + foodsCategory.getId());
                                                    Log.e("Name", "" + foodsCategory.getCategoryName());
//                                            // Persist unmanaged objects
                                                    final FoodsCategory managedFoodsCategory = realm.copyToRealm(foodsCategory);
                                                    foodItems.getFoodsCategories().add(managedFoodsCategory);

                                                }

                                                JSONArray menuFoodItemPhotosArray = menuFoodItem.getJSONArray("photos");

                                                for (int p = 0; p < menuFoodItemPhotosArray.length(); p++) {

                                                    JSONObject photo = menuFoodItemPhotosArray.getJSONObject(p);

                                                    PhotoModel photoModel = new PhotoModel();
                                                    photoModel.setId(photo.getInt("id"));
                                                    photoModel.setUrl(photo.getString("image_url"));

                                                    // Persist unmanaged objects
                                                    final PhotoModel managedPhotoModel = realm.copyToRealm(photoModel);
                                                    foodItems.getPhotoModels().add(managedPhotoModel);
                                                }

                                                realm.commitTransaction();
                                                realm.beginTransaction();
//                                        foodItems.setPhotoModels(photoModels);
                                                // Persist unmanaged objects
//                                        final FoodItems managedFoodItems = realm.copyToRealm(foodItems);
                                                realmRestaurant.getFoodItemsArrayList().add(foodItems);
                                            }
                                            //Food Items Array

                                        }// Session Array
                                    }

                                }
                                realm.commitTransaction();

                            }
//                            placeAdapter.notifyDataSetChanged();
                            placeAdapter = new ArrayAdapter<String>(WriteReviewPostActivity.this, android.R.layout.simple_list_item_1, places);
                            userLocation.setAdapter(placeAdapter);
                            realm.close();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }


    UserProfile GetUserData(int uid){
        realm = Realm.getDefaultInstance();
        RealmResults<UserProfile> userProfiles = realm.where(UserProfile.class).equalTo("id", uid).findAll();
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
                                    Constants.PicassoImageSrc(userProfile.getAvatar(),write_reviewer_user_profile_image ,WriteReviewPostActivity.this);
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


    void ShowRattingDialog(){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.ratting_dialogs, null);
        alertDialogBuilder.setView(promptView);

        rate = (RatingBar) promptView.findViewById(R.id.ratingBar);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        rattingValue=String.valueOf(rate.getRating());
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertD = alertDialogBuilder.create();

        alertD.show();
    }


    RealmResults<RestaurantForStatus> GetRestaurants(){
        realm = Realm.getDefaultInstance();
        RealmResults<RestaurantForStatus> restaurants = realm.where(RestaurantForStatus.class).findAll();
        if(restaurants.size()>0){
            return restaurants;
        }
        return null;
    }

}
