package me.wcy.weather.model;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by wcy on 2016/5/27.
 */
public class CityInfo implements Serializable {
    public String name;
    public boolean isAutoLocate;

    public CityInfo(String name, boolean isAutoLocate) {
        this.name = name;
        this.isAutoLocate = isAutoLocate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof CityInfo) {
            CityInfo c = (CityInfo) o;
            return TextUtils.equals(c.name, this.name) && c.isAutoLocate == this.isAutoLocate;
        } else {
            return false;
        }
    }
}
