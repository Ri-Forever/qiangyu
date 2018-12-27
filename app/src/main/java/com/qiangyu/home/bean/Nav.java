package com.qiangyu.home.bean;

public class Nav {
    private int Id;
    private String ShopId;
    private String ParentId;
    private String Name;
    private int Layer;
    private boolean HasChild;
    private String Path;
    private String Icon;
    private boolean Enabled;
    private boolean IsNav;
    private String Leaves;

    public void setId(int Id) {
        this.Id = Id;
    }

    public int getId() {
        return Id;
    }

    public void setShopId(String ShopId) {
        this.ShopId = ShopId;
    }

    public String getShopId() {
        return ShopId;
    }

    public void setParentId(String ParentId) {
        this.ParentId = ParentId;
    }

    public String getParentId() {
        return ParentId;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getName() {
        return Name;
    }

    public void setLayer(int Layer) {
        this.Layer = Layer;
    }

    public int getLayer() {
        return Layer;
    }

    public void setHasChild(boolean HasChild) {
        this.HasChild = HasChild;
    }

    public boolean getHasChild() {
        return HasChild;
    }

    public void setPath(String Path) {
        this.Path = Path;
    }

    public String getPath() {
        return Path;
    }

    public void setIcon(String Icon) {
        this.Icon = Icon;
    }

    public String getIcon() {
        return Icon;
    }

    public void setEnabled(boolean Enabled) {
        this.Enabled = Enabled;
    }

    public boolean getEnabled() {
        return Enabled;
    }

    public void setIsNav(boolean IsNav) {
        this.IsNav = IsNav;
    }

    public boolean getIsNav() {
        return IsNav;
    }

    public void setLeaves(String Leaves) {
        this.Leaves = Leaves;
    }

    public String getLeaves() {
        return Leaves;
    }
}
