package com.qiangyu.home.bean;

import java.io.Serializable;

public class SpecTypes implements Serializable {

    private int Id;
    private String Name;
    private int ShopId;
    public void setId(int Id) {
        this.Id = Id;
    }
    public int getId() {
        return Id;
    }

    public void setName(String Name) {
        this.Name = Name;
    }
    public String getName() {
        return Name;
    }

    public void setShopId(int ShopId) {
        this.ShopId = ShopId;
    }
    public int getShopId() {
        return ShopId;
    }
}
