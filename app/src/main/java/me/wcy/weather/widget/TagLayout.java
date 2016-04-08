package me.wcy.weather.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import me.wcy.weather.R;

/**
 * Created by hzwangchenyan on 2016/4/8.
 */
public class TagLayout extends LinearLayout implements View.OnClickListener {
    private List<View> tagViews = new ArrayList<>(5);

    public TagLayout(Context context) {
        super(context);
        init();
    }

    public TagLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TagLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TagLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.image_weather_tag, this, false);
        addView(view, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        tagViews.add(findViewById(R.id.tv_tag_plant));
        tagViews.add(findViewById(R.id.tv_tag_people));
        tagViews.add(findViewById(R.id.tv_tag_weather));
        tagViews.add(findViewById(R.id.tv_tag_architecture));
        tagViews.add(findViewById(R.id.tv_tag_animal));
        for (View tag : tagViews) {
            tag.setOnClickListener(this);
        }
        tagViews.get(2).setSelected(true);
    }

    public String getTag() {
        for (View tag : tagViews) {
            if (tag.isSelected()) {
                return ((TextView) tag).getText().toString();
            }
        }
        return ((TextView) tagViews.get(2)).getText().toString();
    }

    @Override
    public void onClick(View v) {
        v.setSelected(true);
        for (View tag : tagViews) {
            if (tag != v) {
                tag.setSelected(false);
            }
        }
    }
}
