package com.example.taiga.nokorimono;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by taiga on 2017/10/01.
 */

public class ItemEntity implements Parcelable {
    private int id;
    private String name;
    private String memo;
    private int nokoriPoint;
    private String imageUrl;

    public ItemEntity(){
        nokoriPoint=0;
        id=0;
    }

    protected ItemEntity(Parcel in) {
        id = in.readInt();
        name = in.readString();
        memo = in.readString();
        nokoriPoint = in.readInt();
        imageUrl = in.readString();
    }

    public static final Creator<ItemEntity> CREATOR = new Creator<ItemEntity>() {
        @Override
        public ItemEntity createFromParcel(Parcel in) {
            return new ItemEntity(in);
        }

        @Override
        public ItemEntity[] newArray(int size) {
            return new ItemEntity[size];
        }
    };

    public void setId(int id){
        this.id=id;
    }
    public int getId(){
        return this.id;
    }

    public void setName(String name){
        this.name=name;
    }
    public String getName(){
        return this.name;
    }

    public void setMemo(String memo){
        this.memo=memo;
    }
    public String getMemo(){
        return this.memo;
    }

    public void setNokoriPoint(int nokoriPoint){
        this.nokoriPoint=nokoriPoint;
    }
    public int getNokoriPoint(){
        return this.nokoriPoint;
    }

    public void setImageUrl(String imageUrl){
        this.imageUrl=imageUrl;
    }
    public String getImageUrl(){
        return this.imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(name);
        parcel.writeString(memo);
        parcel.writeInt(nokoriPoint);
        parcel.writeString(imageUrl);
    }
}
