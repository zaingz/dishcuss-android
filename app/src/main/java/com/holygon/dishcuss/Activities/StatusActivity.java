package com.holygon.dishcuss.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.holygon.dishcuss.Model.FoodItems;
import com.holygon.dishcuss.Model.FoodsCategory;
import com.holygon.dishcuss.Model.Restaurant;
import com.holygon.dishcuss.R;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

/**
 * Created by Naeem Ibrahim on 7/25/2016.
 */
public class StatusActivity extends Activity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_activity_check_in_google_api);

        //imageView=(ImageView) findViewById(R.id.cross_button);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        // Get a Realm instance for this thread
        Realm realm = Realm.getDefaultInstance();

        RealmResults<Restaurant> restaurantRealmResults=realm.where(Restaurant.class).findAll();
        realm.beginTransaction();
//        User user = realm.createObject(User.class); // Create managed objects directly
//        user.setEmail("nanan");
//        user.setName("nanan");

        RealmList<FoodItems> foodItems =restaurantRealmResults.get(0).getFoodItemsArrayList();

        for(int i=0;i<foodItems.size();i++){
//            Log.e("Name : ",""+foodItems.get(i).getName());
//            Log.e("ID : ",""+foodItems.get(i).getFoodID());
            RealmList<FoodsCategory> foodsCategories=foodItems.get(i).getFoodsCategories();

            Log.e("FoodsCategories ",""+foodsCategories.size());
//            for (FoodsCategory f :foodsCategories) {
//
//            }
            for (int j=0;j<foodsCategories.size();j++){
//                Log.e("CAT ID : ",""+foodsCategories.get(j).getId());
//                Log.e("CAT NAME : ",""+foodsCategories.get(j).getCategoryName());
            }
        }



        realm.commitTransaction();
        realm.close();

//
//        User user3=new User();
//        user3.setEmail("");



//        final RealmResults<User> users = realm.where(User.class).findAll();
//        users.size(); // => 0 because no dogs have been added to the Realm yet
//
//        User user1 = realm.where(User.class).findFirst();

    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }
}
