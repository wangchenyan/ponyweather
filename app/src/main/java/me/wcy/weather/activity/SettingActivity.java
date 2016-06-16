package me.wcy.weather.activity;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import me.wcy.weather.R;

public class SettingActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        getFragmentManager().beginTransaction().replace(R.id.ll_fragment_container, new SettingFragment()).commit();
    }

    @Override
    protected void setListener() {
    }

    public static class SettingFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preference_setting);
        }
    }
}
