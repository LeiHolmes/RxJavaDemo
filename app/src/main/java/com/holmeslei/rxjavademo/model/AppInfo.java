package com.holmeslei.rxjavademo.model;

import android.graphics.drawable.Drawable;

/**
 * Description:
 * author         xulei
 * Date           2017/7/20
 */

public class AppInfo {
    private String appName;
    private Drawable appIcon;

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
