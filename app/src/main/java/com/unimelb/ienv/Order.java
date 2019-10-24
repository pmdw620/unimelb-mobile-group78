package com.unimelb.ienv;

public class Order {
    private String item;
    private String imgUrl;
    public Order(String item, String imgUrl) {
        this.item = item;
        this.imgUrl = imgUrl;
    }

    public String getItem() {
        return item;
    }
    public void setItem(String item) {
        this.item = item;
    }
    public String getImgUrl() {
        return imgUrl;
    }
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
