package me.wcy.weather.application;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

/**
 * Created by hzwangchenyan on 2016/4/8.
 */
public class LoadMoreListener extends RecyclerView.OnScrollListener {
    private static final String TAG = "LoadMoreListener";
    private Listener mListener;
    private boolean enableLoadMore = true;
    private boolean isLoading = false;

    public LoadMoreListener(Listener listener) {
        mListener = listener;
    }

    public void setEnableLoadMore(boolean enable) {
        enableLoadMore = enable;
    }

    public void onLoadComplete() {
        Log.d(TAG, "onLoadComplete");
        isLoading = false;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (!enableLoadMore || isLoading) {
            return;
        }

        int lastVisibleItem = 0;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        RecyclerView.Adapter adapter = recyclerView.getAdapter();

        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
            lastVisibleItem = linearManager.findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredManager = (StaggeredGridLayoutManager) layoutManager;
            int[] lastPositions = new int[staggeredManager.getSpanCount()];
            staggeredManager.findLastVisibleItemPositions(lastPositions);
            lastVisibleItem = max(lastPositions);
        }

        if (lastVisibleItem + 1 == adapter.getItemCount()) {
            Log.d(TAG, "onLoadMore");
            isLoading = true;
            mListener.onLoadMore();
        }
    }

    private int max(int[] lastPositions) {
        int max = lastPositions[0];
        for (int i : lastPositions) {
            if (i > max) {
                max = i;
            }
        }
        return max;
    }

    public interface Listener {
        void onLoadMore();
    }
}
