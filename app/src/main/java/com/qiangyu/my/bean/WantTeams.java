package com.qiangyu.my.bean;

import java.util.Date;

public class WantTeams {

    private int Id;
    private String WantNo;
    private int ManagerId;
    private String Info;
    private Date JoinDT;
    private String Manager;
    private String ManagerMobile;

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

    public void setInfo(String Info) {
        this.Info = Info;
    }

    public String getInfo() {
        return Info;
    }

    public void setJoinDT(Date JoinDT) {
        this.JoinDT = JoinDT;
    }

    public Date getJoinDT() {
        return JoinDT;
    }

    public void setManager(String Manager) {
        this.Manager = Manager;
    }

    public String getManager() {
        return Manager;
    }

    public void setManagerMobile(String ManagerMobile) {
        this.ManagerMobile = ManagerMobile;
    }

    public String getManagerMobile() {
        return ManagerMobile;
    }
}
