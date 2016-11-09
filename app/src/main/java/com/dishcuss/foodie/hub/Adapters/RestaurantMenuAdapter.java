package com.dishcuss.foodie.hub.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dishcuss.foodie.hub.Models.FoodItems;
import com.dishcuss.foodie.hub.Models.FoodsCategory;
import com.dishcuss.foodie.hub.Models.PhotoModel;
import com.dishcuss.foodie.hub.R;
import com.dishcuss.foodie.hub.Utils.Constants;

import io.realm.RealmList;

/**
 * Created by Naeem Ibrahim on 9/16/2016.
 */
public class RestaurantMenuAdapter extends RecyclerView.Adapter<RestaurantMenuAdapter.ViewHolder> {

    private RealmList<FoodItems> foodItemsRealmList;
    Context context;



    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView menuItemName,menuItemType,menuItemPrice;
        public ImageView menuItemImage;
        public RelativeLayout select_a_menu_parent;
        public ViewHolder(View v) {
            super(v);

            menuItemName = (TextView) v.findViewById(R.id.menu_item_name);
            menuItemType = (TextView) v.findViewById(R.id.menu_item_type);
            menuItemPrice = (TextView) v.findViewById(R.id.menu_item_price);
            menuItemImage=(ImageView) v.findViewById(R.id.menu_item_image);
            select_a_menu_parent=(RelativeLayout) v.findViewById(R.id.select_a_menu_parent);

        }
    }
    public RestaurantMenuAdapter(RealmList<FoodItems> foodItemsRealmList,Context context) {
        this.foodItemsRealmList=foodItemsRealmList;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_menu_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        holder.menuItemName.setText(foodItemsRealmList.get(position).getName());
        holder.menuItemPrice.setText(foodItemsRealmList.get(position).getPrice()+" PKR");

        RealmList<FoodsCategory> foodsCategoryRealmList=foodItemsRealmList.get(position).getFoodsCategories();
        if(foodsCategoryRealmList.size()>0){
            for (int j=0;j<foodsCategoryRealmList.size();j++){
                holder.menuItemType.setText(foodsCategoryRealmList.get(0).getCategoryName());
            }
        }else {
            holder.menuItemType.setVisibility(View.INVISIBLE);
        }



        RealmList<PhotoModel> photoModelRealmList=foodItemsRealmList.get(position).getPhotoModels();
        if(photoModelRealmList.size()>0) {
            for (int j = 0; j < photoModelRealmList.size(); j++) {
                Constants.PicassoImageSrc(photoModelRealmList.get(0).getUrl(),holder.menuItemImage,context);
            }
        }

        holder.select_a_menu_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent=new Intent(context, RestaurantDetailActivity.class);
//                intent.putExtra("RestaurantID", restaurantRealmList.get(position).getId());
//                context.startActivity(intent);
            }
        });
        holder.setIsRecyclable(false);
    }

    @Override
    public int getItemCount() {
        return foodItemsRealmList.size();
    }
}
