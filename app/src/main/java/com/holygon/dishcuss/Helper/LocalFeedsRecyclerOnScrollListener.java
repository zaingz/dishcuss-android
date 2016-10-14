package com.holygon.dishcuss.Helper;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.holygon.dishcuss.Fragments.HomeFragment2;

/**
 * Created by Naeem Ibrahim on 10/4/2016.
 */
public abstract class LocalFeedsRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = LocalFeedsRecyclerOnScrollListener.class.getSimpleName();

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 0; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    int prevOffset=0;
    int currentOffset=0;
    int offset=0;

    private int current_page = 1;

    private LinearLayoutManager mLinearLayoutManager;

    public LocalFeedsRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached

            // Do something
            current_page++;

            if(HomeFragment2.localFeedsReviews==HomeFragment2.localFeedsCheckIns){
                offset=HomeFragment2.localFeedsReviews;
            }
            else if(HomeFragment2.localFeedsReviews<HomeFragment2.localFeedsCheckIns){
                offset=HomeFragment2.localFeedsCheckIns;
            }
            else if(HomeFragment2.localFeedsReviews>HomeFragment2.localFeedsCheckIns){
                offset=HomeFragment2.localFeedsReviews;
            }

            currentOffset=offset+prevOffset;
            prevOffset=currentOffset;

            onLoadMore(current_page,currentOffset);

            loading = true;
        }
    }

    public abstract void onLoadMore(int current_page, int visibleItemCount);
}
