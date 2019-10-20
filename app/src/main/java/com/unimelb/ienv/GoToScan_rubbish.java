package com.unimelb.ienv;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.unimelb.ienv.zxing.android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.unimelb.ienv.zxing.android.CaptureActivity;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.firestore.*;
import com.google.android.gms.tasks.*;
import com.google.android.gms.tasks.OnCompleteListener;


public class GoToScan_rubbish extends AppCompatActivity {
    private static final String DECODED_CONTENT_KEY = "codedContent";
    private static final String DECODED_BITMAP_KEY = "codedBitmap";
    private static final int REQUEST_CODE_SCAN = 0x0000;

    private Button btn_scan;
    private TextView tv_scanResult;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth auth;
    SQLiteDatabase sqlDatabase;
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_go_to_scan_rubbish);
        tv_scanResult = (TextView) findViewById(R.id.tv_scanResult);
        btn_scan = (Button) findViewById(R.id.btn_scan);




    }
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:
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
                if (content == "complete"){
                    String username=FirebaseAuth.getInstance().getCurrentUser().getUid();
                    SQLiteOpenHelper dbHelper = new TaskDBOpener(this);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    TaskDBModel task = new TaskDBModel();
                    task.setRubbish(1);
                    int id1 = 1;
                    db.update(TaskDBModel.TABLE_NAME, task.toContentValues(),"id = ?", new String[]{String.valueOf(id1)});
                    Cursor cursor = db.rawQuery("select * from TaskCompleter ",
                            null);
                    while (cursor.moveToNext()) {
                        String id = cursor.getString(0);
                        String rubbish = cursor.getString(1);
                        String dining = cursor.getString(2);
                        String walk = cursor.getString(3);
                        String quiz = cursor.getString(4);
                        System.out.println("query--->" + id + "," + rubbish + "," + dining+","+walk+","+quiz);//输出数据
                    }
                }

                //返回的BitMap图像
                Bitmap bitmap = data.getParcelableExtra(DECODED_BITMAP_KEY);

                tv_scanResult.setText("你扫描到的内容是：" + content);
            }
        }
    }

}
