package com.dyg.siginprint.main.model;

import java.io.Serializable;

public class FunItemBean implements Serializable {
    private String title;
    private int iconName;
    private String ident;

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }

    public FunItemBean(String title, int iconName , String ident){
        this.title = title;
        this.iconName = iconName;
        this.ident = ident;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIconName() {
        return iconName;
    }

    public void setIconName(int iconName) {
        this.iconName = iconName;
    }
}
