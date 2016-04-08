package me.wcy.weather.adapter;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

/**
 * Created by hzwangchenyan on 2016/4/8.
 */
public class LoadMoreListener extends RecyclerView.OnScrollListener {
    private static final String TAG = "LoadMoreListener";
    private StaggeredGridLayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
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
        if (!enableLoadMore) {
            return;
        }

        if (mLayoutManager == null) {
            mLayoutManager = (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
        }
        if (mAdapter == null) {
            mAdapter = recyclerView.getAdapter();
        }

        int[] lastPositions = new int[mLayoutManager.getSpanCount()];
        mLayoutManager.findLastVisibleItemPositions(lastPositions);
        int lastVisibleItem = max(lastPositions);

        if (!isLoading && lastVisibleItem + 1 == mAdapter.getItemCount()) {
            Log.d(TAG, "onLoadMore");
            mListener.onLoadMore();
            isLoading = true;
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
