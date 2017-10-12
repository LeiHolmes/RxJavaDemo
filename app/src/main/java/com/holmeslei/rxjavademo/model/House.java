package com.holmeslei.rxjavademo.model;

/**
 * Description:   房源实体
 * author         xulei
 * Date           2017/7/4
 */

public class House {
    private float size; //大小
    private int floor; //楼层
    private int price; //总价
    private String decoration; //装修程度
    private String communityName; //小区名称

    public House() {
    }

    public House(float size, int floor, int price, String decoration, String communityName) {
        this.size = size;
        this.floor = floor;
        this.price = price;
        this.decoration = decoration;
        this.communityName = communityName;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDecoration() {
        return decoration;
    }

    public void setDecoration(String decoration) {
        this.decoration = decoration;
    }

    public String getCommunityName() {
        return communityName;
    }

    public void setCommunityName(String communityName) {
        this.communityName = communityName;
    }

    @Override
    public String toString() {
        return "House{" +
                "size=" + size +
                ", floor=" + floor +
                ", price=" + price +
                ", decoration='" + decoration + '\'' +
                ", communityName='" + communityName + '\'' +
                '}';
    }
}
