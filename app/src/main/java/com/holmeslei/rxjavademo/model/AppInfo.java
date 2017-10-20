package com.holmeslei.rxjavademo.model;

import android.graphics.drawable.Drawable;

/**
 * Description:   应用实体
 * author         xulei
 * Date           2017/7/20
 */

public class AppInfo {
    private String appName; //应用名称
    private Drawable appIcon; //应用图标

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                "appName='" + appName + '\'' +
                ", appIcon=" + appIcon +
                '}';
    }
}
