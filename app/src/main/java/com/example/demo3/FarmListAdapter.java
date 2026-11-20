package com.example.demo3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;

public class FarmListAdapter extends BaseAdapter {

    private List<Farm> farmList;
    private LayoutInflater inflater;

    public FarmListAdapter(List<Farm> farmList, LayoutInflater inflater) {
        this.farmList = farmList;
        this.inflater = inflater;
    }

    @Override
    public int getCount() {
        return farmList == null ? 0 : farmList.size();
    }

    @Override
    public Object getItem(int position) {
        return farmList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_farm, parent, false);
            holder = new ViewHolder();
            holder.tvName = convertView.findViewById(R.id.tv_farm_name);
            holder.tvInfo = convertView.findViewById(R.id.tv_farm_info);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Farm farm = farmList.get(position);
        holder.tvName.setText(farm.getName() + "  ⭐" + String.format("%.1f", farm.getRating()));
        holder.tvInfo.setText(farm.getAddress() + " · " + farm.getFruits());

        return convertView;
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvInfo;
    }
}