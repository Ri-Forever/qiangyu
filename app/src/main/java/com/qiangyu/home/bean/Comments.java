package com.qiangyu.home.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Comments implements Serializable {

    private String OrderNo;
    private int SKUId;
    private int GoodsId;
    private String SKU_SpecDesc;
    private String GoodsName;
    private int MemberId;
    private int Rating;
    private String Comments;
    private Date CommDT;
    private int IsShowName;
    private String UserName;
    private String HeadPic;
    private List<String> CommentPics;
    public void setOrderNo(String OrderNo) {
        this.OrderNo = OrderNo;
    }
    public String getOrderNo() {
        return OrderNo;
    }

    public void setSKUId(int SKUId) {
        this.SKUId = SKUId;
    }
    public int getSKUId() {
        return SKUId;
    }

    public void setGoodsId(int GoodsId) {
        this.GoodsId = GoodsId;
    }
    public int getGoodsId() {
        return GoodsId;
    }

    public void setSKU_SpecDesc(String SKU_SpecDesc) {
        this.SKU_SpecDesc = SKU_SpecDesc;
    }
    public String getSKU_SpecDesc() {
        return SKU_SpecDesc;
    }

    public void setGoodsName(String GoodsName) {
        this.GoodsName = GoodsName;
    }
    public String getGoodsName() {
        return GoodsName;
    }

    public void setMemberId(int MemberId) {
        this.MemberId = MemberId;
    }
    public int getMemberId() {
        return MemberId;
    }

    public void setRating(int Rating) {
        this.Rating = Rating;
    }
    public int getRating() {
        return Rating;
    }

    public void setComments(String Comments) {
        this.Comments = Comments;
    }
    public String getComments() {
        return Comments;
    }

    public Date getCommDT() {
        return CommDT;
    }

    public void setCommDT(Date commDT) {
        CommDT = commDT;
    }

    public void setIsShowName(int IsShowName) {
        this.IsShowName = IsShowName;
    }
    public int getIsShowName() {
        return IsShowName;
    }

    public void setUserName(String UserName) {
        this.UserName = UserName;
    }
    public String getUserName() {
        return UserName;
    }

    public void setHeadPic(String HeadPic) {
        this.HeadPic = HeadPic;
    }
    public String getHeadPic() {
        return HeadPic;
    }

    public void setCommentPics(List<String> CommentPics) {
        this.CommentPics = CommentPics;
    }
    public List<String> getCommentPics() {
        return CommentPics;
    }
}
