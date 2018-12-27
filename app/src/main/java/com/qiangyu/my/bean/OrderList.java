package com.qiangyu.my.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class OrderList implements Serializable {

    private String OrderNo;
    private int MemberId;
    private String DesignerId;
    private int OrderType;
    private String OrderTypeValue;
    private Date OrderDT;
    private String DelivaryDT;
    private String Receiver;
    private String Mobile;
    private String Address;
    private int DelivaryType;
    private int DelivaryFee;
    private int OrderBill;
    private int OrderNum;
    private int Discount;
    private int OrderFee;
    private boolean IsCoupon;
    private String CouponId;
    private int Status;
    private String StatusValue;
    private int PayType;
    private String PayOrderNo;
    private String PayDT;
    private String RefundDT;
    private String RefundNo;
    private String Remark;
    private String Description;
    private String TrackingNumber;
    private String ExpressCompany;
    private String DesignerName;
    private String DesignQQ;
    private String DesignWeixin;
    private String DesignMobile;
    private String DesignHeadPic;
    private List<Details> Details;

    public void setOrderNo(String OrderNo) {
        this.OrderNo = OrderNo;
    }

    public String getOrderNo() {
        return OrderNo;
    }

    public void setMemberId(int MemberId) {
        this.MemberId = MemberId;
    }

    public int getMemberId() {
        return MemberId;
    }

    public void setDesignerId(String DesignerId) {
        this.DesignerId = DesignerId;
    }

    public String getDesignerId() {
        return DesignerId;
    }

    public void setOrderType(int OrderType) {
        this.OrderType = OrderType;
    }

    public int getOrderType() {
        return OrderType;
    }

    public void setOrderTypeValue(String OrderTypeValue) {
        this.OrderTypeValue = OrderTypeValue;
    }

    public String getOrderTypeValue() {
        return OrderTypeValue;
    }

    public Date getOrderDT() {
        return OrderDT;
    }

    public void setOrderDT(Date orderDT) {
        OrderDT = orderDT;
    }

    public void setDelivaryDT(String DelivaryDT) {
        this.DelivaryDT = DelivaryDT;
    }

    public String getDelivaryDT() {
        return DelivaryDT;
    }

    public void setReceiver(String Receiver) {
        this.Receiver = Receiver;
    }

    public String getReceiver() {
        return Receiver;
    }

    public void setMobile(String Mobile) {
        this.Mobile = Mobile;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public String getAddress() {
        return Address;
    }

    public void setDelivaryType(int DelivaryType) {
        this.DelivaryType = DelivaryType;
    }

    public int getDelivaryType() {
        return DelivaryType;
    }

    public void setDelivaryFee(int DelivaryFee) {
        this.DelivaryFee = DelivaryFee;
    }

    public int getDelivaryFee() {
        return DelivaryFee;
    }

    public void setOrderBill(int OrderBill) {
        this.OrderBill = OrderBill;
    }

    public int getOrderBill() {
        return OrderBill;
    }

    public void setOrderNum(int OrderNum) {
        this.OrderNum = OrderNum;
    }

    public int getOrderNum() {
        return OrderNum;
    }

    public void setDiscount(int Discount) {
        this.Discount = Discount;
    }

    public int getDiscount() {
        return Discount;
    }

    public void setOrderFee(int OrderFee) {
        this.OrderFee = OrderFee;
    }

    public int getOrderFee() {
        return OrderFee;
    }

    public void setIsCoupon(boolean IsCoupon) {
        this.IsCoupon = IsCoupon;
    }

    public boolean getIsCoupon() {
        return IsCoupon;
    }

    public void setCouponId(String CouponId) {
        this.CouponId = CouponId;
    }

    public String getCouponId() {
        return CouponId;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatusValue(String StatusValue) {
        this.StatusValue = StatusValue;
    }

    public String getStatusValue() {
        return StatusValue;
    }

    public void setPayType(int PayType) {
        this.PayType = PayType;
    }

    public int getPayType() {
        return PayType;
    }

    public void setPayOrderNo(String PayOrderNo) {
        this.PayOrderNo = PayOrderNo;
    }

    public String getPayOrderNo() {
        return PayOrderNo;
    }

    public void setPayDT(String PayDT) {
        this.PayDT = PayDT;
    }

    public String getPayDT() {
        return PayDT;
    }

    public void setRefundDT(String RefundDT) {
        this.RefundDT = RefundDT;
    }

    public String getRefundDT() {
        return RefundDT;
    }

    public void setRefundNo(String RefundNo) {
        this.RefundNo = RefundNo;
    }

    public String getRefundNo() {
        return RefundNo;
    }

    public void setRemark(String Remark) {
        this.Remark = Remark;
    }

    public String getRemark() {
        return Remark;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getDescription() {
        return Description;
    }

    public void setTrackingNumber(String TrackingNumber) {
        this.TrackingNumber = TrackingNumber;
    }

    public String getTrackingNumber() {
        return TrackingNumber;
    }

    public void setExpressCompany(String ExpressCompany) {
        this.ExpressCompany = ExpressCompany;
    }

    public String getExpressCompany() {
        return ExpressCompany;
    }

    public void setDesignerName(String DesignerName) {
        this.DesignerName = DesignerName;
    }

    public String getDesignerName() {
        return DesignerName;
    }

    public void setDesignQQ(String DesignQQ) {
        this.DesignQQ = DesignQQ;
    }

    public String getDesignQQ() {
        return DesignQQ;
    }

    public void setDesignWeixin(String DesignWeixin) {
        this.DesignWeixin = DesignWeixin;
    }

    public String getDesignWeixin() {
        return DesignWeixin;
    }

    public void setDesignMobile(String DesignMobile) {
        this.DesignMobile = DesignMobile;
    }

    public String getDesignMobile() {
        return DesignMobile;
    }

    public void setDesignHeadPic(String DesignHeadPic) {
        this.DesignHeadPic = DesignHeadPic;
    }

    public String getDesignHeadPic() {
        return DesignHeadPic;
    }

    public void setDetails(List<Details> Details) {
        this.Details = Details;
    }

    public List<Details> getDetails() {
        return Details;
    }
}
