package me.wcy.weather.addcity;

import android.app.Activity;
import android.content.Context;

import java.util.List;

import io.reactivex.Observable;
import me.wcy.weather.BaseModel;
import me.wcy.weather.BasePresenter;
import me.wcy.weather.BaseView;
import me.wcy.weather.constants.CityType;
import me.wcy.weather.model.CityEntity;

/**
 * Created by hzwangchenyan on 2017/12/28.
 */
public interface AddCityContract {

    interface Model extends BaseModel {
        Observable<CityEntity> getAllCity(Context context);

        Observable<List<CityEntity>> getProvince(Context context);

        Observable<List<CityEntity>> getCity(Context context, String province);

        Observable<List<CityEntity>> getArea(Context context, String city);

        Observable<List<CityEntity>> search(Context context, String keyword);
    }

    interface View extends BaseView {
        Activity getActivity();

        void showProgress(String message);

        void cancelProgress();

        void showSnack(CharSequence message);

        void showProvince(List<CityEntity> provinceList);

        void showCity(List<CityEntity> cityList);

        void showArea(List<CityEntity> areaList);

        void showSearching();

        void cancelSearch();

        void showSearchSuccess(List<CityEntity> searchList);

        void showSearchError();

        void setTitle(CharSequence title);

        void finish();
    }

    interface Presenter extends BasePresenter {
        void showProvince();

        void showCity(String province);

        void showArea(String city);

        void search(String keyword);

        void locate();

        void onItemClick(CityEntity cityEntity);

        void onBackPressed();

        CityType getType();
    }
}
