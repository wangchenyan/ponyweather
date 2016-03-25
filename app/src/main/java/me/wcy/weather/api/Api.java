package me.wcy.weather.api;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hzwangchenyan on 2016/3/24.
 */
public class Api {
    private static final String BASE_URL = "https://api.heweather.com/x3/";
    public static final String HE_KEY = "dd01315398b64840a1765e5674e27f8f";

    private Api() {
    }

    private static Retrofit sRetrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .build();

    private static IApi sIApi = sRetrofit.create(IApi.class);

    public static IApi getIApi() {
        return sIApi;
    }
}
