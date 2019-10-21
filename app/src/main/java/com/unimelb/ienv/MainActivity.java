package com.unimelb.ienv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, BottomNavigationView.OnNavigationItemSelectedListener{
    private Button regiBtn;
    private Button loginBtn;
    private Button forgotBtn;
    private EditText loginEmail;
    private EditText loginPwd;
    public static FirebaseAuth mAuth;
    public static FirebaseUser currentUser;

    private BindService bindService;
    private TextView textView;
    private boolean isBind;
    SQLiteDatabase sqlDatabase;
    TaskDBModel task = new TaskDBModel();
    private int initstepcount =0;

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()){
            case R.id.navigation_profile:
                fragment = new ProfileFragment();
                textView.setVisibility(View.INVISIBLE);
                break;

            case R.id.navigation_task:
                fragment = new TaskFragment();
                textView.setVisibility(View.VISIBLE);
                break;

            case R.id.navigation_leadingboard:
                fragment = new LeadingBoardFragment();
                textView.setVisibility(View.INVISIBLE);
                break;

            case R.id.navigation_home:
                fragment = new HomeFragment();
                textView.setVisibility(View.INVISIBLE);
                break;
        }

        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment){
        if(fragment!=null){

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

            return true;
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        // check if user is signed in (non-null)
        currentUser = mAuth.getCurrentUser();

        loginBtn = (Button) findViewById(R.id.loginBtn);
        forgotBtn = (Button) findViewById(R.id.forgotBtn);
        regiBtn = (Button) findViewById(R.id.regiBtn);
        loginEmail = (EditText) findViewById(R.id.loginEmail);
        loginPwd = (EditText) findViewById(R.id.loginPwd);
        regiBtn.setOnClickListener(this);
        loginBtn.setOnClickListener(this);
        forgotBtn.setOnClickListener(this);

        // check if user is logged in or not
        updateUI(currentUser);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.regiBtn:{
                Intent intent = new Intent(this, RegisterActivity.class);
                this.startActivity(intent);
                break;
            }
            case R.id.loginBtn:{
                // perform login authentication function
                mAuth.signInWithEmailAndPassword(loginEmail.getText().toString(), loginPwd.getText().toString())
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    currentUser = mAuth.getCurrentUser();
                                    Toast.makeText(MainActivity.this, "Log in button clicked", Toast.LENGTH_SHORT).show();
                                    updateUI(currentUser);
                                } else{
                                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
            }
            case R.id.forgotBtn:{
                // perform find lost password function
                Intent intent = new Intent(this, ForgotPwdActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.button_rubbish:{
                Intent intent = new Intent(this, GoToScan_rubbish.class);
                startActivity(intent);
                break;
            }
            case R.id.button_dining:{
                Intent intent = new Intent(this, GoToScan_dining.class);
                startActivity(intent);
                break;
            }

        }
    }

    public void updateUI(FirebaseUser user){
        if(user!=null){
            setContentView(R.layout.activity_dashboard);
            textView = (TextView) findViewById(R.id.busu);
            textView.setVisibility(View.INVISIBLE);
            BottomNavigationView navView = findViewById(R.id.nav_view);

            // set default to home fragment
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            navView.setOnNavigationItemSelectedListener(this);
            SQLiteOpenHelper dbHelper = new TaskDBOpener(this);
            sqlDatabase = dbHelper.getWritableDatabase();
            Cursor cursor = sqlDatabase.rawQuery("select * from TaskCompleter ",
                    null);
            int id=1,dining=0,walk=0,quiz=0,rubbish = 0;
            while (cursor.moveToNext()) {
                id = Integer.parseInt(cursor.getString(0));
                rubbish = Integer.parseInt(cursor.getString(1));
                dining = Integer.parseInt(cursor.getString(2));
                walk = Integer.parseInt(cursor.getString(3));
                quiz = Integer.parseInt(cursor.getString(4));
            }
            textView.setText(walk + "");
            Log.i("dashboard—init","当前步数"+walk);
            this.initstepcount= walk;

            Intent intent = new Intent(MainActivity.this, BindService.class);
            isBind =  bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            startService(intent); //绷定并且开启一个服务，绷定是为了方便数据交换，启动是为了当当前app不在活动页的时候，计步服务不会被关闭。需要保证当activity不为活跃状态是计步服务在后台能一直运行！

        }
    }
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                int res= msg.arg1+initstepcount;
                textView.setText(res + "");
                Cursor cursor = sqlDatabase.rawQuery("select * from TaskCompleter ",
                        null);
                int id=1,dining=0,walk=0,quiz=0,rubbish = 0;
                while (cursor.moveToNext()) {
                    id = Integer.parseInt(cursor.getString(0));
                    rubbish = Integer.parseInt(cursor.getString(1));
                    dining = Integer.parseInt(cursor.getString(2));
                    walk = Integer.parseInt(cursor.getString(3));
                    quiz = Integer.parseInt(cursor.getString(4));
                }
                task.setDining(dining);
                task.setQuiz(quiz);
                task.setWalk(res);
                task.setRubbish(rubbish);
                sqlDatabase.update(TaskDBModel.TABLE_NAME, task.toContentValues(),"id = ?", new String[]{String.valueOf(id)});
                System.out.println("query--->" + id + "," + rubbish + "," + dining+","+walk+","+quiz);//输出数据
            }
            return false;
        }
    });
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BindService.LcBinder lcBinder = (BindService.LcBinder) service;
            bindService = lcBinder.getService();
            bindService.registerCallback(new UpdateUiCallBack() {
                @Override
                public void updateUi(int stepCount) {
                    //当前接收到stepCount数据，就是最新的步数
                    Message message = Message.obtain();
                    message.what = 1;
                    message.arg1 = stepCount;
                    handler.sendMessage(message);
                    Log.i("dashboard—updateUi","当前步数"+stepCount);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onStart() {
        super.onStart();
    }
    @Override
    public void onDestroy() {  //app被关闭之前，service先解除绑定，如果不解除绑定下次Activity切换到活动界面的时候又会重新开启一个新的计步线程。
        super.onDestroy();
        if (isBind) {
            this.unbindService(serviceConnection);
        }
    }
}

