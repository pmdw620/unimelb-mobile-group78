package com.unimelb.ienv;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;
import java.util.Random;

public class HomeFragment extends Fragment {
    TextView tips1;
    TextView tips2;
    TextView today;
    TextView week;
    View rootview;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootview =   inflater.inflate(R.layout.fragment_home, null);
        today = (TextView) rootview.findViewById(R.id.todaypoints);
        week =(TextView) rootview.findViewById(R.id.weekpoints);
        tips1 = (TextView)rootview.findViewById(R.id.tips1);
        tips2 =(TextView) rootview.findViewById(R.id.tips2);
        int height = getHeight(getActivity());
        int width = getWidth(getActivity());
        LinearLayout linearLayout = rootview.findViewById(R.id.linearLayout);
        LinearLayout linearLayout1 = rootview.findViewById(R.id.linearLayout1);
        LinearLayout linearLayout2 = rootview.findViewById(R.id.linearLayout2);
        ImageView image = rootview.findViewById(R.id.imageView3);
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) linearLayout.getLayoutParams();
        lp.height = (int)(height/3);
        linearLayout.setLayoutParams(lp);
        ViewGroup.LayoutParams llp = (ViewGroup.LayoutParams) linearLayout1.getLayoutParams();
        llp.height = height/7;
        llp.width = (int)(width/1.6);
        linearLayout1.setLayoutParams(llp);
        ViewGroup.LayoutParams lllp = (ViewGroup.LayoutParams) linearLayout2.getLayoutParams();
        lllp.height = height/6;
        lllp.width = (int)(width/1.6);
        linearLayout2.setLayoutParams(lllp);
        ViewGroup.LayoutParams ip = (ViewGroup.LayoutParams) image.getLayoutParams();
        ip.width = width;
        image.setLayoutParams(ip);

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
        int total = dining+(walk/1000)+quiz+rubbish;
        System.out.println(total);
        today.setText(Integer.toString(total));
        Random rand = new Random();
        // Update data from firebase database
        int tips1_index = rand.nextInt(4);
        int tips2_index = rand.nextInt(4)+3;
        final FirebaseFirestore firedb = FirebaseFirestore.getInstance();
        FirebaseAuth auth;
        final String username=FirebaseAuth.getInstance().getCurrentUser().getUid();
        firedb.collection("UserCollection").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("test", "DocumentSnapshot data: " + document.getData());
                        Map data = document.getData();
                        Long weekpoints = (Long)data.get("currWeekPoints");
                        week.setText(weekpoints.toString());
                    } else {
                        Log.d("test", "No such document");
                    }
                } else {
                    Log.d("test", "get failed with ", task.getException());
                }
            }
        });
        firedb.collection("Tips")
                .whereEqualTo("id", tips1_index)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("test", document.getId() + " => " + document.getData());
                                tips1.setText(document.getData().get("content").toString());
                            }
                        } else {
                            Log.d("test", "Error getting documents: ", task.getException());
                        }
                    }
                });
        firedb.collection("Tips")
                .whereEqualTo("id", tips2_index)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("test", document.getId() + " => " + document.getData());
                                tips2.setText(document.getData().get("content").toString());
                            }
                        } else {
                            Log.d("test", "Error getting documents: ", task.getException());
                        }
                    }
                });

        return rootview;

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
    public void onResume() {
        super.onResume();
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
        int total = dining+(walk/1000)+quiz+rubbish;
        System.out.println(total);
        today.setText(Integer.toString(total));
        Random rand = new Random();
        // Update data from firebase database
        int tips1_index = rand.nextInt(4);
        int tips2_index = rand.nextInt(4)+3;
        final FirebaseFirestore firedb = FirebaseFirestore.getInstance();
        FirebaseAuth auth;
        final String username=FirebaseAuth.getInstance().getCurrentUser().getUid();
        firedb.collection("UserCollection").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("test", "DocumentSnapshot data: " + document.getData());
                        Map data = document.getData();
                        Long weekpoints = (Long)data.get("currWeekPoints");
                        week.setText(weekpoints.toString());
                    } else {
                        Log.d("test", "No such document");
                    }
                } else {
                    Log.d("test", "get failed with ", task.getException());
                }
            }
        });
        firedb.collection("Tips")
                .whereEqualTo("id", tips1_index)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("test", document.getId() + " => " + document.getData());
                                tips1.setText(document.getData().get("content").toString());
                            }
                        } else {
                            Log.d("test", "Error getting documents: ", task.getException());
                        }
                    }
                });
        firedb.collection("Tips")
                .whereEqualTo("id", tips2_index)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("test", document.getId() + " => " + document.getData());
                                tips2.setText(document.getData().get("content").toString());
                            }
                        } else {
                            Log.d("test", "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

}
