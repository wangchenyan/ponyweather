package me.wcy.weather.model;

import cn.bmob.v3.BmobObject;

/**
 * Created by hzwangchenyan on 2016/4/6.
 */
public class ImageWeather extends BmobObject {
    /**
     * 用户名
     */
    private String userName;
    /**
     * 图片
     */
    private String imageUrl;
    /**
     * 位置
     */
    private String location;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
