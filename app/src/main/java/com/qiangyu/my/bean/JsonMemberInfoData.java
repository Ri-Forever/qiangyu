package com.qiangyu.my.bean;

import java.util.List;

public class JsonMemberInfoData {

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

        private List<MemberInfo> MemberInfo;

        public void setMemberInfo(List<MemberInfo> MemberInfo) {
            this.MemberInfo = MemberInfo;
        }

        public List<MemberInfo> getMemberInfo() {
            return MemberInfo;
        }

    }
}