package com.holygon.dishcuss.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.holygon.dishcuss.Chat.ChatScreenActivity;
import com.holygon.dishcuss.R;
import com.holygon.dishcuss.Utils.Constants;

/**
 * Created by Naeem Ibrahim on 8/18/2016.
 */
public class PunditSelectionActivity extends Activity {


    ImageView crossButton;
    LinearLayout Desi_pundit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pundits_list);
        crossButton=(ImageView)findViewById(R.id.pundits_screens_cross_button);
        Desi_pundit=(LinearLayout) findViewById(R.id.Desi_pundit);
        crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Desi_pundit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Constants.skipLogin) {
                    Intent intent = new Intent(PunditSelectionActivity.this, ChatScreenActivity.class);
                    intent.putExtra("PunditName", "Mr.John Wick");
                    intent.putExtra("PunditType", "Desi Pundit");
                    startActivity(intent);
                }
            }
        });

    }
}
