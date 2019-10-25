package com.unimelb.ienv;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.icu.util.TimeUnit;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static GoogleMap mMap;
    private ArrayList<Service> services;
    public static ConcurrentHashMap<String, Service> nearbyServices;
    private final int MAX_DISTANCE_CALCULATION_THREAD = 100;
    private ExecutorService threadPool = Executors.newFixedThreadPool(MAX_DISTANCE_CALCULATION_THREAD);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        services = new ArrayList<>();
        nearbyServices = new ConcurrentHashMap<>();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getServices();
    }

    private void getServices(){
        db.collection("service").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                String name = documentSnapshot.get("name").toString();
                                String type = documentSnapshot.getString("type");
                                double lat = documentSnapshot.getDouble("latitude");
                                double lnt = documentSnapshot.getDouble("longtitude");
                                Service service = new Service(name, type, lat, lnt);
                                services.add(service);
                            }
                            getNearbyServices();
                        }
                    }
                });
    }

    private void getNearbyServices(){
        // get user's location
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Location myLocation = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        double currLat = myLocation.getLatitude();
        double currLnt = myLocation.getLongitude();
        Log.d("Number of services", String.valueOf(services.size()));
        for(Service service : services){
            try{
                Log.d("NOTICED", "New runnable generating");
                Runnable distCalcRunnable = new DistanceCalculatorRunnable(currLat, currLnt, service);
                threadPool.execute(distCalcRunnable);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        // UI Thread wait till all threads in threadpool executed.
        threadPool.shutdown();
        while(!threadPool.isTerminated()){}
        markServices();
    }

    private void markServices(){
        Log.d("TEST", "Mark Service starts now! Number of Nearby Services: " + nearbyServices.size());
        for(Map.Entry<String, Service> entry: nearbyServices.entrySet()){
            Service service = entry.getValue();
            addMarker(service);
        }
    }


    public static void addMarker(Service service){
        LatLng tmp = new LatLng(service.getLat(), service.getLnt());
        Log.d("Service Marked", service.getName());
        if(service.getType().equals("restaurant")){
            MarkerOptions marker = new MarkerOptions().position(tmp).title(service.getName());
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_restaurant));
            mMap.addMarker(marker);
        } else if(service.getType().equals("rubbishbin")){
            MarkerOptions marker = new MarkerOptions().position(tmp).title(service.getName());
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_rubbishbin));
            mMap.addMarker(marker);
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-37.8107314, 144.9587798);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Da Wei"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        } else{
        }

        mMap.setMyLocationEnabled(true);
        getZoomedView();
    }

    private void getZoomedView(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));


        if (location != null)
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(14)                   // Sets the zoom
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }


}
