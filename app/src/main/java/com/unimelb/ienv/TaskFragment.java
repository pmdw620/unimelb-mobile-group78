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

import android.content.Intent;
import android.widget.ImageView;
import android.content.Context;
import android.view.WindowManager;
import android.view.Display;
import android.os.Build;
import android.graphics.Point;
import android.widget.LinearLayout;

import android.widget.Toast;

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

        int height = getHeight(getActivity());
        int width = getWidth(getActivity());
        LinearLayout linearLayout1 = rootview.findViewById(R.id.linearLayout1);
        LinearLayout linearLayout2 = rootview.findViewById(R.id.linearLayout1);
        LinearLayout linearLayout3 = rootview.findViewById(R.id.linearLayout2);
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) linearLayout1.getLayoutParams();
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
        updateImage();
        dining_go = (ImageView) getView().findViewById(R.id.dining_go);
        rubbish_go = (ImageView) getView().findViewById(R.id.storeBtn);
        quiz_go = (ImageView) getView().findViewById(R.id.logoutBtn);

        dining_go.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                int dining = getData()[1];
                if(dining >= 8){
                    Toast.makeText(getContext(), "Reach Today's Limit: 8 points!", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getContext(), "Reach Today's Limit: 5 points!", Toast.LENGTH_SHORT).show();
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
            ImageView image1 =getView().findViewById(R.id.storeBtn);
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
