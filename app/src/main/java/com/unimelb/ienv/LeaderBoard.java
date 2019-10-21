package com.unimelb.ienv;

public class LeaderBoard {
    private int imageId;
    private String name;
    private int points;
    public LeaderBoard(int imageId,String name,int points){
        this.imageId = imageId;
        this.name = name;
        this.points = points;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoints() {
        return points;
    }
}
