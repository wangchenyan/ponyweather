package me.wcy.weather;

import android.app.Activity;
import android.content.Context;

/**
 * Created by hzwangchenyan on 2017/12/28.
 */
public interface BaseView {
    boolean isDestroy();

    Context getContext();

    Activity getActivity();
}
