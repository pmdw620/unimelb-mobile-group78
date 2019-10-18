package com.unimelb.ienv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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

//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.util.HashMap;
//import java.util.Map;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button regiBtn;
    private Button loginBtn;
    private Button forgotBtn;
    private EditText loginEmail;
    private EditText loginPwd;
    public static FirebaseAuth mAuth;
    public static FirebaseUser currentUser;
//    private DatabaseReference mDatabase;

    List<Fragment> mFragments;
    private int lastIndex;

    private BindService bindService;
    private TextView textView;
    private boolean isBind;
    private int steptaskcount = 0;
    private NumberProgressBar bnp;

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
//                                    mDatabase = FirebaseDatabase.getInstance().getReference();
                                    Toast.makeText(MainActivity.this, "Log in button clicked", Toast.LENGTH_LONG).show();
                                    updateUI(currentUser);
                                } else{
                                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
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
        }
    }
    public void initData() {
        mFragments = new ArrayList<>();
        mFragments.add(new HomeFragment());
        mFragments.add(new TaskFragment());
        mFragments.add(new LeadingBoardFragment());
        mFragments.add(new ProfileFragment());
        // 初始化展示MessageFragment
        setFragmentPosition(0);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Log.d("123", "onNavigationItemSelected is click: ");
            switch (item.getItemId()) {

                case R.id.navigation_home:
                    Log.d("dashboard", "R.id.navigation_home: ");
                    setFragmentPosition(0);
                    break;
                case R.id.navigation_task:
                    Log.d("dashboard", "R.id.navigation_dashboard: ");
                    setFragmentPosition(1);
                    break;
                case R.id.navigation_leadingboard:
                    Log.d("dashboard", "R.id.leading_board: ");
                    setFragmentPosition(2);
                    break;
                case R.id.navigation_profile:
                    Log.d("dashboard", "R.id.navigation_me: ");
                    setFragmentPosition(3);
                    break;
            }
            Log.d("dashboard", "xxxxx ");
            return true;
        }
    };
    private void setFragmentPosition(int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment currentFragment = mFragments.get(position);
        Fragment lastFragment = mFragments.get(lastIndex);
        lastIndex = position;
        ft.hide(lastFragment);
        if (!currentFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().remove(currentFragment).commit();
            ft.add(R.id.fragment_container, currentFragment);
        }
        ft.show(currentFragment);
        ft.commitAllowingStateLoss();

    }
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                textView.setText(msg.arg1 + "");
            }
            TextView a = (TextView)mFragments.get(1).getView().findViewById(R.id.bushu);
            bnp = (NumberProgressBar)mFragments.get(1).getView().findViewById(R.id.pb_update_progress);
            TextView b = (TextView)mFragments.get(1).getView().findViewById(R.id.currentscore);
            if(msg.arg1 > 30 && steptaskcount == 0 ) {
                Log.d("handler","Complete task of running");
                steptaskcount = 1 ;
                String uid = currentUser.getUid();
                int current = Integer.parseInt((b.getText().toString()))+1;
                Log.d("handler","set b as"+current);
                b.setText(current + "");
            }

//            if(b!=null)
//            {
//
//                Log.d("handler","current"+Integer.parseInt((b.getText().toString()))+1);
//            }
            if(a!=null){
                a.setText(msg.arg1 + "");
                int res = msg.arg1;
                if (res<100){
                    bnp.setProgress(res);

                }
                else {
                    bnp.setProgress(100);
                }
            }
            else{
                Log.d("handler", "TextView a is null");
            }
            return false;
        }
    });
    public void updateUI(FirebaseUser user){
        if(user!=null){
            setContentView(R.layout.activity_dashboard);
            BottomNavigationView navView = findViewById(R.id.nav_view);

            // set default to home fragment
            navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            initData();
            textView = (TextView) findViewById(R.id.busu);
            Intent intent = new Intent(MainActivity.this, BindService.class);
            isBind =  bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            startService(intent); //绷定并且开启一个服务，绷定是为了方便数据交换，启动是为了当当前app不在活动页的时候，计步服务不会被关闭。需要保证当activity不为活跃状态是计步服务在后台能一直运行！

        }
    }
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
