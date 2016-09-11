package com.holygon.dishcuss.Activities;

import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;

/**
 * Created by Naeem Ibrahim on 9/1/2016.
 */
public class SplashActivity extends AppCompatActivity {


    ImageView sp_2,sp_3,sp_4,sp_5,sp_6;

    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        try{
            Thread.sleep(500);
        }catch(InterruptedException e){

        }

        sp_2 = (ImageView) findViewById(R.id.sp_2);
        sp_3 = (ImageView) findViewById(R.id.sp_3);
        sp_4 = (ImageView) findViewById(R.id.sp_4);
        sp_5 = (ImageView) findViewById(R.id.sp_5);
        sp_6 = (ImageView) findViewById(R.id.sp_6);


        Animation animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_right_to_left);
        Animation animation2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_left_to_right);
        Animation animation3 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_bottom_to_up);

        Animation animation4 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_up_anim);
        Animation animation5 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.logo_down_anim);



        sp_2.startAnimation(animation5);
        sp_3.startAnimation(animation4);
        sp_4.startAnimation(animation2);
        sp_5.startAnimation(animation1);
        sp_6.startAnimation(animation3);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(Constants.GetUserLoginStatus(SplashActivity.this)){
                    Constants.skipLogin=false;
                    Intent intent=new Intent(SplashActivity.this,HomeActivity.class);
                    startActivity(intent);

                    finish();
                }
                else
                {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void animateDiagonalPan(View v) {
        AnimatorSet animSetXY = new AnimatorSet();

//        ObjectAnimator y = ObjectAnimator.ofFloat(v,
//                "translationY",v.getY(), targetY);
//
//        ObjectAnimator x = ObjectAnimator.ofFloat(v,
//                "translationX", v.getX(), targetX);
//
//        animSetXY.playTogether(x, y);
//        animSetXY.setInterpolator(new LinearInterpolator(1f));
//        animSetXY.setDuration(300);
//        animSetXY.start();
    }
}
