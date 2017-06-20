package me.wcy.weather.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wcy on 2017/3/26.
 */
public class CityEntity implements Comparable {
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
        return this.id.compareTo(((CityEntity) another).id);
    }

    public String getArea() {
        return area;
    }

    public String getCity() {
        return city;
    }

    public String getProvince() {
        return province;
    }
}
