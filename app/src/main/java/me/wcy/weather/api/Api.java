package me.wcy.weather.api;

import io.reactivex.Observable;
import me.wcy.weather.model.WeatherData;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class Api {
    private static final String BASE_URL = "https://free-api.heweather.com/v5/";

    public interface IApi {
        @GET("weather")
        Observable<WeatherData> getWeather(@Query("city") String city, @Query("key") String key);
    }

    private Api() {
    }

    private static Retrofit sRetrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build();

    private static IApi sIApi = sRetrofit.create(IApi.class);

    public static IApi getIApi() {
        return sIApi;
    }
}
