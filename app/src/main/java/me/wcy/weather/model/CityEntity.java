package me.wcy.weather.model;

import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wcy on 2017/3/26.
 */
public class CityEntity implements Comparable {
    @SerializedName("id")
    private String id;
    @SerializedName("cityEn")
    private String areaEn;
    @SerializedName("cityZh")
    private String areaZh;
    @SerializedName("countryCode")
    private String countryCode;
    @SerializedName("countryEn")
    private String countryEn;
    @SerializedName("countryZh")
    private String countryZh;
    @SerializedName("provinceEn")
    private String provinceEn;
    @SerializedName("provinceZh")
    private String provinceZh;
    @SerializedName("leaderEn")
    private String cityEn;
    @SerializedName("leaderZh")
    private String cityZh;
    @SerializedName("lat")
    private String lat;
    @SerializedName("lon")
    private String lon;

    public String getAreaZh() {
        return areaZh;
    }

    public String getProvinceZh() {
        return provinceZh;
    }

    public String getCityZh() {
        return cityZh;
    }

    @Override
    public int compareTo(@NonNull Object o) {
        return this.id.compareTo(((CityEntity) o).id);
    }
}
