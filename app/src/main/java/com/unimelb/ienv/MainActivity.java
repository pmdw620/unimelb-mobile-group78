package com.unimelb.ienv;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button regiBtn;
    private Button loginBtn;
    private Button forgotBtn;
    private EditText loginEmail;
    private EditText loginPwd;
    public static FirebaseAuth mAuth;
    public static FirebaseUser currentUser;
    private FirebaseFirestore firedb;

    List<Fragment> mFragments;
    private int lastIndex;

    private BindService bindService;
    private TextView textView;
    private boolean isBind;
    SQLiteDatabase sqlDatabase;
    TaskDBModel task = new TaskDBModel();
    private int initstepcount =0;
    public static int currentstep=0;

    private NumberProgressBar bnp;
//    private Intromanager intromanager;

    public void initData() {
        mFragments = new ArrayList<>();
        mFragments.add(new HomeFragment());
        mFragments.add(new TaskFragment());
        mFragments.add(new LeadingBoardFragment());
        mFragments.add(new ProfileFragment());

    }

    public int getinitcount(){
        return currentstep;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        intromanager = new Intromanager(this);
//        Log.i("init-intro",""+intromanager.check());
//        if (!intromanager.check()){
//            intromanager.setFirst(false);
//            Intent i = new Intent(MainActivity.this,Main2Activity.class);
//            startActivity(i);
//            finish();
//        }
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
        initData();

        // check if user is logged in or not
        updateUI(currentUser);

    }
    public void updateTime(){
        final String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        final FirebaseFirestore firedb = FirebaseFirestore.getInstance();
        final String username=FirebaseAuth.getInstance().getCurrentUser().getUid();
        long time=System.currentTimeMillis();
        Date date=new Date(time);
        SimpleDateFormat format=new SimpleDateFormat("E");
        final String weekday = format.format(new Date());
        Log.e("time","time6="+weekday);

        firedb.collection("UserCollection").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("test", "DocumentSnapshot data: " + document.getData());
                        Map data = document.getData();
                        Long lastLogin = (Long) data.get("continueLogin");
                        String lastDate = data.get("lastLoginTime").toString();

                        if (lastDate.equals(currentDate)){
                            Log.d("test", "Same day!");
                        }
                        else {
                            lastDate = currentDate;
                            lastLogin +=1;
                            firedb.collection("UserCollection").document(username).update("continueLogin",lastLogin);
                            firedb.collection("UserCollection").document(username).update("lastLoginTime",lastDate);
                            SQLiteOpenHelper dbHelper = new TaskDBOpener(getApplicationContext());
                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            TaskDBModel updatetask = new TaskDBModel();
                            Long weekpoints = (Long)data.get("currWeekPoints");
                            Long totalpoints = (Long)data.get("totalPoints");
                            int id = 1;
                            db.update(TaskDBModel.TABLE_NAME, updatetask.toContentValues(),"id = ?", new String[]{String.valueOf(id)});
                            TextView tv = findViewById(R.id.todaypoints);
                            tv.setText("0");
                            if(weekday.contains("Mon")){
                                firedb.collection("UserCollection").document(username).update("currWeekPoints",0);
                                weekpoints = weekpoints-weekpoints;
                            }
                            if (lastLogin%5==0){
                                firedb.collection("UserCollection").document(username).update("currWeekPoints",weekpoints+5);
                                weekpoints = weekpoints+5;
                                firedb.collection("UserCollection").document(username).update("totalPoints",totalpoints+5);
                                Toast toast = Toast.makeText(getApplicationContext(), "Login "+lastLogin+" Days ! Earned extra 5 points", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER,0,0);
                                LinearLayout toastView = (LinearLayout) toast.getView();
                                toastView.setGravity(Gravity.CENTER);
                                toastView.setPadding(0,20,0,0);
                                ImageView imageCodeProject = new ImageView(getApplicationContext());
                                imageCodeProject.setImageResource(R.drawable.congrat);
                                toastView.addView(imageCodeProject, 0);
                                ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) imageCodeProject.getLayoutParams();
                                lp.height = 125;
                                lp.width=125;
                                imageCodeProject.setLayoutParams(lp);

                                toast.show();
                            }
                            else{
                                Toast toast = Toast.makeText(getApplicationContext(), "Congrats for a new day! You have continued logged in for " + lastLogin + " day(s)！", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER,0,0);
                                LinearLayout toastView = (LinearLayout) toast.getView();
                                toastView.setGravity(Gravity.CENTER);
                                toastView.setPadding(0,20,0,0);
                                ImageView imageCodeProject = new ImageView(getApplicationContext());
                                imageCodeProject.setImageResource(R.drawable.congrat);
                                toastView.addView(imageCodeProject, 0);
                                ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) imageCodeProject.getLayoutParams();
                                lp.height = 125;
                                lp.width=125;
                                imageCodeProject.setLayoutParams(lp);

                                toast.show();

                            }
                            Log.d("testtestestetst", weekpoints.toString());
                            TextView wp = findViewById(R.id.weekpoints);
                            wp.setText(weekpoints.toString());
                        }

                    } else {
                        Log.d("test", "No such document");
                    }
                } else {
                    Log.d("test", "get failed with ", task.getException());
                }
            }
        });
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
                if(!loginEmail.getText().toString().equals("") && !loginPwd.getText().toString().equals("")){
                    // perform login authentication function
                    mAuth.signInWithEmailAndPassword(loginEmail.getText().toString(), loginPwd.getText().toString())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        currentUser = mAuth.getCurrentUser();
                                        //Toast.makeText(MainActivity.this, "Log in button clicked", Toast.LENGTH_SHORT).show();
                                        updateUI(currentUser);
                                    } else{
                                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else{
                    Toast.makeText(getApplicationContext(), "username/password should not be null", Toast.LENGTH_SHORT).show();
                }

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

    public void updateUI(FirebaseUser user){
        if(user!=null){
            updateTime();
            setContentView(R.layout.activity_dashboard);
            textView = (TextView) findViewById(R.id.busu);
            textView.setVisibility(View.INVISIBLE);
            BottomNavigationView navView = findViewById(R.id.nav_view);
            // 初始化展示MessageFragment
            setFragmentPosition(0);

            // set default to home fragment
            navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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

            this.currentstep=walk;
            this.initstepcount= walk;
            Intent intent = new Intent(MainActivity.this, BindService.class);
            isBind =  bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            startService(intent); //绷定并且开启一个服务，绷定是为了方便数据交换，启动是为了当当前app不在活动页的时候，计步服务不会被关闭。需要保证当activity不为活跃状态是计步服务在后台能一直运行！



        }
    }
    private void updatefirebase (){
        final String username=FirebaseAuth.getInstance().getCurrentUser().getUid();
        firedb = FirebaseFirestore.getInstance();
        firedb.collection("UserCollection").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("test", "DocumentSnapshot data: " + document.getData());
                        Map data = document.getData();
                        Long weekpoints = (Long)data.get("currWeekPoints")+1;
                        System.out.println(weekpoints);
                        Long totalpoints = (Long)data.get("totalPoints")+1;
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
    }
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 1) {
                if (((msg.arg1+initstepcount)/1000)>(currentstep/1000) &&(msg.arg1+initstepcount<=10000) ){
                    updatefirebase();
                }
                currentstep= msg.arg1+initstepcount;
                View view = mFragments.get(1).getView();
                textView.setText( currentstep+ "");
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
                task.setWalk(currentstep);
                task.setRubbish(rubbish);
                sqlDatabase.update(TaskDBModel.TABLE_NAME, task.toContentValues(),"id = ?", new String[]{String.valueOf(id)});
                System.out.println("query--->" + id + "," + rubbish + "," + dining+","+walk+","+quiz);//输出数据


                if(view!=null){
                    TextView a = (TextView)mFragments.get(1).getView().findViewById(R.id.bushu);
                    bnp = (NumberProgressBar)mFragments.get(1).getView().findViewById(R.id.pb_update_progress);
                    a.setText("Current steps Today :"+ currentstep);

                    if (currentstep<10000){
                        bnp.setProgress(currentstep/100);

                    }
                    else {
                        bnp.setProgress(100);
                    }
                }
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

