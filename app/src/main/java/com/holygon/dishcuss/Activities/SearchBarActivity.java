package com.holygon.dishcuss.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.holygon.dishcuss.R;

/**
 * Created by Naeem Ibrahim on 8/11/2016.
 */
public class SearchBarActivity extends AppCompatActivity {

    EditText search_bar_edit_text;
    ImageView search_bar_image_search;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search_bar_activity);
        search_bar_edit_text=(EditText)findViewById(R.id.search_bar_edit_text);
        search_bar_image_search=(ImageView)findViewById(R.id.search_bar_image_search);


        search_bar_image_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SearchBarActivity.this, SelectRestaurantSearchActivity.class);
                intent.putExtra("CategoryName",search_bar_edit_text.getText().toString());
                startActivity(intent);
            }
        });
    }
}
