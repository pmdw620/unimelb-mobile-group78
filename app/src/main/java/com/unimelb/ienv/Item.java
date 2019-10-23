package com.unimelb.ienv;

public class Item {
    private String item;
    private String imgUrl;
    private int score;
    public Item(String item, String imgUrl, int score) {
        this.item = item;
        this.imgUrl = imgUrl;
        this.score = score;
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
    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score= score;
    }
}