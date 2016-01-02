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
    private Context mContext;
    private LifeIndex mLifeIndexes[];
    private int mIcons[];
    private int mSelection;

    public LifeIndexAdapter(Context context, LifeIndex lifeIndexes[]) {
        this.mContext = context;
        this.mLifeIndexes = lifeIndexes;
        this.mSelection = -1;
        mIcons = new int[]{R.drawable.ic_life_index_icon_chuanyi,
                R.drawable.ic_life_index_icon_xiche,
                R.drawable.ic_life_index_icon_lvyou,
                R.drawable.ic_life_index_icon_ganmao,
                R.drawable.ic_life_index_icon_yundong,
                R.drawable.ic_life_index_icon_ziwaixian};
    }

    @Override
    public int getCount() {
        return mLifeIndexes.length;
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
        convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_weather_life_index_list_item, null);
        holder = new ViewHolder();
        holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
        holder.tvTips = (TextView) convertView.findViewById(R.id.tv_tips);
        holder.tvZS = (TextView) convertView.findViewById(R.id.tv_zs);
        holder.tvDes = (TextView) convertView.findViewById(R.id.tv_des);
        holder.ivArrow = (ImageView) convertView.findViewById(R.id.iv_arrow);
        holder.ivIcon.setImageResource(mIcons[position]);
        holder.tvTips.setText(mLifeIndexes[position].getTipt());
        holder.tvZS.setText(mLifeIndexes[position].getZs());
        holder.tvDes.setText(mLifeIndexes[position].getDes());
        if (position == mSelection) {
            holder.tvDes.setVisibility(View.VISIBLE);
            holder.ivArrow.setImageResource(R.drawable.ic_arrow_open);
        } else {
            holder.tvDes.setVisibility(View.GONE);
            holder.ivArrow.setImageResource(R.drawable.ic_arrow_close);
        }
        return convertView;
    }

    public void setSelection(int position) {
        if (mSelection != position) {
            mSelection = position;
        } else {
            mSelection = -1;
        }
    }

    class ViewHolder {
        ImageView ivIcon;
        TextView tvTips;
        TextView tvZS;
        TextView tvDes;
        ImageView ivArrow;
    }
}
