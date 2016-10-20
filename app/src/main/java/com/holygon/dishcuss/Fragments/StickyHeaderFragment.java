package com.holygon.dishcuss.Fragments;

import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.holygon.dishcuss.Adapters.StickyTestAdapter;

import ca.barrenechea.widget.recyclerview.decoration.StickyHeaderDecoration;

public class StickyHeaderFragment extends BaseDecorationFragment implements RecyclerView.OnItemTouchListener {

    private StickyHeaderDecoration decor;

    @Override
    protected void setAdapterAndDecor(RecyclerView list) {

        final StickyTestAdapter adapter = new StickyTestAdapter(this.getActivity());
        decor = new StickyHeaderDecoration(adapter);
        setHasOptionsMenu(true);
        list.setAdapter(adapter);
        list.addItemDecoration(decor, 1);
        list.addOnItemTouchListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        // really bad click detection just for demonstration purposes
        // it will not allow the list to scroll if the swipe motion starts
        // on top of a header
        View v = rv.findChildViewUnder(e.getX(), e.getY());
        return v == null;
//        return rv.findChildViewUnder(e.getX(), e.getY()) != null;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        // only use the "UP" motion event, discard all others
        if (e.getAction() != MotionEvent.ACTION_UP) {
            return;
        }

        // find the header that was clicked
        View view = decor.findHeaderViewUnder(e.getX(), e.getY());

        if (view instanceof TextView) {
            Toast.makeText(this.getActivity(), ((TextView) view).getText() + " clicked", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        // do nothing
    }
}
