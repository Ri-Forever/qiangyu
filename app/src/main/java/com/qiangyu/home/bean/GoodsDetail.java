package com.qiangyu.home.bean;

import java.io.Serializable;

public class GoodsDetail implements Serializable {

    private int Id;
    private int ShopId;
    private int CategoryId;
    private String Code;
    private String Name;
    private String Remark;
    private int Price;
    private String SpecTypes;
    private int CommissionType1;
    private int Commission1;
    private int CommissionType2;
    private int Commission2;
    private int CommissionType3;
    private int Commission3;
    private boolean IsHot;
    private boolean IsRecommend;
    private int Sort;
    private int Status;
    private String Description;
    private int SoldNum;
    private int SoldNum2;
    private int Divisive;
    private String SpecTypesName;
    private String Picture;
    public void setId(int Id) {
        this.Id = Id;
    }
    public int getId() {
        return Id;
    }

    public void setShopId(int ShopId) {
        this.ShopId = ShopId;
    }
    public int getShopId() {
        return ShopId;
    }

    public void setCategoryId(int CategoryId) {
        this.CategoryId = CategoryId;
    }
    public int getCategoryId() {
        return CategoryId;
    }

    public void setCode(String Code) {
        this.Code = Code;
    }
    public String getCode() {
        return Code;
    }

    public void setName(String Name) {
        this.Name = Name;
    }
    public String getName() {
        return Name;
    }

    public void setRemark(String Remark) {
        this.Remark = Remark;
    }
    public String getRemark() {
        return Remark;
    }

    public void setPrice(int Price) {
        this.Price = Price;
    }
    public int getPrice() {
        return Price;
    }

    public String getSpecTypes() {
        return SpecTypes;
    }

    public void setSpecTypes(String specTypes) {
        SpecTypes = specTypes;
    }

    public void setCommissionType1(int CommissionType1) {
        this.CommissionType1 = CommissionType1;
    }
    public int getCommissionType1() {
        return CommissionType1;
    }

    public void setCommission1(int Commission1) {
        this.Commission1 = Commission1;
    }
    public int getCommission1() {
        return Commission1;
    }

    public void setCommissionType2(int CommissionType2) {
        this.CommissionType2 = CommissionType2;
    }
    public int getCommissionType2() {
        return CommissionType2;
    }

    public void setCommission2(int Commission2) {
        this.Commission2 = Commission2;
    }
    public int getCommission2() {
        return Commission2;
    }

    public void setCommissionType3(int CommissionType3) {
        this.CommissionType3 = CommissionType3;
    }
    public int getCommissionType3() {
        return CommissionType3;
    }

    public void setCommission3(int Commission3) {
        this.Commission3 = Commission3;
    }
    public int getCommission3() {
        return Commission3;
    }

    public void setIsHot(boolean IsHot) {
        this.IsHot = IsHot;
    }
    public boolean getIsHot() {
        return IsHot;
    }

    public void setIsRecommend(boolean IsRecommend) {
        this.IsRecommend = IsRecommend;
    }
    public boolean getIsRecommend() {
        return IsRecommend;
    }

    public void setSort(int Sort) {
        this.Sort = Sort;
    }
    public int getSort() {
        return Sort;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }
    public int getStatus() {
        return Status;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }
    public String getDescription() {
        return Description;
    }

    public void setSoldNum(int SoldNum) {
        this.SoldNum = SoldNum;
    }
    public int getSoldNum() {
        return SoldNum;
    }

    public void setSoldNum2(int SoldNum2) {
        this.SoldNum2 = SoldNum2;
    }
    public int getSoldNum2() {
        return SoldNum2;
    }

    public void setDivisive(int Divisive) {
        this.Divisive = Divisive;
    }
    public int getDivisive() {
        return Divisive;
    }

    public void setSpecTypesName(String SpecTypesName) {
        this.SpecTypesName = SpecTypesName;
    }
    public String getSpecTypesName() {
        return SpecTypesName;
    }

    public void setPicture(String Picture) {
        this.Picture = Picture;
    }
    public String getPicture() {
        return Picture;
    }
}
