package me.wcy.weather.addcity;

import android.database.Observable;

import java.util.List;

import me.wcy.weather.BaseModel;
import me.wcy.weather.BasePresenter;
import me.wcy.weather.BaseView;
import me.wcy.weather.model.CityEntity;

/**
 * Created by hzwangchenyan on 2017/12/28.
 */
public interface AddCityContract {

    interface Model extends BaseModel {
        Observable<List<CityEntity>> getProvince();

        Observable<List<CityEntity>> getCity(String province);

        Observable<List<CityEntity>> getArea(String city);

        Observable<List<CityEntity>> search(String keyword);
    }

    interface View extends BaseView<Presenter> {
        void showProgress();

        void cancelProgress();

        void showProvince(List<CityEntity> provinceList);

        void showCity(List<CityEntity> cityList);

        void showArea(List<CityEntity> areaList);

        void showSearching();

        void showSearchEmpty();

        void showSearchResult(List<CityEntity> searchList);
    }

    interface Presenter extends BasePresenter {
        void search(String keyword);

        void itemClick();

        void showProvince();

        void showCity(String province);

        void showArea(String city);

        void locate();
    }
}
