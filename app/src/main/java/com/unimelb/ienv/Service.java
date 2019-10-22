package com.unimelb.ienv;

public class Service {
    private String name;
    private String type;
    private double lat;
    private double lnt;

    public Service(String name, String type, double lat, double lnt){
        this.name = name;
        this.type = type;
        this.lat = lat;
        this.lnt = lnt;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLnt() {
        return lnt;
    }

    public void setLnt(double lnt) {
        this.lnt = lnt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
