package com.qiangyu.my.bean;

import java.io.Serializable;

public class Details implements Serializable {

    private String OrderNo;
    private int SKU;
    private int GoodsId;
    private int Price;
    private double SalePrice1;
    private double SalePrice3;
    private double SalePrice2;
    private int Num;
    private int Amount;
    private String SpecValues;
    private String SpecValues_Desc;
    private String Picture;
    private boolean SKUDisabled;
    private String Description;
    private boolean Disabled;
    private int Divisive;
    private boolean IsEvaluated;
    private String DraftPath;
    private String ImgPath;
    private String Remark;
    private int CategoryId;
    private String CategoryName;
    private String GoodsCode;
    private String GoodsName;
    private String GoodsRemark;

    public void setOrderNo(String OrderNo) {
        this.OrderNo = OrderNo;
    }

    public String getOrderNo() {
        return OrderNo;
    }

    public void setSKU(int SKU) {
        this.SKU = SKU;
    }

    public int getSKU() {
        return SKU;
    }

    public void setGoodsId(int GoodsId) {
        this.GoodsId = GoodsId;
    }

    public int getGoodsId() {
        return GoodsId;
    }

    public void setPrice(int Price) {
        this.Price = Price;
    }

    public int getPrice() {
        return Price;
    }

    public double getSalePrice1() {
        return SalePrice1;
    }

    public void setSalePrice1(double salePrice1) {
        SalePrice1 = salePrice1;
    }

    public double getSalePrice3() {
        return SalePrice3;
    }

    public void setSalePrice3(double salePrice3) {
        SalePrice3 = salePrice3;
    }

    public double getSalePrice2() {
        return SalePrice2;
    }

    public void setSalePrice2(double salePrice2) {
        SalePrice2 = salePrice2;
    }

    public void setNum(int Num) {
        this.Num = Num;
    }

    public int getNum() {
        return Num;
    }

    public void setAmount(int Amount) {
        this.Amount = Amount;
    }

    public int getAmount() {
        return Amount;
    }

    public void setSpecValues(String SpecValues) {
        this.SpecValues = SpecValues;
    }

    public String getSpecValues() {
        return SpecValues;
    }

    public void setSpecValues_Desc(String SpecValues_Desc) {
        this.SpecValues_Desc = SpecValues_Desc;
    }

    public String getSpecValues_Desc() {
        return SpecValues_Desc;
    }

    public void setPicture(String Picture) {
        this.Picture = Picture;
    }

    public String getPicture() {
        return Picture;
    }

    public void setSKUDisabled(boolean SKUDisabled) {
        this.SKUDisabled = SKUDisabled;
    }

    public boolean getSKUDisabled() {
        return SKUDisabled;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getDescription() {
        return Description;
    }

    public void setDisabled(boolean Disabled) {
        this.Disabled = Disabled;
    }

    public boolean getDisabled() {
        return Disabled;
    }

    public void setDivisive(int Divisive) {
        this.Divisive = Divisive;
    }

    public int getDivisive() {
        return Divisive;
    }

    public void setIsEvaluated(boolean IsEvaluated) {
        this.IsEvaluated = IsEvaluated;
    }

    public boolean getIsEvaluated() {
        return IsEvaluated;
    }

    public void setDraftPath(String DraftPath) {
        this.DraftPath = DraftPath;
    }

    public String getDraftPath() {
        return DraftPath;
    }

    public void setImgPath(String ImgPath) {
        this.ImgPath = ImgPath;
    }

    public String getImgPath() {
        return ImgPath;
    }

    public void setRemark(String Remark) {
        this.Remark = Remark;
    }

    public String getRemark() {
        return Remark;
    }

    public void setCategoryId(int CategoryId) {
        this.CategoryId = CategoryId;
    }

    public int getCategoryId() {
        return CategoryId;
    }

    public void setCategoryName(String CategoryName) {
        this.CategoryName = CategoryName;
    }

    public String getCategoryName() {
        return CategoryName;
    }

    public void setGoodsCode(String GoodsCode) {
        this.GoodsCode = GoodsCode;
    }

    public String getGoodsCode() {
        return GoodsCode;
    }

    public void setGoodsName(String GoodsName) {
        this.GoodsName = GoodsName;
    }

    public String getGoodsName() {
        return GoodsName;
    }

    public void setGoodsRemark(String GoodsRemark) {
        this.GoodsRemark = GoodsRemark;
    }

    public String getGoodsRemark() {
        return GoodsRemark;
    }
}
