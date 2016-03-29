package me.wcy.weather.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hzwangchenyan on 2016/3/24.
 */
public class Weather implements Serializable {
    public String status;
    /**
     * 城市基本信息
     */
    public BasicEntity basic;
    /**
     * 空气质量指数
     */
    public AqiEntity aqi;
    /**
     * 实况天气
     */
    public NowEntity now;
    /**
     * 生活指数
     */
    public SuggestionEntity suggestion;
    /**
     * 天气预报
     */
    public List<DailyForecastEntity> daily_forecast;
    /**
     * 每小时天气预报
     */
    public List<HourlyForecastEntity> hourly_forecast;

    public static class BasicEntity implements Serializable {
        /**
         * 城市名称
         */
        public String city;
        /**
         * 城市ID
         */
        public String cnty;
        /**
         * 国家名称
         */
        public String id;
        /**
         * 纬度
         */
        public String lat;
        /**
         * 经度
         */
        public String lon;
        /**
         * 数据更新时间,24小时制
         */
        public UpdateEntity update;

        public static class UpdateEntity implements Serializable {
            /**
             * 数据更新的当地时间
             */
            public String loc;
            /**
             * 数据更新的UTC时间
             */
            public String utc;
        }
    }

    public static class AqiEntity implements Serializable {
        /**
         * 城市数据
         */
        public CityEntity city;

        public static class CityEntity implements Serializable {
            /**
             * 空气质量指数
             */
            public String aqi;
            /**
             * 一氧化碳1小时平均值(ug/m³)
             */
            public String co;
            /**
             * 二氧化氮1小时平均值(ug/m³)
             */
            public String no2;
            /**
             * 臭氧1小时平均值(ug/m³)
             */
            public String o3;
            /**
             * PM10 1小时平均值(ug/m³)
             */
            public String pm10;
            /**
             * PM2.5 1小时平均值(ug/m³)
             */
            public String pm25;
            /**
             * 空气质量类别
             */
            public String qlty;
            /**
             * 二氧化硫1小时平均值(ug/m³)
             */
            public String so2;
        }
    }

    public static class NowEntity implements Serializable {
        /**
         * 天气状况
         */
        public CondEntity cond;
        /**
         * 体感温度
         */
        public String fl;
        /**
         * 湿度(%)
         */
        public String hum;
        /**
         * 降雨量(mm)
         */
        public String pcpn;
        /**
         * 气压
         */
        public String pres;
        /**
         * 当前温度(摄氏度)
         */
        public String tmp;
        /**
         * 能见度(km)
         */
        public String vis;
        /**
         * 风力状况
         */
        public WindEntity wind;

        public static class CondEntity implements Serializable {
            /**
             * 天气代码
             */
            public String code;
            /**
             * 天气描述
             */
            public String txt;
        }
    }

    public static class SuggestionEntity implements Serializable {
        /**
         * 舒适指数
         */
        public Entity comf;
        /**
         * 洗车指数
         */
        public Entity cw;
        /**
         * 穿衣指数
         */
        public Entity drsg;
        /**
         * 感冒指数
         */
        public Entity flu;
        /**
         * 运动指数
         */
        public Entity sport;
        /**
         * 旅游指数
         */
        public Entity trav;
        /**
         * 紫外线指数
         */
        public Entity uv;

        public static class Entity implements Serializable {
            /**
             * 简介
             */
            public String brf;
            /**
             * 详情
             */
            public String txt;
        }
    }

    public static class DailyForecastEntity implements Serializable {
        /**
         * 天文数值
         */
        public AstroEntity astro;
        /**
         * 天气状况
         */
        public CondEntity cond;
        /**
         * 当地日期
         */
        public String date;
        /**
         * 湿度(%)
         */
        public String hum;
        /**
         * 降雨量(mm)
         */
        public String pcpn;
        /**
         * 降水概率
         */
        public String pop;
        /**
         * 气压
         */
        public String pres;
        /**
         * 温度
         */
        public TmpEntity tmp;
        /**
         * 能见度(km)
         */
        public String vis;
        /**
         * 风力状况
         */
        public WindEntity wind;

        public static class AstroEntity implements Serializable {
            /**
             * 日出时间
             */
            public String sr;
            /**
             * 日落时间
             */
            public String ss;
        }

        public static class CondEntity implements Serializable {
            /**
             * 白天天气代码
             */
            public String code_d;
            /**
             * 夜间天气代码
             */
            public String code_n;
            /**
             * 白天天气描述
             */
            public String txt_d;
            /**
             * 夜间天气描述
             */
            public String txt_n;
        }

        public static class TmpEntity implements Serializable {
            /**
             * 最高温度(摄氏度)
             */
            public String max;
            /**
             * 最低温度(摄氏度)
             */
            public String min;
        }
    }

    public static class HourlyForecastEntity implements Serializable {
        /**
         * 当地日期和时间
         */
        public String date;
        /**
         * 湿度(%)
         */
        public String hum;
        /**
         * 降水概率
         */
        public String pop;
        /**
         * 气压
         */
        public String pres;
        /**
         * 当前温度(摄氏度)
         */
        public String tmp;
        /**
         * 风力状况
         */
        public WindEntity wind;
    }

    public static class WindEntity implements Serializable {
        /**
         * 风向(角度)
         */
        public String deg;
        /**
         * 风向(方向)
         */
        public String dir;
        /**
         * 风力等级
         */
        public String sc;
        /**
         * 风速(Kmph)
         */
        public String spd;
    }
}
