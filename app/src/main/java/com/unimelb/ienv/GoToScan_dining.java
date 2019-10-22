package com.unimelb.ienv;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.unimelb.ienv.zxing.android.CaptureActivity;

import java.util.Map;

public class GoToScan_dining extends AppCompatActivity {
    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";
    private static final int REQUEST_CODE_SCAN = 0x0000;

    private Button btn_scan;
    private TextView tv_scanResult;
    FirebaseFirestore firedb = FirebaseFirestore.getInstance();
    private FirebaseAuth auth;
    SQLiteDatabase sqlDatabase;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_to_scan_dining);
        tv_scanResult = (TextView) findViewById(R.id.tv_scanResult);
        btn_scan = (Button) findViewById(R.id.btn_scan);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dining_go:
                //动态权限申请
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
                } else {
                    goScan();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 跳转到扫码界面扫码
     */
    private void goScan(){
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SCAN);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,@NonNull  String[] permissions,@NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goScan();
                } else {
                    Toast.makeText(this, "你拒绝了权限申请，可能无法打开相机扫码哟！", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_CODE_SCAN && resultCode == RESULT_OK) {
            if (data != null) {
                //返回的文本内容　
                String content = data.getStringExtra(DECODED_CONTENT_KEY);
                System.out.println(content);
                final int value = Integer.parseInt(content);
                SQLiteOpenHelper dbHelper = new TaskDBOpener(this);
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
                final int rubbish1 = rubbish;
                task.setDining(dining);
                task.setQuiz(quiz);
                task.setWalk(walk);
                task.setRubbish(Math.min(5,rubbish+value));
                db.update(TaskDBModel.TABLE_NAME, task.toContentValues(),"id = ?", new String[]{String.valueOf(id)});
                System.out.println("query--->" + id + "," + rubbish + "," + dining+","+walk+","+quiz);//输出数据
                final String username=FirebaseAuth.getInstance().getCurrentUser().getUid();
                firedb.collection("UserCollection").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d("test", "DocumentSnapshot data: " + document.getData());
                                Map data = document.getData();
                                Long weekpoints = (Long)data.get("currWeekPoints")+(Math.min(5,rubbish1+value)-rubbish1);
                                System.out.println(weekpoints);
                                Long totalpoints = (Long)data.get("totalPoints")+(Math.min(5,rubbish1+value)-rubbish1);
                                System.out.println(totalpoints);
                                firedb.collection("UserCollection").document(username).update("currWeekPoints",weekpoints);
                                firedb.collection("UserCollection").document(username).update("totalPoints",totalpoints);
                            } else {
                                Log.d("test", "No such document");
                            }
                        } else {
                            Log.d("test", "get failed with ", task.getException());
                        }
                    }
                });

                //返回的BitMap图像
                Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);
            }
        }
    }

}
