package com.dishcuss.foodie.hub.Activities;

import android.Manifest;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.zxing.Result;
import com.dishcuss.foodie.hub.Models.User;
import com.dishcuss.foodie.hub.R;
import com.dishcuss.foodie.hub.Utils.RuntimePermissionsActivity;
import com.dishcuss.foodie.hub.Utils.URLs;

import org.json.JSONObject;

import java.io.IOException;

import io.realm.Realm;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Naeem Ibrahim on 8/19/2016.
 */
public class ScanQRCodeActivity  extends RuntimePermissionsActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView mScannerView;
    final private int REQUEST_PERMISSIONS = 123;

    Camera camera;
    OkHttpClient client;
    String message=null;
    int OfferID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_qr_code_activity);
        client = new OkHttpClient();
        ScanQRCodeActivity.super.requestAppPermissions(new
                        String[]{Manifest.permission.CAMERA,}, R.string.runtime_permissions_txt,REQUEST_PERMISSIONS);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            OfferID = bundle.getInt("OfferID");
        }

        if(camera!=null){
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }

        QrScanners();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {

    }

    public void QrScanner(View view){


        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera
    }

    void QrScanners(){


        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera
    }

    @Override
    public void onPause() {
        super.onPause();
        if(camera!=null){
            mScannerView.stopCamera();
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.release();
            camera = null;
        }
        mScannerView.stopCamera(); // Stop camera on pause
    }

    @Override
    protected void onStop() {
        super.onStop();
        mScannerView.stopCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here

        Log.e("handler", rawResult.getText()); // Prints scan results
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)

        SendDataOnServer(rawResult.getText().toString());
        // show the scanner result into dialog box.
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Scan Result");
//        builder.setMessage(rawResult.getText());
//        AlertDialog alert1 = builder.create();
//        alert1.show();
        // If you would like to resume scanning, call this method below:
        // mScannerView.resumeCameraPreview(this);
    }


    void SendDataOnServer(String QRCode){

        // Get a Realm instance for this thread
        Realm realm = Realm.getDefaultInstance();
        // Persist your data in a transaction

        User users = realm.where(User.class).findFirst();

        FormBody body = new FormBody.Builder()
                .add("code",QRCode)
                .add("offer_id",""+OfferID)
                .build();


        Request request = new Request.Builder()
                .url(URLs.QR)
                .addHeader("Authorization", "Token token="+users.getToken())
                .post(body)
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
                    if(jsonObject.has("message")){
                        message=jsonObject.getString("message");
                    }
                    finish();
                }catch (Exception e){
                    Log.i("Exception ::",""+ e.getMessage());
                }

            }
        });

        while (message==null){}

        if(message.equals("Successfully claimed!")){
            Toast.makeText(ScanQRCodeActivity.this,message,Toast.LENGTH_LONG).show();
            finish();
        }else {
            Toast.makeText(ScanQRCodeActivity.this,message,Toast.LENGTH_LONG).show();
            finish();
        }
    }
}
