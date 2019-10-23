package com.unimelb.ienv;

import android.content.Context;
import android.content.Intent;
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
    private TextView displayName;
    private de.hdodenhof.circleimageview.CircleImageView avatar;
    private FirebaseFirestore db;


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
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) avatar.getLayoutParams();
        lp.height = (int)(height/8);
        avatar.setLayoutParams(lp);
        lp = (ViewGroup.LayoutParams) linearLayout1.getLayoutParams();
        lp.height = (int)(height/8);
        linearLayout1.setLayoutParams(lp);
        lp = (ViewGroup.LayoutParams) linearLayout2.getLayoutParams();
        lp.height = height/8;
        linearLayout2.setLayoutParams(lp);
        lp = (ViewGroup.LayoutParams) linearLayout3.getLayoutParams();
        lp.height = height/8;
        linearLayout3.setLayoutParams(lp);
        return rootview;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logoutBtn = (ImageView) getView().findViewById(R.id.logoutBtn);
        storeBtn = (ImageView) getView().findViewById(R.id.storeBtn);
        mapBtn = (ImageView) getView().findViewById(R.id.mapBtn);
        displayName = (TextView) getView().findViewById(R.id.displayName);
        avatar = getView().findViewById(R.id.avatar);
        db = FirebaseFirestore.getInstance();

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
}