# 小马天气
小马天气是一款运行在Android系统上开源天气信息查询软件，无广告，无多余权限。<br>
界面美观，人性化设计，支持中国所有城市的天气预报软件。<br>
支持自动定位，空气质量指数实时预报，提供各种生活指数。<br>
简约，而不简单。

## 前言
部分界面参考[xcc3641](https://github.com/xcc3641)的开源项目[SeeWeather](https://github.com/xcc3641/SeeWeather)，在此表示感谢！<br>
第一次开始这个项目是2014年4月份，我还在大二的时候，当时有一个程序设计实践课，想着简单点就选了天气预报这个题目，时隔两年又重新拾起，给它脱胎换骨，就是想拿他作为一个学习新知识的实战项目，期间也做过改动，比如曾经把网络请求模块从[android-async-http](https://github.com/loopj/android-async-http)改为[Volley](https://android.googlesource.com/platform/frameworks/volley)。

* **开源不易，希望能给个Star鼓励** 
* 项目地址：https://github.com/ChanWong21/PonyWeather
* 有问题请提Issues

### 下载地址
360手机助手：http://zhushou.360.cn/detail/index/soft_id/2826694<br>
百度应用：http://shouji.baidu.com/software/item?docid=9102703<br>
应用宝：http://android.myapp.com/myapp/detail.htm?apkName=me.wcy.weather

更新说明
---
`v 1.5`
* 重构代码，全新风格设计
* 增加支持国内全部县级以上城市可选

`v 1.4`
* 支持Android 6.0
* 支持x86手机

TODO
---
* 城市管理
* 自动夜间模式
* 实景天气
* 语音播报
* 桌面小部件

项目
---
**公开API**
* 天气：[和风天气](http://www.heweather.com/)
* 定位：[高德定位](http://lbs.amap.com/api/android-location-sdk/)

**开源技术**
* [Rxjava](https://github.com/ReactiveX/RxJava)
* [Retrofit](https://github.com/square/retrofit)
* [ASimpleCache](https://github.com/yangfuhai/ASimpleCache)

**部分源码**

网络请求用的是`RxJava+Retrofit`，用`ACache`缓存
```java
private void fetchDataFromCache(final String city) {
    Weather weather = (Weather) mACache.getAsObject(city);
    if (weather == null) {
        fetchDataFromNetWork(city, false);
    } else {
        updateView(weather);
    }
}

private void fetchDataFromNetWork(final String city) {
    Api.getIApi().getWeather(city, Api.HE_KEY)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .filter(new Func1<WeatherData, Boolean>() {
                @Override
                public Boolean call(final WeatherData weatherData) {
                    return weatherData.weathers.get(0).status.equals("ok");
                }
            })
            .map(new Func1<WeatherData, Weather>() {
                @Override
                public Weather call(WeatherData weatherData) {
                    return weatherData.weathers.get(0);
                }
            })
            .doOnNext(new Action1<Weather>() {
                @Override
                public void call(Weather weather) {
                    mACache.put(Extras.CITY, city);
                    mACache.put(city, weather, ACache.TIME_HOUR);
                }
            })
            .subscribe(new Subscriber<Weather>() {
                updateView(weather);
            });
}
```

截图
---
![](https://raw.githubusercontent.com/ChanWong21/PonyWeather/master/art/screenshot_01.jpg)
![](https://raw.githubusercontent.com/ChanWong21/PonyWeather/master/art/screenshot_02.jpg)
![](https://raw.githubusercontent.com/ChanWong21/PonyWeather/master/art/screenshot_03.jpg)
![](https://raw.githubusercontent.com/ChanWong21/PonyWeather/master/art/screenshot_04.jpg)

关于作者
---
简书：http://www.jianshu.com/users/3231579893ac<br>
微博：http://weibo.com/wangchenyan1993

License
---
    Copyright 2016 Chay Wong

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

图片来源于网络，版权属于原作者。
