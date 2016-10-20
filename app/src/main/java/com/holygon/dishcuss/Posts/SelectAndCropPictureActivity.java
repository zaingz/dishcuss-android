package com.holygon.dishcuss.Posts;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.holygon.dishcuss.R;
import com.naver.android.helloyako.imagecrop.view.ImageCropView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Naeem Ibrahim on 9/30/2016.
 */
public class SelectAndCropPictureActivity extends AppCompatActivity {
    public static final String TAG = "CropActivity";

    private ImageCropView imageCropView;
    private float[] positionInfo;
    File files=null;
    FrameLayout images_parent_layout;
    Button select_image_button;
    Button crop_image_button;


    private void SelectImage() {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(SelectAndCropPictureActivity.this);
        builder.setTitle("Add Photo!");
        builder.setCancelable(false);
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
                    finish();
                }
            }
        });
        builder.show();
    }


    //*******************PROGRESS******************************
    private ProgressDialog mSpinner;

    private void showSpinner(String title) {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle(title);
        mSpinner.show();
        mSpinner.setCancelable(false);
        mSpinner.setCanceledOnTouchOutside(false);
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
        setContentView(R.layout.activity_crop);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageView done=(ImageView)findViewById(R.id.crop_done);
        imageCropView = (ImageCropView) findViewById(R.id.image);
        images_parent_layout=(FrameLayout)findViewById(R.id.images_parent_layout);
        select_image_button=(Button)findViewById(R.id.select_image_button);
        crop_image_button=(Button)findViewById(R.id.crop_btn);
        crop_image_button.setVisibility(View.GONE);

        imageCropView.setGridInnerMode(ImageCropView.GRID_ON);
        imageCropView.setGridOuterMode(ImageCropView.GRID_ON);
        images_parent_layout.setVisibility(View.GONE);

        select_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                SelectImage();
            }
        });


        findViewById(R.id.ratio11btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click 1 : 1");
                if(isPossibleCrop(1,1)){
                    imageCropView.setAspectRatio(1, 1);
                } else {
                    Toast.makeText(SelectAndCropPictureActivity.this,R.string.can_not_crop,Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.ratio34btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click 3 : 4");
                if(isPossibleCrop(3,4)){
                    imageCropView.setAspectRatio(3, 4);
                } else {
                    Toast.makeText(SelectAndCropPictureActivity.this,R.string.can_not_crop,Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.ratio43btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click 4 : 3");
                if(isPossibleCrop(4,3)){
                    imageCropView.setAspectRatio(4, 3);
                } else {
                    Toast.makeText(SelectAndCropPictureActivity.this,R.string.can_not_crop,Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.ratio169btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click 16 : 9");
                if(isPossibleCrop(16,9)){
                    imageCropView.setAspectRatio(16, 9);
                } else {
                    Toast.makeText(SelectAndCropPictureActivity.this,R.string.can_not_crop,Toast.LENGTH_SHORT).show();
                }
            }
        });

        findViewById(R.id.ratio916btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "click 9 : 16");
                if(isPossibleCrop(9,16)){
                    imageCropView.setAspectRatio(9, 16);
                } else {
                    Toast.makeText(SelectAndCropPictureActivity.this,R.string.can_not_crop,Toast.LENGTH_SHORT).show();
                }
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!imageCropView.isChangingScale()) {
                    Bitmap b = imageCropView.getCroppedImage();
                    File f=bitmapConvertToFile(b);
                    imageCropView.setImageFilePath(f.toString());

                }
            }
        });

        SelectImage();
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {

                images_parent_layout.setVisibility(View.VISIBLE);
//                crop_image_button.setVisibility(View.VISIBLE);
//                select_image_button.setVisibility(View.GONE);
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
                    imageCropView.setImageBitmap(bitmap);
                    imageCropView.setAspectRatio(4,3);
                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream outFile = null;
                    files = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    try {
                        outFile = new FileOutputStream(files);
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

                if(data!=null) {

                    images_parent_layout.setVisibility(View.VISIBLE);
//                    crop_image_button.setVisibility(View.VISIBLE);
//                    select_image_button.setVisibility(View.GONE);
                    Uri selectedImageUri = data.getData();
                    String[] projection = {MediaStore.MediaColumns.DATA};
                    Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                            null);
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    cursor.moveToFirst();
                    String selectedImagePath = cursor.getString(column_index);
                    files=new File(selectedImagePath);
                    imageCropView.setImageFilePath(files.toString());
                    imageCropView.setAspectRatio(4,3);
                }
            }
        }
    }


    private boolean isPossibleCrop(int widthRatio, int heightRatio){
        int bitmapWidth = imageCropView.getViewBitmap().getWidth();
        int bitmapHeight = imageCropView.getViewBitmap().getHeight();
        return !(bitmapWidth < widthRatio && bitmapHeight < heightRatio);
    }

    public File bitmapConvertToFile(Bitmap bitmap) {
//        showSpinner("Croping...");
        FileOutputStream fileOutputStream = null;
        File bitmapFile = null;
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           // Toast.makeText(SelectAndCropPictureActivity.this,"file saved",Toast.LENGTH_LONG).show();
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("result",files);
                            setResult(Activity.RESULT_OK,returnIntent);
//                            DismissSpinner();
                            finish();
                        }
                    });
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

    public void onClickSaveButton(View v) {
        positionInfo = imageCropView.getPositionInfo();
        View restoreButton = findViewById(R.id.restore_btn);
        if (!restoreButton.isEnabled()) {
            restoreButton.setEnabled(true);
        }
    }

    public void onClickRestoreButton(View v) {
        imageCropView.applyPositionInfo(positionInfo);
    }
}
