package com.holmeslei.rxjavademo.model;

import java.util.List;

/**
 * Description:
 * author         xulei
 * Date           2017/7/4
 */

public class Community {
    private String communityName;
    private List<House> houses;

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
