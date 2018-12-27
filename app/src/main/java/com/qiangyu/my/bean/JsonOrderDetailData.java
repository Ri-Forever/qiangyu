package com.qiangyu.my.bean;

import java.util.List;

public class JsonOrderDetailData {

    private String Code;
    private Result Result;
    private String NonceStr;
    private String TimeStamp;
    private String Sign;

    public void setCode(String Code) {
        this.Code = Code;
    }

    public String getCode() {
        return Code;
    }

    public void setResult(Result Result) {
        this.Result = Result;
    }

    public Result getResult() {
        return Result;
    }

    public void setNonceStr(String NonceStr) {
        this.NonceStr = NonceStr;
    }

    public String getNonceStr() {
        return NonceStr;
    }

    public void setTimeStamp(String TimeStamp) {
        this.TimeStamp = TimeStamp;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setSign(String Sign) {
        this.Sign = Sign;
    }

    public String getSign() {
        return Sign;
    }

    public class Result {

        private List<OrderList> OrderList;
        private List<PayInfo> PayInfo;

        public void setOrderList(List<OrderList> OrderList) {
            this.OrderList = OrderList;
        }

        public List<OrderList> getOrderList() {
            return OrderList;
        }

        public void setPayInfo(List<PayInfo> PayInfo) {
            this.PayInfo = PayInfo;
        }

        public List<PayInfo> getPayInfo() {
            return PayInfo;
        }

    }
}