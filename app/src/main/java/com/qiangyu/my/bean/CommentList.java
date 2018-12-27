package com.qiangyu.my.bean;

import java.util.List;

public class CommentList {
    private Manager Manager;
    private Member Member;
    private List<String> WantCommentPics;
    private int Id;
    private String WantNo;
    private int ManagerId;
    private int MemberId;
    private int Rating;
    private String Comments;
    private String CommDT;
    private int IsShowName;

    public void setManager(Manager Manager) {
        this.Manager = Manager;
    }

    public Manager getManager() {
        return Manager;
    }

    public void setMember(Member Member) {
        this.Member = Member;
    }

    public Member getMember() {
        return Member;
    }

    public void setWantCommentPics(List<String> WantCommentPics) {
        this.WantCommentPics = WantCommentPics;
    }

    public List<String> getWantCommentPics() {
        return WantCommentPics;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public int getId() {
        return Id;
    }

    public void setWantNo(String WantNo) {
        this.WantNo = WantNo;
    }

    public String getWantNo() {
        return WantNo;
    }

    public void setManagerId(int ManagerId) {
        this.ManagerId = ManagerId;
    }

    public int getManagerId() {
        return ManagerId;
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

    public String getCommDT() {
        return CommDT;
    }

    public void setCommDT(String commDT) {
        CommDT = commDT;
    }

    public void setIsShowName(int IsShowName) {
        this.IsShowName = IsShowName;
    }

    public int getIsShowName() {
        return IsShowName;
    }
}
