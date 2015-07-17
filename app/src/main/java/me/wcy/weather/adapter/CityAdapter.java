/**
 * 2015-3-28
 */
package me.wcy.weather.adapter;

import me.wcy.weather.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author wcy
 * 
 */
public class CityAdapter extends BaseAdapter {
	private Context context;
	private String[] citys;

	public CityAdapter(Context context, String[] citys) {
		super();
		this.context = context;
		this.citys = citys;
	}

	@Override
	public int getCount() {
		return citys.length;
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
			convertView = LayoutInflater.from(context).inflate(
					R.layout.select_city_item, null);
			holder = new ViewHolder();
			holder.city = (TextView) convertView.findViewById(R.id.city);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.city.setText(citys[position]);
		return convertView;
	}

	class ViewHolder {
		TextView city;
	}
}
