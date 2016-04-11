package me.wcy.weather.application;

import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;

import com.baidu.speechsynthesizer.SpeechSynthesizer;
import com.baidu.speechsynthesizer.SpeechSynthesizerListener;
import com.baidu.speechsynthesizer.publicutility.SpeechError;

import me.wcy.weather.R;
import me.wcy.weather.utils.SnackbarUtils;
import me.wcy.weather.utils.SystemUtils;

/**
 * Created by hzwangchenyan on 2016/4/11.
 */
public class SpeechListener implements SpeechSynthesizerListener {
    private static final String TAG = "SpeechListener";
    private Activity activity;
    private FloatingActionButton fab;

    public SpeechListener(Activity activity) {
        this.activity = activity;
        fab = (FloatingActionButton) activity.findViewById(R.id.fab_speech);
    }

    @Override
    public void onStartWorking(SpeechSynthesizer speechSynthesizer) {
        Log.d(TAG, "onStartWorking");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fab.setEnabled(false);
                SystemUtils.voiceAnimation(fab, true);
            }
        });
    }

    @Override
    public void onSpeechStart(SpeechSynthesizer speechSynthesizer) {
        Log.d(TAG, "onSpeechStart");
    }

    @Override
    public void onNewDataArrive(SpeechSynthesizer speechSynthesizer, byte[] bytes, boolean b) {
        Log.d(TAG, "onNewDataArrive");
    }

    @Override
    public void onBufferProgressChanged(SpeechSynthesizer speechSynthesizer, int i) {
        Log.d(TAG, "onBufferProgressChanged");
    }

    @Override
    public void onSpeechProgressChanged(SpeechSynthesizer speechSynthesizer, int i) {
        Log.d(TAG, "onSpeechProgressChanged");
    }

    @Override
    public void onSpeechPause(SpeechSynthesizer speechSynthesizer) {
        Log.d(TAG, "onSpeechPause");
    }

    @Override
    public void onSpeechResume(SpeechSynthesizer speechSynthesizer) {
        Log.d(TAG, "onSpeechResume");
    }

    @Override
    public void onCancel(SpeechSynthesizer speechSynthesizer) {
        Log.d(TAG, "onCancel");
    }

    @Override
    public void onSynthesizeFinish(SpeechSynthesizer speechSynthesizer) {
        Log.d(TAG, "onSynthesizeFinish");
    }

    @Override
    public void onSpeechFinish(SpeechSynthesizer speechSynthesizer) {
        Log.d(TAG, "onSpeechFinish");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fab.setEnabled(true);
                SystemUtils.voiceAnimation(fab, false);
            }
        });
    }

    @Override
    public void onError(SpeechSynthesizer speechSynthesizer, final SpeechError speechError) {
        Log.e(TAG, "SpeechError:" + speechError.errorCode + "," + speechError.errorDescription);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SnackbarUtils.show(activity, speechError.errorDescription);
            }
        });
    }
}
