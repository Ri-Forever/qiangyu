package com.qiangyu.home.bean;

public class ShopList {

    private int Id;
    private String ParentId;
    private String Code;
    private String Name;
    private String ShortName;
    private String Contact;
    private String Tel;
    private String Address;
    private int Sort;
    private boolean Enabled;
    private String Lng;
    private String Lat;
    private String Range;

    public void setId(int Id) {
        this.Id = Id;
    }

    public int getId() {
        return Id;
    }

    public void setParentId(String ParentId) {
        this.ParentId = ParentId;
    }

    public String getParentId() {
        return ParentId;
    }

    public void setCode(String Code) {
        this.Code = Code;
    }

    public String getCode() {
        return Code;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getName() {
        return Name;
    }

    public void setShortName(String ShortName) {
        this.ShortName = ShortName;
    }

    public String getShortName() {
        return ShortName;
    }

    public void setContact(String Contact) {
        this.Contact = Contact;
    }

    public String getContact() {
        return Contact;
    }

    public void setTel(String Tel) {
        this.Tel = Tel;
    }

    public String getTel() {
        return Tel;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public String getAddress() {
        return Address;
    }

    public void setSort(int Sort) {
        this.Sort = Sort;
    }

    public int getSort() {
        return Sort;
    }

    public void setEnabled(boolean Enabled) {
        this.Enabled = Enabled;
    }

    public boolean getEnabled() {
        return Enabled;
    }

    public void setLng(String Lng) {
        this.Lng = Lng;
    }

    public String getLng() {
        return Lng;
    }

    public void setLat(String Lat) {
        this.Lat = Lat;
    }

    public String getLat() {
        return Lat;
    }

    public void setRange(String Range) {
        this.Range = Range;
    }

    public String getRange() {
        return Range;
    }
}
