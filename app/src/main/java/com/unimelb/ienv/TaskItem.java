package com.unimelb.ienv;

public class TaskItem {
    private String desc_up;
    private String desc_down;
    private int imgId;

    public TaskItem(String up, String down, int id){
        desc_up = up;
        desc_down = down;
        this.imgId = id;
    }

    public String getUpDesc(){
        return desc_up;
    }

    public String getDownDesc(){
        return desc_down;
    }

    public int getImgId(){
        return imgId;
    }
}
