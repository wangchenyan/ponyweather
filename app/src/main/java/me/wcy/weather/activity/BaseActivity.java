package me.wcy.weather.activity;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import me.wcy.weather.util.ViewInjector;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    public void setContentView(int layoutResID) {
        View view = getLayoutInflater().inflate(layoutResID, null);
        setContentView(view);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ViewInjector.initInjectedView(this);
    }

}
