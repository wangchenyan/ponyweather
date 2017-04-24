package me.wcy.weather.model;

import cn.bmob.v3.BmobObject;
import me.wcy.weather.utils.proguard.NoProGuard;

public class ImageWeather extends BmobObject implements NoProGuard {
    /**
     * 用户名
     */
    private String userName;
    /**
     * 图片
     */
    private String imageUrl;
    /**
     * 这一刻的想法
     */
    private String say;
    /**
     * 位置
     */
    private Location location;
    /**
     * 城市
     */
    private String city;
    /**
     * 标签
     */
    private String tag;
    /**
     * 点赞数
     */
    private Long praise;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSay() {
        return say;
    }

    public void setSay(String say) {
        this.say = say;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getPraise() {
        return praise;
    }

    public void setPraise(Long praise) {
        this.praise = praise;
    }
}
