package me.wcy.weather.activity;

import java.lang.reflect.Field;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

import me.wcy.weather.util.ViewInject;

public class BaseActivity extends AppCompatActivity {

    @Override
    public void setContentView(int layoutResID) {
        View view = getLayoutInflater().inflate(layoutResID, null);
        setContentView(view);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initInjectedView();
    }

    private void initInjectedView() {
        Field[] fields = getClass().getDeclaredFields();
        if (fields != null && fields.length > 0) {
            for (Field field : fields) {
                try {
                    field.setAccessible(true);
                    if (field.get(this) != null) {
                        continue;
                    }
                    ViewInject viewInject = field
                            .getAnnotation(ViewInject.class);
                    if (viewInject != null) {
                        int viewId = viewInject.id();
                        field.set(this, findViewById(viewId));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
