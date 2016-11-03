package com.dishcuss.foodie.hub.Helper;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.dishcuss.foodie.hub.Adapters.NotificationAdapter;

/**
 * Created by Naeem Ibrahim on 8/18/2016.
 */
public class NotificationTouchHelper extends ItemTouchHelper.SimpleCallback {

    private NotificationAdapter notificationAdapter;

    public NotificationTouchHelper(NotificationAdapter notificationAdapter){
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.notificationAdapter = notificationAdapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        //TODO: Not implemented here
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //Remove item
        notificationAdapter.remove(viewHolder.getAdapterPosition());
    }
}
