package com.unimelb.ienv;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.ListView;
import java.util.*;

public class TaskFragment extends Fragment {

    private List<TaskItem> list = new ArrayList<>();
    LayoutInflater inflater1;
    private ImageView dining_go;
    private ImageView rubbish_go;
    private ImageView quiz_go;
//    int image[] = {R.drawable.dining, R.drawable.recycle, R.drawable.quiz};
//    String[] desc_up = {"Eco IN Dining", "Eco IN Recycle", "Eco IN Quiz"};
//    String[] desc_down = {"Scan the QR code and get the eco dining score",
//            "Scan the QR code and get the score", "Answer 10 Eco quiz questsions and get 50 score"};
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inflater1 = inflater;


        final View rootview = inflater.inflate(R.layout.fragment_task, null);
        return rootview;


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dining_go = (ImageView) getView().findViewById(R.id.dining_go);
        rubbish_go = (ImageView) getView().findViewById(R.id.rubbish_go);
        quiz_go = (ImageView) getView().findViewById(R.id.quiz_go);

        dining_go.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GoToScan_dining.class);
                startActivity(intent);
            }
        });

        rubbish_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GoToScan_rubbish.class);
                startActivity(intent);
            }
        });

        // quiz_go holder!!!!
    }
}


