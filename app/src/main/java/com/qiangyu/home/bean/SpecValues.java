package com.qiangyu.home.bean;

import java.io.Serializable;

public class SpecValues implements Serializable {

    private int GoodsId;
    private int TypeId;
    private int ValueId;
    private boolean Enabled;
    private String SpecValueName;

    public void setGoodsId(int GoodsId) {
        this.GoodsId = GoodsId;
    }

    public int getGoodsId() {
        return GoodsId;
    }

    public void setTypeId(int TypeId) {
        this.TypeId = TypeId;
    }

    public int getTypeId() {
        return TypeId;
    }

    public void setValueId(int ValueId) {
        this.ValueId = ValueId;
    }

    public int getValueId() {
        return ValueId;
    }

    public void setEnabled(boolean Enabled) {
        this.Enabled = Enabled;
    }

    public boolean getEnabled() {
        return Enabled;
    }

    public String getSpecValueName() {
        return SpecValueName;
    }

    public void setSpecValueName(String specValueName) {
        SpecValueName = specValueName;
    }
}
