package me.wcy.weather.weather;

import android.Manifest;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.baidu.speechsynthesizer.SpeechSynthesizer;
import com.baidu.speechsynthesizer.SpeechSynthesizerListener;
import com.baidu.speechsynthesizer.publicutility.SpeechError;

import me.wcy.weather.BuildConfig;
import me.wcy.weather.model.Weather;
import me.wcy.weather.utils.PermissionReq;
import me.wcy.weather.utils.Utils;

/**
 * Created by hzwangchenyan on 2018/1/8.
 */
public class WeatherSpeechPresenter implements WeatherContract.SpeechPresenter, SpeechSynthesizerListener {
    private static final String TAG = "WeatherSpeechPresenter";
    private WeatherContract.Model model;
    private WeatherContract.View view;
    private SpeechSynthesizer mSpeechSynthesizer;
    private Handler handler;

    public WeatherSpeechPresenter(WeatherContract.Model model, WeatherContract.View view) {
        this.model = model;
        this.view = view;
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onCreate() {
        mSpeechSynthesizer = new SpeechSynthesizer(view.getContext(), "holder", this);
        mSpeechSynthesizer.setApiKey(BuildConfig.BD_TTS_API_KEY, BuildConfig.BD_TTS_SECRET_KEY);
        mSpeechSynthesizer.setAudioStreamType(AudioManager.STREAM_MUSIC);
    }

    @Override
    public void onDestroy() {
        mSpeechSynthesizer.cancel();
    }

    @Override
    public void speech() {
        Weather weather = model.getCurrentWeatherFromCache();
        if (weather == null) {
            return;
        }
        PermissionReq.with(view.getActivity())
                .permissions(Manifest.permission.READ_PHONE_STATE)
                .result(new PermissionReq.Result() {
                    @Override
                    public void onGranted() {
                        String text = Utils.voiceText(view.getContext(), weather.daily_forecast.get(0));
                        mSpeechSynthesizer.speak(text);
                    }

                    @Override
                    public void onDenied() {
                        view.showSnack("没有权限，无法播报天气！");
                    }
                })
                .request();
    }

    @Override
    public void onStartWorking(SpeechSynthesizer speechSynthesizer) {
        Log.i(TAG, "onStartWorking");
        handler.post(() -> {
            view.setSpeechFabEnable(false);
            view.setSpeechFabAnimation(true);
        });
    }

    @Override
    public void onSpeechStart(SpeechSynthesizer speechSynthesizer) {
    }

    @Override
    public void onNewDataArrive(SpeechSynthesizer speechSynthesizer, byte[] bytes, boolean b) {
    }

    @Override
    public void onBufferProgressChanged(SpeechSynthesizer speechSynthesizer, int i) {
    }

    @Override
    public void onSpeechProgressChanged(SpeechSynthesizer speechSynthesizer, int i) {
    }

    @Override
    public void onSpeechPause(SpeechSynthesizer speechSynthesizer) {
    }

    @Override
    public void onSpeechResume(SpeechSynthesizer speechSynthesizer) {
    }

    @Override
    public void onCancel(SpeechSynthesizer speechSynthesizer) {
    }

    @Override
    public void onSynthesizeFinish(SpeechSynthesizer speechSynthesizer) {
    }

    @Override
    public void onSpeechFinish(SpeechSynthesizer speechSynthesizer) {
        Log.i(TAG, "onSpeechFinish");
        handler.post(() -> {
            view.setSpeechFabEnable(true);
            view.setSpeechFabAnimation(false);
        });
    }

    @Override
    public void onError(SpeechSynthesizer speechSynthesizer, SpeechError speechError) {
        Log.e(TAG, "SpeechError:" + speechError.errorCode + "," + speechError.errorDescription);
        handler.post(() -> view.showSnack(speechError.errorDescription));
    }
}
