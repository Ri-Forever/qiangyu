package com.qiangyu.my.bean;

import java.util.List;

public class JsonCommentListData {

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

        private List<CommentList> CommentList;

        public void setCommentList(List<CommentList> CommentList) {
            this.CommentList = CommentList;
        }

        public List<CommentList> getCommentList() {
            return CommentList;
        }

    }
}
