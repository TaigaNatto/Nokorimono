package com.example.taiga.nokorimono;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by taiga on 2017/09/24.
 */

public class GridMainAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private ArrayList<ItemEntity> mItemArray;

    public GridMainAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mItemArray=new ArrayList<>();
    }

    private static class ViewHolder {
        public TextView gridTextView;
        public ImageView imageView;
        public ImageView batteryView;
    }

    public int getCount() {
        return mItemArray.size();
    }

    public ItemEntity getItem(int position) {
        return mItemArray.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void add(ItemEntity item){
        mItemArray.add(item);
    }

    public void clear(){
        mItemArray.clear();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.grid_item_main, null);
            holder = new ViewHolder();
            holder.gridTextView = (TextView)convertView.findViewById(R.id.grid_textview);
            holder.imageView=(ImageView)convertView.findViewById(R.id.grid_image);
            holder.batteryView=(ImageView)convertView.findViewById(R.id.grid_battery);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.gridTextView.setText(mItemArray.get(position).getName());
        //残りポイントで残高変更
        switch (mItemArray.get(position).getNokoriPoint()) {
            case 0:
                holder.batteryView.setImageResource(R.drawable.battery_0_720);
                break;
            case 1:
                holder.batteryView.setImageResource(R.drawable.battery_1_720);
                break;
            case 2:
                holder.batteryView.setImageResource(R.drawable.battery_2_720);
                break;
            case 3:
                holder.batteryView.setImageResource(R.drawable.battery_3_720);
                break;
            case 4:
                holder.batteryView.setImageResource(R.drawable.battery_4_720);
                break;
        }

        return convertView;
    }

}
