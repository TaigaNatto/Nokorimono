package com.example.taiga.nokorimono;

/**
 * Created by taiga on 2017/10/01.
 */

public class ItemEntity {
    private int id;
    private String name;
    private String memo;
    private int nokoriPoint;
    private String imageUrl;

    public ItemEntity(){
        nokoriPoint=0;
        id=0;
    }

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
}
