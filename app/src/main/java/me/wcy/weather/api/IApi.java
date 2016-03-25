package me.wcy.weather.api;

import me.wcy.weather.model.HeWeatherData;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by hzwangchenyan on 2016/3/24.
 */
public interface IApi {
    @GET("weather")
    Observable<HeWeatherData> getWeather(@Query("city") String city, @Query("key") String key);
}
