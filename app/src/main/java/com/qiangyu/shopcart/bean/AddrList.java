package com.qiangyu.shopcart.bean;

public class AddrList {

    private int Id;
    private int ParentId;
    private int ShopId;
    private String Address;
    private boolean Enabled;
    private int Sort;
    private String Description;
    private String QiShou;
    private String AddressStr;

    private int MemberId;
    private String Contacts;
    private String Title;
    private String Mobile;
    private String LocationIds;
    private String Location;
    private String IsDefault;
    private String Lng;
    private String Lat;
    private int DAId;

    public boolean isEnabled() {
        return Enabled;
    }

    public int getMemberId() {
        return MemberId;
    }

    public void setMemberId(int memberId) {
        MemberId = memberId;
    }

    public String getContacts() {
        return Contacts;
    }

    public void setContacts(String contacts) {
        Contacts = contacts;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMobile() {
        return Mobile;
    }

    public void setMobile(String mobile) {
        Mobile = mobile;
    }

    public String getLocationIds() {
        return LocationIds;
    }

    public void setLocationIds(String locationIds) {
        LocationIds = locationIds;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public String getIsDefault() {
        return IsDefault;
    }

    public void setIsDefault(String isDefault) {
        IsDefault = isDefault;
    }

    public String getLng() {
        return Lng;
    }

    public void setLng(String lng) {
        Lng = lng;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public int getDAId() {
        return DAId;
    }

    public void setDAId(int DAId) {
        this.DAId = DAId;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public int getId() {
        return Id;
    }

    public void setParentId(int ParentId) {
        this.ParentId = ParentId;
    }

    public int getParentId() {
        return ParentId;
    }

    public void setShopId(int ShopId) {
        this.ShopId = ShopId;
    }

    public int getShopId() {
        return ShopId;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public String getAddress() {
        return Address;
    }

    public void setEnabled(boolean Enabled) {
        this.Enabled = Enabled;
    }

    public boolean getEnabled() {
        return Enabled;
    }

    public void setSort(int Sort) {
        this.Sort = Sort;
    }

    public int getSort() {
        return Sort;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getDescription() {
        return Description;
    }

    public void setQiShou(String QiShou) {
        this.QiShou = QiShou;
    }

    public String getQiShou() {
        return QiShou;
    }

    public void setAddressStr(String AddressStr) {
        this.AddressStr = AddressStr;
    }

    public String getAddressStr() {
        return AddressStr;
    }

    @Override
    public String toString() {
        return "AddrList{" +
                "Id=" + Id +
                ", ParentId=" + ParentId +
                ", ShopId=" + ShopId +
                ", Address='" + Address + '\'' +
                ", Enabled=" + Enabled +
                ", Sort=" + Sort +
                ", Description='" + Description + '\'' +
                ", QiShou='" + QiShou + '\'' +
                ", AddressStr='" + AddressStr + '\'' +
                ", MemberId=" + MemberId +
                ", Contacts='" + Contacts + '\'' +
                ", Title='" + Title + '\'' +
                ", Mobile='" + Mobile + '\'' +
                ", LocationIds='" + LocationIds + '\'' +
                ", Location='" + Location + '\'' +
                ", IsDefault=" + IsDefault +
                ", Lng='" + Lng + '\'' +
                ", Lat='" + Lat + '\'' +
                ", DAId=" + DAId +
                '}';
    }
}
