package com.qiangyu.shopcart.bean;

public class CartList {
    private int ShopId;
    private int MemberId;
    private int SKU;
    private double Num;
    private int Price;
    private double SalePrice1;
    private double SalePrice3;
    private double SalePrice2;
    private int Discount;
    private String Desc;
    private boolean IsSelected;
    private int GoodsId;
    private String GoodsName;
    private String Picture;
    private String SpecValues;
    private String SpecValuesDesc;
    private String OrderNo;
    private String Description;
    private int Divisive;
    private int Amount;
    private int TeamAmount;
    /**
     * 是否被选中
     */
    private boolean isSelected = true;

    public boolean isSelected() {
        return IsSelected;
    }

    public void setSelected(boolean selected) {
        IsSelected = selected;
    }

    public void setShopId(int ShopId) {
        this.ShopId = ShopId;
    }

    public int getShopId() {
        return ShopId;
    }

    public void setMemberId(int MemberId) {
        this.MemberId = MemberId;
    }

    public int getMemberId() {
        return MemberId;
    }

    public void setSKU(int SKU) {
        this.SKU = SKU;
    }

    public int getSKU() {
        return SKU;
    }

    public void setNum(double Num) {
        this.Num = Num;
    }

    public double getNum() {
        return Num;
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

    public void setSalePrice3(double SalePrice3) {
        this.SalePrice3 = SalePrice3;
    }

    public double getSalePrice3() {
        return SalePrice3;
    }

    public void setSalePrice2(double SalePrice2) {
        this.SalePrice2 = SalePrice2;
    }

    public double getSalePrice2() {
        return SalePrice2;
    }

    public void setDiscount(int Discount) {
        this.Discount = Discount;
    }

    public int getDiscount() {
        return Discount;
    }

    public void setDesc(String Desc) {
        this.Desc = Desc;
    }

    public String getDesc() {
        return Desc;
    }

    public void setIsSelected(boolean IsSelected) {
        this.IsSelected = IsSelected;
    }

    public boolean getIsSelected() {
        return IsSelected;
    }

    public void setGoodsId(int GoodsId) {
        this.GoodsId = GoodsId;
    }

    public int getGoodsId() {
        return GoodsId;
    }

    public void setGoodsName(String GoodsName) {
        this.GoodsName = GoodsName;
    }

    public String getGoodsName() {
        return GoodsName;
    }

    public void setPicture(String Picture) {
        this.Picture = Picture;
    }

    public String getPicture() {
        return Picture;
    }

    public void setSpecValues(String SpecValues) {
        this.SpecValues = SpecValues;
    }

    public String getSpecValues() {
        return SpecValues;
    }

    public void setSpecValuesDesc(String SpecValuesDesc) {
        this.SpecValuesDesc = SpecValuesDesc;
    }

    public String getSpecValuesDesc() {
        return SpecValuesDesc;
    }

    public void setOrderNo(String OrderNo) {
        this.OrderNo = OrderNo;
    }

    public String getOrderNo() {
        return OrderNo;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getDescription() {
        return Description;
    }

    public void setDivisive(int Divisive) {
        this.Divisive = Divisive;
    }

    public int getDivisive() {
        return Divisive;
    }

    public void setAmount(int Amount) {
        this.Amount = Amount;
    }

    public int getAmount() {
        return Amount;
    }

    public void setTeamAmount(int TeamAmount) {
        this.TeamAmount = TeamAmount;
    }

    public int getTeamAmount() {
        return TeamAmount;
    }
}
