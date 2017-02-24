package me.wcy.weather.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Weather implements Serializable {
    @SerializedName("status")
    public String status;
    /**
     * 城市基本信息
     */
    @SerializedName("basic")
    public BasicEntity basic;
    /**
     * 空气质量指数
     */
    @SerializedName("aqi")
    public AqiEntity aqi;
    /**
     * 实况天气
     */
    @SerializedName("now")
    public NowEntity now;
    /**
     * 生活指数
     */
    @SerializedName("suggestion")
    public SuggestionEntity suggestion;
    /**
     * 天气预报
     */
    @SerializedName("daily_forecast")
    public List<DailyForecastEntity> daily_forecast;
    /**
     * 每小时天气预报
     */
    @SerializedName("hourly_forecast")
    public List<HourlyForecastEntity> hourly_forecast;

    public static class BasicEntity implements Serializable {
        /**
         * 城市名称
         */
        @SerializedName("city")
        public String city;
        /**
         * 城市ID
         */
        @SerializedName("cnty")
        public String cnty;
        /**
         * 国家名称
         */
        @SerializedName("id")
        public String id;
        /**
         * 纬度
         */
        @SerializedName("lat")
        public String lat;
        /**
         * 经度
         */
        @SerializedName("lon")
        public String lon;
        /**
         * 数据更新时间,24小时制
         */
        @SerializedName("update")
        public UpdateEntity update;

        public static class UpdateEntity implements Serializable {
            /**
             * 数据更新的当地时间
             */
            @SerializedName("loc")
            public String loc;
            /**
             * 数据更新的UTC时间
             */
            @SerializedName("utc")
            public String utc;
        }
    }

    public static class AqiEntity implements Serializable {
        /**
         * 城市数据
         */
        @SerializedName("city")
        public CityEntity city;

        public static class CityEntity implements Serializable {
            /**
             * 空气质量指数
             */
            @SerializedName("aqi")
            public String aqi;
            /**
             * 一氧化碳1小时平均值(ug/m³)
             */
            @SerializedName("co")
            public String co;
            /**
             * 二氧化氮1小时平均值(ug/m³)
             */
            @SerializedName("no2")
            public String no2;
            /**
             * 臭氧1小时平均值(ug/m³)
             */
            @SerializedName("o3")
            public String o3;
            /**
             * PM10 1小时平均值(ug/m³)
             */
            @SerializedName("pm10")
            public String pm10;
            /**
             * PM2.5 1小时平均值(ug/m³)
             */
            @SerializedName("pm25")
            public String pm25;
            /**
             * 空气质量类别
             */
            @SerializedName("qlty")
            public String qlty;
            /**
             * 二氧化硫1小时平均值(ug/m³)
             */
            @SerializedName("so2")
            public String so2;
        }
    }

    public static class NowEntity implements Serializable {
        /**
         * 天气状况
         */
        @SerializedName("cond")
        public NowCondEntity cond;
        /**
         * 体感温度
         */
        @SerializedName("fl")
        public String fl;
        /**
         * 湿度(%)
         */
        @SerializedName("hum")
        public String hum;
        /**
         * 降雨量(mm)
         */
        @SerializedName("pcpn")
        public String pcpn;
        /**
         * 气压
         */
        @SerializedName("pres")
        public String pres;
        /**
         * 当前温度(摄氏度)
         */
        @SerializedName("tmp")
        public String tmp;
        /**
         * 能见度(km)
         */
        @SerializedName("vis")
        public String vis;
        /**
         * 风力状况
         */
        @SerializedName("wind")
        public WindEntity wind;
    }

    public static class SuggestionEntity implements Serializable {
        /**
         * 舒适指数
         */
        @SerializedName("comf")
        public Entity comf;
        /**
         * 洗车指数
         */
        @SerializedName("cw")
        public Entity cw;
        /**
         * 穿衣指数
         */
        @SerializedName("drsg")
        public Entity drsg;
        /**
         * 感冒指数
         */
        @SerializedName("flu")
        public Entity flu;
        /**
         * 运动指数
         */
        @SerializedName("sport")
        public Entity sport;
        /**
         * 旅游指数
         */
        @SerializedName("trav")
        public Entity trav;
        /**
         * 紫外线指数
         */
        @SerializedName("uv")
        public Entity uv;

        public static class Entity implements Serializable {
            /**
             * 简介
             */
            @SerializedName("brf")
            public String brf;
            /**
             * 详情
             */
            @SerializedName("txt")
            public String txt;
        }
    }

    public static class DailyForecastEntity implements Serializable {
        /**
         * 天文数值
         */
        @SerializedName("astro")
        public AstroEntity astro;
        /**
         * 天气状况
         */
        @SerializedName("cond")
        public ForecastCondEntity cond;
        /**
         * 当地日期
         */
        @SerializedName("date")
        public String date;
        /**
         * 湿度(%)
         */
        @SerializedName("hum")
        public String hum;
        /**
         * 降雨量(mm)
         */
        @SerializedName("pcpn")
        public String pcpn;
        /**
         * 降水概率
         */
        @SerializedName("pop")
        public String pop;
        /**
         * 气压
         */
        @SerializedName("pres")
        public String pres;
        /**
         * 温度
         */
        @SerializedName("tmp")
        public TmpEntity tmp;
        /**
         * 紫外线指数
         */
        @SerializedName("uv")
        public String uv;
        /**
         * 能见度(km)
         */
        @SerializedName("vis")
        public String vis;
        /**
         * 风力状况
         */
        @SerializedName("wind")
        public WindEntity wind;

        public static class AstroEntity implements Serializable {
            /**
             * 月升时间
             */
            @SerializedName("mr")
            public String mr;
            /**
             * 月落时间
             */
            @SerializedName("ms")
            public String ms;
            /**
             * 日出时间
             */
            @SerializedName("sr")
            public String sr;
            /**
             * 日落时间
             */
            @SerializedName("ss")
            public String ss;
        }

        public static class ForecastCondEntity implements Serializable {
            /**
             * 白天天气代码
             */
            @SerializedName("code_d")
            public String code_d;
            /**
             * 夜间天气代码
             */
            @SerializedName("code_n")
            public String code_n;
            /**
             * 白天天气描述
             */
            @SerializedName("txt_d")
            public String txt_d;
            /**
             * 夜间天气描述
             */
            @SerializedName("txt_n")
            public String txt_n;
        }

        public static class TmpEntity implements Serializable {
            /**
             * 最高温度(摄氏度)
             */
            @SerializedName("max")
            public String max;
            /**
             * 最低温度(摄氏度)
             */
            @SerializedName("min")
            public String min;
        }
    }

    public static class HourlyForecastEntity implements Serializable {
        /**
         * 天气状况
         */
        @SerializedName("cond")
        public NowCondEntity cond;
        /**
         * 当地日期和时间
         */
        @SerializedName("date")
        public String date;
        /**
         * 湿度(%)
         */
        @SerializedName("hum")
        public String hum;
        /**
         * 降水概率
         */
        @SerializedName("pop")
        public String pop;
        /**
         * 气压
         */
        @SerializedName("pres")
        public String pres;
        /**
         * 当前温度(摄氏度)
         */
        @SerializedName("tmp")
        public String tmp;
        /**
         * 风力状况
         */
        @SerializedName("wind")
        public WindEntity wind;
    }

    public static class NowCondEntity implements Serializable {
        /**
         * 天气代码
         */
        @SerializedName("code")
        public String code;
        /**
         * 天气描述
         */
        @SerializedName("txt")
        public String txt;
    }

    public static class WindEntity implements Serializable {
        /**
         * 风向(角度)
         */
        @SerializedName("deg")
        public String deg;
        /**
         * 风向(方向)
         */
        @SerializedName("dir")
        public String dir;
        /**
         * 风力等级
         */
        @SerializedName("sc")
        public String sc;
        /**
         * 风速(Kmph)
         */
        @SerializedName("spd")
        public String spd;
    }
}
