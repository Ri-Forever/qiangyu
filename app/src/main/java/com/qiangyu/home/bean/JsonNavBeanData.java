package com.qiangyu.home.bean;

import java.util.List;

public class JsonNavBeanData {
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

        private List<Nav> Nav;
        public void setNav(List<Nav> Nav) {
            this.Nav = Nav;
        }
        public List<Nav> getNav() {
            return Nav;
        }

    }
}
