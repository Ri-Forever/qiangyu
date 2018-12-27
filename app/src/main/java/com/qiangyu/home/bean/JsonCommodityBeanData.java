package com.qiangyu.home.bean;

import java.io.Serializable;
import java.util.List;

public class JsonCommodityBeanData implements Serializable {

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

    public class Result implements Serializable {

        private List<GoodsDetail> GoodsDetail;
        private List<SKUs> SKUs;
        private List<SpecTypes> SpecTypes;
        private List<SpecValues> SpecValues;
        private List<GoodsPics> GoodsPics;
        private List<Comments> Comments;
        public void setGoodsDetail(List<GoodsDetail> GoodsDetail) {
            this.GoodsDetail = GoodsDetail;
        }
        public List<GoodsDetail> getGoodsDetail() {
            return GoodsDetail;
        }

        public void setSKUs(List<SKUs> SKUs) {
            this.SKUs = SKUs;
        }
        public List<SKUs> getSKUs() {
            return SKUs;
        }

        public void setSpecTypes(List<SpecTypes> SpecTypes) {
            this.SpecTypes = SpecTypes;
        }
        public List<SpecTypes> getSpecTypes() {
            return SpecTypes;
        }

        public void setSpecValues(List<SpecValues> SpecValues) {
            this.SpecValues = SpecValues;
        }
        public List<SpecValues> getSpecValues() {
            return SpecValues;
        }

        public void setGoodsPics(List<GoodsPics> GoodsPics) {
            this.GoodsPics = GoodsPics;
        }
        public List<GoodsPics> getGoodsPics() {
            return GoodsPics;
        }

        public void setComments(List<Comments> Comments) {
            this.Comments = Comments;
        }
        public List<Comments> getComments() {
            return Comments;
        }

    }
}
