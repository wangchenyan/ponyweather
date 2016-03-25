package me.wcy.weather.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;

import butterknife.Bind;
import me.wcy.weather.R;
import me.wcy.weather.adapter.DailyForecastAdapter;
import me.wcy.weather.adapter.HourlyForecastAdapter;
import me.wcy.weather.adapter.SuggestionAdapter;
import me.wcy.weather.api.Api;
import me.wcy.weather.model.HeWeather;
import me.wcy.weather.model.HeWeatherData;
import me.wcy.weather.utils.ACache;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class HeWeatherActivity extends BaseActivity {
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.lv_hourly_forecast)
    ListView lvHourlyForecast;
    @Bind(R.id.lv_suggestion)
    ListView lvSuggestion;
    @Bind(R.id.lv_daily_forecast)
    ListView lvDailyForecast;
    private ACache mACache;
    private String mCity;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_he_weather);

        mACache = ACache.get(getApplicationContext());
        mCity = mACache.getAsString("city");
        if (TextUtils.isEmpty(mCity)) {
            mCity = "北京";
        }
        mProgress = new ProgressDialog(this);
        setTitle(mCity);
        getWeather(mCity);
    }

    @Override
    protected void setListener() {

    }

    private void getWeather(final String city) {
        Api.getIApi().getWeather(city, Api.HE_KEY)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mProgress.show();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .filter(new Func1<HeWeatherData, Boolean>() {
                    @Override
                    public Boolean call(HeWeatherData heWeatherData) {
                        return heWeatherData.heWeathers != null
                                && !heWeatherData.heWeathers.isEmpty()
                                && heWeatherData.heWeathers.get(0).status.equals("ok");
                    }
                })
                .doOnNext(new Action1<HeWeatherData>() {
                    @Override
                    public void call(HeWeatherData heWeatherData) {
                        mACache.put(city, heWeatherData);
                    }
                })
                .subscribe(new Subscriber<HeWeatherData>() {
                    @Override
                    public void onCompleted() {
                        Log.e("getWeather", "onCompleted");
                        mProgress.cancel();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("getWeather onError", e.getMessage());
                    }

                    @Override
                    public void onNext(HeWeatherData heWeatherData) {
                        updateView(heWeatherData.heWeathers.get(0));
                    }
                });
    }

    private void updateView(HeWeather heWeather) {
        lvHourlyForecast.setAdapter(new HourlyForecastAdapter(heWeather.hourly_forecast));
        lvSuggestion.setAdapter(new SuggestionAdapter(heWeather.suggestion));
        lvDailyForecast.setAdapter(new DailyForecastAdapter(heWeather.daily_forecast));
    }
}
