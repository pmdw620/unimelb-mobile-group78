package com.unimelb.ienv;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class ProfileFragment extends Fragment {

    private Button logoutBtn;
    private Button storeBtn;

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
