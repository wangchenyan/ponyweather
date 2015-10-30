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
import android.widget.ImageView;
import android.widget.TextView;

import me.wcy.weather.R;
import me.wcy.weather.model.LifeIndex;

/**
 * @author wcy
 */
@SuppressLint({"InflateParams", "ViewHolder"})
public class LifeIndexAdapter extends BaseAdapter {
    private Context context;
    private LifeIndex lifeIndexs[];
    private int icons[];
    private int selection;

    public LifeIndexAdapter(Context context, LifeIndex lifeIndexs[]) {
        this.context = context;
        this.lifeIndexs = lifeIndexs;
        this.selection = -1;
        icons = new int[]{R.drawable.ic_life_index_icon_chuanyi,
                R.drawable.ic_life_index_icon_xiche,
                R.drawable.ic_life_index_icon_lvyou,
                R.drawable.ic_life_index_icon_ganmao,
                R.drawable.ic_life_index_icon_yundong,
                R.drawable.ic_life_index_icon_ziwaixian};
    }

    @Override
    public int getCount() {
        return lifeIndexs.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        convertView = LayoutInflater.from(context).inflate(R.layout.life_index_item, null);
        holder = new ViewHolder();
        holder.icon = (ImageView) convertView.findViewById(R.id.life_index_icon);
        holder.tipt = (TextView) convertView.findViewById(R.id.tipt);
        holder.zs = (TextView) convertView.findViewById(R.id.zs);
        holder.des = (TextView) convertView.findViewById(R.id.des);
        holder.arrow = (ImageView) convertView.findViewById(R.id.arrow);
        holder.icon.setImageResource(icons[position]);
        holder.tipt.setText(lifeIndexs[position].getTipt());
        holder.zs.setText(lifeIndexs[position].getZs());
        holder.des.setText(lifeIndexs[position].getDes());
        if (position == selection) {
            holder.des.setVisibility(View.VISIBLE);
            holder.arrow.setImageResource(R.drawable.ic_arrow_open);
        } else {
            holder.des.setVisibility(View.GONE);
            holder.arrow.setImageResource(R.drawable.ic_arrow_close);
        }
        return convertView;
    }

    public void setSelection(int position) {
        if (selection != position) {
            selection = position;
        } else {
            selection = -1;
        }
    }

    class ViewHolder {
        ImageView icon;
        TextView tipt;
        TextView zs;
        TextView des;
        ImageView arrow;
    }

}
