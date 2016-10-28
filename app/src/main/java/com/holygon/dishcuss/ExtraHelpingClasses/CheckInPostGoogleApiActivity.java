package com.holygon.dishcuss.ExtraHelpingClasses;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.holygon.dishcuss.Adapters.PlaceArrayAdapter;
import com.holygon.dishcuss.Model.User;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;
import com.holygon.dishcuss.Utils.URLs;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 8/11/2016.
 */
public class CheckInPostGoogleApiActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    OkHttpClient client;
    AutoCompleteTextView userLocation;
    EditText status;
    String statusStr="";
    String imagePath="";
    ImageView imageView;
    LinearLayout select_photo_layout;

    File file=null;


    //Location Auto Selection
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private GoogleApiClient mGoogleApiClientLoction;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(31.5497, 74.3436), new LatLng(31.5497, 74.3436));
    String loc="";
    double longitud;
    double latitud;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    //Location Auto Selection


    TextView headerName,postClick;

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
        setContentView(R.layout.post_activity_check_in_google_api);
        final Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        client=new OkHttpClient();

        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        userLocation=(AutoCompleteTextView) findViewById(R.id.checkIn_address);
        imageView= (ImageView) findViewById(R.id.imageView_pic_upload_check_in);
        select_photo_layout= (LinearLayout) findViewById(R.id.select_photo_layout_checkIn);
        status=(EditText)findViewById(R.id.post_status);
        headerName=(TextView)findViewById(R.id.toolbar_name);
        postClick=(TextView)findViewById(R.id.click_post);
        headerName.setText("Check In");
        userLocation.setOnItemClickListener(mAutocompleteClickListenerLocationSelection);
        userLocation.setTextSize(20);
        userLocation.setThreshold(3);
        userLocation.setTextColor(Color.parseColor("#FFE4770A"));
        userLocation.setAdapter(mPlaceArrayAdapter);

        if(!loc.equals("")){
            userLocation.setText(loc);
        }

        if(Constants.checkPlayServices(this)) {
            buildGoogleApiClient();
        }
        postClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!status.getText().toString().equals("")){
                    statusStr=status.getText().toString();
                }
                SendDataOnServer();
            }
        });

        select_photo_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setVisibility(View.VISIBLE);
                SelectImage();
            }
        });
    }


    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClientLoction = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        mGoogleApiClient = new GoogleApiClient.Builder(CheckInPostGoogleApiActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
    }

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
                    .addFormDataPart("post[title]","New Check In")
                    .addFormDataPart("post[status]",statusStr)
                    .addFormDataPart("post[checkin_attributes][address]", ""+userLocation.getText().toString())
                    .addFormDataPart("post[checkin_attributes][lat]",""+latitud)
                    .addFormDataPart("post[checkin_attributes][long]",""+longitud)
                    .addFormDataPart("post[checkin_attributes][restaurant_id]",""+10)
                    .build();

        }else {
            requestBody= new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("post[image][]", "")
                    .addFormDataPart("post[title]","New Check In")
                    .addFormDataPart("post[status]",statusStr)
                    .addFormDataPart("post[checkin_attributes][address]", ""+userLocation.getText().toString())
                    .addFormDataPart("post[checkin_attributes][lat]",""+latitud)
                    .addFormDataPart("post[checkin_attributes][long]",""+longitud)
                    .addFormDataPart("post[checkin_attributes][restaurant_id]",""+10)
                    .build();

        }


//        FormBody body = new FormBody.Builder()
//                .add("post[title]","New Check In")
//                .add("post[status]",statusStr)
//                .add("post[checkin_attributes][address]", ""+userLocation.getText().toString())
//                .add("post[image][]", "")
//                .add("post[checkin_attributes][lat]",""+latitud)
//                .add("post[checkin_attributes][long]",""+longitud)
//                .add("post[checkin_attributes][restaurant_id]",""+10)
//                .build();

//        File file = new File(""); //provide a valid file
//        ApiCall.POST(client, url, RequestBuilder.uploadRequestBody("title", "png", "someUploadToken", file));

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
                        Toast.makeText(CheckInPostGoogleApiActivity.this,"Checked In Successfully",Toast.LENGTH_LONG).show();
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

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);



    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        mGoogleApiClientLoction.connect();
    }




    private AdapterView.OnItemClickListener mAutocompleteClickListenerLocationSelection
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);


            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallbackUserLocation);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallbackUserLocation
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();
            userLocation.setText(place.getAddress());
            userLocation.dismissDropDown();
            userLocation.setSelection(place.getAddress().length());
            loc=place.getName().toString();
            LatLng latLng=place.getLatLng();
            longitud=latLng.longitude;
            latitud=latLng.latitude;
        }
    };




    private void SelectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(CheckInPostGoogleApiActivity.this);
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
}
