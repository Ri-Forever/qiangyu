package com.qiangyu.home.bean;

import java.io.Serializable;

public class SKUs implements Serializable {

    private int Id;
    private int GoodsId;
    private String SpecValues;
    private int Price;
    private double SalePrice1;
    private double SalePrice2;
    private double SalePrice3;
    private boolean Disabled;

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

    public void setSpecValues(String SpecValues) {
        this.SpecValues = SpecValues;
    }

    public String getSpecValues() {
        return SpecValues;
    }

    public void setPrice(int Price) {
        this.Price = Price;
    }

    public int getPrice() {
        return Price;
    }

    public void setSalePrice1(double SalePrice1) {
        this.SalePrice1 = SalePrice1;
    }

    public double getSalePrice1() {
        return SalePrice1;
    }

    public void setSalePrice2(double SalePrice2) {
        this.SalePrice2 = SalePrice2;
    }

    public double getSalePrice2() {
        return SalePrice2;
    }

    public void setSalePrice3(double SalePrice3) {
        this.SalePrice3 = SalePrice3;
    }

    public double getSalePrice3() {
        return SalePrice3;
    }

    public void setDisabled(boolean Disabled) {
        this.Disabled = Disabled;
    }

    public boolean getDisabled() {
        return Disabled;
    }
}
