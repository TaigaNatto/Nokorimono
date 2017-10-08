package com.example.taiga.nokorimono;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by taiga on 2017/09/24.
 */

public class GridMainAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private ArrayList<ItemEntity> mItemArray;

    FirebaseStorage fStorage;

    public GridMainAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mItemArray=new ArrayList<>();
        fStorage=FirebaseStorage.getInstance();
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

        final ViewHolder holder;
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

        String dlUri=mItemArray.get(position).getImageUrl();
        if(dlUri!=null){
            Picasso.with(mContext).load(dlUri).fit().centerCrop().into(holder.imageView);
        }

        //名前が5文字以上であれば縮小処理
        if(mItemArray.get(position).getName().length()>5) {
            holder.gridTextView.setText(mItemArray.get(position).getName().substring(0,4)+"...");
        }
        else{
            holder.gridTextView.setText(mItemArray.get(position).getName());
        }
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
