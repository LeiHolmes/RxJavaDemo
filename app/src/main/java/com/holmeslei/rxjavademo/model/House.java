package com.holmeslei.rxjavademo.model;

/**
 * Description:
 * author         xulei
 * Date           2017/7/4
 */

public class House {
    private float size;
    private int floor;
    private int price;
    private String decoration;

    public House() {
    }

    public House(float size, int floor, int price, String decoration) {
        this.size = size;
        this.floor = floor;
        this.price = price;
        this.decoration = decoration;
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

    @Override
    public String toString() {
        return "House{" +
                "size=" + size +
                ", floor=" + floor +
                ", price=" + price +
                ", decoration='" + decoration + '\'' +
                '}';
    }
}
