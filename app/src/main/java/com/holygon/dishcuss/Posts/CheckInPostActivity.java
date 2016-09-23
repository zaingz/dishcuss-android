package com.holygon.dishcuss.Posts;

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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.holygon.dishcuss.Model.FoodItems;
import com.holygon.dishcuss.Model.FoodsCategory;
import com.holygon.dishcuss.Model.PhotoModel;
import com.holygon.dishcuss.Model.Restaurant;
import com.holygon.dishcuss.Model.ReviewModel;
import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.Model.UserProfile;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;
import com.holygon.dishcuss.Utils.URLs;

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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 8/15/2016.
 */
public class CheckInPostActivity extends AppCompatActivity {


    OkHttpClient client;
    AutoCompleteTextView userLocation;
    EditText status;
    String statusStr="";
    String imagePath="";
    ImageView imageView;
    ImageView select_photo_layout;
    String loc="";
    double restaurantLongitude;
    double restaurantLatitude;
    int restaurantID;
    File file=null;
    UserProfile userProfile=new UserProfile();
    Realm realm;

    TextView headerName,postClick;

    ArrayList<String> places=new ArrayList<>();
    ArrayList<Integer> resID;
    ArrayList<Double> placeLat;
    ArrayList<Double> placeLong;

    ArrayAdapter<String> placeAdapter;
    String postCategory,setPostURL;

    public static String Review="Review";
    public static String UploadPic="UploadPic";
    public static String CheckIn="CheckIn";


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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity_check_in_post);

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


        TextView write_reviewer_user_name=(TextView)findViewById(R.id.write_reviewer_user_name);
        de.hdodenhof.circleimageview.CircleImageView
                write_reviewer_user_profile_image=(de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.write_reviewer_user_profile_image);

        write_reviewer_user_name.setText(userProfile.getUsername());
        if (!userProfile.getAvatar().equals(""))
        {
            Constants.PicassoImageSrc(userProfile.getAvatar(),write_reviewer_user_profile_image ,CheckInPostActivity.this);
        }


        postClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!status.getText().toString().equals("")){
                    statusStr=status.getText().toString();
                }

                if(restaurantID!=0) {
                    SendDataOnServer();
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

        userLocation.addTextChangedListener(new CheckPercentage());
        userLocation.setOnItemClickListener(mAutocompleteClickListenerLocationSelection);

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

            restaurantID=resID.get(position);
            restaurantLatitude=placeLat.get(position);
            restaurantLongitude=placeLong.get(position);
        }
    };
    void SendDataOnServer(){
        showSpinner("Please wait...");
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
                    .addFormDataPart("post[title]","Upload a picture")
                    .addFormDataPart("post[status]",statusStr)
                    .addFormDataPart("post[checkin_attributes][address]", ""+userLocation.getText().toString())
                    .addFormDataPart("post[checkin_attributes][lat]",""+restaurantLatitude)
                    .addFormDataPart("post[checkin_attributes][long]",""+restaurantLongitude)
                    .addFormDataPart("post[checkin_attributes][restaurant_id]",""+restaurantID)
                    .build();

        }else {
            requestBody= new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("post[image][]", "")
                    .addFormDataPart("post[title]","Upload a picture")
                    .addFormDataPart("post[status]",statusStr)
                    .addFormDataPart("post[checkin_attributes][address]", ""+userLocation.getText().toString())
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
                    finish();
                }catch (Exception e){
                    Log.i("Exception ::",""+ e.getMessage());
                }
                finally
                {
                }

            }
        });
    }

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
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                imagePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(imagePath));
                Log.w("path of image", imagePath+"");
                file=new File(imagePath);
                imageView.setImageBitmap(thumbnail);
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

        Log.e("RES DATA","Called");

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

                                places.add(restaurantObj.getString("name"));
                                resID.add(restaurantObj.getInt("id"));

                                Log.e("RES Name",restaurantObj.getString("name"));

                                realmRestaurant.setLocation(restaurantObj.getString("location"));
                                realmRestaurant.setOpening_time(restaurantObj.getString("opening"));
                                realmRestaurant.setClosing_time(restaurantObj.getString("closing"));
                                realmRestaurant.setRatting(restaurantObj.getDouble("rating"));

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
                                JSONArray jsonDataCheckInsArray = restaurantObj.getJSONArray("checkins");
                                JSONArray jsonDataReviewsArray = restaurantObj.getJSONArray("reviews");
                                JSONArray jsonDataCallsArray = restaurantObj.getJSONArray("call_nows");

                                realmRestaurant.setReview_count(jsonDataReviewsArray.length());
                                realmRestaurant.setBookmark_count(jsonDataLikesArray.length());
                                realmRestaurant.setBeen_here_count(jsonDataCheckInsArray.length());

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

                                for (int r = 0; r < jsonDataReviewsArray.length();r++) {

                                    JSONObject reviewObj = jsonDataReviewsArray.getJSONObject(r);

                                    ReviewModel reviewModel=new ReviewModel();

                                    reviewModel.setReview_ID(reviewObj.getInt("id"));
                                    reviewModel.setReviewable_id(reviewObj.getInt("reviewable_id"));
                                    reviewModel.setReview_title(reviewObj.getString("title"));
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

                                    final ReviewModel managedReviewModel= realm.copyToRealm(reviewModel);

                                    realmRestaurant.getReviewModels().add(managedReviewModel);

                                }

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
                            placeAdapter = new ArrayAdapter<String>(CheckInPostActivity.this, android.R.layout.simple_list_item_1, places);
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
        Log.e("Count",""+userProfiles.size());
        if(userProfiles.size()>0){
            realm.beginTransaction();
            realm.commitTransaction();
            return userProfiles.get(userProfiles.size()-1);
        }
        return null;
    }
}
