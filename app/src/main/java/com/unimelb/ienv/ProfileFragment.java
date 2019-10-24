package com.unimelb.ienv;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class ProfileFragment extends Fragment {

    LayoutInflater inflater1;
    private ImageView logoutBtn;
    private ImageView storeBtn;
    private ImageView mapBtn;
    private ImageView historyBtn;
    private TextView displayName;
    private TextView my_score;
    private de.hdodenhof.circleimageview.CircleImageView avatar;
    private FirebaseFirestore db;
    private boolean mLocationPermissionGranted = false;
    private final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1111;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater1 = inflater;
        final View rootview = inflater.inflate(R.layout.fragment_profile, null);
        int height = getHeight(getActivity());
        int width = getWidth(getActivity());
        avatar = rootview.findViewById(R.id.avatar);
        LinearLayout linearLayout1 = rootview.findViewById(R.id.linearLayout1);
        LinearLayout linearLayout2 = rootview.findViewById(R.id.linearLayout2);
        LinearLayout linearLayout3 = rootview.findViewById(R.id.linearLayout3);
        LinearLayout linearLayout4 = rootview.findViewById(R.id.linearLayout4);
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) avatar.getLayoutParams();
        lp.height = (int)(height/10);
        avatar.setLayoutParams(lp);
        lp = (ViewGroup.LayoutParams) linearLayout1.getLayoutParams();
        lp.height = (int)(height/12);
        linearLayout1.setLayoutParams(lp);
        lp = (ViewGroup.LayoutParams) linearLayout2.getLayoutParams();
        lp.height = (int)height/12;
        linearLayout2.setLayoutParams(lp);
        lp = (ViewGroup.LayoutParams) linearLayout3.getLayoutParams();
        lp.height = (int)height/12;
        linearLayout3.setLayoutParams(lp);
        lp = (ViewGroup.LayoutParams) linearLayout4.getLayoutParams();
        lp.height = (int)height/12;
        linearLayout4.setLayoutParams(lp);
        return rootview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logoutBtn = (ImageView) getView().findViewById(R.id.logoutBtn);
        storeBtn = (ImageView) getView().findViewById(R.id.storeBtn);
        mapBtn = (ImageView) getView().findViewById(R.id.mapBtn);
        historyBtn = (ImageView)getView().findViewById(R.id.itemBtn) ;
        displayName = (TextView) getView().findViewById(R.id.displayName);
        avatar = getView().findViewById(R.id.avatar);

        my_score = (TextView) getView().findViewById(R.id.my_score);
        db = FirebaseFirestore.getInstance();
        if(!mLocationPermissionGranted){
            getLocationPermission();
        }

        DocumentReference dr = db.collection("UserCollection").document(MainActivity.currentUser.getUid());
        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    String name = documentSnapshot.getString("name");
                    displayName.setText(name);
                    int avatarID = documentSnapshot.getLong("avatarId").intValue();
                    avatar.setImageResource(avatarID);
                } else{
                    Toast.makeText(getContext(), "Document does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                MainActivity.mAuth.signOut();
                Intent i = new Intent(getActivity(),MainActivity.class);
                startActivity(i);
                getActivity().finish();
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                startActivity(intent);
            }
        });

        storeBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent i = new Intent(getActivity(),ShopActivity.class);
                startActivity(i);
            }
        });

        historyBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
               Intent i = new Intent(getActivity(),OrderActivity.class);
               startActivity(i);
            }
        });

        setScore();
    }

    private void setScore(){
        DocumentReference dr = db.collection("UserCollection").document(MainActivity.currentUser.getUid());
        dr.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                long score = documentSnapshot.getLong("totalPoints");
                my_score.setText("Total Points: " + score);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        setScore();
    }

    public static int getWidth(Context mContext){
        int width=0;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.HONEYCOMB){
            Point size = new Point();
            display.getSize(size);
            width = size.x;
        }
        else{
            width = display.getWidth();  // deprecated
        }
        return width;
    }

    public static int getHeight(Context mContext){
        int height=0;
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if(Build.VERSION.SDK_INT>Build.VERSION_CODES.HONEYCOMB){
            Point size = new Point();
            display.getSize(size);
            height = size.y;
        }else{
            height = display.getHeight();  // deprecated
        }
        return height;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }
}