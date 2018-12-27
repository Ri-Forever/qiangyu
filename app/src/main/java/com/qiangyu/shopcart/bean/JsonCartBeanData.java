package com.qiangyu.shopcart.bean;

import java.util.List;

public class JsonCartBeanData {
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

        private List<CartList> CartList;
        private List<PicList> PicList;

        public void setCartList(List<CartList> CartList) {
            this.CartList = CartList;
        }

        public List<CartList> getCartList() {
            return CartList;
        }

        public void setPicList(List<PicList> PicList) {
            this.PicList = PicList;
        }

        public List<PicList> getPicList() {
            return PicList;
        }

    }
}
