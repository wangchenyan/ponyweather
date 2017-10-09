package me.wcy.weather.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 设置瀑布流均等间距<br>
 * 由于瀑布流无法确定item位置，因此只能通过给RecyclerView设置padding解决<br>
 * 使用该方法的瀑布流不适合使用滚动条<br>
 * Created by hzwangchenyan on 2017/10/9.
 */
public class StaggeredGridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private int spacing;

    public StaggeredGridSpacingItemDecoration(int spacing) {
        this.spacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int halfSpacing = spacing / 2;
        parent.setPadding(halfSpacing, halfSpacing, halfSpacing, halfSpacing);

        outRect.left = halfSpacing;
        outRect.top = halfSpacing;
        outRect.right = halfSpacing;
        outRect.bottom = halfSpacing;
    }
}
