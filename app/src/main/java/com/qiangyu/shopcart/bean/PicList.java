package com.qiangyu.shopcart.bean;

public class PicList {
    private int Id;
    private int GoodsId;
    private int PictureType;
    private String Picture;
    private int Sort;

    public void setId(int Id) {
        this.Id = Id;
    }

    public int getId() {
        return Id;
    }

    public void setGoodsId(int GoodsId) {
        this.GoodsId = GoodsId;
    }

    public int getGoodsId() {
        return GoodsId;
    }

    public void setPictureType(int PictureType) {
        this.PictureType = PictureType;
    }

    public int getPictureType() {
        return PictureType;
    }

    public void setPicture(String Picture) {
        this.Picture = Picture;
    }

    public String getPicture() {
        return Picture;
    }

    public void setSort(int Sort) {
        this.Sort = Sort;
    }

    public int getSort() {
        return Sort;
    }

}
