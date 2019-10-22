package com.unimelb.ienv;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
import android.widget.Toast;

import java.util.*;

public class TaskFragment extends Fragment {

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
        updateImage();
        dining_go = (ImageView) getView().findViewById(R.id.dining_go);
        rubbish_go = (ImageView) getView().findViewById(R.id.rubbish_go);
        quiz_go = (ImageView) getView().findViewById(R.id.quiz_go);

        dining_go.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int dining = getData()[1];
                if(dining >= 8){
                    Toast.makeText(getContext(), "Reach Today's Limit!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(getActivity(), GoToScan_dining.class);
                    startActivity(intent);
                }
            }
        });

        rubbish_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rubbish = getData()[0];
                if(rubbish >= 5){
                    Toast.makeText(getContext(), "Reach Today's Limit!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(getActivity(), GoToScan_rubbish.class);
                    startActivity(intent);
                }
            }
        });

        // quiz_go holder!!!!
    }
    public int[] getData(){
        SQLiteOpenHelper dbHelper = new TaskDBOpener(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        TaskDBModel task = new TaskDBModel();

        Cursor cursor = db.rawQuery("select * from TaskCompleter ",
                null);
        int id=1,dining=0,walk=0,quiz=0,rubbish = 0;
//                db.update(TaskDBModel.TABLE_NAME, task.toContentValues(),"id = ?", new String[]{String.valueOf(id)});
        while (cursor.moveToNext()) {
            id = Integer.parseInt(cursor.getString(0));
            rubbish = Integer.parseInt(cursor.getString(1));
            dining = Integer.parseInt(cursor.getString(2));
            walk = Integer.parseInt(cursor.getString(3));
            quiz = Integer.parseInt(cursor.getString(4));
        }
        int returnNumber[] = new int[2];
        returnNumber[0] = rubbish;
        returnNumber[1] = dining;
        return returnNumber;
    }

    public void updateImage(){
        int rubbish = getData()[0];
        int dining = getData()[1];
        if (rubbish>=5){
            ImageView image1 =getView().findViewById(R.id.rubbish_go);
            image1.setImageResource(R.drawable.gogrey);

        }
        if (dining>=8){
            ImageView image1 =getView().findViewById(R.id.dining_go);
            image1.setImageResource(R.drawable.gogrey);

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateImage();
    }
}
