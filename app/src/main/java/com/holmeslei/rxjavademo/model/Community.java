package com.holmeslei.rxjavademo.model;

import java.util.List;

/**
 * Description:   小区实体
 * author         xulei
 * Date           2017/7/4
 */

public class Community {
    private String communityName; //小区名称
    private List<House> houses; //房源集合

    public Community() {
    }

    public Community(String communityName, List<House> houses) {
        this.communityName = communityName;
        this.houses = houses;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    public List<House> getHouses() {
        return houses;
    }

    public void setHouses(List<House> houses) {
        this.houses = houses;
    }

    @Override
    public String toString() {
        return "Community{" +
                "communityName='" + communityName + '\'' +
                ", houses=" + houses +
                '}';
    }
}
