package com.example.taiga.nokorimono;

import java.util.ArrayList;

/**
 * Created by taiga on 2017/11/01.
 */

public class UserEntity {
    private String usrEmail;
    private ArrayList<String> otherGroup;
    private int limit;

    public UserEntity(String usrEmail,int limit){
        this.usrEmail=usrEmail;
        this.limit=limit;
    }

    public String getUsrEmail(){
        return this.usrEmail;
    }
    public void setUsrEmail(String usrEmail){
        this.usrEmail=usrEmail;
    }

    public ArrayList<String> getOtherGroup(){
        return this.otherGroup;
    }
    public void setOtherGroup(ArrayList<String> otherGroup){
        this.otherGroup=otherGroup;
    }

    public int getLimit(){
        return this.limit;
    }
    public void setLimit(int limit){
        this.limit=limit;
    }
}
