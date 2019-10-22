package com.unimelb.ienv;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    private Button logoutBtn;
    private Button storeBtn;
    private Button mapBtn;
    private TextView displayName;
    private de.hdodenhof.circleimageview.CircleImageView avatar;
    private FirebaseFirestore db;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logoutBtn = (Button) getView().findViewById(R.id.logoutBtn);
        storeBtn = (Button) getView().findViewById(R.id.storeBtn);
//        mapBtn = (Button) getView().findViewById(R.id.mapBtn);
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

//        mapBtn.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
////                Intent intent = new Intent(getContext(), MapActivity.class);
////                startActivity(intent);
//            }
//        });

        logoutBtn.setOnClickListener(new View.OnClickListener(){
           public void onClick(View v){
               MainActivity.mAuth.signOut();
               Intent i = new Intent(getActivity(),MainActivity.class);
               startActivity(i);
               getActivity().finish();
           }
        });
    }
}
