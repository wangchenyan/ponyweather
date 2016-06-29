package me.wcy.weather.model;

import android.support.annotation.NonNull;

/**
 * Created by wcy on 2016/6/29.
 */
public class CityInfoEntity implements Comparable {
    public String id;
    public String area_en;
    public String area;
    public String city;
    public String province;

    @Override
    public int compareTo(@NonNull Object another) {
        return this.id.compareTo(((CityInfoEntity) another).id);
    }
}
