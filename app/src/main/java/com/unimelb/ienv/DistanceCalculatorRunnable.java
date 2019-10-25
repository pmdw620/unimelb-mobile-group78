package com.unimelb.ienv;

import android.util.Log;

import com.unimelb.ienv.MapsActivity;
import com.unimelb.ienv.Service;

public class DistanceCalculatorRunnable implements Runnable {

    private double originLat;
    private double originLnt;
    private Service service;

    // initialize a runnable
    public DistanceCalculatorRunnable(double originLat, double originLnt, Service service){
        this.originLat = originLat;
        this.originLnt = originLnt;
        this.service = service;
    }

    @Override
    public void run() {
        double dist = distance(originLat, originLnt, service.getLat(), service.getLnt());
        if(dist <= 3){
            MapsActivity.nearbyServices.put(service.getName(),  service);
        }
        Log.d(String.valueOf(originLat), String.valueOf(originLnt));
        Log.d(service.getName(), String.valueOf(dist));

    }

    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515 * 1.609344;
            return dist;
        }
    }
}
