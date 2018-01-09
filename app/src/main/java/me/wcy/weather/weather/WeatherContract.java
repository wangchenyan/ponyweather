package me.wcy.weather.weather;

import android.content.Intent;

import io.reactivex.Observable;
import me.wcy.weather.BaseModel;
import me.wcy.weather.BasePresenter;
import me.wcy.weather.BaseView;
import me.wcy.weather.model.CityInfo;
import me.wcy.weather.model.Weather;

/**
 * Created by hzwangchenyan on 2018/1/8.
 */
public interface WeatherContract {

    interface Model extends BaseModel {
        CityInfo getCurrentCity();

        Weather getCurrentWeatherFromCache();

        Weather getWeatherFromCache(CityInfo cityInfo);

        Observable<Weather> getWeatherFromNet(CityInfo cityInfo);

        void cacheCity(CityInfo cityInfo);

        void cacheWeather(CityInfo cityInfo, Weather weather);
    }

    interface View extends BaseView {
        void setTitle(CharSequence title);

        void showWeather(Weather weather);

        void hideWeatherView();

        void setRefreshing(boolean refreshing);

        void showSnack(CharSequence message);

        void setSpeechFabEnable(boolean enable);

        void setSpeechFabAnimation(boolean start);

        void scrollToTopAndExpand();
    }

    interface Presenter extends BasePresenter {
        void onRefresh();

        void onActivityResult(int requestCode, int resultCode, Intent data);

        void startImageWeather();

        void startManage();

        void startSetting();

        void startAbout();

        void share();
    }

    interface SpeechPresenter extends BasePresenter {
        void speech();
    }
}
