/**
 * 2015-3-28
 */
package me.wcy.weather.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import me.wcy.weather.R;

/**
 * @author wcy
 */
public class CityAdapter extends BaseAdapter {
    private Context mContext;
    private String[] mCities;

    public CityAdapter(Context context, String[] cities) {
        this.mContext = context;
        this.mCities = cities;
    }

    @Override
    public int getCount() {
        return mCities.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_select_city_list_item, null);
            holder = new ViewHolder();
            holder.tvCity = (TextView) convertView.findViewById(R.id.tv_city);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvCity.setText(mCities[position]);
        return convertView;
    }

    class ViewHolder {
        TextView tvCity;
    }
}
