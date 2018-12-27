package com.qiangyu.my.bean;

import java.util.Date;

public class PayInfo {

    private String OrderNo;
    private int TradeType;
    private int TradeMoney;
    private int MemberId;
    private Date TradeDT;
    private String TradeType_desc;
    private String TradeMoney_desc;
    private String AfterBal_desc;
    private String TradeDT_desc;

    public void setOrderNo(String OrderNo) {
        this.OrderNo = OrderNo;
    }

    public String getOrderNo() {
        return OrderNo;
    }

    public void setTradeType(int TradeType) {
        this.TradeType = TradeType;
    }

    public int getTradeType() {
        return TradeType;
    }

    public void setTradeMoney(int TradeMoney) {
        this.TradeMoney = TradeMoney;
    }

    public int getTradeMoney() {
        return TradeMoney;
    }

    public void setMemberId(int MemberId) {
        this.MemberId = MemberId;
    }

    public int getMemberId() {
        return MemberId;
    }

    public void setTradeDT(Date TradeDT) {
        this.TradeDT = TradeDT;
    }

    public Date getTradeDT() {
        return TradeDT;
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

    public void setTradeDT_desc(String TradeDT_desc) {
        this.TradeDT_desc = TradeDT_desc;
    }

    public String getTradeDT_desc() {
        return TradeDT_desc;
    }
}
