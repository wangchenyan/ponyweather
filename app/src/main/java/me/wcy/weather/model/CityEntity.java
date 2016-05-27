package me.wcy.weather.model;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by wcy on 2016/5/27.
 */
public class CityEntity implements Serializable {
    public String name;
    public boolean isAutoLocate;

    public CityEntity(String name, boolean isAutoLocate) {
        this.name = name;
        this.isAutoLocate = isAutoLocate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof CityEntity) {
            CityEntity c = (CityEntity) o;
            return TextUtils.equals(c.name, this.name) && c.isAutoLocate == this.isAutoLocate;
        } else {
            return false;
        }
    }
}
