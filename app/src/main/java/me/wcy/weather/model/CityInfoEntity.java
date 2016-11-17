package me.wcy.weather.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wcy on 2016/6/29.
 */
public class CityInfoEntity implements Comparable {
    @SerializedName("id")
    public String id;
    @SerializedName("area_en")
    public String area_en;
    @SerializedName("area")
    public String area;
    @SerializedName("city")
    public String city;
    @SerializedName("province")
    public String province;

    @Override
    public int compareTo(@NonNull Object another) {
        return this.id.compareTo(((CityInfoEntity) another).id);
    }
}
