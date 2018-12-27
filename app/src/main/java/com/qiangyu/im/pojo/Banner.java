package com.qiangyu.im.pojo;

/**
 * Auto-generated: 2018-09-11 18:18:36
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Banner {

    private int Id;
    private String Picture;
    private String Url;
    private int Sort;
    private boolean Enabled;
    private int Type;
    private String TypeStr;
    private String Title;
    private String Content;
    private String Description;
    private String Remarks;
    private int CreateUserId;
    private String CreateDT;
    private int ModifyUserId;
    private String ModifyDT;
    private String DishPicture;
    public void setId(int Id) {
        this.Id = Id;
    }
    public int getId() {
        return Id;
    }

    public void setPicture(String Picture) {
        this.Picture = Picture;
    }
    public String getPicture() {
        return Picture;
    }

    public void setUrl(String Url) {
        this.Url = Url;
    }
    public String getUrl() {
        return Url;
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

    public void setType(int Type) {
        this.Type = Type;
    }
    public int getType() {
        return Type;
    }

    public void setTypeStr(String TypeStr) {
        this.TypeStr = TypeStr;
    }
    public String getTypeStr() {
        return TypeStr;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }
    public String getTitle() {
        return Title;
    }

    public void setContent(String Content) {
        this.Content = Content;
    }
    public String getContent() {
        return Content;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }
    public String getDescription() {
        return Description;
    }

    public void setRemarks(String Remarks) {
        this.Remarks = Remarks;
    }
    public String getRemarks() {
        return Remarks;
    }

    public void setCreateUserId(int CreateUserId) {
        this.CreateUserId = CreateUserId;
    }
    public int getCreateUserId() {
        return CreateUserId;
    }

    public void setCreateDT(String CreateDT) {
        this.CreateDT = CreateDT;
    }
    public String getCreateDT() {
        return CreateDT;
    }

    public void setModifyUserId(int ModifyUserId) {
        this.ModifyUserId = ModifyUserId;
    }
    public int getModifyUserId() {
        return ModifyUserId;
    }

    public void setModifyDT(String ModifyDT) {
        this.ModifyDT = ModifyDT;
    }
    public String getModifyDT() {
        return ModifyDT;
    }

    public void setDishPicture(String DishPicture) {
        this.DishPicture = DishPicture;
    }
    public String getDishPicture() {
        return DishPicture;
    }

    @Override
    public String toString() {
        return "Banner{" +
                "Id=" + Id +
                ", PictureAdapter='" + Picture + '\'' +
                ", Url='" + Url + '\'' +
                ", Sort=" + Sort +
                ", Enabled=" + Enabled +
                ", Type=" + Type +
                ", TypeStr='" + TypeStr + '\'' +
                ", Title='" + Title + '\'' +
                ", Content='" + Content + '\'' +
                ", Description='" + Description + '\'' +
                ", Remarks='" + Remarks + '\'' +
                ", CreateUserId=" + CreateUserId +
                ", CreateDT='" + CreateDT + '\'' +
                ", ModifyUserId=" + ModifyUserId +
                ", ModifyDT='" + ModifyDT + '\'' +
                ", DishPicture='" + DishPicture + '\'' +
                '}';
    }
}