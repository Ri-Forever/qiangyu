package com.qiangyu.my.bean;

public class PictList {

    private int Id;
    private String WantNo;
    private String Picture;
    public void setId(int Id) {
        this.Id = Id;
    }
    public int getId() {
        return Id;
    }

    public void setWantNo(String WantNo) {
        this.WantNo = WantNo;
    }
    public String getWantNo() {
        return WantNo;
    }

    public void setPicture(String Picture) {
        this.Picture = Picture;
    }
    public String getPicture() {
        return Picture;
    }
}
