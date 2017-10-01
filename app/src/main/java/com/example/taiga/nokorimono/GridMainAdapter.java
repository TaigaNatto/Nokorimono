package com.example.taiga.nokorimono;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by taiga on 2017/09/24.
 */

public class GridMainAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private ArrayList<String> mItemArray;

    public GridMainAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mItemArray=new ArrayList<>();
    }

    private static class ViewHolder {
        public TextView gridTextView;
    }

    public int getCount() {
        return mItemArray.size();
    }

    public Object getItem(int position) {
        return mItemArray.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public void add(String item){
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
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.gridTextView.setText(mItemArray.get(position));

        return convertView;
    }

}
