package com.qiangyu.home.bean;

import com.qiangyu.shopcart.bean.JsonCartBeanData;

import java.io.Serializable;

/**
 * 商品对象
 */
public class GoodsBean implements Serializable {
    //价格
    private String cover_price;
    //图片
    private String figure;
    //名称
    private String name;
    //产品ID
    private String product_id;
    //备注
    private String remark;

    //商品属性
    private String SpecValues;

    //商品详情
    private JsonCommodityBeanData.Result result;

    //购物车
    private JsonCartBeanData.Result cartResult;

    private int number = 1;

    /**
     * 是否被选中
     */
    private boolean isSelected = true;

    public GoodsBean() {
    }

    public GoodsBean(String name, String cover_price, String figure, String product_id) {
        this.name = name;
        this.cover_price = cover_price;
        this.figure = figure;
        this.product_id = product_id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getCover_price() {
        return cover_price;
    }

    public void setCover_price(String cover_price) {
        this.cover_price = cover_price;
    }

    public String getFigure() {
        return figure;
    }

    public void setFigure(String figure) {
        this.figure = figure;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getSpecValues() {
        return SpecValues;
    }

    public void setSpecValues(String specValues) {
        SpecValues = specValues;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public JsonCommodityBeanData.Result getResult() {
        return result;
    }

    public void setResult(JsonCommodityBeanData.Result result) {
        this.result = result;
    }

    public JsonCartBeanData.Result getCartResult() {
        return cartResult;
    }

    public void setCartResult(JsonCartBeanData.Result cartResult) {
        this.cartResult = cartResult;
    }

    @Override
    public String toString() {
        return "GoodsBean{" +
                "cover_price='" + cover_price + '\'' +
                ", figure='" + figure + '\'' +
                ", name='" + name + '\'' +
                ", product_id='" + product_id + '\'' +
                ", remark='" + remark + '\'' +
                ", number=" + number +
                ", isSelected=" + isSelected +
                '}';
    }
}
