package com.qiangyu.my.bean;

import java.util.Date;

public class Record {

    private Members Members;
    private int Id;
    private int MemberId;
    private String ShopId;
    private String OperId;
    private String OperName;
    private Date TradeDT;
    private int TradeType;
    private int BeforeBal;
    private double TradeMoney;
    private double AfterBal;
    private String OrderNo;
    private String Description;
    private String TradeType_desc;
    private String TradeMoney_desc;
    private String AfterBal_desc;
    private Date TradeDT_desc;
    private boolean IsIncome;
    public void setMembers(Members Members) {
        this.Members = Members;
    }
    public Members getMembers() {
        return Members;
    }

    public void setId(int Id) {
        this.Id = Id;
    }
    public int getId() {
        return Id;
    }

    public void setMemberId(int MemberId) {
        this.MemberId = MemberId;
    }
    public int getMemberId() {
        return MemberId;
    }

    public void setShopId(String ShopId) {
        this.ShopId = ShopId;
    }
    public String getShopId() {
        return ShopId;
    }

    public void setOperId(String OperId) {
        this.OperId = OperId;
    }
    public String getOperId() {
        return OperId;
    }

    public void setOperName(String OperName) {
        this.OperName = OperName;
    }
    public String getOperName() {
        return OperName;
    }

    public void setTradeDT(Date TradeDT) {
        this.TradeDT = TradeDT;
    }
    public Date getTradeDT() {
        return TradeDT;
    }

    public void setTradeType(int TradeType) {
        this.TradeType = TradeType;
    }
    public int getTradeType() {
        return TradeType;
    }

    public void setBeforeBal(int BeforeBal) {
        this.BeforeBal = BeforeBal;
    }
    public int getBeforeBal() {
        return BeforeBal;
    }

    public void setTradeMoney(double TradeMoney) {
        this.TradeMoney = TradeMoney;
    }
    public double getTradeMoney() {
        return TradeMoney;
    }

    public void setAfterBal(double AfterBal) {
        this.AfterBal = AfterBal;
    }
    public double getAfterBal() {
        return AfterBal;
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

    public void setTradeType_desc(String TradeType_desc) {
        this.TradeType_desc = TradeType_desc;
    }
    public String getTradeType_desc() {
        return TradeType_desc;
    }

    public void setTradeMoney_desc(String TradeMoney_desc) {
        this.TradeMoney_desc = TradeMoney_desc;
    }
    public String getTradeMoney_desc() {
        return TradeMoney_desc;
    }

    public void setAfterBal_desc(String AfterBal_desc) {
        this.AfterBal_desc = AfterBal_desc;
    }
    public String getAfterBal_desc() {
        return AfterBal_desc;
    }

    public void setTradeDT_desc(Date TradeDT_desc) {
        this.TradeDT_desc = TradeDT_desc;
    }
    public Date getTradeDT_desc() {
        return TradeDT_desc;
    }

    public void setIsIncome(boolean IsIncome) {
        this.IsIncome = IsIncome;
    }
    public boolean getIsIncome() {
        return IsIncome;
    }
}
