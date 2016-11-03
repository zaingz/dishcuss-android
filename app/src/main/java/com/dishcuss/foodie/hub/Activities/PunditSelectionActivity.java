package com.dishcuss.foodie.hub.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.dishcuss.foodie.hub.Chat.ChatScreenActivity;
import com.dishcuss.foodie.hub.R;

/**
 * Created by Naeem Ibrahim on 8/18/2016.
 */
public class PunditSelectionActivity extends Activity {


    ImageView crossButton;
    LinearLayout Desi_pundit;
    LinearLayout Sasta_pundit;
    LinearLayout FastFood_pundit;
    LinearLayout Continental_pundit;
    LinearLayout Foreign_pundit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pundits_list);
        crossButton=(ImageView)findViewById(R.id.pundits_screens_cross_button);

        Desi_pundit=(LinearLayout) findViewById(R.id.Desi_pundit);
        Sasta_pundit=(LinearLayout) findViewById(R.id.Sasta_pundit);
        FastFood_pundit=(LinearLayout) findViewById(R.id.fast_food_pundit);
        Continental_pundit=(LinearLayout) findViewById(R.id.Continental_pundit);
        Foreign_pundit=(LinearLayout) findViewById(R.id.Foregin_pundit);


        crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Desi_pundit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(PunditSelectionActivity.this, ChatScreenActivity.class);
                    intent.putExtra("PunditName", "Mr. Sheeda Pehlwan");
                    intent.putExtra("PunditType", "desi");
                    intent.putExtra("PunditNumber", 1);
                    startActivity(intent);

            }
        });

        Sasta_pundit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    Intent intent = new Intent(PunditSelectionActivity.this, ChatScreenActivity.class);
                    intent.putExtra("PunditName", "Mr.Miskeen");
                    intent.putExtra("PunditType", "sasta");
                    intent.putExtra("PunditNumber", 2);
                    startActivity(intent);
            }
        });

        FastFood_pundit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(PunditSelectionActivity.this, ChatScreenActivity.class);
                    intent.putExtra("PunditName", "Ms. Hawahawai");
                    intent.putExtra("PunditType", "fast_food");
                    intent.putExtra("PunditNumber", 3);
                    startActivity(intent);
            }
        });
        Continental_pundit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(PunditSelectionActivity.this, ChatScreenActivity.class);
                    intent.putExtra("PunditName", "Mr.Breganzza");
                    intent.putExtra("PunditType", "continental");
                    intent.putExtra("PunditNumber", 4);
                    startActivity(intent);
            }
        });
        Foreign_pundit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(PunditSelectionActivity.this, ChatScreenActivity.class);
                    intent.putExtra("PunditName", "Mr.John Wick");
                    intent.putExtra("PunditType", "foreign");
                    intent.putExtra("PunditNumber", 5);
                    startActivity(intent);
                }
        });
    }
}
