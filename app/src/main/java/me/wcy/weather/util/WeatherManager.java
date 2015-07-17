/**
 * 2015-3-26
 */
package me.wcy.weather.util;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.wcy.weather.R;
import me.wcy.weather.database.DBHelper;
import me.wcy.weather.model.LifeIndex;
import me.wcy.weather.model.Weather;
import me.wcy.weather.model.WeatherInfo;
import me.wcy.weather.model.WeatherResult;

import android.annotation.SuppressLint;
import android.content.Context;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.table.TableUtils;

/**
 * @author wcy
 */
@SuppressLint("SimpleDateFormat")
public class WeatherManager {
    public static final String AIR_QULITY = "air_quality";
    public static final String AIR_QULITY_BG = "air_quality_bg";
    private Context context;
    private DBHelper dbHelper;
    private Dao<Weather, String> weatherDao;
    private Dao<WeatherInfo, String> weatherInfoDao;
    private Dao<LifeIndex, String> lifeIndexDao;
    private SimpleDateFormat sdf;

    public WeatherManager(Context context) {
        super();
        this.context = context;
        try {
            init();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void init() throws SQLException {
        dbHelper = new DBHelper(context);
        weatherDao = dbHelper.getDao(Weather.class);
        weatherInfoDao = dbHelper.getDao(WeatherInfo.class);
        lifeIndexDao = dbHelper.getDao(LifeIndex.class);
    }

    public void storeData(WeatherResult weatherResult) throws SQLException,
            ParseException {
        TableUtils.clearTable(dbHelper.getConnectionSource(), Weather.class);
        TableUtils
                .clearTable(dbHelper.getConnectionSource(), WeatherInfo.class);
        TableUtils.clearTable(dbHelper.getConnectionSource(), LifeIndex.class);
        // 保存天气信息
        Weather weather = weatherResult.getResults()[0];
        // 更新日期
        String date = weatherResult.getDate();
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        weather.setDate(sdf.parse(date).getTime());
        // 更新时间
        sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(new Date(System.currentTimeMillis()));
        weather.setUpdateTime(sdf.parse(time).getTime());
        // 实时温度
        String currentInfo = weather.getWeather_data()[0].getDate();
        if (currentInfo.contains("实时")) {
            int start = currentInfo.indexOf("：");
            int end = currentInfo.indexOf(")");
            String currentTemp = currentInfo.substring(start + 1, end);
            weather.setCurrentTemp(currentTemp);
        } else {
            String temp = weather.getWeather_data()[0].getTemperature();
            if (temp.contains("~")) {
                temp = temp.substring(0, temp.indexOf(" ")) + "℃";
            }
            weather.setCurrentTemp(temp);
        }
        // 检查PM2.5是否为空
        if (weather.getPm25().length() == 0) {
            weather.setPm25("?");
        }
        weatherDao.createOrUpdate(weather);
        // 保存天气预报
        int index = currentInfo.indexOf(" ");
        String weekday = currentInfo.substring(0, index);
        weather.getWeather_data()[0].setDate(weekday);
        WeatherInfo weatherInfos[] = weather.getWeather_data();
        for (WeatherInfo weatherInfo : weatherInfos) {
            weatherInfoDao.createOrUpdate(weatherInfo);
        }
        // 保存生活指数
        LifeIndex lifeIndexs[] = weather.getIndex();
        for (LifeIndex lifeIndex : lifeIndexs) {
            lifeIndexDao.createOrUpdate(lifeIndex);
        }
    }

    public Weather getData() throws SQLException {
        if (weatherDao.countOf() == 0) {
            return null;
        }
        Weather weather = weatherDao.queryForAll().get(0);
        List<WeatherInfo> weatherInfoList = weatherInfoDao.queryForAll();
        List<LifeIndex> lifeIndexList = lifeIndexDao.queryForAll();
        weather.setWeather_data(weatherInfoList
                .toArray(new WeatherInfo[weatherInfoList.size()]));
        weather.setIndex(lifeIndexList.toArray(new LifeIndex[lifeIndexList
                .size()]));
        return weather;
    }

    public String getUpdateTime(Weather weather) {
        sdf = new SimpleDateFormat("HH:mm");
        String updateTime = sdf.format(new Date(weather.getUpdateTime()))
                + " 更新";
        return updateTime;
    }

    /**
     * 获取当前日期（含农历）
     *
     * @return
     */
    public String getDate() {
        CalendarUtil calendarUtil = new CalendarUtil();
        sdf = new SimpleDateFormat("MM月dd日");
        String date = sdf.format(new Date(System.currentTimeMillis()));
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        String lunar = calendarUtil.getChineseMonth(year, month, day)
                + calendarUtil.getChineseDay(year, month, day);
        date = date + " 农历" + lunar;
        return date;
    }

    public String getCurrentWeather(Weather weather) {
        String currentWeather = weather.getWeather_data()[0].getWeather();
        if (currentWeather.contains("转")) {
            currentWeather = currentWeather.substring(0,
                    currentWeather.indexOf("转"));
        }
        return currentWeather;
    }

    /**
     * 超过2小时未更新自动更新
     *
     * @param weather
     * @return
     * @throws ParseException
     */
    public boolean autoUpdate(Weather weather) throws ParseException {
        sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(new Date(System.currentTimeMillis()));
        long currentDate = sdf.parse(date).getTime();
        // 超过1天未更新
        if (currentDate - weather.getDate() >= 60 * 60 * 24 * 1000) {
            return true;
        }
        sdf = new SimpleDateFormat("HH:mm");
        String time = sdf.format(new Date(System.currentTimeMillis()));
        long currentTime = sdf.parse(time).getTime();
        // 超过2小时未更新
        if (currentTime - weather.getUpdateTime() >= 60 * 60 * 2 * 1000) {
            return true;
        }
        return false;
    }

    public Map<String, Object> getAirQuality(Weather weather) {
        Map<String, Object> airQualityMap = new HashMap<String, Object>();
        int pm25, bg;
        String quality;
        try {
            pm25 = Integer.parseInt(weather.getPm25());
            if (pm25 <= 50) {
                quality = "空气优";
                bg = R.mipmap.ic_air_quality_bg_1;
            } else if (pm25 <= 100) {
                quality = "空气良";
                bg = R.mipmap.ic_air_quality_bg_2;
            } else if (pm25 <= 150) {
                quality = "轻度污染";
                bg = R.mipmap.ic_air_quality_bg_3;
            } else if (pm25 <= 200) {
                quality = "中度污染";
                bg = R.mipmap.ic_air_quality_bg_4;
            } else if (pm25 <= 300) {
                quality = "重度污染";
                bg = R.mipmap.ic_air_quality_bg_5;
            } else {
                quality = "严重污染";
                bg = R.mipmap.ic_air_quality_bg_6;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            quality = "";
            bg = R.mipmap.ic_air_quality_bg_1;
        }
        airQualityMap.put(AIR_QULITY, quality);
        airQualityMap.put(AIR_QULITY_BG, bg);
        return airQualityMap;
    }
}
